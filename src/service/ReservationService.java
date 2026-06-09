package service;

import repository.MovieRepository;
import repository.ReservationRepository;
import repository.ShowRepository;
import repository.UserRepository;
import utility.OutputPrinter;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ReservationService {
    private final ReservationRepository rRepo;
    private final ShowRepository sRepo;
    private final UserRepository uRepo;
    private final MovieRepository mRepo;
    private final OutputPrinter op;

    public ReservationService(ReservationRepository rRepo, ShowRepository sRepo,
                              UserRepository uRepo, MovieRepository mRepo, OutputPrinter op) {
        this.rRepo = rRepo;
        this.sRepo = sRepo;
        this.uRepo = uRepo;
        this.mRepo = mRepo;
        this.op = op;
    }

    public static int countTicketsByShow(ReservationRepository rRepo, Long showId) {
        int ticketsNumber = 0;
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                ticketsNumber += reservations.stream().filter(r -> r.getShowId().equals(showId)).mapToInt(Reservation::getTicketsNumber).sum();
        } finally {
            rRepo.endSequentialReading();
        }
        return ticketsNumber;
    }


    public int printMyReservations(String username) {
        rRepo.startSequentialReading();
        int printedItems = 0;
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null) {
                reservations = reservations.stream().filter(r -> r.getUsername().equals(username)).toList();
                printedItems += reservations.size();
                for (Reservation r : reservations) {
                    User u = uRepo.findById(r.getUsername());
                    Show s = sRepo.findById(r.getShowId());
                    Movie m = mRepo.findById(s.getMovieId());
                    op.printlnMarked(new FullReservationDetails(r, u, s, m).toString());
                }
            }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }

    public Long addReservation(String username, Long showId, Short ticketsNumber) {
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

    public void editReservation(String username, Long reservationId, Long newShowId, Short ticketsNumber) {
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

        Short tickets = (ticketsNumber != null) ? ticketsNumber : r.getTicketsNumber();
        int seatsTaken = countTicketsByShow(rRepo, newShowId);
        if (seatsTaken + tickets > 200)
            throw new PromptException("(!) Posti insufficienti. Disponibili: " + (200 - seatsTaken) + ".");
        r.setShowId(newShowId);
        r.setTicketsNumber(tickets);
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

    public int printTodayReservations() {
        rRepo.startSequentialReading();
        int printedItems = 0;
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                for (Reservation r : reservations) {
                    Show s = sRepo.findById(r.getShowId());
                    if (LocalDateTime.now().getYear() == s.getShowDate().getYear() && LocalDateTime.now().getDayOfYear() == s.getShowDate().getDayOfYear()) {
                        User u = uRepo.findById(r.getUsername());
                        Movie m = mRepo.findById(s.getMovieId());
                        op.printlnMarked(new ReservationDetails(r, u, s, m).toString());
                        printedItems++;
                    }
                }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }

    public int searchAndPrintReservations(Long reservationId, String name,
                                                String surname, String partialTitle,
                                                LocalDate from, LocalDate to) {
        boolean nameExists = false;
        if (nameExists = (name != null && !name.isBlank()))
            name = name.toLowerCase();
        boolean surnameExists = false;
        if (surnameExists = (surname != null && !surname.isBlank()))
            surname = surname.toLowerCase();
        boolean titleExists = false;
        if (titleExists = (partialTitle != null && !partialTitle.isBlank()))
            partialTitle = partialTitle.toLowerCase();
        int printedItems = 0;
        rRepo.startSequentialReading();
        try {
            List<Reservation> reservations;
            while ((reservations = rRepo.getNextItems()) != null)
                for (Reservation r : reservations) {
                    User user = uRepo.findById(r.getUsername());
                    Show show = sRepo.findById(r.getShowId());
                    Movie movie = mRepo.findById(show.getMovieId());

                    if (reservationId != null && !r.getId().equals(reservationId))
                        continue;

                    if (nameExists || surnameExists) {
                        if ((nameExists && !name.equals(user.getName().toLowerCase())) || (surnameExists && !surname.equals(user.getSurname().toLowerCase())))
                            continue;
                    }

                    if (from != null || to != null || titleExists) {
                        if (from != null || to != null) {
                            LocalDateTime date = show.getShowDate();
                            if ((from != null && (date.getYear() < from.getYear() || date.getDayOfYear() < from.getDayOfYear())) ||
                                    (to != null && (date.getYear() > to.getYear() || date.getDayOfYear() > to.getDayOfYear())))
                                continue;
                        }
                        if (titleExists && !movie.getTitle().toLowerCase().contains(partialTitle))
                                continue;
                    }

                    op.printlnMarked(new ReservationDetails(r, user, show, movie).toString());
                    printedItems++;
                }
        } finally {
            rRepo.endSequentialReading();
        }
        return printedItems;
    }
}
