package chevalier.vladimir.gmail.com.helpmaster.entities;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EventItem implements Serializable, Comparable<EventItem> {

    public EventItem() {
    }

    private String date;
    private String consumer;
    private String staff;
    private String service;
    private int idService;
    private int discount;
    private String phoneConsumer;
    private int cost;
    private boolean status;
    private double primeCost;

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


    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getPrimeCost() {
        return primeCost;
    }

    public void setPrimeCost(double primeCost) {
        this.primeCost = primeCost;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventItem eventItem = (EventItem) o;

        if (idService != eventItem.idService) return false;
        if (discount != eventItem.discount) return false;
        if (!date.equals(eventItem.date)) return false;
        if (!consumer.equals(eventItem.consumer)) return false;
        if (!staff.equals(eventItem.staff)) return false;
        if (!service.equals(eventItem.service)) return false;
        return phoneConsumer != null ? phoneConsumer.equals(eventItem.phoneConsumer) : eventItem.phoneConsumer == null;
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + consumer.hashCode();
        result = 31 * result + staff.hashCode();
        result = 31 * result + service.hashCode();
        result = 31 * result + idService;
        result = 31 * result + discount;
        result = 31 * result + (phoneConsumer != null ? phoneConsumer.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventItem{" +
                "date='" + date + '\'' +
                ", consumer='" + consumer + '\'' +
                ", staff='" + staff + '\'' +
                ", service='" + service + '\'' +
                ", idService=" + idService +
                ", discount=" + discount +
                ", phoneConsumer='" + phoneConsumer + '\'' +
                ", cost=" + cost +
                ", status=" + status +
                ", primeCost=" + primeCost +
                '}';
    }


    @Override
    public int compareTo(@NonNull EventItem event) {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date convertedDate1 = dateFormat.parse(this.getDate());
            Date conv = dateFormat.parse(event.getDate());
            return convertedDate1.compareTo(conv);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
}