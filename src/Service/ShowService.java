package Service;

import DataAccessObject.MovieDAO;
import DataAccessObject.ReservationDAO;
import DataAccessObject.ShowDAO;
import Model.Movie;
import Model.Show;
import Model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowService {
    private final ShowDAO sDao;
    private final ReservationDAO rDao;
    private final MovieDAO mDao;

    public ShowService(ShowDAO sDao,
                ReservationDAO rDao, MovieDAO mDao) {
        this.sDao = sDao;
        this.rDao = rDao;
        this.mDao = mDao;
    }

    public List<Show> searchShow(String partialTitle, String genre,
                                 LocalDate from, LocalDate to,
                                 Double minCost, Double maxCost) {
        List<Show> result = new ArrayList<>();
        MovieDAO mDao = new MovieDAO();
        for (Show s : sDao.findAll()) {
            Movie mov = mDao.findById(s.getMovieId());
            if (partialTitle != null && !partialTitle.isBlank()) {
                if (!mov.getTitle().toLowerCase().contains(partialTitle.toLowerCase())) {
                    continue;
                }
            }
            if (genre!= null && !genre.isBlank()) {
                if (!mov.getGenre().equalsIgnoreCase(genre)) {
                    continue;
                }
            }
            if (from != null && s.getShowDate().isBefore(from)) {
                continue;
            }
            if (to != null && s.getShowDate().isAfter(to)) {
                continue;
            }
            if (minCost != null && s.getTicketCost() < minCost) {
                continue;
            }
            if (maxCost != null && s.getTicketCost() > maxCost) {
                continue;
            }
            result.add(s);
        }
        return result;
    }

    public String[] visualizeShow(Long showId) {
        Show s = sDao.findById(showId);
        int seatsTaken = sDao.countTicketByShow(idShow);
        int seatsFree = 200 - seatsTaken;
        return new showDetails(s, seatsFree);
    }

    public void addShow(Movie movie, LocalDateTime showDate, float ticketCost) {
        LocalDateTime endNew = showDate.plusMinutes(movie.getRunningTime());
        for (Show s : sDao.findAll()) {
            LocalDateTime endS = s.getShowDate().plusMinutes(mDao.findById(s.getMovieId()).getRunningTime());
            if (showDate.isBefore(endS) && endNew.isAfter(s.getShowDate())) {
                throw new IllegalStateException("Overlap with the show " + s.getId());
            }
        }
        Long id = sDao.generateNextId();
        Show newS = new Show(id, movie.getId(), showDate, ticketCost);
        sDao.insert(newS);
    }

    public boolean anyReservationForTheShow(Long showId){
        return !rDao.findAll().stream().filter(p -> p.getShowId() == showId).toList().isEmpty();
    }

    public void editShow(Long showId, LocalDateTime newShowDate, float newTicketCost) {
        Show s = sDao.findById(showId);
        if (s == null) {
            throw new IllegalArgumentException("Inexistent show");
        }
        if (anyReservationForTheShow(showId)) {
            throw new IllegalStateException("Not allowed to edit: reservations for this show exist.");
        }
        if (newShowDate != null) s.setShowDate(newShowDate);
        if (newTicketCost != null) s.setTicketCost(newTicketCost);
        sDao.insert(s);
    }

    public void deleteShow(long showId) {
        Show s = sDao.findById(showId);
        if (s == null) {
            throw new IllegalArgumentException("Inexistent show.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new IllegalStateException("Not allowed to delete: reservations for this show exist.");
        }
        sDao.delete(showId);
    }
}
