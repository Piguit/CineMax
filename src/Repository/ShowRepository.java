package Repository;

import Model.Movie;
import Model.Show;
import Repository.GenericRepository;

import java.io.IOException;
import java.util.List;

public class ShowRepository {
        public static final String FILE_NAME = "show_repository.txt";
        private GenericRepository<Long, Show> r;

        public ShowRepository() {
            try {
                this.r = new GenericRepository<>(new Show(), FILE_NAME);
            } catch (IOException e) {
                //Non sarà la gestione definitiva ovviamente
                System.err.println("Impossibile creare collegamento con la base di dati.");
                System.exit(0);
            }
        };

        public List<Show> findAll() {
            return r.findAll();
        }

        public Show findById(long id) {
            return r.findById(id);
        }

        public boolean insert(Show show) {
            return r.save(show);
        }

        public boolean delete(long id) {
            return r.delete(id);
        }

        public boolean update(Show show) {
            return r.update(show);
        }
}
