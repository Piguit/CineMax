package Repository;

import java.util.List;

import Model.User;

public class UserRepository implements FileRepository<User, String> {
    public static final String FILE_NAME = "user_repository.txt";
    private GenericRepository<String, User> r;

    public UserRepository() {
        this.r = new GenericRepository<>(new User(), FILE_NAME);
    }

    public List<User> findAll() {
        return r.findAll();
    }

    public User findById(String id) {
        return r.findById(id);
    }

    public boolean insert(User movie) {
        return r.save(movie);
    }

    public boolean delete(String id) {
        return r.delete(id);
    }

    public boolean update(User movie) {
        return r.update(movie);
    }

    public String getMaxId() {
        return r.getMaxId();
    }
}
