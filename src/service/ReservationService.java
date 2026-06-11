package service;

import repository.MovieRepository;
import repository.ReservationRepository;
import repository.ShowRepository;
import repository.UserRepository;
import utility.OutputPrinter;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * La classe {@code ReservationService} gestisce tutte le operazioni relative
 * alle prenotazioni in CineMax.
 * </p>
 * <p>
 * Fornisce metodi per:
 *   Stampare le prenotazioni di un utente ({@code printMyReservations})
 *   Aggiungere una nuova prenotazione ({@code addReservation}) con controlli
 *   Modificare una prenotazione esistente ({@code editReservation}) con
 *       controlli di coerenza
 *   Eliminare una prenotazione ({@code deleteReservation}) solo per
 *       proiezioni già avvenute
 *   Visualizzare i dettagli di una prenotazione ({@code visualizeReservation})
 *   Stampare le prenotazioni della giornata corrente ({@code printTodayReservations})
 *   Cercare prenotazioni secondo vari criteri ({@code searchAndPrintReservations})
 * </p>
 * <p>
 * La capienza massima della sala è 200 posti e il numero di posti occupati
 * viene calcolato sommando i biglietti delle prenotazioni per una determinata proiezione.
 * </p>
 * <p>
 * Le eccezioni sollevate sono di tipo {@link PromptException} per segnalare
 * situazioni prevedibili (proiezione inesistente, data passata, posti insufficienti,
 * prenotazione già esistente, etc.).
 * </p>
 */
public class ReservationService {
    private final ReservationRepository rRepo;
    private final ShowRepository sRepo;
    private final UserRepository uRepo;
    private final MovieRepository mRepo;
    private final OutputPrinter op;

    /**
     * Costruttore che inizializza il servizio prenotazioni.
     * @param rRepo repository delle prenotazioni
     * @param sRepo repository delle proiezioni
     * @param uRepo repository degli utenti
     * @param mRepo repository dei film
     * @param op    oggetto per la stampa formattata dell'output
     */
    public ReservationService(ReservationRepository rRepo, ShowRepository sRepo,
                              UserRepository uRepo, MovieRepository mRepo, OutputPrinter op) {
        this.rRepo = rRepo;
        this.sRepo = sRepo;
        this.uRepo = uRepo;
        this.mRepo = mRepo;
        this.op = op;
    }

