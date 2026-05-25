package Service;

import DataAccessObject.UserDAO;
import Model.Role;
import Model.User;
import Utility.PasswordHandler;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Authentication {
    private final UserDAO uDao;

    public Authentication(UserDAO uDao) {
        this.uDao = uDao;
    }

    public User login (String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User u = uDao.findById(username);
        if (u == null) {
            return null;
        }
        if (PasswordHandler.verifyPassword(password, u.getPassword())) {
            return u;
        }
        return null;
    }

    public boolean signUp(String username, String name, String surname,
                          String password, String birthDate,
                          String residence) throws NoSuchAlgorithmException, InvalidKeySpecException  {
        if (uDao.findById(username) != null) {
            return false;
        }
        password = PasswordHandler.hashPassword(password);
        User newUser = new User(username, name, surname, password, birthDate, residence, Role.CLIENT.name());
        return uDao.insert(newUser);
    }
}
