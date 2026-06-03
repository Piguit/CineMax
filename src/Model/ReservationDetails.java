package Model;

import java.time.LocalDateTime;

public class ReservationDetails {
        private String reservationId;
        private String name;
        private String surname;
        private String showDate;
        private String ticketsNumber;
        private String ticketCost;
        private String totalCost;
        private String title;

    public ReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = String.valueOf(show.getShowDate());
        this.ticketsNumber = String.valueOf(reservation.getTicketsNumber());
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.totalCost = String.valueOf(show.getTicketCost() * reservation.getTicketsNumber());
        this.title = movie.getTitle();
    }

    public String toString() {
        return "RIEPILOGO PRENOTAZIONE:" + "\n- id: " + reservationId + "\n- nome: " + name
                + "\n- cognome: " + surname + "\n- data e orario: " + showDate + "\n- numero di biglietti: " + ticketsNumber
                + "\n- costo del biglietto: " + ticketCost + "\n- costo totale: " + totalCost + "\n- film: " + title;
    }
}

