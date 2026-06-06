package Model;

import java.time.format.DateTimeFormatter;

public class ShowDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String showId;
    private String title;
    private String showDate;
    private String ticketCost;

    public ShowDetails(Show show, Movie movie) {
        this.showId = String.valueOf(show.getId());
        this.title = movie.getTitle();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.ticketCost = String.valueOf(show.getTicketCost());
    }

    public String toString() {
        return "RIEPILOGO PROIEZIONE:" + "\n\t- id: " + showId + "\n\t- titolo: " + title
                + "\n\t- data e orario: " + showDate + "\n\t- costo del biglietto: " + ticketCost;
    }
}
