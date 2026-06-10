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

public class CineMax {
    public static final String MARGIN = "        ";
    public static final String MARGIN_END = "      * ";
    public static final int ROW_LENGTH = 76;

    public static void main(String[] args) {
        OutputPrinter op = new OutputPrinter(MARGIN, MARGIN_END, ROW_LENGTH);
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
