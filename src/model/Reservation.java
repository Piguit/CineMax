package model;

/**
 * <p>
 * La classe {@code Reservation} si utilizza per rappresentare le prenotazioni all'interno
 * del sistema con la memorizzazione dei dati richiesti.
 * </p>
 * <p>
 * Implementa le interfacce {@link ItemInitializer} e {@link Identifiable}
 * con il campo {@code reservationId} che rappresenta univocamente la prenotazione.
 * Comprende dei getters e setters relativi ai campi della prenotazione.
 * </p>
 * @author Edo Hodzic 761022
 * @author Piergiorgio Tomaciello 761013
 * @author Tamirat Tucci 756969
 * CO
 */
public class Reservation implements ItemInitializer<Reservation>, Identifiable<Long> {
    private long reservationId;
    private String username;
    private long showId;
    private short ticketsNumber;

    public Reservation() {}

    /**
     * Costruttore che istanza un oggetto {@code Reservation} con i relativi campi
     * @param reservationId id di prenotazione
     * @param username nome utente
     * @param showId id di proiezione
     * @param ticketsNumber numero di biglietti
     */
    public Reservation(Long reservationId, String username, Long showId, Short ticketsNumber) {
        this.reservationId = reservationId;
        this.username = username;
        this.showId = showId;
        this.ticketsNumber = ticketsNumber;
    }

    /**
     * Costruttore che istanza un oggetto {@code Reservation} ed esegue il parsing
     * di un array di stringhe contenente i dati della prenotazione.
     * <p>
     * L'array di stringhe è ordinato nel seguente modo:
     * [0] id prenotazione, [1] nome utente, [2] id proiezione, [3] numero di biglietti
     * </p>
     * @param array array di stringhe composto dai 4 campi ordinati della prenotazione
     */
    public Reservation (String[] array) {
        this.reservationId = Long.parseLong(array[0]);
        this.username = array[1];
        this.showId = Long.parseLong(array[2]);
        this.ticketsNumber = Short.parseShort(array[3]);
    }

    /**
     * Istanzia e restituisce un nuovo oggetto {@code Reservation}
     * a partire da un array di stringhe contenente i suoi dati.
     * @param array array di stringhe con i campi ordinati dell'oggetto
     * @return oggetto istanziato del tipo {@code Reservation}
     */
    public Reservation getNewItem(String[] array) {return new Reservation(array);}
    
    /**
     * Restituisce l'id univoco della prenotazione
     * @return id univoco della prenotazione
     */
    public Long getId() {return reservationId;}
    
    /**
     * Restituisce l'array di stringhe composto da tutti i campi
     * del tipo {@code Reservation}
     * @return array di stringhe dei campi della classe {@code Reservation}
     */
    public String[] getFields(){
        return new String[]{String.valueOf(reservationId), username, String.valueOf(showId),
                String.valueOf(ticketsNumber)} ;
    }

    /**
     * Restituisce il nome utente della prenotazione
     * @return nome utente della prenotazione
     */
    public String getUsername() {return username;}

    /**
     * Restituisce l'id di proiezione nella prenotazione
     * @return l'id di proiezione della prenotazione
     */
    public Long getShowId() {return showId;}

    /**
     * Restituisce il numero di biglietti
     * @return numero di biglietti
     */
    public Short getTicketsNumber(){return ticketsNumber; }

    /** @param reservationId id di prenotazione da assegnare */
    public void setReservationId(long reservationId){this.reservationId = reservationId; }
    
    /** @param username nome utente della prenotazione da assegnare */
    public void setUsername(String username){this.username = username; }
    
    /** @param showId id di proiezione della prenotazione da assegnare*/
    public void setShowId(long showId){this.showId = showId; }
    
    /** @param ticketsNumber numero di biglietti da assegnare */
    public void setTicketsNumber(short ticketsNumber){this.ticketsNumber = ticketsNumber; }
    
    /**
     * Restituisce una descrizione testuale delle informazioni della prenotazione
     * con la concatenazione di tutti i campi divisi dal separatore '|'.
     * @return stringa contenente i dati della prenotazione nell'ordine di memorizzazione
     */
    public String toString() {
        String result = "";
        result += reservationId + " | ";
        result += username + " | ";
        result += showId + " | ";
        result += ticketsNumber;
        return result;
    }
}