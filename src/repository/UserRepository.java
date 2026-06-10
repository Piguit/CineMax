package repository;

import java.util.List;

import model.User;

/**
 * Repository specifica per entità di tipo {@link User}.
 * Implementa l'interfaccia {@link FileRepository} per oggetti
 * {@link User} identificabili tramite id {@link String}.
 */
public class UserRepository implements FileRepository<User, String> {
    /** Il nome del file in cui salvare gli utenti. */
    public static final String FILE_NAME = "user_repository.txt";
    
    /** Il template generico che esegue l'I/O su file. */
    private GenericRepository<String, User> r;

    /**
     * Istanzia il template generico {@link #r} con l'entità
     * utente modello e il nome del file per la persistenza.
     */
    public UserRepository() {
        this.r = new GenericRepository<>(new User(), FILE_NAME);
    }

    /**
     * Restituisce l'utente con l'identificativo specificato.
     * @param id identificativo da cercare
     * @return l'utente corrispondente, {@code null} se non presente
     */
    public User findById(String id) {
        return r.findById(id);
    }

    /**
     * Inserisce un nuovo utente nel file.
     * @param user utente da inserire; il suo id non deve essere già presente
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se
     *         un utente con lo stesso id esiste già
     */
    public boolean insert(User user) {
        return r.save(user);
    }

    /**
     * Elimina l'utente con l'identificativo specificato.
     * @param id identificativo dell'utente da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se
     *         l'utente non è presente
     */
    public boolean delete(String id) {
        return r.delete(id);
    }

    /**
     * Aggiorna l'utente in input rendendo le modifiche persistenti.
     * @param user utente le cui modifiche devono essere salvate
     * @return {@code true} se la modifica è avvenuta, {@code false} se
     *         l'utente non è presente
     */
    public boolean update(User user) {
        return r.update(user);
    }

    /**
     * Restituisce l'identificativo massimo presente nel repository.
     * @return id massimo, {@code null} se il repository è vuoto
     */
    public String getMaxId() {
        return r.getMaxId();
    }

    /**
     * Restituisce il prossimo blocco di utenti nella sessione di lettura
     * sequenziale corrente. Deve essere preceduto da {@link #startSequentialReading()}
     * e seguito da {@link #endSequentialReading()}.
     * @return lista di utenti del blocco corrente, {@code null} a fine file
     */
    public List<User> getNextItems() {
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
