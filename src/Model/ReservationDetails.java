package Model;

import java.time.format.DateTimeFormatter;
/**
 * <p>
 * La classe {@code ReservationDetails} si utilizza per rappresentare dei
 * dati specifici di una prenotazione all'interno del sistema.
 * </p>
 */
public class ReservationDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private String reservationId;
    private String name;
    private String surname;
    private String showDate;
    private String title;

    /**
     * Costruttore che istanza un oggetto {@code ReservationDetails} con i relativi campi.
     * @param reservation istanza di {@link Reservation} composto da: id prenotazione
     * @param client istanza di {@link User} composta da: nome e cognome dell'utente
     * @param show istanza di {@link Show} composto da: data di proiezione
     * @param movie istanza di {@link Movie} composto da: titolo del film
     */
    public ReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.title = movie.getTitle();
    }
    /**
     * Restituisce una descrizione testuale delle informazioni di riepilogo della prenotazione
     * @return stringa contenente i dati di riepilogo della prenotazione nell'ordine specificato. <br>
     * (id prenotazione, nome, cognome, data e orario proiezione, titolo del film)
     */
    public String toString() {
        return "RIEPILOGO PRENOTAZIONE:" + "\n- id: " + reservationId + "\n- nome: " + name
                + "\n- cognome: " + surname + "\n- data e orario: " + showDate + "\n- titolo: " + title;
    }
}
