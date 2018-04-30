package chevalier.vladimir.gmail.com.helpmaster.entities;

/**
 * Created by chevalier on 21.09.17.
 */

public class SalaryItem {
    public SalaryItem() {
    }

    private String date;
    private String serviceName;
    private String consumerName;
    private Double sum;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SalaryItem that = (SalaryItem) o;

        if (!date.equals(that.date)) return false;
        if (!serviceName.equals(that.serviceName)) return false;
        if (!consumerName.equals(that.consumerName)) return false;
        return sum.equals(that.sum);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + serviceName.hashCode();
        result = 31 * result + consumerName.hashCode();
        result = 31 * result + sum.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SalaryItem{" +
                "date='" + date + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", consumerName='" + consumerName + '\'' +
                ", sum=" + sum +
                '}';
    }
}
