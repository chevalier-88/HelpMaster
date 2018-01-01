package chevalier.vladimir.gmail.com.helpmaster.entities;

import java.io.File;
import java.io.Serializable;

/**
 * Created by chevalier on 30.07.17.
 */

public class Consumer implements Serializable{
    public Consumer() {

    }

    private String name;
    private String surname;
    private String pathToPhoto;
    private String phoneNumber;
//    private File photo;
    private double balance;
    private int discount;
    private String description;

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

    public String getPathToPhoto() {
        return pathToPhoto;
    }

    public void setPathToPhoto(String pathToPhoto) {
        this.pathToPhoto = pathToPhoto;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public File getPhoto() {
//        return photo;
//    }

//    public void setPhoto(File photo) {
//        this.photo = photo;
//    }

    @Override
    public String toString() {
        return "Consumer{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", pathToPhoto='" + pathToPhoto + '\'' +
                ", balance=" + balance +
                ", discount=" + discount +
                ", description='" + description + '\'' +
                '}';
    }
}
