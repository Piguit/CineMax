package service;

import repository.MovieRepository;
import repository.ReservationRepository;
import repository.ShowRepository;
import model.Movie;
import model.Reservation;
import model.Show;
import model.FullShowDetails;
import model.ShowDetails;
import utility.OutputPrinter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * La classe {@code ShowService} gestisce le operazioni relative alle proiezioni
 * all'interno di CineMax.
 * </p>
 * <p>
 * Fornisce metodi per la ricerca e la stampa di proiezioni in base a vari
 * criteri opzionali ({@code searchAndPrintShows}), la visualizzazione dei dettagli
 * completi di una proiezione ({@code visualizeShow}), l'aggiunta di nuove proiezioni
 * con controllo di sovrapposizione ({@code addShow}), la modifica ({@code editShow})
 * e l'eliminazione ({@code deleteShow}) di proiezioni esistenti (solo se non hanno
 * prenotazioni associate).
 * </p>
 * <p>
 * La capacità massima della sala è fissata a 200 posti e il numero di posti liberi
 * viene calcolato sottraendo i biglietti prenotati dalla capienza totale.
 * </p>
 */
public class ShowService {
    private final ShowRepository sRepo;
    private final ReservationRepository rRepo;
    private final MovieRepository mRepo;
    private final OutputPrinter op;

    /**
     * Costruttore che inizializza il servizio proiezioni.
     * @param sRepo repository delle proiezioni
     * @param rRepo repository delle prenotazioni
     * @param mRepo repository dei film
     * @param op    oggetto per la stampa dell'output
     */
    public ShowService(ShowRepository sRepo, ReservationRepository rRepo, MovieRepository mRepo, OutputPrinter op) {
        this.sRepo = sRepo;
        this.rRepo = rRepo;
        this.mRepo = mRepo;
        this.op = op;
    }

/**
 * Cerca proiezioni in base a criteri opzionali e stampa i risultati a video.
 * <p>
 * I criteri di ricerca sono:
 * {@code partialTitle}: sottostringa del titolo (case‑insensitive)
 * {@code genre}: genere del film (case‑insensitive, uguaglianza esatta)
 * {@code from} e {@code to}: intervallo di date (estremi inclusi)
 * {@code minCost} e {@code maxCost}: intervallo del costo del biglietto
 * I parametri {@code null} o vuoti indicano l'assenza del filtro corrispondente.
 * </p>
 * <p>
 * La scansione avviene in modo sequenziale sul repository delle proiezioni
 * e per ogni proiezione viene recuperato il film associato per applicare
 * i filtri su titolo e genere. I risultati sono infine stampati a blocchi.
 * </p>
 * @param partialTitle sottostringa del titolo del film
 * @param genre        genere del film
 * @param from         data di inizio intervallo
 * @param to           data di fine intervallo
 * @param minCost      costo minimo del biglietto
 * @param maxCost      costo massimo del biglietto
 * @return il numero di proiezioni stampate che soddisfano i criteri
 */
    public int searchAndPrintShows(String partialTitle, String genre,
                                 LocalDate from, LocalDate to,
                                 Float minCost, Float maxCost) {
        boolean titleExists = false;
        if (titleExists = (partialTitle != null && !partialTitle.isBlank()))
            partialTitle = partialTitle.toLowerCase();
        boolean genreExists = false;
        if (genreExists = (genre != null && !genre.isBlank()))
            genre = genre.toLowerCase();
        int printedItems = 0;
        sRepo.startSequentialReading();
        try {
            List<Show> shows;
            while ((shows = sRepo.getNextItems()) != null) {
                List<String> strings = new ArrayList<>();
                for (Show s : shows) {
                    Movie movie = mRepo.findById(s.getMovieId());
                    if (titleExists && !movie.getTitle().toLowerCase().contains(partialTitle)) {
                        continue;
                    }
                    if (genreExists && !movie.getGenre().toLowerCase().equals(genre)) {
                        continue;
                    }
                    LocalDateTime date = s.getShowDate();
                    if (from != null && !(date.getYear() > from.getYear() ||
                        (date.getYear() == from.getYear() && date.getDayOfYear() >= from.getDayOfYear()))) {
                        continue;
                    }
                    if (to != null && !(date.getYear() < to.getYear() ||
                        (date.getYear() == to.getYear() && date.getDayOfYear() <= to.getDayOfYear()))) {
                        continue;
                    }
                    if (minCost != null && s.getTicketCost() < minCost) {
                        continue;
                    }
                    if (maxCost != null && s.getTicketCost() > maxCost) {
                        continue;
                    }
                    strings.add(new ShowDetails(s, movie).toString());
                }
                
                printedItems += strings.size();
                op.printlnMarkedByChunk(strings);
            }
        } finally {
            sRepo.endSequentialReading();
        }
        return printedItems;
    }

