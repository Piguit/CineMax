package repository;

import model.Reservation;

import java.util.List;

public class ReservationRepository implements FileRepository<Reservation, Long> {
    public static final String FILE_NAME = "reservation_repository.txt";
    private GenericRepository<Long, Reservation> r;

    public ReservationRepository() {
        this.r = new GenericRepository<>(new Reservation(), FILE_NAME);
    }

    public Reservation findById(Long id) {
        return r.findById(id);
    }

    public boolean insert(Reservation reservation) {
        return r.save(reservation);
    }

    public boolean delete(Long id) {
        return r.delete(id);
    }

    public boolean update(Reservation reservation) {
        return r.update(reservation);
    }
    
    public Long getMaxId() {
        return r.getMaxId();
    }



    public List<Reservation> getNextItems() {
        return r.getNextItems();
    }

    public void startSequentialReading() {
        r.startSequentialReading();
    }

    public void endSequentialReading() {
        r.endSequentialReading();
    }
}
