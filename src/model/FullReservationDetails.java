package model;

import java.time.format.DateTimeFormatter;
/**
 * La classe {@code FullReservationDetails} si utilizza per rappresentare
 * tutti i dati di una prenotazione all'interno del sistema.
 * </p>
 */
public class FullReservationDetails {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private String reservationId;
    private String name;
    private String surname;
    private String showDate;
    private String ticketsNumber;
    private String ticketCost;
    private String totalCost;
    private String title;

    /**
     * Costruttore che istanzia un oggetto {@code FullReservationDetails} con i relativi campi.
     * @param reservation istanza di {@link Reservation} composto da: id, numero biglietti (contribuisce al costo totale)
     * @param client istanza di {@link User} composto da: nome e cognome dell'utente
     * @param show istanza di {@link Show} composto da: data e ora, costo del biglietto (contribuisce al costo totale)
     * @param movie istanza di {@link Movie} composto da: titolo del film
     */
    public FullReservationDetails(Reservation reservation, User client, Show show, Movie movie) {
        this.reservationId = String.valueOf(reservation.getId());
        this.name = client.getName();
        this.surname = client.getSurname();
        this.showDate = show.getShowDate().format(DATE_TIME_FORMAT);
        this.ticketsNumber = String.valueOf(reservation.getTicketsNumber());
        this.ticketCost = String.valueOf(show.getTicketCost());
        this.totalCost = String.valueOf(show.getTicketCost() * reservation.getTicketsNumber());
        this.title = movie.getTitle();
    }
    /**
     * Restituisce una descrizione testuale di tutte le informazioni della prenotazione.
     * @return stringa contenente tutti i dati della prenotazione nell'ordine specificato. <br>
     * (id prenotazione, nome, cognome, data e orario, numero di biglietti, costo del biglietto, costo totale, titolo film)
     */
    public String toString() {
        return "RIEPILOGO PRENOTAZIONE:" + "\n- id: " + reservationId + "\n- nome: " + name
                + "\n- cognome: " + surname + "\n- data e orario: " + showDate + "\n- numero di biglietti: " + ticketsNumber
                + "\n- costo del biglietto: " + ticketCost + "\n- costo totale: " + totalCost + "\n- film: " + title;
    }
}

