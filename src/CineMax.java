import Repository.MovieRepository;
import Repository.ReservationRepository;
import Repository.ShowRepository;
import Repository.UserRepository;
import Service.Authentication;
import Service.MovieService;
import Service.ReservationService;
import Service.ShowService;
import TUInterface.TextUserInterface;
import Utility.SafetyException;
import Repository.FileException;

public class CineMax {
    public static void main(String[] args){
        try {
            UserRepository uRepo = new UserRepository();
            MovieRepository mRepo = new MovieRepository();
            ShowRepository sRepo = new ShowRepository();
            ReservationRepository rRepo = new ReservationRepository();
            Authentication authService = new Authentication(uRepo);
            MovieService mService = new MovieService(mRepo);
            ShowService sService = new ShowService(sRepo, rRepo, mRepo);
            ReservationService rService = new ReservationService(rRepo, sRepo, uRepo, mRepo);
            TextUserInterface tui = new TextUserInterface(authService, mService, sService, rService);
            tui.getAdminPanel();
            tui.start();
        } catch (FileException e) {
            System.out.println(e.getMessage());
        } catch (SafetyException e) {
            System.out.println(e.getMessage());
        }
    }    
}
