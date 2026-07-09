package cinemax;

import repository.MovieRepository;
import repository.ReservationRepository;
import repository.ShowRepository;
import repository.UserRepository;
import service.Authentication;
import service.MovieService;
import service.ReservationService;
import service.ShowService;
import textuserinterface.TextUserInterface;
import utility.SafetyException;
import repository.FileException;
import utility.OutputPrinter;

/**
 * Classe che contiene il metodo main. Possiede dei campi final
 * che centralizzano il controllo del formato di output dell'intera
 * applicazione.
 * @author Piergiorgio Tomaciello 761013
 * CO
 */
public class CineMax {
    /** Il margine standard da anteporre alle stringhe stampate. */
    static final String MARGIN = "        ";
    /**
     * Il margine speciale da anteporre alle stringhe in un elenco
     * di opzioni o risultati.
    */
    static final String MARKER = "      * ";
    /** Lunghezza massima di caratteri stampabili per riga (non considera la lunghezza dei margini) */
    static final int ROW_LENGTH = 76;

    /**
     * Metodo main che istanzia tutti i componenti necessari, applicando
     * le opportune dependency-injections e avviando infine l'interfaccia
     * testuale dell'applicazione.
     * @param args array di stringhe in chiamata al programma; l'applicazione non ne fa uso
     */
    public static void main(String[] args) {
        OutputPrinter op = new OutputPrinter(MARGIN, MARKER, ROW_LENGTH);
        try {
            UserRepository uRepo = new UserRepository();
            MovieRepository mRepo = new MovieRepository();
            ShowRepository sRepo = new ShowRepository();
            ReservationRepository rRepo = new ReservationRepository();

            Authentication authService = new Authentication(uRepo);
            MovieService mService = new MovieService(mRepo, op);
            ShowService sService = new ShowService(sRepo, rRepo, mRepo, op);
            ReservationService rService = new ReservationService(rRepo, sRepo, uRepo, mRepo, op);
            
            TextUserInterface tui = new TextUserInterface(authService, mService, sService, rService, op);
            tui.start();
        } catch (FileException | SafetyException e) {
            op.println(e.getMessage());
        }
    }    
}
