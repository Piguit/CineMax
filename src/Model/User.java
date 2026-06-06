package Model;

import java.time.LocalDate;

public class User implements ItemInitializer<User>, Identifiable<String> {
    private String username;
    private String name;
    private String surname;
    private String password;
    private LocalDate birthDate;
    private String residence;
    private Role role;

    public User() {}

    public User(String username, String name, String surname, String password, LocalDate birthDate, String residence, Role role) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.birthDate = birthDate;
        this.residence = residence;
        this.role = role;
    }

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

    public User getNewItem(String[] fields) {
        return new User(fields);
    }

    public String getId(){
        return username;
    }

    public String[] getFields(){
        return new String[]{username, name, surname, password, String.valueOf(birthDate), residence, role.name()};
    }

    public String getName(){
        return name;
    }
    public String getSurname(){
        return surname;
    }
    public String getPassword(){
        return password;
    }
    public LocalDate getBirthDate(){
        return birthDate;
    }
    public String getResidence(){
        return residence;
    }
    public Role getRole(){
        return role;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setSurname(String surname){
        this.surname = surname;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setBirthDate(LocalDate birthDate){
        this.birthDate = birthDate;
    }
    public void setResidence(String residence){
        this.residence = residence;
    }
    public void setRole(Role role){
        this.role = role;
    }
}

