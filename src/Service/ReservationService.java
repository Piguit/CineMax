package Service;

import Repository.MovieRepository;
import Repository.ReservationRepository;
import Repository.ShowRepository;
import Repository.UserRepository;
import Model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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


    public List<FullReservationDetails> visualizeMyReservations(String username) {
        List<FullReservationDetails> result = new ArrayList<>();
        List<Reservation> reservations = rRepo.findAll();
        if (reservations.isEmpty())
            return result;
        reservations = reservations.stream().filter(r -> r.getUsername().equals(username)).toList();
        for (Reservation r : reservations) {
            User u = uRepo.findById(r.getUsername());
            Show s = sRepo.findById(r.getShowId());
            Movie m = mRepo.findById(s.getMovieId());
            result.add(new FullReservationDetails(r, u, s, m));
        }
        return result;
    }

    public Long addReservation(String username, Long showId, Byte ticketsNumber) {
        Show s = sRepo.findById(showId);
        if (s == null)
            throw new PromptException("(!) Proiezione inesistente.");
        if (s.getShowDate().isBefore(LocalDateTime.now()))
            throw new PromptException("(!) Proiezione gia' avvenuta.");
        int seatsTaken = countTicketsByShow(rRepo, showId);
        if (seatsTaken + ticketsNumber > 200)
            throw new PromptException("(!) Posti insufficienti. Disponibili: " + (200 - seatsTaken) + ".");
        Long id = rRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Reservation newR = new Reservation(id, username, showId, ticketsNumber);
        rRepo.insert(newR);
        return id;
    }

    public void editReservation(String username, Long reservationId, Long newShowId) {
        Reservation r = rRepo.findById(reservationId);
        Show newS = sRepo.findById(newShowId);
        if (r == null) {
            if (newS == null)
                throw new PromptException("(!) Prenotazione e proiezione inesistenti.");
            throw new PromptException("(!) Prenotazione inesistente.");
        }
        if (!r.getUsername().equals(username))
            throw new PromptException("(!) La prenotazione appartiene ad un altro utente.");
        Show oldS = sRepo.findById(r.getShowId());

        if (newS == null)
            throw new PromptException("(!) Proiezione inesistente.");
        if (oldS.getShowDate().isBefore(LocalDateTime.now()) ||
                (newS.getShowDate()).isBefore(LocalDateTime.now()))
            throw new PromptException("(!) Le proiezioni non devono essere gia' avvenute.");

        int seatsTaken = countTicketsByShow(rRepo, newShowId);
        if (seatsTaken + r.getTicketsNumber() > 200)
            throw new PromptException("(!) Posti insufficienti. Disponibili: " + (200 - seatsTaken) + ".");
        r.setShowId(newShowId);
        rRepo.update(r);
    }

    public void deleteReservation(String username, Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null)
            throw new PromptException("(!) Prenotazione inesistente.");
        if (!r.getUsername().equals(username))
            throw new PromptException("(!) La prenotazione appartiene ad un altro utente.");
        Show s = sRepo.findById(r.getShowId());
        if (s.getShowDate().isAfter(LocalDateTime.now())) {
            throw new PromptException("(!) Si può eliminare solamente la prenotazione di una proiezione gia' avvenuta.");
        }
        rRepo.delete(reservationId);
    }

    public FullReservationDetails visualizeReservation(Long reservationId) {
        Reservation r = rRepo.findById(reservationId);
        if (r == null)
            return null;
        User u = uRepo.findById(r.getUsername());
        Show s = sRepo.findById(r.getShowId());
        Movie m = mRepo.findById(s.getMovieId());
        return new FullReservationDetails(r, u, s, m);
    }

    public List<ReservationDetails> visualizeTodayReservations() {
        List<ReservationDetails> result = new ArrayList<>();
        List<Reservation> reservations = rRepo.findAll();
        for (Reservation r : reservations) {
            Show s = sRepo.findById(r.getShowId());
            if (LocalDateTime.now().getYear() == s.getShowDate().getYear() && LocalDateTime.now().getDayOfYear() == s.getShowDate().getDayOfYear()) {
                User u = uRepo.findById(r.getUsername());
                Movie m = mRepo.findById(s.getMovieId());
                result.add(new ReservationDetails(r, u, s, m));
            }
        }
        return result;
    }

    public List<ReservationDetails> searchReservations(Long reservationId, String name,
                                                String surname, String partialTitle,
                                                LocalDate from, LocalDate to) {
        List<ReservationDetails> result = new ArrayList<>();
        List<Reservation> reservations = rRepo.findAll();
        for (Reservation r : reservations) {
            User user = uRepo.findById(r.getUsername());
            Show show = sRepo.findById(r.getShowId());
            Movie movie = mRepo.findById(show.getMovieId());

            if (reservationId != null && !r.getId().equals(reservationId))
                continue;

            if ((name != null && !name.isBlank()) || (surname != null && !surname.isBlank())) {
                if ((name != null && !name.isBlank() && !name.equals(user.getName())) || (surname != null && !surname.isBlank() && !surname.equals(user.getSurname())))
                    continue;
            }

            if (from != null || to != null || (partialTitle != null && !partialTitle.isBlank())) {
                if (from != null || to != null) {
                    LocalDateTime date = show.getShowDate();
                    if ((from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) ||
                            (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())))
                        continue;
                }
                if (partialTitle != null && !partialTitle.isBlank()) {
                    if (!movie.getTitle().toLowerCase().contains(partialTitle.toLowerCase()))
                        continue;
                }
            }

            result.add(new ReservationDetails(r, user, show, movie));
        }
        return result;
    }

    /*public List<FullReservationDetails> searchReservationsInDetail(Long reservationId, String name,
                                                String surname, String partialTitle,
                                                LocalDate from, LocalDate to) {
        List<FullReservationDetails> result = new ArrayList<>();
        List<Reservation> reservations = rRepo.findAll();
        for (Reservation r : reservations) {
            User user = null;
            Show show = null;
            Movie movie = null;

            if (reservationId != null && !r.getId().equals(reservationId))
                continue;

            if ((name != null && !name.isBlank()) || (surname != null && !surname.isBlank())) {
                user = uRepo.findById(r.getUsername());
                if ((name != null && !name.isBlank() && !name.equals(user.getName())) || (surname != null && !surname.isBlank() && !surname.equals(user.getSurname())))
                    continue;
            }

            if (from != null || to != null || (partialTitle != null && !partialTitle.isBlank())) {
                show = sRepo.findById(r.getShowId());
                if (from != null || to != null) {
                    LocalDateTime date = show.getShowDate();
                    if ((from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) ||
                            (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())))
                        continue;
                }
                if (partialTitle != null && !partialTitle.isBlank()) {
                    movie = mRepo.findById(show.getMovieId());
                    if (!movie.getTitle().toLowerCase().contains(partialTitle.toLowerCase()))
                        continue;
                }
            }

            result.add(new FullReservationDetails(r, user, show, movie));
        }
        return result;
    }*/
}
