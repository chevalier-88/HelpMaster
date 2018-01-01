package chevalier.vladimir.gmail.com.helpmaster.entities;

/**
 * Created by chevalier on 21.09.17.
 */

public class SalaryItem {
    public SalaryItem() {
    }

//    private int idItem;
    private String date;
    private String serviceName;
    private String consumerName;
    private Double sum;

//    public int getIdItem() {
//        return idItem;
//    }

//    public void setIdItem(int idItem) {
//        this.idItem = idItem;
//    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }
}
