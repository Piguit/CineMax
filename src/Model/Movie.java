package Model;

public class Movie implements ItemInitializer<Movie>, Identifiable<Long> {
    private Long movieId;
    private String title;
    private String director;
    private short year;
    private String genre;
    private short runningTime;
    private byte minAge;

    public Movie() {}

    public Movie(String movieId, String title, String director, String year,
                 String genre, String runningTime, String minAge) {
        this.movieId = Long.parseLong(movieId);
        this.title = title;
        this.director = director;
        this.year = Short.parseShort(year);
        this.genre = genre;
        this.runningTime = Short.parseShort(runningTime);
        this.minAge = Byte.parseByte(minAge);
    }

    public Movie(String[] array) {
        this.movieId = Long.parseLong(array[0]);
        this.title = array[1];
        this.director = array[2];
        this.year = Short.parseShort(array[3]);
        this.genre = array[4];
        this.runningTime = Short.parseShort(array[5]);
        this.minAge = Byte.parseByte(array[6]);
    }

    public Movie getNewItem(String[] array) {
        return new Movie(array);
    }

    public Long getId() {
        return movieId;
    }

    public String[] getFields() {
        return new String[]{String.valueOf(movieId), title, director, String.valueOf(year),
            genre, String.valueOf(runningTime), String.valueOf(minAge)};
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public short getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public short getRunningTime() {
        return runningTime;
    }

    public byte getMinAge() {
        return minAge;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setRunningTime(short runningTime) {
        this.runningTime = runningTime;
    }

    public void setMinAge(byte minAge) {
        this.minAge = minAge;
    }

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