package repository;

import java.util.List;

import model.Show;

/**
 * Repository specifica per entità di tipo {@link Show}.
 * Implementa l'interfaccia {@link FileRepository} per oggetti
 * {@link Show} identificabili tramite id {@link Long}.
 * @author Piergiorgio Tomaciello 761013
 * @author Tamirat Tucci 756969
 * CO
 */
public class ShowRepository implements FileRepository<Show, Long> {
    /** Il nome del file in cui salvare le proiezioni. */
    public static final String FILE_NAME = "show_repository.txt";
    
    /** Il template generico che esegue l'I/O su file. */
    private GenericRepository<Long, Show> r;

    /**
     * Istanzia il template generico {@link #r} con l'entità
     * proiezione modello e il nome del file per la persistenza.
     */
    public ShowRepository() {
        this.r = new GenericRepository<>(new Show(), FILE_NAME);
    }

    /**
     * Restituisce la proiezione con l'identificativo specificato.
     * @param id identificativo da cercare
     * @return la proiezione corrispondente, {@code null} se non presente
     */
    public Show findById(Long id) {
        return r.findById(id);
    }

    /**
     * Inserisce una nuova proiezione nel file.
     * @param show proiezione da inserire; il suo id non deve essere già presente
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se
     *         una proiezione con lo stesso id esiste già
     */
    public boolean insert(Show show) {
        return r.save(show);
    }

    /**
     * Elimina la proiezione con l'identificativo specificato.
     * @param id identificativo della proiezione da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se
     *         la proiezione non è presente
     */
    public boolean delete(Long id) {
        return r.delete(id);
    }

    /**
     * Aggiorna la proiezione in input rendendo le modifiche persistenti.
     * @param show proiezione le cui modifiche devono essere salvate
     * @return {@code true} se la modifica è avvenuta, {@code false} se
     *         la proiezione non è presente
     */
    public boolean update(Show show) {
        return r.update(show);
    }

    /**
     * Restituisce l'identificativo massimo presente nel repository.
     * @return id massimo, {@code null} se il repository è vuoto
     */
    public Long getMaxId() {
        return r.getMaxId();
    }

    /**
     * Restituisce il prossimo blocco di proiezioni nella sessione di lettura
     * sequenziale corrente. Deve essere preceduto da {@link #startSequentialReading()}
     * e seguito da {@link #endSequentialReading()}.
     * @return lista di proiezioni del blocco corrente, {@code null} a fine file
     */
    public List<Show> getNextItems() {
        return r.getNextItems();
    }

    /** Avvia la sessione di lettura. */
    public void startSequentialReading() {
        r.startSequentialReading();
    }

    /** Chiudi la sessione di lettura. */
    public void endSequentialReading() {
        r.endSequentialReading();
    }
}
