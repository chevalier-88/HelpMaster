package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;


public class LocalSQLiteStorage extends SQLiteOpenHelper {
    private Context context;
    private ContentValues cv;

    public LocalSQLiteStorage(Context context) {
        super(context, context.getResources().getString(R.string.database_name), null, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getResources().getString(R.string.create_table_event_items));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//NOP
    }


    public void insertEventItem(EventItem item) {
        cv = new ContentValues();

        cv.put(context.getResources().getString(R.string.date), item.getDate().trim());
        cv.put(context.getResources().getString(R.string.consumer), item.getConsumer().trim());
        cv.put(context.getResources().getString(R.string.staff), item.getStaff().trim());
        cv.put(context.getResources().getString(R.string.service), item.getService());
        cv.put(context.getResources().getString(R.string.id_service), item.getIdService());
        cv.put(context.getResources().getString(R.string.discount), item.getDiscount());
        cv.put(context.getResources().getString(R.string.phone_consumer), item.getPhoneConsumer());
        cv.put(context.getResources().getString(R.string.cost), item.getCost());
        cv.put(context.getResources().getString(R.string.status), item.isStatus());
        cv.put(context.getResources().getString(R.string.prime_cost), item.getPrimeCost());

        this.getReadableDatabase().insert(context.getResources().getString(R.string.table_name), null, cv);
        cv.clear();
        cv = null;
        this.close();
    }


    public void insertEventItems(List<EventItem> items) {
        cv = new ContentValues();
        for (EventItem item : items) {
            cv.put(context.getResources().getString(R.string.date), item.getDate().trim());
            cv.put(context.getResources().getString(R.string.consumer), item.getConsumer().trim());
            cv.put(context.getResources().getString(R.string.staff), item.getStaff().trim());
            cv.put(context.getResources().getString(R.string.service), item.getService());
            cv.put(context.getResources().getString(R.string.id_service), item.getIdService());
            cv.put(context.getResources().getString(R.string.discount), item.getDiscount());
            cv.put(context.getResources().getString(R.string.phone_consumer), item.getPhoneConsumer());
            cv.put(context.getResources().getString(R.string.cost), item.getCost());
            cv.put(context.getResources().getString(R.string.status), item.isStatus());
            cv.put(context.getResources().getString(R.string.prime_cost), item.getPrimeCost());
            this.getReadableDatabase().insert(context.getResources().getString(R.string.table_name), null, cv);
        }
        cv.clear();
        cv = null;
        this.close();
    }

    public List<EventItem> readTableEventItems() {
        List<EventItem> result = new LinkedList<>();
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_name), null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int iDate = c.getColumnIndex(context.getResources().getString(R.string.date));
            int iConsumer = c.getColumnIndex(context.getResources().getString(R.string.consumer));
            int iStaff = c.getColumnIndex(context.getResources().getString(R.string.staff));
            int iService = c.getColumnIndex(context.getResources().getString(R.string.service));
            int iIdService = c.getColumnIndex(context.getResources().getString(R.string.id_service));
            int iDiscount = c.getColumnIndex(context.getResources().getString(R.string.discount));
            int iPhoneConsumer = c.getColumnIndex(context.getResources().getString(R.string.phone_consumer));
            int iCost = c.getColumnIndex(context.getResources().getString(R.string.cost));
            int iStatus = c.getColumnIndex(context.getResources().getString(R.string.status));
            int iPrimeCost = c.getColumnIndex(context.getResources().getString(R.string.prime_cost));
            do {
                String date = c.getString(iDate);
                String consumer = c.getString(iConsumer);
                String staff = c.getString(iStaff);
                String service = c.getString(iService);
                int idService = c.getInt(iIdService);
                int discount = c.getInt(iDiscount);
                String phoneConsumer = c.getString(iPhoneConsumer);
                int cost = c.getInt(iCost);
                boolean status = Boolean.parseBoolean(c.getString(iStatus));
                double primeCost = c.getDouble(iPrimeCost);
                EventItem event = new EventItem();
                event.setDate(date);
                event.setConsumer(consumer);
                event.setStaff(staff);
                event.setService(service);
                event.setIdService(idService);
                event.setDiscount(discount);
                event.setPhoneConsumer(phoneConsumer);
                event.setCost(cost);
                event.setStatus(status);
                event.setPrimeCost(primeCost);
                result.add(event);
            } while (c.moveToNext());
            c.close();
            return result;
        } else {
            c.close();
            return result;
        }
    }

    public void deleteEventItem(EventItem item) {

        this.getReadableDatabase().delete(context.getResources().getString(R.string.table_name),
                context.getResources().getString(R.string.date) + " = \'" + item.getDate() + "\' AND "
                        + context.getResources().getString(R.string.consumer) + " =\'" + item.getConsumer() + "\' AND " +
                        context.getResources().getString(R.string.service) + " = \'" + item.getService() + "\'", null);
    }

    public void updateEvent(List<EventItem> locList) {
        cv = new ContentValues();

        List<EventItem> dbList = this.readTableEventItems();

        List<EventItem> newItems = new LinkedList<>();

        for (EventItem i : locList) {
            if (!dbList.contains(i)) {
                EventItem item = new EventItem();
                item.setDate(i.getDate());
                item.setConsumer(i.getConsumer());
                item.setStaff(i.getStaff());
                item.setService(i.getService());
                item.setIdService(i.getIdService());
                item.setPhoneConsumer(i.getPhoneConsumer());
                item.setDiscount(i.getDiscount());
                item.setCost(i.getCost());
                item.setPrimeCost(i.getPrimeCost());
                item.setStatus(i.isStatus());
                newItems.add(item);
            }
        }
        String date = context.getResources().getString(R.string.date);
        String consumer = context.getResources().getString(R.string.consumer);
        String staff = context.getResources().getString(R.string.staff);
        String service = context.getResources().getString(R.string.service);
        String idService = context.getResources().getString(R.string.id_service);
        String discount = context.getResources().getString(R.string.discount);
        String phoneConsumer = context.getResources().getString(R.string.phone_consumer);
        String cost = context.getResources().getString(R.string.cost);
        String status = context.getResources().getString(R.string.status);
        String primeConst = context.getResources().getString(R.string.prime_cost);

        int j = 0;

        for (int i = 0; i < dbList.size(); i++) {
            if (!locList.contains(dbList.get(i))) {
                EventItem newEvent = newItems.get(j++);
                cv.put(date, newEvent.getDate());
                cv.put(consumer, newEvent.getConsumer());
                cv.put(staff, newEvent.getStaff());
                cv.put(service, newEvent.getService());
                cv.put(idService, newEvent.getIdService());
                cv.put(discount, newEvent.getDiscount());
                cv.put(phoneConsumer, newEvent.getPhoneConsumer());
                cv.put(cost, newEvent.getCost());
                cv.put(status, newEvent.isStatus());
                cv.put(primeConst, newEvent.getPrimeCost());

                this.getReadableDatabase().update(context.getResources().getString(R.string.table_name),
                        cv, context.getResources().getString(R.string.date) + " = \"" + dbList.get(i).getDate().trim() + "\" AND " +
                                context.getResources().getString(R.string.consumer) + " = \"" + dbList.get(i).getConsumer().trim() + "\" AND " +
                                context.getResources().getString(R.string.service) + " = \"" + dbList.get(i).getService() + "\"", null);
            }
        }
        cv.clear();
        cv = null;
        this.close();
    }

    public void cleanDB() {
        this.getReadableDatabase().execSQL(context.getResources().getString(R.string.drop_table));
    }
}