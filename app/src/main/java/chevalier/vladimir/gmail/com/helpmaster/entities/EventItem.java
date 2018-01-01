package chevalier.vladimir.gmail.com.helpmaster.entities;

import java.io.Serializable;

/**
 * Created by chevalier on 20.08.17.
 */

public class EventItem implements Serializable {

    public EventItem() {
    }

    private String date;
    private String consumer;
    private String staff;
    private String service;
    private int idService;
    private int discount;
    private String phoneConsumer;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getPhoneConsumer() {
        return phoneConsumer;
    }

    public void setPhoneConsumer(String phoneConsumer) {
        this.phoneConsumer = phoneConsumer;
    }

    @Override
    public String toString() {
        return "EventItem{" +
                "date='" + date + '\'' +
                ", consumer='" + consumer + '\'' +
                ", staff='" + staff + '\'' +
                ", service='" + service + '\'' +
                ", discount=" + discount +
                '}';
    }
}
