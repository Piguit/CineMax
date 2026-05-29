package Utility;

import Model.Reservation;
import Repository.ReservationRepository;

import java.util.List;

public class TicketsHandler {
    public static int countTicketsByShow(ReservationRepository rRepo, Long showId) {
        return rRepo.findAll().stream().filter(r -> r.getShowId().equals(showId))
                .mapToInt(Reservation::getTicketsNumber).sum();
    }
}
