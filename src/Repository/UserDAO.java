package Repository;

import java.io.IOException;
import java.util.List;

import Model.User;

public class UserDAO implements FileRepository<User, String> {
    public static final String FILE_NAME = "user_repository.txt";
    private GenericRepository<String, User> r;

    public UserDAO() {
        try {
            this.r = new GenericRepository<>(new User(), FILE_NAME);
        } catch (IOException e) {
            //Non sarà la gestione definitiva ovviamente
            System.err.println("Impossibile creare collegamento con la base di dati.");
            System.exit(0);
        }
    };

    public List<User> findAll() {
        return r.findAll();
    }

    public User findById(String id) {
        return r.findById(id);
    }

    public boolean insert(User movie) {
        return r.save(movie);
    }
    
    public boolean update(User movie) {
        return r.update(movie);
    }
    
    public boolean delete(String id) {
        return r.delete(id);
    }
}
