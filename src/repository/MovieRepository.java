package repository;

import java.util.List;

import model.Movie;

public class MovieRepository implements FileRepository<Movie, Long> {
    public static final String FILE_NAME = "movie_repository.txt";
    private GenericRepository<Long, Movie> r;

    public MovieRepository() {
        this.r = new GenericRepository<>(new Movie(), FILE_NAME);
    }

    public Movie findById(Long id) {
        return r.findById(id);
    }

    public boolean insert(Movie movie) {
        return r.save(movie);
    }

    public boolean delete(Long id) {
        return r.delete(id);
    }

    public boolean update(Movie movie) {
        return r.update(movie);
    }

    public Long getMaxId() {
        return r.getMaxId();
    }



    public List<Movie> getNextItems() {
        return r.getNextItems();
    }

    public void startSequentialReading() {
        r.startSequentialReading();
    }

    public void endSequentialReading() {
        r.endSequentialReading();
    }
}
