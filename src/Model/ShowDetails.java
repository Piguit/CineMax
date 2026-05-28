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

    public ShowDetails(Movie movie, Show show) {
        this.showId = String.valueOf(show.getId());
        this.title = movie.getTitle();
        this.genre = movie.getGenre();
        this.director = movie.getDirector();
        this.year = String.valueOf(movie.getYear());
        this.runningTime = String.valueOf(movie.getRunningTime());
        this.showDate = String.valueOf(show.getShowDate());
        this.ticketCost = String.valueOf(show.getTicketCost());
    }

    public String toString() {
        return "RIEPILOGO PROIEZIONE: " + showId + "|" + title + "|"
                + genre + "|" + director + "|" + year + "|" + runningTime
                + "|" + showDate + "|" + ticketCost + "|";
    }
}
