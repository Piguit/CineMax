package Repository;

import Model.Reservation;
import Model.Show;
import Repository.GenericRepository;

import java.io.IOException;
import java.util.List;

public class ReservationRepository {
    public static final String FILE_NAME = "Reservation_repository.txt";
    private GenericRepository<Long, Reservation> r;

    public ReservationRepository() {
        try {
            this.r = new GenericRepository<>(new Reservation(), FILE_NAME);
        } catch (IOException e) {
            //Non sarà la gestione definitiva ovviamente
            System.err.println("Impossibile creare collegamento con la base di dati.");
            System.exit(0);
        }
    };

    public List<Reservation> findAll() {
        return r.findAll();
    }

    public Reservation findById(long id) {
        return r.findById(id);
    }

    public boolean insert(Reservation reservation) {
        return r.save(reservation);
    }

    public boolean delete(long id) {
        return r.delete(id);
    }

    public boolean update(Reservation reservation) {
        return r.update(reservation);
    }
}
