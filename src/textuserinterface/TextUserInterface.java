package textuserinterface;

import model.*;
import service.*;
import utility.OutputPrinter;

import java.time.*;

/**
 * Gestisce il ciclo di interazione testuale con l'utente.
 * Presenta menu differenziati per ruolo ({@link model.Role}), raccoglie
 * l'input tramite {@link MenuHelper} e delega ogni elaborazione al layer service.
 * Non contiene logica di business né accede direttamente ai repository.
 * Le {@link PromptException} lanciate dai service vengono catturate qui
 * e mostrate all'utente senza interrompere il ciclo dell'applicazione.
 * @author Piergiorgio Tomaciello 761013
 * @author Tamirat Tucci 756969
 * CO
 */
public class TextUserInterface {
    /** Separatore stampato all'inizio di ogni menu. */
    private static final String DIVIDER = "\n********\n\n";
    /** Separatore tra sezioni dell'output. */
    private static final String SEPARATOR = "\n";

    /** Service per autenticazione e gestione dei ruoli utente. */
    private final Authentication authService;
    /** Service per le operazioni sul catalogo dei film. */
    private final MovieService movService;
    /** Service per le operazioni sulle proiezioni. */
    private final ShowService showService;
    /** Service per le operazioni sulle prenotazioni. */
    private final ReservationService resService;
    /** Helper per la lettura e validazione dell'input da console. */
    private final MenuHelper mh;
    /** Printer per l'output formattato a console. */
    private final OutputPrinter op;

    /** Utente attualmente autenticato; {@code null} se nessun utente ha effettuato il login
     * o subito dopo {@link #logout()}. */
    private User currentUser;

    /**
     * Costruisce la TUI iniettando i service e il printer.
     * Istanzia internamente il {@link MenuHelper}.
     * @param auth service di autenticazione
     * @param mov service dei film
     * @param show service delle proiezioni
     * @param res service delle prenotazioni
     * @param op printer per l'output a console
     */
    public TextUserInterface(Authentication auth, MovieService mov, ShowService show, ReservationService res, OutputPrinter op) {
        this.authService = auth;
        this.movService = mov;
        this.showService = show;
        this.resService = res;
        this.currentUser = null;
        this.op = op;
        this.mh = new MenuHelper(op);
    }

    /**
     * Chiama il pannello per la registrazione dell'amministratore, che si aprirà se
     * l'applicazione è al primo avvio. Poi fa partire il loop di interazione, in cui
     * seleziona il menu appropriato in base al ruolo di {@link #currentUser} (o il
     * menu ospite se {@code null}). Il loop termina solo con {@code System.exit(0)}.
     */
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

    /** Mostra il menu ospite (utente non loggato) con le opzioni di ricerca, registrazione e login. */
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

    /**
     * Mostra il menu cliente con le operazioni di ricerca sulle proiezioni e
     * le operazioni di manipolazione sulle proprie prenotazioni.
     * */
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

    /**
     * Mostra il menu proiezionista con le operazioni di ricerca su film e proiezioni e
     * le operazioni di manipolazione sulle proiezioni.
     * */
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

    /** Mostra il menu bigliettaio con le operazioni di ricerca e visualizzazione delle prenotazioni. */
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

