package service;

import repository.UserRepository;
import model.Role;
import model.User;
import utility.PasswordHandler;
import utility.SafetyException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

/**
 * <p>
 * La classe {@code Authentication} gestisce tutte le operazioni relative
 * all'autenticazione e alla registrazione degli utenti di CineMax.
 * </p>
 * <p>
 * Fornisce metodi per il login ({@code signIn}), la registrazione di clienti
 * e amministratori ({@code signUp}, {@code adminSignUp}), la promozione a
 * proiezionista o bigliettaio ({@code makeProjectionist},
 * {@code makeBoxOfficeClerk}), e un controllo per verificare se è il primo
 * accesso all'applicazione ({@code isFirstAccess}).
 * </p>
 * <p>
 * Tutte le password vengono gestite in modo sicuro tramite la classe
 * {@link PasswordHandler}, che applica hashing e verifica.
 * </p>
 */
public class Authentication {
    private final UserRepository uRepo;

    /**
     * Costruttore che inizializza il servizio di autenticazione.
     * @param uRepo il repository degli utenti utilizzato per l'accesso ai dati
     */
    public Authentication(UserRepository uRepo) {
        this.uRepo = uRepo;
    }

    /**
     * Verifica se la repository degli utenti è vuota, ovvero se è il primo avvio
     * dell'applicazione.
     * @return {@code true} se non esistono utenti, {@code false} altrimenti
     */
    public boolean isFirstAccess() {
        uRepo.startSequentialReading();
        boolean result = true;
        try {
            if (uRepo.getNextItems() != null)
                result = false;
        } finally {
            uRepo.endSequentialReading();
        }
        return result;
    }

    /**
     * Tenta di autenticare un utente a partire da username e password.
     * @param username lo username dell'utente
     * @param password la password in chiaro fornita dall'utente
     * @return l'oggetto {@link User} corrispondente se le credenziali sono valide
     * @throws PromptException se lo username non esiste o la password è errata
     * @throws SafetyException se si verifica un errore critico durante la verifica della password
     */
    public User signIn(String username, String password) {
        try {
            User u = uRepo.findById(username);
            if (u == null)
                throw new PromptException("(!) Username inesistente.");
            if (PasswordHandler.verifyPassword(password, u.getPassword()))
                return u;
            else
                throw new PromptException("(!) Password errata.");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SafetyException("<!> ERRORE FATALE. Si e' verificato un problema durante la verifica delle credenziali di sicurezza.");
        }
    }

    /**
     * Registra un nuovo cliente (ruolo {@link Role#CLIENT}) nel sistema.
     * @param username username desiderato che deve essere univoco
     * @param name nome del cliente
     * @param surname cognome del cliente
     * @param password password in chiaro che verrà sottoposta ad hashing
     * @param birthDate data di nascita che può essere {@code null}
     * @param residence luogo di domicilio
     * @return {@code true} se la registrazione ha successo, {@code false}
     *         se lo username è già occupato
     * @throws SafetyException se si verifica un errore critico durante l'hashing della password
     */
    public boolean signUp(String username, String name, String surname,
                          String password, LocalDate birthDate,
                          String residence) {
        try {
            if (uRepo.findById(username) != null) {
                return false;
            }
            password = PasswordHandler.hashPassword(password);
            User newUser = new User(username, name, surname, password, birthDate, residence, Role.CLIENT);
            return uRepo.insert(newUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SafetyException("<!> ERRORE FATALE. Si e' verificato un problema durante la verifica delle credenziali di sicurezza.");
        }
    }

    /**
     * Registra un nuovo amministratore (ruolo {@link Role#ADMIN}) nel sistema.
     * @param username username desiderato che deve essere univoco
     * @param name nome dell'amministratore
     * @param surname cognome dell'amministratore
     * @param password password in chiaro che verrà sottoposta ad hashing
     * @param birthDate data di nascita che può essere {@code null}
     * @param residence luogo di domicilio
     * @return {@code true} se la registrazione ha successo, {@code false}
     *         se lo username è già occupato
     * @throws SafetyException se si verifica un errore critico durante l'hashing della password
     */
    public boolean adminSignUp(String username, String name, String surname,
                          String password, LocalDate birthDate,
                          String residence) {
        try {
            if (uRepo.findById(username) != null) {
                return false;
            }
            password = PasswordHandler.hashPassword(password);
            User newUser = new User(username, name, surname, password, birthDate, residence, Role.ADMIN);
            return uRepo.insert(newUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SafetyException("<!> ERRORE FATALE. Si e' verificato un problema durante la verifica delle credenziali di sicurezza.");
        }
    }

    /**
     * Eleva un utente esistente al ruolo di proiezionista ({@link Role#PROJECTIONIST}).
     * L'operazione non è consentita sull'account amministratore.
     * @param username lo username dell'utente da promuovere
     * @throws PromptException se l'utente non esiste o se è un amministratore
     */
    public void makeProjectionist(String username) {
        User user = uRepo.findById(username);
        if (user == null)
            throw new PromptException("(!) Utente inesistente.");
        if (user.getRole().equals(Role.ADMIN))
            throw new PromptException("(!) L'account admin non puo' subire cambi di ruolo.");
        user.setRole(Role.PROJECTIONIST);
        uRepo.update(user);
    }

    /**
     * Eleva un utente esistente al ruolo di bigliettaio ({@link Role#BOXOFFICECLERK}).
     * L'operazione non è consentita sull'account amministratore.
     * @param username lo username dell'utente da promuovere
     * @throws PromptException se l'utente non esiste o se è un amministratore
     */
    public void makeBoxOfficeClerk(String username) {
        User user = uRepo.findById(username);
        if (user == null)
            throw new PromptException("(!) Utente inesistente.");
        if (user.getRole().equals(Role.ADMIN))
            throw new PromptException("(!) L'account admin non puo' subire cambi di ruolo.");
        user.setRole(Role.BOXOFFICECLERK);
        uRepo.update(user);
    }
}
