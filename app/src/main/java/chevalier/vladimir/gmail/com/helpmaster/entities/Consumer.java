package chevalier.vladimir.gmail.com.helpmaster.entities;

import java.io.File;
import java.io.Serializable;

/**
 * Created by chevalier on 30.07.17.
 */

public class Consumer implements Serializable {
    public Consumer() {

    }

    private String name;
    private String surname;
    private String pathToPhoto;
    private String phoneNumber;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Consumer consumer = (Consumer) o;

        if (Double.compare(consumer.balance, balance) != 0) return false;
        if (discount != consumer.discount) return false;
        if (!name.equals(consumer.name)) return false;
        if (!surname.equals(consumer.surname)) return false;
        if (pathToPhoto != null ? !pathToPhoto.equals(consumer.pathToPhoto) : consumer.pathToPhoto != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(consumer.phoneNumber) : consumer.phoneNumber != null)
            return false;
        return description != null ? description.equals(consumer.description) : consumer.description == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + (pathToPhoto != null ? pathToPhoto.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        temp = Double.doubleToLongBits(balance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + discount;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

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