    /** Mostra il menu admin con le operazioni di promozione degli utenti a proiezionista o bigliettaio. */
    private void showAdminMenu() {
        op.println(DIVIDER + "=== CineMax - Menu Admin ===");
        op.printlnMarked("1. Rendi un utente proiezionista");
        op.printlnMarked("2. Rendi un utente bigliettaio");
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

    /** Legge le credenziali e autentica l'utente; in caso di errore, stampa un messaggio a video. */
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

    /** Reimposta currentUser a null. */
    private void logout() {
        op.println("<---");
        currentUser = null;
    }
    
    //---------------- GUEST

    /** Raccoglie i filtri opzionali (titolo, genere, date, range di prezzo) e delega la ricerca a ShowService. */
    private void searchShows() {
        String title = mh.readString("Titolo: ", false);
        String genre = mh.readString("Genere: ", false);
        LocalDate from = mh.readDate("Da data - gg/mm/aaaa: ", false);
        LocalDate to = mh.readDate("A data - gg/mm/aaaa: ", false);
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

    /** Legge l'id della proiezione e stampa l'oggetto FullShowDetails corrispondente. */
    private void visualizeShow() {
        FullShowDetails result = showService.visualizeShow(mh.readLong("Identificativo della proiezione: ", true));
        op.print(SEPARATOR);
        if (result != null)
            op.printlnMarked(result.toString());
        else
            op.println("Nessun risultato trovato.");
    }

    /** Raccoglie i dati anagrafici e le credenziali dell'utente e tenta la registrazione. */
    private void register() {
        String name = mh.readString("Nome: ", true);
        String surname = mh.readString("Cognome: ", true);
        String residence = mh.readString("Domicilio: ", true);
        LocalDate birthDate = mh.readDate("Data di nascita - gg/mm/aaaa: ", true);
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

    /**
     * Legge id della proiezione e numero di biglietti, tenta l'inserimento e stampa l'id assegnato
     * o un messaggio d'avviso in caso di errore.
     * */
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
    
    /** Stampa tutte le prenotazioni dell'utente corrente. */
    private void visualizeMyReservations() {
        int num = resService.printMyReservations(currentUser.getId());
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }
    
    /** Legge l'id della prenotazione, l'id della nuova proiezione e il numero di biglietti, poi tenta la modifica. */
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

    /** Legge l'id della prenotazione e tenta l'eliminazione. */
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

    /** Raccoglie i filtri opzionali (titolo parziale, regista, anno) e delega la ricerca a MovieService. */
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

    /**
     * Raccoglie i dati per aggiungere una proiezione. Se il proiezionista
     * non conosce l'id del film, può inserirne i dati completi: in quel caso
     * viene chiamato {@link MovieService#addMovie(String, String, Short, String, Short, Byte)},
     * che gestisce la deduplicazione e restituisce l'id (esistente o appena creato).
     * Infine raccoglie data, ora e costo del biglietto e tenta l'inserimento.
     */
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
        LocalDateTime date = mh.readDateAndTime("Data e ora - gg/mm/aaaa hh:mm : ", true);
        Float ticketCost = mh.readFloat("Costo del biglietto: ", true);
        op.print(SEPARATOR);
        try {
            Long showId = showService.addShow(id, date, ticketCost);
            op.println("Registrazione riuscita: la nuova proiezione ha id " + showId + ".");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    /** 
     * Legge id della proiezione, nuova data/ora e nuovo costo, e tenta la modifica;
     * fallisce se esistono prenotazioni associate.
     * */
    private void editShow() {
        Long showId = mh.readLong("Identificativo della proiezione: ", true);
        LocalDateTime newShowDate = mh.readDateAndTime("Data e ora nuove - gg/mm/aaaa hh:mm : ", true);
        Float newTicketCost = mh.readFloat("Nuovo prezzo del biglietto: ", true);
        op.print(SEPARATOR);
        try {
            showService.editShow(showId, newShowDate, newTicketCost);
            op.println("Modifica riuscita.");
        } catch (PromptException e) {
            op.println(e.getMessage());
        }
    }

    /** Legge l'id della proiezione e tenta l'eliminazione; fallisce se esistono prenotazioni associate. */
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

    /** Raccoglie i filtri opzionali (id, nome, cognome, titolo, date) e delega la ricerca a ReservationService. */
    private void searchReservations() {
        Long id = mh.readLong("Identificativo della prenotazione: ", false);
        String name = mh.readString("Nome: ", false);
        String surname = mh.readString("Cognome: ", false);
        String partialTitle = mh.readString("Titolo del film: ", false);
        LocalDate from = mh.readDate("Da data - gg/mm/aaaa: ", false);
        LocalDate to = mh.readDate("A data - gg/mm/aaaa: ", false);
        op.print(SEPARATOR);
        int num = resService.searchAndPrintReservations(id, name, surname, partialTitle, from, to);
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    /**
     * Legge l'id della prenotazione e stampa l'oggetto FullReservationDetails corrispondente
     * o un messaggio d'avviso se assente.
     * */
    private void visualizeReservation() {
        FullReservationDetails result = resService.visualizeReservation(mh.readLong("Identificativo della prenotazione: ", true));
        op.print(SEPARATOR);
        if (result != null)
            op.printlnMarked(result.toString());
        else
            op.println("Nessun risultato trovato.");
    }

    /** Stampa tutte le prenotazioni del giorno corrente. */
    private void visualizeTodayReservations() {
        int num = resService.printTodayReservations();
        if (num > 0) {
            op.print(SEPARATOR);
            op.println("Risultati trovati: " + num + ".");
        } else
            op.println("Nessun risultato trovato.");
    }

    //---------------- ADMIN

    /**
     * Gestisce il primo accesso al sistema. Se l'archivio degli utenti è vuoto,
     * forza la creazione interattiva del primo account admin prima di avviare
     * il ciclo normale. Viene invocata da {@link #start()} come passo
     * preliminare, garantendo che l'applicazione non sia mai avviabile
     * senza un account amministratore.
     */
    private void getAdminPanel() {
        if (authService.isFirstAccess()) {
            op.println(DIVIDER + "Primo accesso. E' necessario creare un account admin.");
            String name = mh.readString("Nome: ", true);
            String surname = mh.readString("Cognome: ", true);
            String residence = mh.readString("Domicilio: ", true);
            LocalDate birthDate = mh.readDate("Data di nascita - gg/mm/aaaa: ", true);
            String username = mh.readString("Username: ", true);
            String password = mh.readString("Password: ", true);
            authService.adminSignUp(username, name, surname, password, birthDate, residence);
            op.print(SEPARATOR);
            op.println("L'applicazione e' pronta.");
        }
    }

    /** Legge lo username e promuove l'utente a PROJECTIONIST tramite Authentication. */
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

    /** Legge lo username e promuove l'utente a BOXOFFICECLERK tramite Authentication. */
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
