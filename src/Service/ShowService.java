package Service;

import Repository.MovieRepository;
import Repository.ReservationRepository;
import Repository.ShowRepository;
import Model.Movie;
import Model.Show;
import Model.ShowDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Utility.TicketsHandler.countTicketsByShow;

public class ShowService {
    private final ShowRepository sRepo;
    private final ReservationRepository rRepo;
    private final MovieRepository mRepo;

    public ShowService(ShowRepository sRepo,
                ReservationRepository rRepo, MovieRepository mRepo) {
        this.sRepo = sRepo;
        this.rRepo = rRepo;
        this.mRepo = mRepo;
    }

    public List<Show> searchShow(String partialTitle, String genre,
                                 LocalDate from, LocalDate to,
                                 Double minCost, Double maxCost) {
        List<Show> result = new ArrayList<>();
        for (Show s : sRepo.findAll()) {
            Movie mov = mRepo.findById(s.getMovieId());
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
            LocalDateTime date = s.getShowDate();
            if (from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) {
                continue;
            }
            if (from != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())) {
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

    public ShowDetails visualizeShow(Long showId) {
        Show s = sRepo.findById(showId);
        Movie m = mRepo.findById(s.getMovieId());
        int takenSeats = countTicketsByShow(rRepo, showId);
        int freeSeats = 200 - takenSeats;
        return new ShowDetails(s, m, freeSeats);
    }

    public void addShow(Movie movie, LocalDateTime showDate, Float ticketCost) {
        LocalDateTime endNew = showDate.plusMinutes(movie.getRunningTime());
        for (Show s : sRepo.findAll()) {
            LocalDateTime endS = s.getShowDate().plusMinutes(mRepo.findById(s.getMovieId()).getRunningTime());
            if (showDate.isBefore(endS) && endNew.isAfter(s.getShowDate())) {
                throw new IllegalStateException("Overlap with the show " + s.getId());
            }
        }
        Long id = sRepo.getMaxId() + 1;
        Show newS = new Show(id, movie.getId(), showDate, ticketCost);
        sRepo.insert(newS);
    }

    public boolean anyReservationForTheShow(Long showId){
        return !rRepo.findAll().stream().filter(p -> p.getShowId().equals(showId)).toList().isEmpty();
    }

    public void editShow(Long showId, LocalDateTime newShowDate, Float newTicketCost) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new IllegalArgumentException("Inexistent show");
        }
        if (anyReservationForTheShow(showId)) {
            throw new IllegalStateException("Not allowed to edit: reservations for this show exist.");
        }
        if (newShowDate != null) s.setShowDate(newShowDate);
        if (newTicketCost != null) s.setTicketCost(newTicketCost);
        sRepo.insert(s);
    }

    public void deleteShow(Long showId) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new IllegalArgumentException("Inexistent show.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new IllegalStateException("Not allowed to delete: reservations for this show exist.");
        }
        sRepo.delete(showId);
    }
}
