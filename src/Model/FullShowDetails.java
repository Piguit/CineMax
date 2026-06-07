package Model;

import java.time.format.DateTimeFormatter;
/**
 * <p>
 * La classe {@code FullShowDetails} si utilizza per rappresentare
 * tutti i dati di una proiezione all'interno del sistema.
 * </p>
 */
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

    /**
     * Costruttore che istanza un oggetto {@code FullShowDetails} con i relativi campi.
     * @param show istanza di {@link Show} composto da: id, data e ora, costo biglietto della proiezione
     * @param movie istanza di {@link Movie} composto da: titolo, genere, regista, anno, durata, età minima del film
     * @param freeSeats numero di posti liberi
     */
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
    /**
     * Restituisce una descrizione testuale di tutte le informazioni della proiezione.
     * @return stringa contenente tutti i dati di della proiezione nell'ordine specificato. <br>
     * (id proiezione, titolo, genere, regista, anno, durata, età minima, data e orario,
     * costo biglietto, posti disponibili)
     */
    public String toString() {
        return "RIEPILOGO PROIEZIONE:" + "\n\t- id: " + showId + "\n\t- titolo: " + title + "\n\t- genere: " + genre + "\n\t- regista: "
                + director + "\n\t- anno: " + year + "\n\t- durata: " + runningTime + "\n\t- eta' minima: " + minAge + "\n\t- data e orario: "
                + showDate + "\n\t- costo del biglietto: " + ticketCost + "\n\t- posti disponibili: " + freeSeats;
    }
}
