package Model;

import java.time.LocalDateTime;

public class ReservationDetails {
        private String reservationId;
        private String name;
        private String surname;
        private LocalDateTime showDate;
        private byte ticketsNumber;
        private float ticketCost;
        private float totalCost;
        private String title;
    }

    public ReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = String.valueOf(show.getShowDate());
        this.ticketsNumber = String.valueOf(reservation.getTicketsNumber());
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.totalCost = String.valueOf(ticketCost * ticketsNumber);
        this.title = movie.getTitle();
    }

    public String toString() {
        String compendium = "RIEPILOGO PRENOTAZIONE: " + reservationId + "|" +
                name + "|" + surname + "|" + showDate + "|" + ticketsNumber + "|"
                + ticketCost + "|" + totalCost + "|" + title + "|";
    }
}