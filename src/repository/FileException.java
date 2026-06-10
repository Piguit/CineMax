package repository;

/**
 * Eccezione unchecked lanciata dal layer repository quando un'operazione
 * di I/O su file fallisce in modo non recuperabile (creazione, apertura,
 * lettura, scrittura o spostamento del file di dati). Estende
 * {@link RuntimeException} per non obbligare i chiamanti a gestirla
 * esplicitamente; viene catturata nel {@code main} come errore fatale.
 */
public class FileException extends RuntimeException {
    /**
     * Istanzia l'eccezione con il messaggio descrittivo dell'errore.
     * @param msg descrizione dell'errore di I/O.
     */
    public FileException(String msg) {
        super(msg);
    }
}
