package model;

import java.time.LocalDateTime;
/**
 * <p>
 * La classe {@code Show} si utilizza per rappresentare una proiezione all'interno
 * del sistema con la memorizzazione dei suoi dati.
 * </p>
 * <p>
 * Implementa le interfacce {@link ItemInitializer} e {@link Identifiable}
 * con il campo {@code showId} che rappresenta univocamente la proiezione.
 * Comprende dei getters e setters relativi ai campi della proiezione.
 * </p>
 */
public class Show implements ItemInitializer<Show>, Identifiable<Long> {

    private long showId;
    private long movieId;
    private LocalDateTime showDate;
    private float ticketCost;

    public Show() { }

    /**
     * Costruttore che inizializza un oggetto {@code Show} con i relativi campi
     * @param showId id della proiezione
     * @param movieId id del film
     * @param showDate data di proiezione
     * @param ticketCost costo del biglietto
     */
    public Show(Long showId, Long movieId, LocalDateTime showDate, Float ticketCost) {
        this.showId = showId;

        this.movieId = movieId;
        this.showDate = showDate;
        this.ticketCost = ticketCost;
    }

    /**
     * Costruttore che istanza un oggetto {@code Show} ed esegue il parsing
     * di un array di stringhe contenente i dati della proiezione.
     * <p>
     * L'array di stringhe è ordinato nel seguente modo:
     * [0] id proiezione, [1] id film, [2] data proiezione, [3] costo biglietto
     * </p>
     * @param array array di stringhe composto dai 4 campi ordinati della proiezione
     */
    public Show(String[] array) {
        this.showId = Long.parseLong(array[0]);
        this.movieId = Long.parseLong(array[1]);
        this.showDate = LocalDateTime.parse(array[2]);
        this.ticketCost = Float.parseFloat(array[3]);
    }

    /**
     * Istanzia e restituisce un nuovo oggetto {@code Show}
     * a partire da un array di stringhe contenente i suoi dati.
     * @param array array di stringhe con i campi ordinati dell'oggetto
     * @return oggetto istanziato del tipo {@code Show}
     */
    public Show getNewItem(String[] array) { return new Show(array); }
    
    /**
     * Restituisce l'id univoco della proiezione
     * @return id univoco della proiezione
     */
    public Long getId() { return showId; }
    
    /**
     * Restituisce l'array di stringhe composto da tutti i campi
     * del tipo {@code Show}
     * @return array di stringhe dei campi della classe {@code User}
     */
    public String[] getFields() {
        return new String[]{String.valueOf(showId), String.valueOf(movieId),
                String.valueOf(showDate), String.valueOf(ticketCost)};
    }

    /**
     * Restituisce l'id del film
     * @return id del film
     */
    public Long getMovieId() {return movieId;}

    /**
     * Restituisce la data di proiezione
     * @return data di proiezione
     */
    public LocalDateTime getShowDate() {return showDate;}

    /**
     * Restituisce il costo del biglietto
     * @return costo del biglietto
     */
    public Float getTicketCost() {return ticketCost;}

    /** @param showId id della proiezione da assegnare */
    public void setShowId(long showId) {this.showId = showId;}
    
    /** @param movieId id del film da assegnare */
    public void setMovieId(long movieId) {this.movieId = movieId;}
    
    /** @param showDate data di proiezione da assegnare */
    public void setShowDate(LocalDateTime showDate) {this.showDate = showDate;}
    
    /** @param ticketCost costo del biglietto da assegnare */
    public void setTicketCost(float ticketCost) {this.ticketCost = ticketCost;}
    
    /**
     * Restituisce una descrizione testuale delle informazioni della proiezione
     * con la concatenazione di tutti i campi divisi dal separatore '|'.
     * @return stringa contenente i dati della proiezione nell'ordine di memorizzazione
     */
    public String toString() {
        String result = "";
        result += showId + " | ";
        result += movieId + " | ";
        result += showDate + " | ";
        result += ticketCost;
        return result;
    }

}
