package Model;

import java.time.LocalDateTime;

public class Show implements ItemInitializer<Show>, Identifiable<Long> {

    private Long showId;
    private Long movieId;
    private LocalDateTime showDate;
    private Float ticketCost;

    public Show() { }

    public Show(Long showId, Long movieId, LocalDateTime showDate, Float ticketCost) {
        this.showId = showId;

        this.movieId = movieId;
        this.showDate = showDate;
        this.ticketCost = ticketCost;
    }

    public Show(String[] array) {
        this.showId = Long.parseLong(array[0]);
        this.movieId = Long.parseLong(array[1]);
        this.showDate = LocalDateTime.parse(array[2]);
        this.ticketCost = Float.parseFloat(array[3]);
    }

    @Override
    public Show getNewItem(String[] array) { return new Show(array); }

    @Override
    public Long getId() { return showId; }

    @Override
    public String[] getFields() {
        return new String[]{String.valueOf(showId), String.valueOf(movieId),
                String.valueOf(showDate), String.valueOf(ticketCost)};
    }

    public Long getMovieId() { return movieId; }

    public LocalDateTime getShowDate() { return showDate; }

    public float getTicketCost() { return ticketCost; }


    public void setShowId(Long showId) { this.showId = showId; }

    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public void setShowDate(LocalDateTime showDate) { this.showDate = showDate; }

    public void setTicketCost(Float ticketCost) { this.ticketCost = ticketCost; }

    public String toString() {
        String result = "";
        result += showId + " | ";
        result += movieId + " | ";
        result += showDate + " | ";
        result += ticketCost;
        return result;
    }

}
