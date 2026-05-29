package Model;

import java.time.LocalDateTime;

public class ShowDetails {
    private String showId;
    private String title;
    private String genre;
    private String director;
    private String year;
    private String runningTime;
    private String showDate;
    private String ticketCost;
    private String freeSeats;

    public ShowDetails(Show show, Movie movie, int freeSeats) {
        this.showId = String.valueOf(show.getId());
        this.title = movie.getTitle();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.year = String.valueOf(movie.getYear());
        this.runningTime = String.valueOf(movie.getRunningTime());
        this.showDate = String.valueOf(show.getShowDate());
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.freeSeats = String.valueOf(freeSeats);
    }

    public String toString() {
        return "RIEPILOGO PROIEZIONE:" + "\nid: " + showId + "\ntitolo: " + title + "\ngenere: " + genre + "\nregista: "
                + director + "\nanno: " + year + "\ndurata: " + runningTime + "\ndata e orario: "
                + showDate + "\ncosto del biglietto: " + ticketCost + "\nposti disponibili: " + freeSeats;
    }
}
