package Service;

import Repository.UserRepository;
import Model.Role;
import Model.User;
import Utility.PasswordHandler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

public class Authentication {
    private final UserRepository uRepo;

    public Authentication(UserRepository uRepo) {
        this.uRepo = uRepo;
    }

    public User signIn(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User u = uRepo.findById(username);
        if (u == null) {
            return null;
        }
        if (PasswordHandler.verifyPassword(password, u.getPassword())) {
            return u;
        }
        return null;
    }

    public boolean signUp(String username, String name, String surname,
                          String password, LocalDate birthDate,
                          String residence) throws NoSuchAlgorithmException, InvalidKeySpecException  {
        if (uRepo.findById(username) != null) {
            return false;
        }
        password = PasswordHandler.hashPassword(password);
        User newUser = new User(username, name, surname, password, birthDate, residence, Role.CLIENT);
        return uRepo.insert(newUser);
    }
}
