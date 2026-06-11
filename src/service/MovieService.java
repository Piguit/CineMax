package service;

import model.Movie;
import repository.MovieRepository;

import java.util.ArrayList;
import java.util.List;
import utility.OutputPrinter;

/**
 * <p>
 * La classe {@code MovieService} gestisce le operazioni relative ai film
 * all'interno di CineMax.
 * </p>
 * <p>
 * Fornisce metodi per la ricerca e la stampa di film in base a criteri
 * opzionali ({@code searchAndPrintMovies}) e per l'aggiunta di un nuovo film
 * al catalogo ({@code addMovie}), evitando duplicati e generando un ID
 * incrementale.
 * </p>
 * <p>
 * La ricerca avviene tramite scansione sequenziale del repository dei film
 * e utilizza {@link OutputPrinter} per visualizzare i risultati a blocchi.
 * </p>
 */
public class MovieService {
    private final MovieRepository mRepo;
    private final OutputPrinter op;

    /**
     * Costruttore che inizializza il servizio film.
     * @param mRepo repository dei film
     * @param op    oggetto per la stampa formattata dell'output
     */
    public MovieService(MovieRepository mRepo, OutputPrinter op) {
        this.mRepo = mRepo;
        this.op = op;
    }

    /**
     * Cerca film nel catalogo in base a titolo (parziale), regista (esatto)
     * e anno (esatto), e stampa i risultati.
     * <p>
     * I parametri di ricerca sono opzionali; se {@code null} o vuoti, il
     * corrispondente filtro viene ignorato. La ricerca sul titolo verifica la presenza
     * della sottostringa.
     * </p>
     * @param partialTitle sottostringa del titolo (opzionale, case‑insensitive)
     * @param director nome completo del regista (opzionale, case‑insensitive)
     * @param year anno di uscita (opzionale)
     * @return il numero di film stampati (che soddisfano i criteri)
     */
    public int searchAndPrintMovies(String partialTitle, String director, Short year) {
        boolean titleExists = false;
        if (titleExists = (partialTitle != null && !partialTitle.isBlank()))
            partialTitle = partialTitle.toLowerCase();
        boolean directorExists = false;
        if (directorExists = (director != null && !director.isBlank()))
            director = director.toLowerCase();
        int printedItems = 0;
        mRepo.startSequentialReading();
        try {
            List<Movie> movies;
            while ((movies = mRepo.getNextItems()) != null) {
                List<String> strings = new ArrayList<>();
                for (Movie m : movies) {
                    if ((titleExists && !m.getTitle().toLowerCase().contains(partialTitle)) ||
                        (directorExists && !m.getDirector().toLowerCase().equals(director)) ||
                        (year != null && !m.getYear().equals(year)))
                        continue;
                    strings.add(m.toString());
                }
                printedItems += strings.size();
                op.printlnMarkedByChunk(strings);
            }
        } finally {
            mRepo.endSequentialReading();
        }
        return printedItems;
    }

    /**
     * Aggiunge un nuovo film al catalogo se non esiste già un film identico
     * per titolo, regista, anno, genere, durata ed età minima.
     * <p>
     * In caso di film duplicato, non viene inserito nuovamente ma viene
     * restituito l'ID del film esistente. L'ID per un nuovo film viene
     * calcolato come {@code maxId + 1}.
     * </p>
     * @param title titolo del film
     * @param director regista del film
     * @param year anno di uscita
     * @param genre genere del film
     * @param runningTime durata in minuti
     * @param minAge età minima richiesta per il pubblico
     * @return l'ID del film (esistente o appena creato)
     */
    public Long addMovie(String title, String director, Short year, String genre, Short runningTime, Byte minAge) {
        mRepo.startSequentialReading();
        try {
            List<Movie> movies;
            while ((movies = mRepo.getNextItems()) != null)
                for (Movie m : movies)
                    if (m.getTitle().equalsIgnoreCase(title) && m.getDirector().equalsIgnoreCase(director) && m.getYear().equals(year) &&
                        m.getGenre().equalsIgnoreCase(genre) && m.getRunningTime().equals(runningTime) && m.getMinAge().equals(minAge))
                        return m.getId();
        } finally {
            mRepo.endSequentialReading();
        }

        Long id = mRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Movie movie = new Movie(id, title, director, year, genre, runningTime, minAge);
        mRepo.insert(movie);
        return id;
    }
}
