package Model;

public class Reservation implements ItemInitializer<Reservation>, Identifiable<Long> {
    private long reservationId;
    private String username;
    private long showId;
    private byte ticketsNumber;

    public Reservation() {}

    public Reservation(Long reservationId, String username, Long showId, Byte ticketsNumber) {
        this.reservationId = reservationId;
        this.username = username;
        this.showId = showId;
        this.ticketsNumber = ticketsNumber;
    }

    public Reservation (String[] array) {
        this.reservationId = Long.parseLong(array[0]);
        this.username = array[1];
        this.showId = Long.parseLong(array[2]);
        this.ticketsNumber = Byte.parseByte(array[3]);
    }

    public Reservation getNewItem(String[] array){ return new Reservation(array); }

    public Long getId() {return reservationId; }

    public String[] getFields(){
        return new String[]{String.valueOf(reservationId), username, String.valueOf(showId),
                String.valueOf(ticketsNumber)/*, String.valueOf(showTime)*/} ;
    }

    public String getUsername(){return username; }

    public Long getShowId(){return showId; }

    public Byte getTicketsNumber(){return ticketsNumber; }

    public void setReservationId(long reservationId){this.reservationId = reservationId; }

    public void setUsername(String username){this.username = username; }

    public void setShowId(long showId){this.showId = showId; }

    public void setTicketsNumber(byte ticketsNumber){this.ticketsNumber = ticketsNumber; }

    public String toString() {
        String result = "";
        result += reservationId + " | ";
        result += username + " | ";
        result += showId + " | ";
        result += ticketsNumber;
        return result;
    }
}