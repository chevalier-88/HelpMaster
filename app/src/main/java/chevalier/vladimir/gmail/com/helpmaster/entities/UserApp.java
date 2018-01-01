package chevalier.vladimir.gmail.com.helpmaster.entities;

/**
 * Created by chevalier on 30.07.17.
 */

public class UserApp {
    public UserApp() {
    }

    private String name;
    private String surname;
    private String phone;
    private String Email;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
