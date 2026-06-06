package Service;

import Repository.UserRepository;
import Model.Role;
import Model.User;
import Utility.PasswordHandler;
import Utility.SafetyException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

public class Authentication {
    private final UserRepository uRepo;

    public Authentication(UserRepository uRepo) {
        this.uRepo = uRepo;
    }

    public boolean isFirstAccess() {
        return uRepo.findAll().isEmpty();
    }

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

    public void makeProjectionist(String username) {
        User user = uRepo.findById(username);
        if (user == null)
            throw new PromptException("(!) Utente inesistente.");
        if (user.getRole().equals(Role.ADMIN))
            throw new PromptException("(!) L'account admin non puo' subire cambi di ruolo.");
        user.setRole(Role.PROJECTIONIST);
        uRepo.update(user);
    }

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
