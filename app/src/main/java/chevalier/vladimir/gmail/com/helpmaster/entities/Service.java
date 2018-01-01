package chevalier.vladimir.gmail.com.helpmaster.entities;

import java.io.Serializable;

/**
 * Created by chevalier on 30.07.17.
 */

public class Service implements Serializable {
    public Service() {

    }

    private String nameService;
    private int costService;
    private double firstCostService;
    private int durationService;
    private String descriptionService;

    public String getNameService() {
        return nameService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public int getCostService() {
        return costService;
    }

    public void setCostService(int costService) {
        this.costService = costService;
    }

    public double getFirstCostService() {
        return firstCostService;
    }

    public void setFirstCostService(double firstCostService) {
        this.firstCostService = firstCostService;
    }

    public int getDurationService() {
        return durationService;
    }

    public void setDurationService(int durationService) {
        this.durationService = durationService;
    }

    public String getDescriptionService() {
        return descriptionService;
    }

    public void setDescriptionService(String descriptionService) {
        this.descriptionService = descriptionService;
    }

    @Override
    public String toString() {
        return "Service{" +
                "nameService='" + nameService + '\'' +
                ", costService=" + costService +
                ", firstCostService=" + firstCostService +
                ", durationService=" + durationService +
                ", descriptionService='" + descriptionService + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Service service = (Service) o;

        if (costService != service.costService) return false;
        if (Double.compare(service.firstCostService, firstCostService) != 0) return false;
        if (durationService != service.durationService) return false;
        return nameService.equals(service.nameService);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = nameService.hashCode();
        result = 31 * result + costService;
        temp = Double.doubleToLongBits(firstCostService);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + durationService;
        return result;
    }
}
