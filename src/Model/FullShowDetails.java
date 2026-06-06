package Model;

import java.time.format.DateTimeFormatter;

public class FullShowDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private String showId;
    private String title;
    private String genre;
    private String director;
    private String year;
    private String runningTime;
    private String minAge;
    private String showDate;
    private String ticketCost;
    private String freeSeats;

    public FullShowDetails(Show show, Movie movie, int freeSeats) {
        this.showId = String.valueOf(show.getId());
        this.title = movie.getTitle();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.year = String.valueOf(movie.getYear());
        this.runningTime = String.valueOf(movie.getRunningTime());
        this.minAge = String.valueOf(movie.getMinAge());
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.freeSeats = String.valueOf(freeSeats);
    }

    public String toString() {
        return "RIEPILOGO PROIEZIONE:" + "\n\t- id: " + showId + "\n\t- titolo: " + title + "\n\t- genere: " + genre + "\n\t- regista: "
                + director + "\n\t- anno: " + year + "\n\t- durata: " + runningTime + "\n\t- eta' minima: " + minAge + "\n\t- data e orario: "
                + showDate + "\n\t- costo del biglietto: " + ticketCost + "\n\t- posti disponibili: " + freeSeats;
    }
}
