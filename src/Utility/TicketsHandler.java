package Utility;

import Model.Reservation;
import Repository.ReservationRepository;

import java.util.List;

public class TicketsHandler {
    public static int countTicketsByShow(ReservationRepository rRepo, Long showId) {
        List<Reservation> list = rRepo.findAll();
        list = list.stream().filter(r -> r.getShowId().equals(showId)).toList();
        int ticketsNum = 0;
        for (Reservation r: list)
            ticketsNum += r.getTicketsNumber();
        return ticketsNum;
    }
}
