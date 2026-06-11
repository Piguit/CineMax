package repository;

import java.util.List;

import model.Movie;

/**
 * Repository specifica per entità di tipo {@link Movie}.
 * Implementa l'interfaccia {@link FileRepository} per oggetti
 * {@link Movie} identificabili tramite id {@link Long}.
 * @author Piergiorgio Tomaciello 761013
 * @author Tamirat Tucci 756969
 * CO
 */
public class MovieRepository implements FileRepository<Movie, Long> {
    /** Il nome del file in cui salvare i film. */
    public static final String FILE_NAME = "movie_repository.txt";
    
    /** Il template generico che esegue l'I/O su file. */
    private GenericRepository<Long, Movie> r;

    /**
     * Istanzia il template generico {@link #r} con l'entità
     * film modello e il nome del file per la persistenza.
     */
    public MovieRepository() {
        this.r = new GenericRepository<>(new Movie(), FILE_NAME);
    }

    /**
     * Restituisce il film con l'identificativo specificato.
     * @param id identificativo da cercare
     * @return il film corrispondente, {@code null} se non presente
     */
    public Movie findById(Long id) {
        return r.findById(id);
    }

    /**
     * Inserisce un nuovo film nel file.
     * @param movie film da inserire; il suo id non deve essere già presente
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se
     *         un film con lo stesso id esiste già
     */
    public boolean insert(Movie movie) {
        return r.save(movie);
    }

    /**
     * Elimina il film con l'identificativo specificato.
     * @param id identificativo del film da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se
     *         il film non è presente
     */
    public boolean delete(Long id) {
        return r.delete(id);
    }

    /**
     * Aggiorna il film in input rendendo le modifiche persistenti.
     * @param movie film le cui modifiche devono essere salvate
     * @return {@code true} se la modifica è avvenuta, {@code false} se
     *         il film non è presente
     */
    public boolean update(Movie movie) {
        return r.update(movie);
    }

    /**
     * Restituisce l'identificativo massimo presente nel repository.
     * @return id massimo, {@code null} se il repository è vuoto
     */
    public Long getMaxId() {
        return r.getMaxId();
    }

    /**
     * Restituisce il prossimo blocco di film nella sessione di lettura
     * sequenziale corrente. Deve essere preceduto da {@link #startSequentialReading()}
     * e seguito da {@link #endSequentialReading()}.
     * @return lista di film del blocco corrente, {@code null} a fine file
     */
    public List<Movie> getNextItems() {
        return r.getNextItems();
    }

    /** Avvia la sessione di lettura */
    public void startSequentialReading() {
        r.startSequentialReading();
    }

    /** Chiudi la sessione di lettura */
    public void endSequentialReading() {
        r.endSequentialReading();
    }
}
