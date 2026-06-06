package Repository;

import java.util.List;

import Model.Show;

public class ShowRepository implements FileRepository<Show, Long> {
    public static final String FILE_NAME = "show_repository.txt";
    private GenericRepository<Long, Show> r;

    public ShowRepository() {
        this.r = new GenericRepository<>(new Show(), FILE_NAME);
    }

    public List<Show> findAll() {
        return r.findAll();
    }

    public Show findById(Long id) {
        return r.findById(id);
    }

    public boolean insert(Show show) {
        return r.save(show);
    }

    public boolean delete(Long id) {
        return r.delete(id);
    }

    public boolean update(Show show) {
        return r.update(show);
    }

    public Long getMaxId() {
        return r.getMaxId();
    }
}
