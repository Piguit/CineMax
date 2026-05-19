import Model.Movie;
import Repository.MovieRepository;

import java.io.IOException;
import java.util.List;

public class TestWrite {
    private static String[] names = {"carlo", "anzani", "fibra", "steroidi", "accattone", "pino", "pasticcino"};
    private static String[] numbers = {"1", "2", "3", "4", "5", "6", "7"};

    public static void main(String[] args) throws IOException {
        MovieRepository mr = new MovieRepository("movie_archive.txt");
        /*for (int i = 0; i < 100; i++) {
            Movie movie = new Movie(String.valueOf(i), names[(int) (Math.random() * 10 % 7)],
                    names[(int) (Math.random() * 10 % 7)], numbers[(int) (Math.random() * 10 % 7)],
                    names[(int) (Math.random() * 10 % 7)], numbers[(int) (Math.random() * 10 % 7)],
                    numbers[(int) (Math.random() * 10 % 7)]);
            mr.save(movie);
        }*/
        mr.delete(95L);
        List<Movie> prova = mr.findAll();
        for (Movie m: prova) {
            System.out.println(m);
        }
    }
}
