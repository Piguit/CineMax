package Repository;

import java.io.IOException;
import java.util.List;

import Model.Movie;
import Repository.GenericRepository;

public class MovieRepository {
    public static final String FILE_NAME = "movie_repository.txt";
    private GenericRepository<Long, Movie> r;

    public MovieRepository() {
        try {
            this.r = new GenericRepository<>(new Movie(), FILE_NAME);
        } catch (IOException e) {
            //Non sarà la gestione definitiva ovviamente
            System.err.println("Impossibile creare collegamento con la base di dati.");
            System.exit(0);
        }
    };

    public List<Movie> findAll() {
        return r.findAll();
    }

    public Movie findById(long id) {
        return r.findById(id);
    }

    public boolean insert(Movie movie) {
        return r.save(movie);
    }

    public boolean delete(long id) {
        return r.delete(id);
    }

    public boolean update(Movie movie) {
        return r.update(movie);
    }
}
