package Service;

import Model.Movie;
import Repository.MovieRepository;
import java.util.List;
import Utility.OutputPrinter;

public class MovieService {
    private final MovieRepository mRepo;
    private final OutputPrinter op;

    public MovieService(MovieRepository mRepo, OutputPrinter op) {
        this.mRepo = mRepo;
        this.op = op;
    }

    public int searchAndPrintMovies(String partialTitle, String director, Short year) {
        boolean titleExists = false;
        if (titleExists = (partialTitle != null && !partialTitle.isBlank()))
            partialTitle = partialTitle.toLowerCase();
        boolean directorExists = false;
        if (directorExists = (director != null && !director.isBlank()))
            director = director.toLowerCase();
        int printedItems = 0;
        mRepo.startSequentialReading();
        try {
            List<Movie> movies;
            while ((movies = mRepo.getNextItems()) != null) {
                for (Movie m : movies) {
                    if ((titleExists && !m.getTitle().toLowerCase().contains(partialTitle)) ||
                        (directorExists && !m.getDirector().toLowerCase().equals(director)) ||
                        (year != null && !m.getYear().equals(year)))
                        continue;
                    op.printlnMarked(m.toString());
                    printedItems++;
                }
            }
        } finally {
            mRepo.endSequentialReading();
        }
        return printedItems;
    }
    
    public Long addMovie(String title, String director, Short year, String genre, Short runningTime, Byte minAge) {
        mRepo.startSequentialReading();
        try {
            List<Movie> movies;
            while ((movies = mRepo.getNextItems()) != null)
                for (Movie m : movies)
                    if (m.getTitle().equalsIgnoreCase(title) && m.getDirector().equalsIgnoreCase(director) && m.getYear().equals(year) &&
                        m.getGenre().equalsIgnoreCase(genre) && m.getRunningTime().equals(runningTime) && m.getMinAge().equals(minAge))
                        return m.getId();
        } finally {
            mRepo.endSequentialReading();
        }

        Long id = mRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Movie movie = new Movie(id, title, director, year, genre, runningTime, minAge);
        mRepo.insert(movie);
        return id;
    }
}
