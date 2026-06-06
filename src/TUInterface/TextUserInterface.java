package TUInterface;

import Model.*;
import Service.*;

import java.time.*;
import java.util.*;

public class TextUserInterface {
    private static final String DIVIDER = "\n\n\n";
    private static final String SEPARATOR = "\n";
    private static final String MARGIN = "\t";

    private final Authentication authService;
    private final MovieService movService;
    private final ShowService showService;
    private final ReservationService resService;
    private User currentUser;

    public TextUserInterface(Authentication auth, MovieService mov, ShowService show, ReservationService res) {
        this.authService = auth;
        this.movService = mov;
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
                    case Role.CLIENT:
                        showClientMenu();
                        break;
                    case Role.PROJECTIONIST:
                        showProjectionistMenu();
                        break;
                    case Role.BOXOFFICECLERK:
                        showBoxOfficeClerkMenu();
                        break;
                    case Role.ADMIN:
                        showAdminMenu();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void showGuestMenu() {
        System.out.println(DIVIDER + MARGIN + "=== CineMax - Menu Ospite ===");
        System.out.println(MARGIN + "1. Cerca proiezioni");
        System.out.println(MARGIN + "2. Visualizza dettagli proiezione");
        System.out.println(MARGIN + "3. Registrati come cliente");
        System.out.println(MARGIN + "4. Login");
        System.out.println(MARGIN + "5. Esci");
        int choice = MenuHelper.readInt(MARGIN + "Scelta: ", 1, 5);
        System.out.print(SEPARATOR);
        switch (choice) {
            case 1: searchShows(); break;
            case 2: visualizeShow(); break;
            case 3: register(); break;
            case 4: login(); break;
            case 5: System.exit(0);
            default: break;
        }
    }

    private void showClientMenu() {
        System.out.println(DIVIDER + MARGIN + "=== CineMax - Menu Cliente ===");
        System.out.println(MARGIN + "1. Cerca proiezioni");
        System.out.println(MARGIN + "2. Visualizza dettagli proiezione");
        System.out.println(MARGIN + "3. Aggiungi prenotazione");
        System.out.println(MARGIN + "4. Visualizza mie prenotazioni");
        System.out.println(MARGIN + "5. Modifica prenotazione");
        System.out.println(MARGIN + "6. Cancella prenotazione");
        System.out.println(MARGIN + "7. Logout");
        int choice = MenuHelper.readInt(MARGIN + "Scelta: ", 1, 7);
        System.out.print(SEPARATOR);
        switch (choice) {
            case 1: searchShows(); break;
            case 2: visualizeShow(); break;
            case 3: addReservation(); break;
            case 4: visualizeMyReservations(); break;
            case 5: editReservation(); break;
            case 6: deleteReservation(); break;
            case 7: logout(); break;
            default: break;
        }
    }

    private void showProjectionistMenu() {
        System.out.println(DIVIDER + MARGIN + "=== CineMax - Menu Proiezionista ===");
        System.out.println(MARGIN + "1. Visualizza film");
        System.out.println(MARGIN + "2. Filtra proiezioni");
        System.out.println(MARGIN + "3. Aggiungi proiezione");
        System.out.println(MARGIN + "4. Modifica proiezione");
        System.out.println(MARGIN + "5. Elimina proiezione");
        System.out.println(MARGIN + "6. Logout");
        int choice = MenuHelper.readInt(MARGIN + "Scelta: ", 1, 6);
        System.out.print(SEPARATOR);
        switch (choice) {
            case 1: visualizeMovies(); break;
            case 2: searchShows(); break;
            case 3: addShow(); break;
            case 4: editShow(); break;
            case 5: deleteShow(); break;
            case 6: logout(); break;
            default: break;
        }
    }

    private void showBoxOfficeClerkMenu() {
        System.out.println(DIVIDER + MARGIN + "=== CineMax - Menu Bigliettaio ===");
        System.out.println(MARGIN + "1. Cerca prenotazioni");
        System.out.println(MARGIN + "2. Visualizza prenotazioni odierne");
        System.out.println(MARGIN + "3. Visualizza dettagli prenotazione");
        System.out.println(MARGIN + "4. Logout");
        int choice = MenuHelper.readInt(MARGIN + "Scelta: ", 1, 4);
        System.out.print(SEPARATOR);
        switch (choice) {
            case 1: searchReservations(); break;
            case 2: visualizeTodayReservations(); break;
            case 3: visualizeReservation(); break;
            case 4: logout(); break;
            default: break;
        }
    }

    private void showAdminMenu() {
        System.out.println(DIVIDER + MARGIN + "=== CineMax - Menu Admin ===");
        System.out.println(MARGIN + "1. Aggiungi proiezionista");
        System.out.println(MARGIN + "2. Aggiungi bigliettaio");
        System.out.println(MARGIN + "3. Logout");
        int choice = MenuHelper.readInt(MARGIN + "Scelta: ", 1, 3);
        System.out.print(SEPARATOR);
        switch (choice) {
            case 1: addProjectionist(); break;
            case 2: addBoxOfficeClerk(); break;
            case 3: logout(); break;
            default: break;
        }
    }

    private void login() {
        String username = MenuHelper.readString(MARGIN + "Username: ", true);
        String password = MenuHelper.readString(MARGIN + "Password: ", true);
        try {
            currentUser = authService.signIn(username, password);
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    private void logout() {
        currentUser = null;
    }
    
    //---------------- GUEST

    private void searchShows() {
        String title = MenuHelper.readString(MARGIN + "Titolo: ", false);
        String genre = MenuHelper.readString(MARGIN + "Genere: ", false);
        LocalDate from = MenuHelper.readDate(MARGIN + "Da data - dd/mm/yyyy: ", false);
        LocalDate to = MenuHelper.readDate(MARGIN + "A data - dd/mm/yyyy: ", false);
        Float minCost = MenuHelper.readFloat(MARGIN + "Prezzo minimo: ", false);
        Float maxCost = MenuHelper.readFloat(MARGIN + "Prezzo massimo: ", false);
        List<ShowDetails> shows = showService.searchShows(title, genre, from, to, minCost, maxCost);
        MenuHelper.printList(shows, SEPARATOR, MARGIN);
    }

    private void visualizeShow() {
        FullShowDetails result = showService.visualizeShow(MenuHelper.readLong(MARGIN + "Identificativo della proiezione: ", true));
        if (result != null)
            System.out.println(MARGIN + result);
        else
            System.out.println(MARGIN + "Nessun risultato trovato");
    }

    private void register() {
        String name = MenuHelper.readString(MARGIN + "Nome: ", true);
        String surname = MenuHelper.readString(MARGIN + "Cognome: ", true);
        String residence = MenuHelper.readString(MARGIN + "Domicilio: ", true);
        LocalDate birthDate = MenuHelper.readDate(MARGIN + "Data di nascita - dd/mm/yyyy: ", true);
        String username = MenuHelper.readString(MARGIN + "Username: ", true);
        String password = MenuHelper.readString(MARGIN + "Password: ", true);
        boolean result = authService.signUp(username, name, surname, password, birthDate, residence);
        if (result)
            System.out.println(MARGIN + "La registrazione è avvenuta con successo.");
        else
            System.out.println(MARGIN + "Registrazione annullata: il nome utente è gia' in uso.");
    }

    //---------------- CLIENT

    private void addReservation() {
        Long showId = MenuHelper.readLong(MARGIN + "Identificativo della proiezione: ", true);
        int ticketsNumber = MenuHelper.readInt(MARGIN + "Numero di biglietti: ", 1, 200);

        try {
            Long reservationId = resService.addReservation(currentUser.getId(), showId, (byte) ticketsNumber);
            System.out.println(MARGIN + "Registrazione riuscita: la tua prenotazione ha id " + reservationId + ".");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }
    
    private void visualizeMyReservations() {
        MenuHelper.printList(resService.visualizeMyReservations(currentUser.getId()), SEPARATOR, MARGIN);
    }
    
    private void editReservation() {
        Long reservationId = MenuHelper.readLong(MARGIN + "Identificativo della prenotazione: ", true);
        Long showId = MenuHelper.readLong(MARGIN + "Identificativo della nuova proiezione: ", true);
        try {
            resService.editReservation(currentUser.getId(), reservationId, showId);
            System.out.println(MARGIN + "Modifica riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    private void deleteReservation() {
        Long reservationId = MenuHelper.readLong(MARGIN + "Identificativo della prenotazione: ", true);
        try {
            resService.deleteReservation(currentUser.getId(), reservationId);
            System.out.println(MARGIN + "Eliminazione riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    //---------------- PROJECTIONIST

    private void visualizeMovies() {
        MenuHelper.printList(movService.visualizeMovies(), SEPARATOR, MARGIN);
    }

    private void addShow() {
        Long id = MenuHelper.readLong(MARGIN + "Identificativo del film: ", false);
        if (id == null) {
            String title = MenuHelper.readString(MARGIN + "Titolo: ", true);
            String director = MenuHelper.readString(MARGIN + "Regista: ", true);
            Short year = MenuHelper.readShort(MARGIN + "Anno: ", true);
            String genre = MenuHelper.readString(MARGIN + "Genere: ", true);
            Short runningTime = MenuHelper.readShort(MARGIN + "Durata: ", true);
            Byte minAge = MenuHelper.readByte(MARGIN + "Eta' minima: ", true);
            id = movService.addMovie(title, director, year, genre, runningTime, minAge);
        }
        LocalDateTime date = MenuHelper.readDateAndTime(MARGIN + "Data e ora - dd/mm/yyyy hh:mm:ss: ", true);
        Float ticketCost = MenuHelper.readFloat(MARGIN + "Costo del biglietto: ", true);
        try {
            Long showId = showService.addShow(id, date, ticketCost);
            System.out.println(MARGIN + "Registrazione riuscita: la nuova proiezione ha id " + showId + ".");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    private void editShow() {
        Long showId = MenuHelper.readLong(MARGIN + "Identificativo della proiezione: ", true);
        LocalDateTime newShowDate = MenuHelper.readDateAndTime(MARGIN + "Data e ora nuove - dd/mm/yyyy hh:mm:ss: ", true);
        Float newTicketCost = MenuHelper.readFloat(MARGIN + "Nuovo prezzo del biglietto: ", true);
        try {
            showService.editShow(showId, newShowDate, newTicketCost);
            System.out.println(MARGIN + "Modifica riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    private void deleteShow() {
        Long showId = MenuHelper.readLong(MARGIN + "Identificativo della proiezione: ", true);
        try {
            showService.deleteShow(showId);
            System.out.println(MARGIN + "Eliminazione riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    //---------------- BOXOFFICECLERK

    public void searchReservations() {
        Long id = MenuHelper.readLong(MARGIN + "Identificativo della prenotazione: ", false);
        String name = MenuHelper.readString(MARGIN + "Name: ", false);
        String surname = MenuHelper.readString(MARGIN + "Surname: ", false);
        String partialTitle = MenuHelper.readString(MARGIN + "Titolo del film: ", false);
        LocalDate from = MenuHelper.readDate(MARGIN + "Da data - dd/mm/yyyy: ", false);
        LocalDate to = MenuHelper.readDate(MARGIN + "A data - dd/mm/yyyy: ", false);
        List<ReservationDetails> reservations = resService.searchReservations(id, name, surname, partialTitle, from, to);
        MenuHelper.printList(reservations, SEPARATOR, MARGIN);
    }

    private void visualizeReservation() {
        FullReservationDetails result = resService.visualizeReservation(MenuHelper.readLong(MARGIN + "Identificativo della prenotazione: ", true));
        if (result != null)
            System.out.println(MARGIN + result);
        else
            System.out.println(MARGIN + "Nessun risultato trovato.");
    }

    public void visualizeTodayReservations() {
        MenuHelper.printList(resService.visualizeTodayReservations(), SEPARATOR, MARGIN);
    }

    //---------------- ADMIN

    public void getAdminPanel() {
        if (authService.isFirstAccess()) {
            System.out.println(MARGIN + "Primo accesso. E' necessario creare un account admin.");
            String name = MenuHelper.readString(MARGIN + "Nome: ", true);
            String surname = MenuHelper.readString(MARGIN + "Cognome: ", true);
            String residence = MenuHelper.readString(MARGIN + "Domicilio: ", true);
            LocalDate birthDate = MenuHelper.readDate(MARGIN + "Data di nascita - dd/mm/yyyy: ", true);
            String username = MenuHelper.readString(MARGIN + "Username: ", true);
            String password = MenuHelper.readString(MARGIN + "Password: ", true);
            authService.adminSignUp(username, name, surname, password, birthDate, residence);
            System.out.println(MARGIN + "L'applicazione e' pronta.");
        }
    }

    private void addProjectionist() {
        String username = MenuHelper.readString(MARGIN + "Username: ", true);
        try {
            authService.makeProjectionist(username);
            System.out.println(MARGIN + "Operazione riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }

    private void addBoxOfficeClerk() {
        String username = MenuHelper.readString(MARGIN + "Username: ", true);
        try {
            authService.makeBoxOfficeClerk(username);
            System.out.println(MARGIN + "Operazione riuscita.");
        } catch (PromptException e) {
            System.out.println(MARGIN + e.getMessage());
        }
    }
}
