package Model;

import java.time.LocalDateTime;

public class Reservation implements ItemInitializer<Reservation>, Identifiable<Long> {
    private long reservationId;
    private String username;
    private long showId;
    private byte ticketsNumber;
    private LocalDateTime showTime;

    public Reservation() {}

    public Reservation(String reservationId, String username, String showId, String ticketsNumber, String showTime){
        this.reservationId = Long.parseLong(reservationId);
        this.username = username;
        this.showId = Long.parseLong(showId);
        this.ticketsNumber = Byte.parseByte(ticketsNumber);
        this.showTime = LocalDateTime.parse(showTime);
    }

    public Reservation (String[] array) {
        this.reservationId = Long.parseLong(array[0]);
        this.username = array[1];
        this.showId = Long.parseLong(array[2]);
        this.ticketsNumber = Byte.parseByte(array[3]);
        this.showTime = LocalDateTime.parse(array[4]);
    }

    public Reservation getNewItem(String[] array){ return new Reservation(array); }

    public Long getId() {return reservationId; }

    public String[] getFields(){
        return new String[]{String.valueOf(reservationId), username, String.valueOf(showId),
                String.valueOf(ticketsNumber), String.valueOf(showTime)} ;
    }

    public String getUsername(){return username; }

    public long getShowId(){return showId; }

    public byte getTicketsNumber(){return ticketsNumber; }

    public LocalDateTime getShowTime() {return showTime; }

    public void setReservationId(Long reservationId){this.reservationId = reservationId; }

    public void setUsername(String username){this.username = username; }

    public void setShowId(String showId){this.showId = Long.parseLong(showId); }

    public void setTicketsNumber(byte ticketsNumber){this.ticketsNumber = ticketsNumber; }

    public void setShowTime(LocalDateTime showTime){this.showTime = showTime; }

    public String toString() {
        String result = "";
        result += reservationId + " | ";
        result += username + " | ";
        result += showId + " | ";
        result += ticketsNumber + " | ";
        result += showTime + " | ";
        return result;
    }
}