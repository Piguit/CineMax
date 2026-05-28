package Service;

import DataAccessObject.MovieDAO;
import DataAccessObject.ReservationDAO;
import DataAccessObject.ShowDAO;
import DataAccessObject.UserDAO;
import Model.Movie;
import Model.Reservation;
import Model.Show;
import Model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {
    private final ReservationDAO rDao;
    private final ShowDAO sDao;
    private final UserDAO uDao;
    private final MovieDAO mDao;

    public ReservationService(ReservationDAO rDao, ShowDAO sDao, UserDAO uDao, MovieDAO mDao) {
        this.rDao = rDao;
        this.sDao = sDao;
        this.uDao = uDao;
        this.mDao = mDao;
    }


    public List<Reservation> userReservations(String username) {
        return rDao.findAll().stream().filter(r -> r.getUsername().equals(username)).toList();

    }

    public String addReservation(String username, Long showId, byte ticketsNumber) {
        Show s = sDao.findById(showId);
        if (s == null) throw new IllegalArgumentException("Inexistent show");
        int seatsTaken = sDao.countTicketsByShow(showId);
        if (seatsTaken + ticketsNumber > 200) {
            throw new IllegalStateException("Insufficient seats. Available: " + (200 - seatsTaken));
        }
        String reservationId = rDao.generateNextId();
        Reservation newR = new Reservation(reservationId, username, String.valueOf(s.getMovieId()),
                String.valueOf(ticketsNumber), String.valueOf(s.getShowDate()));
        rDao.insert(newR);
        return reservationId;
    }

    public void editReservation(Long reservationId, Long newShowId) {
        Reservation r = rDao.findById(reservationId);
        if (r == null) throw new IllegalArgumentException("Inexistent reservation");
        Reservation oldR = sDao.findById(r.getShowId());
        Reservation newR = sDao.findById(newShowId);

        if (oldR == null || newR == null) throw new IllegalArgumentException("Inexistent show");
        if ((sDao.findById(oldR.getShowId()).getShowDate()).isBefore(LocalDateTime.now()) ||
                ((sDao.findById(newR.getShowId()).getShowDate()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Both shows must not have been projected");
        }

        int seatsTaken = sDao.countTicketsByShow(newShowId);
        if (seatsTaken + r.getTicketsNumber() > 200) {
            throw new IllegalStateException("There is not a sufficient number of available seats in the new show");
        }
        r.setShowId(newShowId);
        rDao.insert(r);
    }

    public void deleteReservation(Long reservationId) {
        Reservation r = rDao.findById(reservationId);
        if (r == null) throw new IllegalArgumentException("Non existent reservation");
        Show s = sDao.findById(r.getShowId());
        if (s.getShowDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("You can delete the reservation only after the show");
        }
        rDao.delete(reservationId);
    }

    public ReservationDetails visualizeReservation(Long reservationId) {
        Reservation r = rDao.findById(reservationId);
        if (r == null) return null;
        User c = uDao.findById(r.getUsername());
        Show s = sDao.findById(r.getShowId());
        Movie m = mDao.findById(s.getMovieId());
        return new ReservationDetails(r, c, s, m);
    }

    public List<Reservation> searchReservations() {

    }
}
