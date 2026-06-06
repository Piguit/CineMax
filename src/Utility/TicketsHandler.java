package Utility;

import java.util.List;

import Model.Reservation;
import Repository.ReservationRepository;

public class TicketsHandler {
    public static int countTicketsByShow(ReservationRepository rRepo, Long showId) {
        List<Reservation> reservations = rRepo.findAll();
        if (reservations.isEmpty())
            return 0;
        return reservations.stream().filter(r -> r.getShowId().equals(showId))
                .mapToInt(Reservation::getTicketsNumber).sum();
    }
}
