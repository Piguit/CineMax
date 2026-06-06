package Service;

import Repository.MovieRepository;
import Repository.ReservationRepository;
import Repository.ShowRepository;
import Model.Movie;
import Model.Reservation;
import Model.Show;
import Model.FullShowDetails;
import Model.ShowDetails;
import Utility.TicketsHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public List<ShowDetails> searchShows(String partialTitle, String genre,
                                 LocalDate from, LocalDate to,
                                 Float minCost, Float maxCost) {
        List<ShowDetails> result = new ArrayList<>();
        List<Show> shows = sRepo.findAll();
        for (Show s : shows) {
            Movie movie = mRepo.findById(s.getMovieId());
            if (partialTitle != null && !partialTitle.isBlank()) {
                if (!movie.getTitle().toLowerCase().contains(partialTitle.toLowerCase())) {
                    continue;
                }
            }
            if (genre != null && !genre.isBlank()) {
                if (!movie.getGenre().equalsIgnoreCase(genre)) {
                    continue;
                }
            }
            LocalDateTime date = s.getShowDate();
            if (from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) {
                continue;
            }
            if (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())) {
                continue;
            }
            if (minCost != null && s.getTicketCost() < minCost) {
                continue;
            }
            if (maxCost != null && s.getTicketCost() > maxCost) {
                continue;
            }
            result.add(new ShowDetails(s, movie));
        }
        return result;
    }

    /*public List<FullShowDetails> searchShowsInDetail(String partialTitle, String genre,
                                        LocalDate from, LocalDate to,
                                        Float minCost, Float maxCost) {
        List<FullShowDetails> result = new ArrayList<>();
        List<Show> shows = sRepo.findAll();
        for (Show s : shows) {
            Movie movie = mRepo.findById(s.getMovieId());
            if (partialTitle != null && !partialTitle.isBlank()) {
                if (!movie.getTitle().toLowerCase().contains(partialTitle.toLowerCase())) {
                    continue;
                }
            }
            if (genre!= null && !genre.isBlank()) {
                if (!movie.getGenre().equalsIgnoreCase(genre)) {
                    continue;
                }
            }
            LocalDateTime date = s.getShowDate();
            if (from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) {
                continue;
            }
            if (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())) {
                continue;
            }
            if (minCost != null && s.getTicketCost() < minCost) {
                continue;
            }
            if (maxCost != null && s.getTicketCost() > maxCost) {
                continue;
            }
            result.add(new FullShowDetails(s, movie, 200 - TicketsHandler.countTicketsByShow(rRepo, s.getId())));
        }
        return result;
    }*/

    public FullShowDetails visualizeShow(Long showId) {
        Show s = sRepo.findById(showId);
        if (s == null)
            return null;
        Movie m = mRepo.findById(s.getMovieId());
        int takenSeats = TicketsHandler.countTicketsByShow(rRepo, showId);
        int freeSeats = 200 - takenSeats;
        return new FullShowDetails(s, m, freeSeats);
    }

    public Long addShow(Long movieId, LocalDateTime showDate, Float ticketCost) {
        Movie m = mRepo.findById(movieId);
        if (m == null)
            throw new PromptException("(!) Film inesistente.");
        LocalDateTime endNew = showDate.plusMinutes(m.getRunningTime());
        List<Show> shows = sRepo.findAll();
        for (Show s : shows) {
            LocalDateTime endS = s.getShowDate().plusMinutes(mRepo.findById(s.getMovieId()).getRunningTime());
            if ((showDate.isAfter(s.getShowDate()) && showDate.isBefore(endS)) ||
                (endNew.isAfter(s.getShowDate()) && endNew.isBefore(endS)) ||
                (showDate.isBefore(s.getShowDate()) && endNew.isAfter(endS)))
                throw new PromptException("(!) La proiezione si sovrappone con la proiezione " + s.getId() + ".");
        }
        Long id = sRepo.getMaxId();
        id = (id != null) ? id + 1 : 0;
        Show newS = new Show(id, movieId, showDate, ticketCost);
        sRepo.insert(newS);
        return id;
    }

    private boolean anyReservationForTheShow(Long showId) {
        List<Reservation> reservations = rRepo.findAll();
        if (reservations.isEmpty())
            return false;
        return !reservations.stream().filter(p -> p.getShowId().equals(showId)).toList().isEmpty();
    }

    public void editShow(Long showId, LocalDateTime newShowDate, Float newTicketCost) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new PromptException("(!) Proiezione inesistente.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new PromptException("(!) Modifica non permessa: esistono gia' prenotazioni per la proiezione corrente.");
        }
        if (newShowDate != null)
            s.setShowDate(newShowDate);
        if (newTicketCost != null)
            s.setTicketCost(newTicketCost);
        sRepo.update(s);
    }

    public void deleteShow(Long showId) {
        Show s = sRepo.findById(showId);
        if (s == null) {
            throw new PromptException("(!) Proiezione inesistente.");
        }
        if (anyReservationForTheShow(showId)) {
            throw new PromptException("(!) Eliminazione non permessa: esistono gia' prenotazioni per la proiezione corrente.");
        }
        sRepo.delete(showId);
    }
}
