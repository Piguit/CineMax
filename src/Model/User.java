package Model;

import java.time.LocalDate;

/**
 * <p>
 * La classe {@code User} si utilizza per rappresentare un utente all'interno
 * del sistema con la memorizzazione dei suoi dati anagrafici e definisce il
 * suo ruolo corrente tramite la classe enumerativa {@link Role}.
 * </p>
 * <p>
 * Implementa le interfacce {@link ItemInitializer} e {@link Identifiable}
 * con il campo {@code username} che rappresenta univocamente l'utente.
 * Comprende dei getters e setters relativi ai campi dell'utente.
 * </p>
 */
public class User implements ItemInitializer<User>, Identifiable<String> {
    private String username;
    private String name;
    private String surname;
    private String password;
    private LocalDate birthDate;
    private String residence;
    private Role role;

    public User() {}

    /**
     * Costruttore utilizzato per istanzare un oggetto {@code User} con i relativi campi.
     * @param username nome utente univoco
     * @param name nome dell'utente
     * @param surname cognome dell'utente
     * @param password password dell'utente
     * @param birthDate data di nascita dell'utente
     * @param residence indirizzo di residenza dell'utente
     * @param role ruolo dell'utente specificato dalla classe enumerativa {@link Role}
     */
    public User(String username, String name, String surname, String password, LocalDate birthDate, String residence, Role role) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.birthDate = birthDate;
        this.residence = residence;
        this.role = role;
    }

    /**
     * Costruttore che istanza un oggetto {@code User} ed esegue il parsing
     * di un array di stringhe contenente i dati dell'utente.
     * <p>
     * L'array di stringhe è ordinato nel seguente modo: <br>
     * [0] username, [1] nome, [2] cognome, [3] password,
     * [4] data di nascita, [5] indirizzo di residenza, [6] ruolo
     * </p>
     * @param array array di stringhe composto dai 7 campi ordinati dell'utente
     */
    public User(String[] array) {
        this.username = array[0];
        this.name = array[1];
        this.surname = array[2];
        this.password = array[3];
        this.birthDate = LocalDate.parse(array[4]);
        this.residence = array[5];
        switch (array[6]) {
            case "CLIENT":
                this.role = Role.CLIENT;
                break;
            case "PROJECTIONIST":
                this.role = Role.PROJECTIONIST;
                break;
            case "BOXOFFICECLERK":
                this.role = Role.BOXOFFICECLERK;
                break;
            default:
                this.role = Role.ADMIN;
                break;
        }
    }
    /**
     * Istanzia e restituisce un nuovo oggetto {@code User}
     * a partire da un array di stringhe contenente i suoi dati.
     * @param fields array di stringhe con i campi ordinati dell'oggetto
     * @return oggetto istanziato del tipo {@code User}
     */
    public User getNewItem(String[] fields) {
        return new User(fields);
    }

    /**
     * Restituisce il nome utente univoco
     * @return nome utente univoco
     */
    public String getId(){
        return username;
    }

    /**
     * Restituisce l'array di stringhe composto da tutti i campi
     * del tipo {@code User}
     * @return array di stringhe dei campi della classe {@code User}
     */
    public String[] getFields(){
        return new String[]{username, name, surname, password, String.valueOf(birthDate), residence, role.name()};
    }

    /**
     * Restituisce il nome dell'utente
     * @return nome dell'utente
     */
    public String getName(){
        return name;
    }

    /**
     * Restituisce il cognome dell'utente
     * @return cognome dell'utente
     */
    public String getSurname(){
        return surname;
    }

    /**
     * Restituisce la password dell'utente
     * @return password dell'utente
     */
    public String getPassword(){
        return password;
    }

    /**
     * Restituisce la data di nascita dell'utente
     * @return data di nascita dell'utente
     */
    public LocalDate getBirthDate(){
        return birthDate;
    }

    /**
     * Restituisce l'indirizzo di residenza dell'utente
     * @return l'indirizzo di residenza dell'utente
     */
    public String getResidence(){
        return residence;
    }

    /**
     * Restituisce il ruolo dell'utente
     * @return ruolo dell'utente
     */
    public Role getRole(){
        return role;
    }

    /** @param username nuovo nome utente da assegnare */
    public void setUsername(String username){
        this.username = username;
    }
    /** @param name nome dell'utente da assegnare*/
    public void setName(String name){
        this.name = name;
    }
    /** @param surname cognome dell'utente da assegnare*/
    public void setSurname(String surname){
        this.surname = surname;
    }
    /** @param password password dell'utente da assegnare*/
    public void setPassword(String password){
        this.password = password;
    }
    /** @param birthDate data di nascita dell'utente da assegnare*/
    public void setBirthDate(LocalDate birthDate){
        this.birthDate = birthDate;
    }
    /** @param residence indirizzo di residenza dell'utente da assegnare*/
    public void setResidence(String residence){
        this.residence = residence;
    }
    /** @param role ruolo da assegnare all'utente */
    public void setRole(Role role){
        this.role = role;
    }
}