    /**
     * Visualizza i dettagli completi di una proiezione, inclusi i posti liberi.
     * <p>
     * Il metodo recupera la proiezione tramite il suo identificatore, il film
     * associato e calcola il numero di posti occupati sommando i biglietti
     * delle prenotazioni relative a quella proiezione. I posti liberi sono
     * ottenuti come differenza tra la capienza della sala (200) e i posti occupati.
     * </p>
     * @param showId l'ID della proiezione da visualizzare
     * @return un oggetto {@link FullShowDetails} contenente tutti i dettagli
     *         della proiezione e i posti liberi, oppure {@code null} se la
     *         proiezione non esiste
     */
    public FullShowDetails visualizeShow(Long showId) {
        Show s = sRepo.findById(showId);
        if (s == null)
            return null;
        Movie m = mRepo.findById(s.getMovieId());
        int takenSeats = ReservationService.countTicketsByShow(rRepo, showId);
        int freeSeats = 200 - takenSeats;
        return new FullShowDetails(s, m, freeSeats);
    }

    /**
     * Aggiunge una nuova proiezione al sistema dopo aver verificato non vi sia una
     * sovrapposizione con altre proiezioni esistenti.
     * <p>
     * La sovrapposizione viene verificata considerando la data/ora di inizio
     * e la durata del film. Se viene rilevata una sovrapposizione, viene lanciata un'eccezione.
     * </p>
     * <p>
     * L'ID della nuova proiezione viene calcolato incrementando l'ID massimo
     * già presente nel repository.
     * </p>
     * @param movieId ID del film da proiettare
     * @param showDate data e ora di inizio della proiezione
     * @param ticketCost costo del biglietto per questa proiezione
     * @return l'ID della proiezione appena creata
     * @throws PromptException se il film con l'ID specificato non esiste,
     * oppure se la nuova proiezione si sovrappone
     * con una proiezione già esistente
     */
    public Long addShow(Long movieId, LocalDateTime showDate, Float ticketCost) {
        Movie m = mRepo.findById(movieId);
        if (m == null)
            throw new PromptException("(!) Film inesistente.");
        LocalDateTime endNew = showDate.plusMinutes(m.getRunningTime());
        
        sRepo.startSequentialReading();
        try {
            List<Show> shows;
            while ((shows = sRepo.getNextItems()) != null)
                for (Show s : shows) {
                    LocalDateTime endS = s.getShowDate().plusMinutes(mRepo.findById(s.getMovieId()).getRunningTime());
                    if (showDate.isBefore(endS) && endNew.isAfter(s.getShowDate()))
                        throw new PromptException("(!) La proiezione si sovrappone con la proiezione " + s.getId() + ".");
                }
        } finally {
            sRepo.endSequentialReading();
        }
        
        Long id = sRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Show newS = new Show(id, movieId, showDate, ticketCost);
        sRepo.insert(newS);
        return id;
    }

    /**
     * Verifica se esistono prenotazioni per una data proiezione.
     * <p>
     * Metodo privato utilizzato per controllare la presenza di prenotazioni prima
     * di consentire la modifica o l'eliminazione di una proiezione.
     * </p>
     * @param showId ID della proiezione da controllare
     * @return {@code true} se almeno una prenotazione è associata alla proiezione,
     *         {@code false} altrimenti
     */
    private boolean anyReservationForTheShow(Long showId) {
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                if (!reservations.stream().filter(p -> p.getShowId().equals(showId)).toList().isEmpty())
                    return true;
        } finally {
            rRepo.endSequentialReading();
        }
        return false;
    }

    /**
     * Modifica una proiezione esistente (data/ora e/o costo del biglietto).
     * <p>
     * La modifica è consentita solo se non esistono prenotazioni associate
     * alla proiezione. I parametri {@code newShowDate} e {@code newTicketCost}
     * possono essere {@code null} per indicare che quel campo non deve essere
     * modificato.
     * </p>
     * @param showId        ID della proiezione da modificare
     * @param newShowDate   nuova data/ora
     * @param newTicketCost nuovo costo del biglietto
     * @throws PromptException se la proiezione non esiste oppure se esistono
     *                         già prenotazioni per quella proiezione
     */
    public void editShow(Long showId, LocalDateTime newShowDate, Float newTicketCost) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new PromptException("(!) Proiezione inesistente.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new PromptException("(!) Modifica non permessa: esistono gia' prenotazioni per la proiezione corrente.");
        }
        if (newShowDate != null)
            s.setShowDate(newShowDate);
        if (newTicketCost != null)
            s.setTicketCost(newTicketCost);
        sRepo.update(s);
    }

    /**
     * Elimina una proiezione esistente dal sistema.
     * <p>
     * L'eliminazione è consentita solo se non esistono prenotazioni associate
     * alla proiezione.
     * </p>
     * @param showId ID della proiezione da eliminare
     * @throws PromptException se la proiezione non esiste oppure se esistono
     *                         già prenotazioni per quella proiezione
     */
    public void deleteShow(Long showId) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new PromptException("(!) Proiezione inesistente.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new PromptException("(!) Eliminazione non permessa: esistono gia' prenotazioni per la proiezione corrente.");
        }
        sRepo.delete(showId);
    }
}
