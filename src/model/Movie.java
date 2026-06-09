package model;

/**
 * <p>
 * La classe {@code Movie} si utilizza per rappresentare un film all'interno
 * del sistema con la memorizzazione dei suoi dati.
 * </p>
 * <p>
 * Implementa le interfacce {@link ItemInitializer} e {@link Identifiable}
 * con il campo {@code movieId} che rappresenta univocamente il film.
 * Comprende dei getters e setters relativi ai campi del film.
 * </p>
 * @author
 */
public class Movie implements ItemInitializer<Movie>, Identifiable<Long> {
    private long movieId;
    private String title;
    private String director;
    private short year;
    private String genre;
    private short runningTime;
    private byte minAge;

    public Movie() {}

    /**
     * Costruttore utilizzato per istanziare un oggetto {@code Movie} di un film con i relativi campi.
     * @param movieId id del film univoco
     * @param title titolo del film
     * @param director regista del film
     * @param year anno di uscita del film
     * @param genre genere del film
     * @param runningTime durata del film
     * @param minAge età minima richiesta per la visione del film
     */
    public Movie(Long movieId, String title, String director, Short year,
                 String genre, Short runningTime, Byte minAge) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genre = genre;
        this.runningTime = runningTime;
        this.minAge = minAge;
    }
    /**
     * Costruttore che istanza un oggetto {@code Movie} ed esegue il parsing
     * di un array di stringhe contenente i dati del film.
     * <p>
     * L'array di stringhe è ordinato nel seguente modo: <br>
     * [0] id film, [1] titolo, [2] regista, [3] anno di uscita,
     * [4] genere, [5] durata, [6] età minima
     * </p>
     * @param array array di stringhe composto dai 7 campi ordinati del film
     */
    public Movie(String[] array) {
        this.movieId = Long.parseLong(array[0]);
        this.title = array[1];
        this.director = array[2];
        this.year = Short.parseShort(array[3]);
        this.genre = array[4];
        this.runningTime = Short.parseShort(array[5]);
        this.minAge = Byte.parseByte(array[6]);
    }

    /**
     * Istanzia e restituisce un nuovo oggetto {@code Movie}
     * a partire da un array di stringhe contenente i suoi dati.
     * @param array array di stringhe con i campi ordinati dell'oggetto
     * @return oggetto istanziato del tipo {@code Movie}
     */
    public Movie getNewItem(String[] array) {
        return new Movie(array);
    }

    /**
     * Restituisce l'id univoco del film
     * @return id univoco del film
     */
    public Long getId() {
        return movieId;
    }

    /**
     * Restituisce l'array di stringhe composto da tutti i campi
     * del tipo {@code Movie}
     * @return array di stringhe della classe {@code Movie}
     */
    public String[] getFields() {
        return new String[]{String.valueOf(movieId), title, director, String.valueOf(year),
            genre, String.valueOf(runningTime), String.valueOf(minAge)};
    }

    /**
     * Restituisce il titolo del film
     * @return titolo del film
     */
    public String getTitle() {
        return title;
    }

    /**
     * Restituisce il regista del film
     * @return regista del film
     */
    public String getDirector() {
        return director;
    }

    /**
     * Restituisce anno di uscita del film
     * @return anno di uscita del film
     */
    public Short getYear() {
        return year;
    }

    /**
     * Restituisce il genere del film
     * @return genere del film
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Restituisce la durata del film
     * @return durata del film
     */
    public Short getRunningTime() {
        return runningTime;
    }

    /**
     * Restituisce l'età minima richiesta del film
     * @return età minima richiesta del film
     */
    public Byte getMinAge() {
        return minAge;
    }
    /** @param movieId id del film da assegnare*/
    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }
    /** @param title titolo del film da assegnare*/
    public void setTitle(String title) {
        this.title = title;
    }
    /** @param director regista del film da assegnare*/
    public void setDirector(String director) {
        this.director = director;
    }
    /** @param year anno di uscita del film da assegnare*/
    public void setYear(short year) {
        this.year = year;
    }
    /** @param genre genere del film da assegnare*/
    public void setGenre(String genre) {
        this.genre = genre;
    }
    /** @param runningTime durata del film da assegnare*/
    public void setRunningTime(short runningTime) {
        this.runningTime = runningTime;
    }
    /** @param minAge età minima del film da assegnare*/
    public void setMinAge(byte minAge) {
        this.minAge = minAge;
    }

    /**
     * Restituisce una descrizione testuale delle informazioni del film
     * con la concatenazione di tutti i campi divisi dal separatore '|'.
     * @return stringa contenente i dati del film nell'ordine di memorizzazione
     */
    public String toString() {
        String result = "";
        result += movieId + " | ";
        result += title + " | ";
        result += director + " | ";
        result += year + " | ";
        result += genre + " | ";
        result += runningTime + " | ";
        result += minAge;
        return result;
    }
}