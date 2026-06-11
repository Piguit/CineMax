package service;

/**
 * <p>
 * Eccezione utilizzata per segnalare errori prevedibili
 * e comunicabili all'utente finale attraverso l'interfaccia testuale.
 * </p>
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public class PromptException extends RuntimeException {
    public PromptException(String msg) {
        super(msg);
    }
}
