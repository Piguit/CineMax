package Model;

import java.time.format.DateTimeFormatter;

public class FullReservationDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private String reservationId;
    private String name;
    private String surname;
    private String showDate;
    private String ticketsNumber;
    private String ticketCost;
    private String totalCost;
    private String title;

    public FullReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.ticketsNumber = String.valueOf(reservation.getTicketsNumber());
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.totalCost = String.valueOf(show.getTicketCost() * reservation.getTicketsNumber());
        this.title = movie.getTitle();
    }

    public String toString() {
        return "RIEPILOGO PRENOTAZIONE:" + "\n\t- id: " + reservationId + "\n\t- nome: " + name
                + "\n\t- cognome: " + surname + "\n\t- data e orario: " + showDate + "\n\t- numero di biglietti: " + ticketsNumber
                + "\n\t- costo del biglietto: " + ticketCost + "\n\t- costo totale: " + totalCost + "\n\t- film: " + title;
    }
}

