package utility;

/**
 * La classe {@code SafetyException} si utilizza come sottoclasse di {@link RuntimeException}
 * per comunicare eccezioni fatali derivanti da un setup scorretto
 * dell'ambiente di gestione della crittografia.
 */
public class SafetyException extends RuntimeException {
    /**
     * Costruttore che istanzia una {@code SafetyException} con il messaggio specificato.
     * @param msg messaggio che descrive l'errore
     */
    public SafetyException(String msg) {
        super(msg);
    }
}