    /**
     * Metodo statico che conta il numero totale di biglietti prenotati per una data proiezione.
     * @param rRepo repository delle prenotazioni
     * @param showId ID della proiezione
     * @return numero totale di biglietti prenotati per la proiezione specificata
     */
    public static int countTicketsByShow(ReservationRepository rRepo, Long showId) {
        int ticketsNumber = 0;
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                ticketsNumber += reservations.stream().filter(r -> r.getShowId().equals(showId)).mapToInt(Reservation::getTicketsNumber).sum();
        } finally {
            rRepo.endSequentialReading();
        }
        return ticketsNumber;
    }

    /**
     * Stampa tutte le prenotazioni associate a un dato username.
     * @param username nome utente del cliente
     * @return il numero di prenotazioni stampate
     */
    public int printMyReservations(String username) {
        rRepo.startSequentialReading();
        int printedItems = 0;
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null) {
                List<String> strings = new ArrayList<>();
                for (Reservation r : reservations) {
                    if (!r.getUsername().equals(username))
                        continue;
                    User u = uRepo.findById(r.getUsername());
                    Show s = sRepo.findById(r.getShowId());
                    Movie m = mRepo.findById(s.getMovieId());
                    strings.add(new FullReservationDetails(r, u, s, m).toString());
                }
                printedItems += strings.size();
                op.printlnMarkedByChunk(strings);
            }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }

    /**
     * Aggiunge una nuova prenotazione per un cliente a una proiezione.
     * <p>
     * Prima di aggiungere la prenotazione vengono eseguiti i seguenti controlli:
     *   La proiezione deve esistere
     *   La proiezione non deve essere già avvenuta
     *   Non deve esistere già una prenotazione per lo stesso utente e la stessa proiezione
     *   I posti richiesti, sommati a quelli già occupati, non devono superare la capienza massima (200)
     * L'ID della nuova prenotazione viene generato incrementando l'ID massimo
     * presente nel repository.
     * </p>
     * @param username username del cliente che effettua la prenotazione
     * @param showId ID della proiezione per cui prenotare
     * @param ticketsNumber numero di biglietti da prenotare
     * @return l'ID della prenotazione appena creata
     * @throws PromptException se la proiezione non esiste, è già avvenuta,
     *                         se esiste già una prenotazione per lo stesso
     *                         utente/proiezione, o se i posti sono insufficienti
     */
    public Long addReservation(String username, Long showId, Short ticketsNumber) {
        Show s = sRepo.findById(showId);
        if (s == null)
            throw new PromptException("(!) Proiezione inesistente.");
        if (s.getShowDate().isBefore(LocalDateTime.now()))
            throw new PromptException("(!) Proiezione gia' avvenuta.");
        
        int seatsTaken = 0;
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                for (Reservation res : reservations)
                    if (res.getShowId().equals(showId)) {
                        if (res.getUsername().equals(username))
                            throw new PromptException("(!) Prenotazione a nome dell'utente gia' presente. Procedere alla modifica.");
                        seatsTaken += res.getTicketsNumber();
                    }
        } finally {
            rRepo.endSequentialReading();
        }
        if (seatsTaken + ticketsNumber > 200)
            throw new PromptException("(!) Posti insufficienti. Disponibili: " + (200 - seatsTaken) + ".");
        
        Long id = rRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Reservation newR = new Reservation(id, username, showId, ticketsNumber);
        rRepo.insert(newR);
        return id;
    }

    /**
     * Modifica una prenotazione esistente (cambio di proiezione e/o numero di biglietti).
     * <p>
     * La modifica è consentita solo se:
     *   Sia la proiezione originale che quella nuova (se diversa) non sono ancora avvenute
     *   La prenotazione appartiene all'utente che richiede la modifica
     *   Non esiste già una prenotazione dello stesso utente per la nuova proiezione
     *   I posti richiesti sono disponibili sulla nuova proiezione
     * Se la proiezione rimane la stessa, i posti già occupati dall'utente vengono
     * sottratti dal conteggio dei posti occupati.
     * </p>
     * @param username        username del cliente proprietario della prenotazione
     * @param reservationId   ID della prenotazione da modificare
     * @param newShowId       ID della nuova proiezione (può essere uguale a quella originale)
     * @param ticketsNumber   nuovo numero di biglietti
     * @throws PromptException se la prenotazione o la proiezione non esistono,
     *                         se la prenotazione appartiene ad un altro utente,
     *                         se una delle proiezioni è già avvenuta,
     *                         se esiste già una prenotazione duplicata sulla nuova proiezione,
     *                         o se i posti sono insufficienti
     */
    public void editReservation(String username, Long reservationId, Long newShowId, Short ticketsNumber) {
        Reservation r = rRepo.findById(reservationId);
        Show newS = sRepo.findById(newShowId);
        if (newS == null) {
            if (r == null)
                throw new PromptException("(!) Prenotazione e proiezione inesistenti.");
            if (!r.getUsername().equals(username))
                throw new PromptException("(!) La prenotazione appartiene ad un altro utente e la proiezione non esiste.");
            throw new PromptException("(!) Proiezione inesistente.");
        } else {
            if (r == null)
                throw new PromptException("(!) Prenotazione inesistente.");
            if (!r.getUsername().equals(username))
                throw new PromptException("(!) La prenotazione appartiene ad un altro utente.");
        }
        
        Show oldS = sRepo.findById(r.getShowId());
        if (oldS.getShowDate().isBefore(LocalDateTime.now()) ||
                (newS.getShowDate()).isBefore(LocalDateTime.now()))
            throw new PromptException("(!) Le proiezioni non devono essere gia' avvenute.");

        int seatsTaken = 0;
        if (!r.getShowId().equals(newShowId)) {
            rRepo.startSequentialReading();
            try {
                List<Reservation> reservations;
                while ((reservations = rRepo.getNextItems()) != null)
                    for (Reservation res : reservations)
                        if (res.getShowId().equals(newShowId)) {
                            if (res.getUsername().equals(username))
                                throw new PromptException("(!) Prenotazione a nome dell'utente gia' presente. Procedere alla modifica.");
                            seatsTaken += res.getTicketsNumber();
                        }
            } finally {
                rRepo.endSequentialReading();
            }
        } else
            seatsTaken = countTicketsByShow(rRepo, newShowId) - r.getTicketsNumber();
        if (seatsTaken + ticketsNumber > 200)
            throw new PromptException("(!) Posti insufficienti. Disponibili: " + (200 - seatsTaken) + ".");

        r.setShowId(newShowId);
        r.setTicketsNumber(ticketsNumber);
        rRepo.update(r);
    }

    /**
     * Elimina una prenotazione esistente.
     * <p>
     * L'eliminazione è consentita solo se la proiezione associata è già avvenuta
     * (data antecedente alla data odierna) e se la prenotazione appartiene
     * all'utente che richiede l'operazione.
     * </p>
     * @param username username del cliente proprietario della prenotazione
     * @param reservationId ID della prenotazione da eliminare
     * @throws PromptException se la prenotazione non esiste,
     *                         se appartiene ad un altro utente,
     *                         o se la proiezione non è ancora avvenuta
     */
    public void deleteReservation(String username, Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null)
            throw new PromptException("(!) Prenotazione inesistente.");
        if (!r.getUsername().equals(username))
            throw new PromptException("(!) La prenotazione appartiene ad un altro utente.");
        Show s = sRepo.findById(r.getShowId());
        if (!s.getShowDate().isBefore(LocalDateTime.now())) {
            throw new PromptException("(!) Si può eliminare solamente la prenotazione di una proiezione gia' avvenuta.");
        }
        rRepo.delete(reservationId);
    }

    /**
     * Ricevuto un ID, visualizza i dettagli completi della prenotazione ad esso associata.
     * @param reservationId ID della prenotazione da visualizzare
     * @return un oggetto {@link FullReservationDetails} contenente tutti i dati
     *         della prenotazione, oppure {@code null} se la prenotazione non esiste
     */
    public FullReservationDetails visualizeReservation(Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null)
            return null;
        User u = uRepo.findById(r.getUsername());
        Show s = sRepo.findById(r.getShowId());
        Movie m = mRepo.findById(s.getMovieId());
        return new FullReservationDetails(r, u, s, m);
    }

    /**
     * Stampa tutte le prenotazioni relative alla data odierna.
     * <p>
     * Vengono confrontati giorno e anno della data della proiezione con la data
     * corrente. Per ogni prenotazione vengono stampati i dettagli ridotti
     * tramite {@link ReservationDetails#toString()}.
     * </p>
     * @return il numero di prenotazioni stampate
     */
    public int printTodayReservations() {
        rRepo.startSequentialReading();
        int printedItems = 0;
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null) {
                List<String> strings = new ArrayList<>();
                for (Reservation r : reservations) {
                    Show s = sRepo.findById(r.getShowId());
                    if (LocalDateTime.now().getYear() != s.getShowDate().getYear() || LocalDateTime.now().getDayOfYear() != s.getShowDate().getDayOfYear())
                        continue;
                    User u = uRepo.findById(r.getUsername());
                    Movie m = mRepo.findById(s.getMovieId());
                    strings.add(new ReservationDetails(r, u, s, m).toString());
                }

                printedItems += strings.size();
                op.printlnMarkedByChunk(strings);
            }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }

    /**
     * Cerca prenotazioni in base a vari criteri opzionali e stampa i risultati.
     * <p>
     * I criteri di ricerca sono:
     *   {@code reservationId}: ID esatto della prenotazione
     *   {@code name} e {@code surname}: nome e cognome del cliente (case‑insensitive, uguaglianza esatta)
     *   {@code partialTitle}: sottostringa del titolo del film (case‑insensitive)
     *   {@code from} e {@code to}: intervallo di date della proiezione (estremi inclusi)
     * I parametri {@code null} indicano l'assenza del filtro corrispondente.
     * La ricerca viene eseguita scandendo sequenzialmente il repository delle prenotazioni.
     * </p>
     * @param reservationId ID esatto della prenotazione
     * @param name          nome del cliente
     * @param surname       cognome del cliente
     * @param partialTitle  sottostringa del titolo del film
     * @param from          data di inizio intervallo
     * @param to            data di fine intervallo
     * @return il numero di prenotazioni stampate che soddisfano i criteri
     */
    public int searchAndPrintReservations(Long reservationId, String name,
                                                String surname, String partialTitle,
                                                LocalDate from, LocalDate to) {
        boolean nameExists = false;
        if (nameExists = (name != null && !name.isBlank()))
            name = name.toLowerCase();
        boolean surnameExists = false;
        if (surnameExists = (surname != null && !surname.isBlank()))
            surname = surname.toLowerCase();
        boolean titleExists = false;
        if (titleExists = (partialTitle != null && !partialTitle.isBlank()))
            partialTitle = partialTitle.toLowerCase();
        int printedItems = 0;
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null) {
                List<String> strings = new ArrayList<>();
                for (Reservation r : reservations) {
                    User user = uRepo.findById(r.getUsername());
                    Show show = sRepo.findById(r.getShowId());
                    Movie movie = mRepo.findById(show.getMovieId());

                    if (reservationId != null && !r.getId().equals(reservationId))
                        continue;

                    if (nameExists || surnameExists) {
                        if ((nameExists && !name.equals(user.getName().toLowerCase())) || (surnameExists && !surname.equals(user.getSurname().toLowerCase())))
                            continue;
                    }

                    if (from != null || to != null || titleExists) {
                        if (from != null || to != null) {
                            LocalDateTime date = show.getShowDate();
                            if ((from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) ||
                                    (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())))
                                continue;
                        }
                        if (titleExists && !movie.getTitle().toLowerCase().contains(partialTitle))
                                continue;
                    }

                    strings.add(new ReservationDetails(r, user, show, movie).toString());
                }
                printedItems += strings.size();
                op.printlnMarkedByChunk(strings);
            }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }
}
