package textuserinterface;

import model.*;
import service.*;
import utility.OutputPrinter;

import java.time.*;

public class TextUserInterface {
    private static final String DIVIDER = "\n********\n\n";
    private static final String SEPARATOR = "\n";

    private final Authentication authService;
    private final MovieService movService;
    private final ShowService showService;
    private final ReservationService resService;
    private final MenuHelper mh;
    private final OutputPrinter op;

    private User currentUser;

    public TextUserInterface(Authentication auth, MovieService mov, ShowService show, ReservationService res, OutputPrinter op) {
        this.authService = auth;
        this.movService = mov;
        this.showService = show;
        this.resService = res;
        this.currentUser = null;
        this.op = op;
        this.mh = new MenuHelper(op);
    }

    public void start() {
        getAdminPanel();
        while (true) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                switch (currentUser.getRole()) {
                    case Role.ADMIN:
                        showAdminMenu();
                        break;
                    case Role.PROJECTIONIST:
                        showProjectionistMenu();
                        break;
                    case Role.BOXOFFICECLERK:
                        showBoxOfficeClerkMenu();
                        break;
                    default:
                        showClientMenu();
                        break;
                }
            }
        }
    }

    private void showGuestMenu() {
        op.println(DIVIDER + "=== CineMax - Menu Ospite ===");
        op.printlnMarked("1. Cerca proiezioni");
        op.printlnMarked("2. Visualizza dettagli proiezione");
        op.printlnMarked("3. Registrati come cliente");
        op.printlnMarked("4. Login");
        op.printlnMarked("5. Esci");
        int choice = mh.readInt("Scelta: ", 1, 5);
        op.print(SEPARATOR);
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
        op.println(DIVIDER + "=== CineMax - Menu Cliente ===");
        op.printlnMarked("1. Cerca proiezioni");
        op.printlnMarked("2. Visualizza dettagli proiezione");
        op.printlnMarked("3. Aggiungi prenotazione");
        op.printlnMarked("4. Visualizza mie prenotazioni");
        op.printlnMarked("5. Modifica prenotazione");
        op.printlnMarked("6. Cancella prenotazione");
        op.printlnMarked("7. Logout");
        int choice = mh.readInt("Scelta: ", 1, 7);
        op.print(SEPARATOR);
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
        op.println(DIVIDER + "=== CineMax - Menu Proiezionista ===");
        op.printlnMarked("1. Filtra film");
        op.printlnMarked("2. Filtra proiezioni");
        op.printlnMarked("3. Aggiungi proiezione");
        op.printlnMarked("4. Modifica proiezione");
        op.printlnMarked("5. Elimina proiezione");
        op.printlnMarked("6. Logout");
        int choice = mh.readInt("Scelta: ", 1, 6);
        op.print(SEPARATOR);
        switch (choice) {
            case 1: searchMovies(); break;
            case 2: searchShows(); break;
            case 3: addShow(); break;
            case 4: editShow(); break;
            case 5: deleteShow(); break;
            case 6: logout(); break;
            default: break;
        }
    }

    private void showBoxOfficeClerkMenu() {
        op.println(DIVIDER + "=== CineMax - Menu Bigliettaio ===");
        op.printlnMarked("1. Cerca prenotazioni");
        op.printlnMarked("2. Visualizza prenotazioni odierne");
        op.printlnMarked("3. Visualizza dettagli prenotazione");
        op.printlnMarked("4. Logout");
        int choice = mh.readInt("Scelta: ", 1, 4);
        op.print(SEPARATOR);
        switch (choice) {
            case 1: searchReservations(); break;
            case 2: visualizeTodayReservations(); break;
            case 3: visualizeReservation(); break;
            case 4: logout(); break;
            default: break;
        }
    }

    private void showAdminMenu() {
        op.println(DIVIDER + "=== CineMax - Menu Admin ===");
        op.printlnMarked("1. Aggiungi proiezionista");
        op.printlnMarked("2. Aggiungi bigliettaio");
        op.printlnMarked("3. Logout");
        int choice = mh.readInt("Scelta: ", 1, 3);
        op.print(SEPARATOR);
        switch (choice) {
            case 1: makeProjectionist(); break;
            case 2: makeBoxOfficeClerk(); break;
            case 3: logout(); break;
            default: break;
        }
    }

    private void login() {
        String username = mh.readString("Username: ", true);
        String password = mh.readString("Password: ", true);
        try {
            currentUser = authService.signIn(username, password);
        } catch (PromptException e) {
            op.print(SEPARATOR);
            op.println(e.getMessage());
        }
    }

    private void logout() {
        op.println("<---");
        currentUser = null;
    }
    
    //---------------- GUEST

    private void searchShows() {
        String title = mh.readString("Titolo: ", false);
        String genre = mh.readString("Genere: ", false);
        LocalDate from = mh.readDate("Da data - dd/mm/yyyy: ", false);
        LocalDate to = mh.readDate("A data - dd/mm/yyyy: ", false);
        Float minCost = mh.readFloat("Prezzo minimo: ", false);
        Float maxCost = mh.readFloat("Prezzo massimo: ", false);
        op.print(SEPARATOR);
        int num = showService.searchAndPrintShows(title, genre, from, to, minCost, maxCost);
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    private void visualizeShow() {
        FullShowDetails result = showService.visualizeShow(mh.readLong("Identificativo della proiezione: ", true));
        op.print(SEPARATOR);
        if (result != null)
            op.printlnMarked(result.toString());
        else
            op.println("Nessun risultato trovato.");
    }

    private void register() {
        String name = mh.readString("Nome: ", true);
        String surname = mh.readString("Cognome: ", true);
        String residence = mh.readString("Domicilio: ", true);
        LocalDate birthDate = mh.readDate("Data di nascita - dd/mm/yyyy: ", true);
        String username = mh.readString("Username: ", true);
        String password = mh.readString("Password: ", true);
        boolean result = authService.signUp(username, name, surname, password, birthDate, residence);
        op.print(SEPARATOR);
        if (result)
            op.println("La registrazione è avvenuta con successo.");
        else
            op.println("Registrazione annullata: il nome utente è gia' in uso.");
    }

    //---------------- CLIENT

    private void addReservation() {
        Long showId = mh.readLong("Identificativo della proiezione: ", true);
        Short ticketsNumber = mh.readPositiveShort("Numero di biglietti: ", true);
        op.print(SEPARATOR);
        try {
            Long reservationId = resService.addReservation(currentUser.getId(), showId, ticketsNumber);
            op.println("Registrazione riuscita: la tua prenotazione ha id " + reservationId + ".");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }
    
    private void visualizeMyReservations() {
        int num = resService.printMyReservations(currentUser.getId());
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }
    
    private void editReservation() {
        Long reservationId = mh.readLong("Identificativo della prenotazione: ", true);
        Long showId = mh.readLong("Identificativo della nuova proiezione: ", true);
        Short ticketsNumber = mh.readPositiveShort("Numero di biglietti: ", true);
        op.print(SEPARATOR);
        try {
            resService.editReservation(currentUser.getId(), reservationId, showId, ticketsNumber);
            op.println("Modifica riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    private void deleteReservation() {
        Long reservationId = mh.readLong("Identificativo della prenotazione: ", true);
        op.print(SEPARATOR);
        try {
            resService.deleteReservation(currentUser.getId(), reservationId);
            op.println("Eliminazione riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    //---------------- PROJECTIONIST

    private void searchMovies() {
        String partialTitle = mh.readString("Titolo: ", false);
        String director = mh.readString("Regista: ", false);
        Short year = mh.readPositiveShort("Anno: ", false);
        op.print(SEPARATOR);
        int num = movService.searchAndPrintMovies(partialTitle, director, year);
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    private void addShow() {
        Long id = mh.readLong("Identificativo del film: ", false);
        if (id == null) {
            String title = mh.readString("Titolo: ", true);
            String director = mh.readString("Regista: ", true);
            Short year = mh.readPositiveShort("Anno: ", true);
            String genre = mh.readString("Genere: ", true);
            Short runningTime = mh.readPositiveShort("Durata: ", true);
            Byte minAge = mh.readPositiveByte("Eta' minima: ", true);
            id = movService.addMovie(title, director, year, genre, runningTime, minAge);
        }
        LocalDateTime date = mh.readDateAndTime("Data e ora - dd/mm/yyyy hh:mm: ", true);
        Float ticketCost = mh.readFloat("Costo del biglietto: ", true);
        op.print(SEPARATOR);
        try {
            Long showId = showService.addShow(id, date, ticketCost);
            op.println("Registrazione riuscita: la nuova proiezione ha id " + showId + ".");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    private void editShow() {
        Long showId = mh.readLong("Identificativo della proiezione: ", true);
        LocalDateTime newShowDate = mh.readDateAndTime("Data e ora nuove - dd/mm/yyyy hh:mm: ", true);
        Float newTicketCost = mh.readFloat("Nuovo prezzo del biglietto: ", true);
        op.print(SEPARATOR);
        try {
            showService.editShow(showId, newShowDate, newTicketCost);
            op.println("Modifica riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    private void deleteShow() {
        Long showId = mh.readLong("Identificativo della proiezione: ", true);
        op.print(SEPARATOR);
        try {
            showService.deleteShow(showId);
            op.println("Eliminazione riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    //---------------- BOXOFFICECLERK

    private void searchReservations() {
        Long id = mh.readLong("Identificativo della prenotazione: ", false);
        String name = mh.readString("Nome: ", false);
        String surname = mh.readString("Cognome: ", false);
        String partialTitle = mh.readString("Titolo del film: ", false);
        LocalDate from = mh.readDate("Da data - dd/mm/yyyy: ", false);
        LocalDate to = mh.readDate("A data - dd/mm/yyyy: ", false);
        op.print(SEPARATOR);
        int num = resService.searchAndPrintReservations(id, name, surname, partialTitle, from, to);
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    private void visualizeReservation() {
        FullReservationDetails result = resService.visualizeReservation(mh.readLong("Identificativo della prenotazione: ", true));
        op.print(SEPARATOR);
        if (result != null)
            op.printlnMarked(result.toString());
        else
            op.println("Nessun risultato trovato.");
    }

    private void visualizeTodayReservations() {
        int num = resService.printTodayReservations();
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    //---------------- ADMIN

    private void getAdminPanel() {
        if (authService.isFirstAccess()) {
            op.println(DIVIDER + "Primo accesso. E' necessario creare un account admin.");
            String name = mh.readString("Nome: ", true);
            String surname = mh.readString("Cognome: ", true);
            String residence = mh.readString("Domicilio: ", true);
            LocalDate birthDate = mh.readDate("Data di nascita - dd/mm/yyyy: ", true);
            String username = mh.readString("Username: ", true);
            String password = mh.readString("Password: ", true);
            authService.adminSignUp(username, name, surname, password, birthDate, residence);
            op.print(SEPARATOR);
            op.println("L'applicazione e' pronta.");
        }
    }

    private void makeProjectionist() {
        String username = mh.readString("Username: ", true);
        op.print(SEPARATOR);
        try {
            authService.makeProjectionist(username);
            op.println("Operazione riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    private void makeBoxOfficeClerk() {
        String username = mh.readString("Username: ", true);
        op.print(SEPARATOR);
        try {
            authService.makeBoxOfficeClerk(username);
            op.println("Operazione riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }
}
