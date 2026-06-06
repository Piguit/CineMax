package Service;

import Model.Movie;
import Repository.MovieRepository;
import java.util.List;

public class MovieService {
    private final MovieRepository mRepo;

    public MovieService(MovieRepository mRepo) {
        this.mRepo = mRepo;
    }

    public List<Movie> visualizeMovies() {
        return mRepo.findAll();
    }
    
    public Long addMovie(String title, String director, Short year, String genre, Short runningTime, Byte minAge) {
        Long id = mRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        for (Movie m : mRepo.findAll())
            if (m.getTitle().equalsIgnoreCase(title) && m.getDirector().equalsIgnoreCase(director) && m.getYear().equals(year) &&
                m.getGenre().equalsIgnoreCase(genre) && m.getRunningTime().equals(runningTime) && m.getMinAge().equals(minAge))
                return m.getId();
        Movie movie = new Movie(id, title, director, year, genre, runningTime, minAge);
        mRepo.insert(movie);
        return id;
    }
}
