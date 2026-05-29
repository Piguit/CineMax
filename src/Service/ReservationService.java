package Service;

import Repository.MovieRepository;
import Repository.ReservationRepository;
import Repository.ShowRepository;
import Repository.UserRepository;
import Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Utility.TicketsHandler.countTicketsByShow;

public class ReservationService {
    private final ReservationRepository rRepo;
    private final ShowRepository sRepo;
    private final UserRepository uRepo;
    private final MovieRepository mRepo;

    public ReservationService(ReservationRepository rRepo, ShowRepository sRepo,
                              UserRepository uRepo, MovieRepository mRepo) {
        this.rRepo = rRepo;
        this.sRepo = sRepo;
        this.uRepo = uRepo;
        this.mRepo = mRepo;
    }


    public List<Reservation> userReservations(String username) {
        return rRepo.findAll().stream().filter(r -> r.getUsername().equals(username)).toList();

    }

    public Long addReservation(String username, Long showId, Byte ticketsNumber) {
        Show s = sRepo.findById(showId);
        if (s == null) throw new IllegalArgumentException("Inexistent show");
        int seatsTaken = countTicketsByShow(rRepo, showId);
        if (seatsTaken + ticketsNumber > 200) {
            throw new IllegalStateException("Insufficient seats. Available: " + (200 - seatsTaken));
        }
        Long reservationId = rRepo.getMaxId() + 1;
        Reservation newR = new Reservation(reservationId, username, s.getMovieId(), ticketsNumber);
        rRepo.insert(newR);
        return reservationId;
    }

    public void editReservation(Long reservationId, Long newShowId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null) throw new IllegalArgumentException("Inexistent reservation");
        Show oldS = sRepo.findById(r.getShowId());
        Show newS = sRepo.findById(newShowId);

        if (oldS == null || newS == null) throw new IllegalArgumentException("Inexistent show");
        if (oldS.getShowDate().isBefore(LocalDateTime.now()) ||
                (newS.getShowDate()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Both shows must not have been projected");
        }

        int seatsTaken = countTicketsByShow(rRepo, newShowId);
        if (seatsTaken + r.getTicketsNumber() > 200) {
            throw new IllegalStateException("There is not a sufficient number of available seats in the new show");
        }
        r.setShowId(newShowId);
        rRepo.insert(r);
    }

    public void deleteReservation(Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null) throw new IllegalArgumentException("Non existent reservation");
        Show s = sRepo.findById(r.getShowId());
        if (s.getShowDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("You can delete the reservation only after the show");
        }
        rRepo.delete(reservationId);
    }

    public ReservationDetails visualizeReservation(Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null) return null;
        User c = uRepo.findById(r.getUsername());
        Show s = sRepo.findById(r.getShowId());
        Movie m = mRepo.findById(s.getMovieId());
        return new ReservationDetails(r, c, s, m);
    }

    public List<Reservation> searchReservations(Long reservationId, String name,
                                                String surname, String partialTitle,
                                                LocalDate from, LocalDate to) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r: rRepo.findAll()) {
            boolean ok = true;

            if (reservationId != null && !r.getId().equals(reservationId))
                continue;

            if (name != null && !name.isBlank()) {
                List<User> userList = uRepo.findAll();
                String username = r.getUsername();
                for (User u : userList)
                    if (u.getId().equals(username)) {
                        if (!name.equals(u.getName()) || !surname.equals(u.getSurname()))
                            ok = false;
                        break;
                    }

                if (!ok) continue;
            }

            if (from != null || to != null || (partialTitle != null && !partialTitle.isBlank())) {
                List<Show> showList = sRepo.findAll();
                Long showId = r.getShowId();
                if (from != null || to != null) {
                    for (Show s : showList)
                        if (s.getId().equals(showId)) {
                            LocalDateTime date = s.getShowDate();
                            if ((from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) ||
                                    (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())))
                                ok = false;
                            break;
                        }

                    if (!ok) continue;
                }
                if (partialTitle != null && !partialTitle.isBlank()) {
                    boolean stop = false;

                    for (Show s : showList) {
                        if (s.getId().equals(showId)) {
                            Long foundShowId = s.getId();
                            List<Movie> movieList = mRepo.findAll();
                            for (Movie m : movieList)
                                if (m.getId().equals(foundShowId)) {
                                    if (!partialTitle.equals(m.getTitle())) //!!! Utilizzare Levenstein
                                        ok = false;
                                    else
                                        stop = true;
                                    break;
                                }
                            if (!ok || stop)
                                break;
                        }
                    }

                    if (!ok) continue;
                }
            }

            result.add(r);
        }
        return result;
    }
}
