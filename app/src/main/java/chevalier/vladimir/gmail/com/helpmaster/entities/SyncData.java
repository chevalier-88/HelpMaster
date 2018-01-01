package chevalier.vladimir.gmail.com.helpmaster.entities;

import java.io.Serializable;

/**
 * Created by chevalier on 28.11.17.
 */

public class SyncData implements Serializable {
    public SyncData() {
    }

    private String date;
    private String userName;
    private String tableName;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "SyncData{" +
                "date='" + date + '\'' +
                ", userName='" + userName + '\'' +
                ", tableName='" + tableName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyncData syncData = (SyncData) o;

        if (date != null ? !date.equals(syncData.date) : syncData.date != null) return false;
        if (userName != null ? !userName.equals(syncData.userName) : syncData.userName != null)
            return false;
        return tableName != null ? tableName.equals(syncData.tableName) : syncData.tableName == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
//    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//    Date date = new Date();
//   String strDate =  dateFormat.format(date);
