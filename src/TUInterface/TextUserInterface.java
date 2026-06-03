package TUInterface;

import Model.*;
import Service.*;

import java.time.LocalDate;
import java.util.*;

public class TextUserInterface {
    private final Authentication authService;
    private final ShowService showService;
    private final ReservationService resService;
    private User currentUser;

    public TextUserInterface(Authentication auth, ShowService show, ReservationService res) {
        this.authService = auth;
        this.showService = show;
        this.resService = res;
        this.currentUser = null;
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                switch (currentUser.getRole()) {
                    case CLIENT:
                        showClientMenu();
                        break;
                    case PROJECTIONIST:
                        showProjectionistMenu();
                        break;
                    default:
                        showBoxOfficeClerkMenu();
                        break;
                }
            }
        }
    }

    private void showGuestMenu() {
        System.out.println("\n=== CineMax - Menu Ospite ===");
        System.out.println("1. Cerca proiezioni");
        System.out.println("2. Visualizza dettagli proiezione");
        System.out.println("3. Registrati come cliente");
        System.out.println("4. Login");
        System.out.println("5. Esci");
        int choice = MenuHelper.readInt("Scelta: ", 1, 5);
        switch (choice) {
            case 1: searchShows(); break;
            case 2: visualizeShow(); break;
            case 3: register(); break;
            case 4: login(); break;
            default: System.exit(0);
        }
    }

    private void showClientMenu() {
        System.out.println("\n=== CineMax - Menu Cliente ===");
        System.out.println("1. Cerca proiezioni");
        System.out.println("2. Visualizza dettagli proiezione");
        System.out.println("3. Aggiungi prenotazione");
        System.out.println("4. Visualizza mie prenotazioni");
        System.out.println("5. Modifica prenotazione");
        System.out.println("6. Cancella prenotazione");
        System.out.println("7. Logout");
        int choice = MenuHelper.readInt("Scelta: ", 1, 7);
        switch (choice) {
            case 1: searchShows(); break;
            case 2: visualizeShow(); break;
            case 3: addReservation(); break;
            case 4: visualizeMyReservations(); break;
            case 5: editReservation(); break;
            case 6: deleteReservation(); break;
            default: logout(); break;
        }
    }

    private void showProjectionistMenu() {
    }

    private void showBoxOfficeClerkMenu() {
    }
    
    //----------------

    private void searchShows() {
        String title = MenuHelper.readString("Titolo: ", false);
        String genre = MenuHelper.readString("Genere: ", false);
        LocalDate from = MenuHelper.readDate("Da data - dd/mm/yyyy: ", false);
        LocalDate to = MenuHelper.readDate("A data - dd/mm/yyyy: ", false);
        Double minCost = MenuHelper.readDouble("Prezzo minimo: ", false);
        Double maxCost = MenuHelper.readDouble("Prezzo massimo: ", false);
        List<ShowDetails> showsInDetail = showService.searchShowsInDetail(title, genre, from, to, minCost, maxCost);
        MenuHelper.printList(showsInDetail);
    }

    private void visualizeShow() {
        System.out.println(showService.visualizeShow(MenuHelper.readLong("Identificativo: ", true)));
    }

    private void addReservation() {
        Long showId = MenuHelper.readLong("Identificativo dello show: ", false);
        int ticketsNumber = MenuHelper.readInt("Numero di biglietti: ", 1, 200);

        try {
            Long reservationId = resService.addReservation(currentUser.getId(), showId, (byte) ticketsNumber);
            System.out.println("Registrazione riuscita: la tua prenotazione ha id " + reservationId + ".");
        } catch (IllegalArgumentException e) {
            // !!!Aggiungere eccezioni custom
            System.out.println(e.getMessage());
        }
    }
    
    private void visualizeMyReservations() {
        MenuHelper.printList(resService.visualizeMyReservations(currentUser.getId()));
    }
    
    private void editReservation() {
        Long reservationId = MenuHelper.readLong("Identificativo della prenotazione: ", true);
        Long showId = MenuHelper.readLong("Identificativo della nuova proiezione: ", true);
        try {
            resService.editReservation(currentUser.getId(), reservationId, showId);
            System.out.println("Modifica riuscita.");
        } catch (IllegalArgumentException e) {
            // !!!Aggiungere eccezioni custom
            System.out.println(e.getMessage());
        }
    }

    private void deleteReservation() {
        Long reservationId = MenuHelper.readLong("Identificativo della prenotazione: ", true);
        try {
            resService.deleteReservation(currentUser.getId(), reservationId);
            System.out.println("Eliminazione riuscita.");
        } catch (IllegalArgumentException e) {
            // !!!Aggiungere eccezioni custom
            System.out.println(e.getMessage());
        }
    }

    private void register() {
        String name = MenuHelper.readString("Nome: ", true);
        String surname = MenuHelper.readString("Cognome: ", true);
        String residence = MenuHelper.readString("Domicilio: ", true);
        LocalDate birthDate = MenuHelper.readDate("Data di nascita - dd/mm/yyyy: ", true);
        String username = MenuHelper.readString("Username: ", true);
        String password = MenuHelper.readString("Password: ", true);
        boolean result = authService.signUp(username, name, surname, password, birthDate, residence);
        if (result)
            System.out.println("La registrazione è avvenuta con successo.");
        else
            System.out.println("Registrazione annullata: il nome utente è già occupato.");
    }

    private void login() {
        String username = MenuHelper.readString("Username: ", true);
        String password = MenuHelper.readString("Password: ", true);
        currentUser = authService.signIn(username, password);
        if (currentUser == null)
            System.out.println("La login non è andata a buon fine.");
    }

    private void logout() {
        currentUser = null;
    }



    private void visualizeReservation() {
        System.out.println(resService.visualizeReservation(MenuHelper.readLong("Identificativo: ", true)));
    }
}
