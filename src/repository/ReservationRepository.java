package repository;

import model.Reservation;

import java.util.List;

/**
 * Repository specifica per entità di tipo {@link Reservation}.
 * Implementa l'interfaccia {@link FileRepository} per oggetti
 * {@link Reservation} identificabili tramite id {@link Long}.
 */
public class ReservationRepository implements FileRepository<Reservation, Long> {
    /** Il nome del file in cui salvare le prenotazioni. */
    public static final String FILE_NAME = "reservation_repository.txt";
    
    /** Il template generico che esegue l'I/O su file. */
    private GenericRepository<Long, Reservation> r;

    /**
     * Istanzia il template generico {@link #r} con l'entità
     * prenotazione modello e il nome del file per la persistenza.
     */
    public ReservationRepository() {
        this.r = new GenericRepository<>(new Reservation(), FILE_NAME);
    }

    /**
     * Restituisce la prenotazione con l'identificativo specificato.
     * @param id identificativo da cercare
     * @return la prenotazione corrispondente, {@code null} se non presente
     */
    public Reservation findById(Long id) {
        return r.findById(id);
    }

    /**
     * Inserisce una nuova prenotazione nel file.
     * @param reservation prenotazione da inserire; il suo id non deve essere già presente
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se
     *         una prenotazione con lo stesso id esiste già
     */
    public boolean insert(Reservation reservation) {
        return r.save(reservation);
    }

    /**
     * Elimina la prenotazione con l'identificativo specificato.
     * @param id identificativo della prenotazione da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se
     *         la prenotazione non è presente
     */
    public boolean delete(Long id) {
        return r.delete(id);
    }

    /**
     * Aggiorna la prenotazione in input rendendo le modifiche persistenti.
     * @param reservation prenotazione le cui modifiche devono essere salvate
     * @return {@code true} se la modifica è avvenuta, {@code false} se
     *         la prenotazione non è presente
     */
    public boolean update(Reservation reservation) {
        return r.update(reservation);
    }
    
    /**
     * Restituisce l'identificativo massimo presente nel repository.
     * @return id massimo, {@code null} se il repository è vuoto
     */
    public Long getMaxId() {
        return r.getMaxId();
    }

    /**
     * Restituisce il prossimo blocco di prenotazioni nella sessione di lettura
     * sequenziale corrente. Deve essere preceduto da {@link #startSequentialReading()}
     * e seguito da {@link #endSequentialReading()}.
     * @return lista di prenotazioni del blocco corrente, {@code null} a fine file
     */
    public List<Reservation> getNextItems() {
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
