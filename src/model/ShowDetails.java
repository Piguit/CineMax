package model;

import java.time.format.DateTimeFormatter;

/**
 * <p>
 * La classe {@code ShowDetails} si utilizza per rappresentare dei
 * dati specifici di una proiezione all'interno del sistema.
 * </p>
 * @author Edo Hodzic 761022
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public class ShowDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String showId;
    private String title;
    private String showDate;
    private String ticketCost;

    /**
     * Costruttore che istanzia un oggetto {@code ShowDetails} con i relativi campi.
     * @param show istanza di {@link Show} composto da: id, data, costo biglietto
     * @param movie istanza di {@link Movie} composta da: titolo
     */
    public ShowDetails(Show show, Movie movie) {
        this.showId = String.valueOf(show.getId());
        this.title = movie.getTitle();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.ticketCost = String.valueOf(show.getTicketCost());
    }
    
    /**
     * Restituisce una descrizione testuale delle informazioni di riepilogo della proiezione
     * @return stringa contenente i dati di riepilogo della proiezione nell'ordine specificato. <br>
     * (id proiezione, titolo del film, data e orario di proiezione, costo del biglietto)
     */
    public String toString() {
        return "RIEPILOGO PROIEZIONE:" + "\n- id: " + showId + "\n- titolo: " + title
                + "\n- data e orario: " + showDate + "\n- costo del biglietto: " + ticketCost;
    }
}
