package repository;

import java.util.List;

/**
 * Interfaccia generica che definisce il contratto per i repository
 * basati su file. E' parametrizzata sul tipo dell'entità ({@code F}) e
 * sul tipo del suo identificativo ({@code ID}).
 *
 * @param <F>  tipo dell'entità gestita dal repository
 * @param <ID> tipo dell'identificativo univoco dell'entità
 */
public interface FileRepository<F, ID> {
    /**
     * Cerca e restituisce l'entità con l'identificativo specificato.
     * @param id identificativo da cercare
     * @return l'entità corrispondente, {@code null} se non presente
     */
    public F findById(ID id);

    /**
     * Inserisce una nuova entità nel file in append.
     * @param entity entità da inserire; il suo id non deve essere già presente
     * @return {@code true} se l'inserimento è avvenuto, {@code false} se
     *         un'entità con lo stesso id esiste già
     */
    public boolean insert(F entity);

    /**
     * Elimina l'entità con l'identificativo specificato riscrivendo il file
     * su un file temporaneo tramite il pattern copy-on-write.
     * @param id identificativo dell'entità da eliminare
     * @return {@code true} se l'eliminazione è avvenuta, {@code false} se
     *         l'entità non è presente
     */
    public boolean delete(ID id);

    /**
     * Aggiorna l'entità in input rendendo le modifiche persistenti, riscrivendo il file
     * su un file temporaneo tramite il pattern copy-on-write.
     * @param entity entità le cui modifiche devono essere salvate
     * @return {@code true} se la modifica è avvenuta, {@code false} se
     *         l'entità non è presente
     */
    public boolean update(F entity);

    /**
     * Restituisce l'identificativo massimo presente nel repository.
     * @return id massimo, {@code null} se il repository è vuoto
     */
    public ID getMaxId();

    /**
     * Restituisce il prossimo blocco di entità nella sessione di lettura
     * sequenziale corrente. Deve essere preceduto da {@link #startSequentialReading()}
     * e seguito da {@link #endSequentialReading()}.
     * @return lista di entità del blocco corrente, {@code null} a fine file
     */
    public List<F> getNextItems();

    /**
     * Inizializza una sessione di lettura sequenziale aprendo il
     * {@link java.nio.channels.FileChannel} del file. Deve essere
     * chiamato prima di {@link #getNextItems()}.
     */
    public void startSequentialReading();

    /**
     * Chiude il {@link java.nio.channels.FileChannel} della sessione
     * di lettura sequenziale corrente. Deve essere sempre chiamato al termine
     * delle iterazioni di lettura.
     */
    public void endSequentialReading();
}
