package Model;

import java.time.format.DateTimeFormatter;

public class ReservationDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private String reservationId;
    private String name;
    private String surname;
    private String showDate;
    private String title;

    public ReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.title = movie.getTitle();
    }

    public String toString() {
        return "RIEPILOGO PRENOTAZIONE:" + "\n\t- id: " + reservationId + "\n\t- nome: " + name
                + "\n\t- cognome: " + surname + "\n\t- data e orario: " + showDate + "\n\t- titolo: " + title;
    }
}
