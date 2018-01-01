package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;
import chevalier.vladimir.gmail.com.helpmaster.entities.SyncData;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;

/**
 * Created by chevalier on 03.08.17.
 */

public class LocalSqliteHelper extends SQLiteOpenHelper {

    private Context context;
    private ContentValues cv;


    public LocalSqliteHelper(Context context) {
        super(context, context.getResources().getString(R.string.database_name),
                null, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getResources().getString(R.string.create_table_userapp));
        db.execSQL(context.getResources().getString(R.string.create_table_services));
        db.execSQL(context.getResources().getString(R.string.create_table_reg_consumers));
        db.execSQL(context.getResources().getString(R.string.create_table_events));
        db.execSQL(context.getResources().getString(R.string.create_table_synchronization));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean doesDatabaseExists(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }


    public boolean writeNewUserApp(UserApp user) {
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_userapp), null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int indexColumnPhone = c.getColumnIndex(context.getResources().getString(R.string.userapp_column_phone));
            int indexColumnMail = c.getColumnIndex(context.getResources().getString(R.string.userapp_column_email));

            do {

                String phone = c.getString(indexColumnPhone);
                String mail = c.getString(indexColumnMail);

                if (((phone != null) && (phone.length() > 0) && (!phone.equals(user.getPhone()))) &&
                        ((mail != null) && (mail.length() > 8) && (!mail.equals(user.getEmail())))) {

                    cv = new ContentValues();

                    String columnName = context.getResources().getString(R.string.userapp_column_name);
                    String columnSurname = context.getResources().getString(R.string.userapp_column_surname);
                    String columnPhone = context.getResources().getString(R.string.userapp_column_phone);
                    String columnMail = context.getResources().getString(R.string.userapp_column_email);
                    String columnPassword = context.getResources().getString(R.string.userapp_column_password);

                    cv.put(columnName, user.getName());
                    cv.put(columnSurname, user.getSurname());
                    cv.put(columnPhone, user.getPhone());
                    cv.put(columnMail, user.getEmail());
                    cv.put(columnPassword, user.getPassword());

                    this.getReadableDatabase().insert(context.getResources().getString(R.string.table_userapp), null, cv);

                    cv.clear();
                    cv = null;
                    c.close();
                    this.close();

                    return true;
                }
            } while (c.moveToNext());
            cv.clear();
            cv = null;
            c.close();
            this.close();
            return false;
        } else {

            cv = new ContentValues();
            String name = context.getResources().getString(R.string.userapp_column_name);
            String surname = context.getResources().getString(R.string.userapp_column_surname);
            String phone = context.getResources().getString(R.string.userapp_column_phone);
            String mail = context.getResources().getString(R.string.userapp_column_email);
            String password = context.getResources().getString(R.string.userapp_column_password);
            cv.put(name, user.getName());
            cv.put(surname, user.getSurname());
            cv.put(phone, user.getPhone());
            cv.put(mail, user.getEmail());
            cv.put(password, user.getPassword());
            this.getReadableDatabase().insert(context.getResources().getString(R.string.table_userapp), null, cv);

            cv.clear();
            cv = null;
            c.close();
            this.close();

            return true;
        }
    }


    public UserApp getUserApp(String mail, String password) {
        UserApp result;
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_userapp), null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int indexExistsMail = c.getColumnIndex(context.getResources().getString(R.string.userapp_column_email));
            int indexExistsPassword = c.getColumnIndex(context.getResources().getString(R.string.userapp_column_password));

            do {

                String existsMail = c.getString(indexExistsMail);
                String existsPassword = c.getString(indexExistsPassword);
                if (existsMail.equals(mail) && existsPassword.equals(password)) {
                    result = new UserApp();
                    result.setName(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_name))));
                    result.setSurname(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_surname))));
                    result.setPhone(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_phone))));
                    result.setEmail(existsMail);
                    c.close();
                    return result;
                }

            } while (c.moveToNext());
            c.close();
            return null;
        } else {
            c.close();
            return null;
        }
    }


    public UserApp getCurrentUserApp(String mail) {
        UserApp result;
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_userapp), null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int indexExistsMail = c.getColumnIndex(context.getResources().getString(R.string.userapp_column_email));

            do {

                String existsMail = c.getString(indexExistsMail);
                if (existsMail.equals(mail)) {
                    result = new UserApp();
                    result.setName(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_name))));
                    result.setSurname(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_surname))));
                    result.setPhone(c.getString(c.getColumnIndex(context.getResources().getString(R.string.userapp_column_phone))));
                    result.setEmail(existsMail);
                    c.close();
                    return result;
                }

            } while (c.moveToNext());
            c.close();
            return null;
        } else {
            c.close();
            return null;
        }
    }


    public void updatePasswordCurrentUserApp(String mailCurrentUserApp, String newPassword) {

        String columnPasswordUserApp = context.getResources().getString(R.string.userapp_column_password);
        cv = new ContentValues();
        cv.put(columnPasswordUserApp, newPassword);


        this.getReadableDatabase().update(context.getResources().getString(R.string.table_userapp),
                cv, context.getResources().getString(R.string.userapp_column_email) + " = \'" + mailCurrentUserApp + "\'", null);
        cv.clear();
        cv = null;
        this.close();
    }

    public void writeNewService(Service service) {

        cv = new ContentValues();

        String columnName = context.getResources().getString(R.string.services_column_name);
        String columnDuration = context.getResources().getString(R.string.services_column_duration);
        String columnCost = context.getResources().getString(R.string.services_column_cost);
        String columnFirstCost = context.getResources().getString(R.string.services_column_first_cost);
        String columnDescription = context.getResources().getString(R.string.services_column_description);

        cv.put(columnName, service.getNameService());
        cv.put(columnDuration, service.getDurationService());
        cv.put(columnCost, service.getCostService());
        cv.put(columnFirstCost, service.getFirstCostService());
        cv.put(columnDescription, service.getDescriptionService());

        this.getReadableDatabase().insert(context.getResources().getString(R.string.table_services), null, cv);

        cv.clear();
        cv = null;
        this.close();
        //./.

//            if ((service.getNameService() != null && !service.getNameService().equals("")) &&
//                    (service.getDurationService() != null && !service.getDurationService().equals(""))
//                    && (service.getCostServite() != 0.0)) {
//                ref.child("users").child(mAuth.getCurrentUser().getUid()).child("services").child(service.getNameService()).setValue(service);
//            } else {
//                Toast.makeText(context, "fields is empty!!!", Toast.LENGTH_SHORT).show();
//            }

        //./.
    }

    public void editService(String keyName, Service service) {

        cv = new ContentValues();

        String columnNameService = context.getResources().getString(R.string.services_column_name);
        String columnDurationService = context.getResources().getString(R.string.services_column_duration);
        String columnCostService = context.getResources().getString(R.string.services_column_cost);
        String columnFirstCostService = context.getResources().getString(R.string.services_column_first_cost);
        String columnDescriptionService = context.getResources().getString(R.string.services_column_description);

        cv.put(columnNameService, service.getNameService());
        cv.put(columnDurationService, service.getDurationService());
        cv.put(columnCostService, service.getCostService());
        cv.put(columnFirstCostService, service.getFirstCostService());
        System.out.println("+++++++++++++++++++++++++++ firstCost - " + service.getFirstCostService());
        cv.put(columnDescriptionService, service.getDescriptionService());

        this.getReadableDatabase().update(context.getResources().getString(R.string.table_services),
                cv, context.getResources().getString(R.string.services_column_name) + " = \'" + keyName + "\'", null);
        cv.clear();
        cv = null;
        this.close();
    }

    public List getListServicesName() {
        List<String> result = new ArrayList<>();
        result.add(context.getResources().getString(R.string.select_service));
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_services), null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int indexServiceName = c.getColumnIndex(context.getResources().getString(R.string.services_column_name));

            do {

                String service = c.getString(indexServiceName);
                result.add(service);

            } while (c.moveToNext());
            c.close();
        }
        return result;
    }

    public List getListServices() {
        List<Service> result = new ArrayList<>();
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_services), null, null, null, null, null, null);

//        if (c.moveToFirst()) {
//
//            int indexServiceName = c.getColumnIndex(context.getResources().getString(R.string.services_column_name));
//            int indexServiceCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_cost));
//            int indexServiceFirstCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_first_cost));
//            int indexServiceDuration = c.getColumnIndex(context.getResources().getString(R.string.services_column_duration));
//            int indexServiceDescription = c.getColumnIndex(context.getResources().getString(R.string.services_column_description));
//
//            do {
//
//                String serviceName = c.getString(indexServiceName);
//                int serviceCost = c.getInt(indexServiceCost);
//                double serviceFirstCost = c.getDouble(indexServiceFirstCost);
//                int serviceDuration = c.getInt(indexServiceDuration);
//                String serviceDescription = c.getString(indexServiceDescription);
//                Service service = new Service();
//                service.setNameService(serviceName);
//                service.setCostService(serviceCost);
//                service.setFirstCostService(serviceFirstCost);
//                service.setDurationService(serviceDuration);
//                service.setDescriptionService(serviceDescription);
//
//                result.add(service);
//
//            } while (c.moveToNext());
//            c.close();
//            return result;
//        } else {
//            c.close();
//            return null;
//        }


//        ..ю.ю.ю.ю.ю..ю.ю.ю.ю.ю.ю..юю.ю.ю.ю.ю
        if (c.moveToFirst()) {

            int indexServiceName = c.getColumnIndex(context.getResources().getString(R.string.services_column_name));
            int indexServiceCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_cost));
            int indexServiceFirstCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_first_cost));
            int indexServiceDuration = c.getColumnIndex(context.getResources().getString(R.string.services_column_duration));
            int indexServiceDescription = c.getColumnIndex(context.getResources().getString(R.string.services_column_description));

            do {

                String serviceName = c.getString(indexServiceName);
                int serviceCost = c.getInt(indexServiceCost);
                double serviceFirstCost = c.getDouble(indexServiceFirstCost);
                int serviceDuration = c.getInt(indexServiceDuration);
                String serviceDescription = c.getString(indexServiceDescription);
                Service service = new Service();
                service.setNameService(serviceName);
                service.setCostService(serviceCost);
                service.setFirstCostService(serviceFirstCost);

                service.setDurationService(serviceDuration);
                service.setDescriptionService(serviceDescription);

                result.add(service);

            } while (c.moveToNext());
            c.close();
            return result;
        } else {
            c.close();
            return null;
        }

//        if (dataSnapshot.exists() && dataSnapshot.getChildren().iterator().hasNext()) {
//            List<BookkeeperService> array = new ArrayList<>();
//            BookkeeperService ie;
////            Iterable<DataSnapshot> localSnap = dataSnapshot.getChildren().iterator().next().
////                    getChildren().iterator().next().getChildren().iterator().next().getChildren();
//            Iterable<DataSnapshot> localSnap = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("services").getChildren();
//            for (DataSnapshot dataS : localSnap) {
//                ie = new BookkeeperService(dataS.getValue(BookkeeperService.class).getNameService().toString(),
//                        dataS.getValue(BookkeeperService.class).getDurationService().toString(),
//                        Double.valueOf(dataS.getValue(BookkeeperService.class).getCostServite()),
//                        dataS.getValue(BookkeeperService.class).getDescriptionService().toString());
//                array.add(ie);
//            }
//            ServicesListAdapter adapter = new ServicesListAdapter(getContext(), R.layout.custom_item_service, array);//..//..//..//..//..//
//            listViewServices.setAdapter(adapter);
//    }

    }

    public Service getService(String serviceName) {
        Cursor c = this.getReadableDatabase().query(context.getResources().getString(R.string.table_services), null, null, null, null, null, null);

        if (c.moveToFirst()) {

            int indexServiceName = c.getColumnIndex(context.getResources().getString(R.string.services_column_name));
            int indexServiceCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_cost));
            int indexServiceFirstCost = c.getColumnIndex(context.getResources().getString(R.string.services_column_first_cost));
            int indexServiceDuration = c.getColumnIndex(context.getResources().getString(R.string.services_column_duration));
            int indexServiceDescription = c.getColumnIndex(context.getResources().getString(R.string.services_column_description));

            do {
                if (serviceName.equals(c.getString(indexServiceName))) {

                    int serviceCost = c.getInt(indexServiceCost);
                    double serviceFirstCost = c.getDouble(indexServiceFirstCost);
                    int serviceDuration = c.getInt(indexServiceDuration);
                    String serviceDescription = c.getString(indexServiceDescription);

                    Service service = new Service();
                    service.setNameService(serviceName);
                    service.setCostService(serviceCost);
                    service.setFirstCostService(serviceFirstCost);
                    System.out.println(serviceFirstCost + " ++++++++++++++++++++++ inside getService");
                    service.setDurationService(serviceDuration);
                    service.setDescriptionService(serviceDescription);

                    return service;
                }
            } while (c.moveToNext());
            c.close();
            return null;
        } else {
            c.close();
            return null;
        }
    }

    public void deleteService(String keyName) {
        this.getReadableDatabase().delete(context.getResources().getString(R.string.table_services),
                context.getResources().getString(R.string.services_column_name) + " = \'" + keyName + "\'", null);
    }

    public void writeNewEvent(EventItem event) {

        cv = new ContentValues();

        String columnDate = context.getResources().getString(R.string.events_column_date);
        String columnConsumer = context.getResources().getString(R.string.events_column_consumer);
        String columStaff = context.getResources().getString(R.string.events_column_id_userapp);//int
        String columnService = context.getResources().getString(R.string.events_column_id_service);//int
        String columnDiscount = context.getResources().getString(R.string.events_column_discount);//int
        String columnPhoneConsumer = context.getResources().getString(R.string.events_column_phone_number);

        cv.put(columnDate, event.getDate());
        cv.put(columnConsumer, event.getConsumer());
        cv.put(columStaff, event.getStaff());
        cv.put(columnService, event.getIdService());
        cv.put(columnDiscount, event.getDiscount());
        if (event.getPhoneConsumer().length() != 0)

            cv.put(columnPhoneConsumer, event.getPhoneConsumer());

        this.getReadableDatabase().insert(context.getResources().getString(R.string.table_events), null, cv);

        cv.clear();
        cv = null;
        this.close();
    }

    public List<EventItem> getListEvents() {
//        this.getReadableDatabase().execSQL(context.getResources().getString(R.string.delete_table_synchronization));
        List<EventItem> result = new ArrayList<>();
        Cursor cursorTableEvents = this.getReadableDatabase().query(context.getResources().getString(R.string.table_events), null, null, null, null, null, null);

        if (cursorTableEvents.moveToFirst()) {

            int indexEventDate = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_date));
            int indexEventConsumer = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_consumer));
            int indexEventStaff = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_id_userapp));
            int indexEventService = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_id_service));
            int indexEventDiscount = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_discount));
            int indexEventPhoneNumber = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_phone_number));

            List<Service> services = this.getListServices();

            do {

                int idService = cursorTableEvents.getInt(indexEventService);
                int discount = cursorTableEvents.getInt(indexEventDiscount);
                String date = cursorTableEvents.getString(indexEventDate);
                String consumer = cursorTableEvents.getString(indexEventConsumer);
                String staff = cursorTableEvents.getString(indexEventStaff);
                String phoneConsumer = cursorTableEvents.getString(indexEventPhoneNumber);

                EventItem event = new EventItem();
                event.setDate(date);
                event.setConsumer(consumer);
                event.setStaff(staff);
                event.setService(services.get(idService - 1).getNameService());
                event.setIdService(idService);
                event.setDiscount(discount);
                event.setPhoneConsumer(phoneConsumer);

                result.add(event);

            } while (cursorTableEvents.moveToNext());
            cursorTableEvents.close();
            return result;
        } else {
            cursorTableEvents.close();
            return null;
        }
    }

    public void updateExistsEvent(EventItem oldEvent, EventItem updateEvent) {

        cv = new ContentValues();

        String columnDate = context.getResources().getString(R.string.events_column_date);
        String columnConsumer = context.getResources().getString(R.string.events_column_consumer);
        String columStaff = context.getResources().getString(R.string.events_column_id_userapp);//int
        String columnService = context.getResources().getString(R.string.events_column_id_service);
        String columnDiscount = context.getResources().getString(R.string.events_column_discount);//int
        String columnPhoneConsumer = context.getResources().getString(R.string.events_column_phone_number);

        cv.put(columnDate, updateEvent.getDate());
        cv.put(columnConsumer, updateEvent.getConsumer());
        cv.put(columStaff, updateEvent.getStaff());
        cv.put(columnService, updateEvent.getIdService());
        cv.put(columnDiscount, updateEvent.getDiscount());
        if (updateEvent.getPhoneConsumer().length() != 0)
            cv.put(columnPhoneConsumer, updateEvent.getPhoneConsumer());

        this.getReadableDatabase().update(context.getResources().getString(R.string.table_events),
                cv, context.getResources().getString(R.string.events_column_date) + " = \'" + oldEvent.getDate() + "\' AND " +
                        context.getResources().getString(R.string.events_column_consumer) + " = \'" + oldEvent.getConsumer() + "\' AND " +
                        context.getResources().getString(R.string.events_column_id_service) + " = \'" + oldEvent.getIdService() + "\'", null);

        cv.clear();
        cv = null;
        this.close();
    }

    public void deleteEvent(String date, String consumer) {
        this.getReadableDatabase().delete(context.getResources().getString(R.string.table_events),
                context.getResources().getString(R.string.events_column_date) + " = \'" + date + "\' AND " +
                        context.getResources().getString(R.string.events_column_consumer) + " = \'" + consumer + "\'", null);
    }

    public EventItem getEvent(String date, String consumer) {

        Cursor cursorTableEvents = this.getReadableDatabase().query(context.getResources().getString(R.string.table_events), null, null, null, null, null, null);

        if (cursorTableEvents.moveToFirst()) {

            int indexEventDate = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_date));
            int indexEventConsumer = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_consumer));
            int indexEventStaff = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_id_userapp));
            int indexEventService = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_id_service));
            int indexEventDiscount = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_discount));
            int indexEventPhoneNumber = cursorTableEvents.getColumnIndex(context.getResources().getString(R.string.events_column_phone_number));

            List<Service> services = this.getListServices();

            do {
                if (date.trim().equals(cursorTableEvents.getString(indexEventDate).trim()) &&
                        consumer.trim().equals(cursorTableEvents.getString(indexEventConsumer))) {

                    int idService = cursorTableEvents.getInt(indexEventService);
                    int discount = cursorTableEvents.getInt(indexEventDiscount);
                    String staff = cursorTableEvents.getString(indexEventStaff);
                    String phoneConsumer = cursorTableEvents.getString(indexEventPhoneNumber);

                    EventItem event = new EventItem();
                    event.setDate(date);
                    event.setConsumer(consumer);
                    event.setStaff(staff);
                    event.setService(services.get(idService - 1).getNameService());
                    event.setIdService(idService);
                    event.setDiscount(discount);
                    event.setPhoneConsumer(phoneConsumer);
                    return event;
                }
            } while (cursorTableEvents.moveToNext());
            cursorTableEvents.close();
            return null;
        } else {
            cursorTableEvents.close();
            return null;
        }

    }

    public void writeNewConsumer(Consumer consumer) {

        cv = new ContentValues();

        String nameConsumer = context.getResources().getString(R.string.consumer_column_name);
        String surnameConsumer = context.getResources().getString(R.string.consumer_column_surname);
        String photoConsumer = context.getResources().getString(R.string.consumer_column_photo);
        String phoneNumberConsumer = context.getResources().getString(R.string.consumer_column_phone_number);
        String balanceConsumer = context.getResources().getString(R.string.consumer_column_balans);
        String discountConsumer = context.getResources().getString(R.string.consumer_column_discount);
        String descriptionConsumer = context.getResources().getString(R.string.consumer_column_description);

        cv.put(nameConsumer, consumer.getName());
        cv.put(surnameConsumer, consumer.getSurname());
        cv.put(photoConsumer, consumer.getPathToPhoto());
        cv.put(phoneNumberConsumer, consumer.getPhoneNumber());
        cv.put(balanceConsumer, consumer.getBalance());
        cv.put(discountConsumer, consumer.getDiscount());
        cv.put(descriptionConsumer, consumer.getDescription());

        this.getReadableDatabase().insert(context.getResources().getString(R.string.table_consumers), null, cv);

        cv.clear();
        cv = null;
        this.close();

    }

    public List<Consumer> getListConsumers() {
        List<Consumer> result = new ArrayList<>();
        Cursor cursorTableConsumers = this.getReadableDatabase().query(context.getResources().getString(R.string.table_consumers), null, null, null, null, null, null);

        if (cursorTableConsumers.moveToFirst()) {

            int indexConsumerName = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_name));
            int indexConsumerSurname = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_surname));
            int indexConsumerPhoto = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_photo));
            int indexConsumerPhoneNumber = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_phone_number));
            int indexConsumerBalance = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_balans));
            int indexConsumerDiscount = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_discount));
            int indexConsumerDescription = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_description));


            do {
                String name = cursorTableConsumers.getString(indexConsumerName);
                String surname = cursorTableConsumers.getString(indexConsumerSurname);
                String pathToPhoto = cursorTableConsumers.getString(indexConsumerPhoto);
                String phoneNumber = cursorTableConsumers.getString(indexConsumerPhoneNumber);
                Double balance = Double.valueOf(cursorTableConsumers.getString(indexConsumerBalance));
                Integer discount = Integer.valueOf(cursorTableConsumers.getString(indexConsumerDiscount));
                String description = cursorTableConsumers.getString(indexConsumerDescription);


                Consumer consumer = new Consumer();
                consumer.setName(name);
                consumer.setSurname(surname);
                consumer.setPathToPhoto(pathToPhoto);
                consumer.setPhoneNumber(phoneNumber);
                consumer.setBalance(balance);
                consumer.setDiscount(discount);
                consumer.setDescription(description);


                result.add(consumer);

            } while (cursorTableConsumers.moveToNext());
            cursorTableConsumers.close();
            return result;
        } else {
            cursorTableConsumers.close();
            return null;
        }
    }

    public List<String> getListNameConsumers() {
        List<String> result = new ArrayList<>();
        Cursor cursorTableConsumers = this.getReadableDatabase().query(context.getResources().getString(R.string.table_consumers), null, null, null, null, null, null);

        if (cursorTableConsumers.moveToFirst()) {

            int indexConsumerName = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_name));
            int indexConsumerSurname = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_surname));


            do {
                String name = cursorTableConsumers.getString(indexConsumerName);
                String surname = cursorTableConsumers.getString(indexConsumerSurname);
                result.add(name + " " + surname);

            } while (cursorTableConsumers.moveToNext());
            cursorTableConsumers.close();
            return result;
        } else {
            cursorTableConsumers.close();
            return null;
        }
    }

    public Consumer getConsumer(String nameConsumer) {
        String[] fullName;
        if (nameConsumer.contains(" ")) {
            fullName = nameConsumer.split(" ");
            Cursor cursorTableConsumers = this.getReadableDatabase().query(context.getResources().getString(R.string.table_consumers), null, null, null, null, null, null);

            if (cursorTableConsumers.moveToFirst()) {

                int indexConsumerName = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_name));
                int indexConsumerSurname = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_surname));
                int indexConsumerPhoto = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_photo));
                int indexConsumerPhoneNumber = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_phone_number));
                int indexConsumerBalance = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_balans));
                int indexConsumerDiscount = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_discount));
                int indexConsumerDescription = cursorTableConsumers.getColumnIndex(context.getResources().getString(R.string.consumer_column_description));


                do {
                    String name = cursorTableConsumers.getString(indexConsumerName);
                    String surname = cursorTableConsumers.getString(indexConsumerSurname);

                    if (name.equals(fullName[0]) && surname.equals(fullName[1])) {
                        String resultName = cursorTableConsumers.getString(indexConsumerName);
                        String resultSurname = cursorTableConsumers.getString(indexConsumerSurname);
                        String resultPhtoPath = cursorTableConsumers.getString(indexConsumerPhoto);
                        String resultPhone = cursorTableConsumers.getString(indexConsumerPhoneNumber);
                        Double resultBalance = Double.valueOf(cursorTableConsumers.getString(indexConsumerBalance));
                        Integer resultDiscount = Integer.valueOf(cursorTableConsumers.getString(indexConsumerDiscount));
                        String resultDescription = cursorTableConsumers.getString(indexConsumerDescription);

                        Consumer result = new Consumer();
                        result.setName(resultName);
                        result.setSurname(resultSurname);
                        result.setPathToPhoto(resultPhtoPath);
                        result.setPhoneNumber(resultPhone);
                        result.setBalance(resultBalance);
                        result.setDiscount(resultDiscount);
                        result.setDescription(resultDescription);
                        return result;
                    }

                } while (cursorTableConsumers.moveToNext());
                cursorTableConsumers.close();
                return null;
            } else {
                cursorTableConsumers.close();
                return null;
            }
        } else {
            return null;
        }
    }

    public void updateConsumer(String name, String surname, Consumer consumer) {


        cv = new ContentValues();

        String columnNameConsumer = context.getResources().getString(R.string.consumer_column_name);
        String columnSurnameConsumer = context.getResources().getString(R.string.consumer_column_surname);
        String columnPhotoConsumer = context.getResources().getString(R.string.consumer_column_photo);
        String columnPhoneNumberConsumer = context.getResources().getString(R.string.consumer_column_phone_number);
        String columnBalanceConsumer = context.getResources().getString(R.string.consumer_column_balans);
        String columnDiscountConsumer = context.getResources().getString(R.string.consumer_column_discount);
        String columnDescriptionConsumer = context.getResources().getString(R.string.consumer_column_description);

        cv.put(columnNameConsumer, consumer.getName());
        cv.put(columnSurnameConsumer, consumer.getSurname());
        cv.put(columnPhotoConsumer, consumer.getPathToPhoto());
        cv.put(columnPhoneNumberConsumer, consumer.getPhoneNumber());
        cv.put(columnBalanceConsumer, consumer.getBalance());
        cv.put(columnDiscountConsumer, consumer.getDiscount());
        cv.put(columnDescriptionConsumer, consumer.getDescription());

        this.getReadableDatabase().update(context.getResources().getString(R.string.table_consumers),
                cv, context.getResources().getString(R.string.consumer_column_name) + " = \'" + name + "\' AND " +
                        context.getResources().getString(R.string.consumer_column_surname) + " = \'" + surname + "\'", null);

        cv.clear();
        cv = null;
        this.close();
    }

    public void deleteConsumer(String name, String surname) {
        this.getReadableDatabase().delete(context.getResources().getString(R.string.table_consumers), context.getResources().getString(R.string.consumer_column_name) + " = \'" + name + "\' AND " +
                context.getResources().getString(R.string.consumer_column_surname) + " = \'" + surname + "\'", null);
    }

    public void subtractFromBalanceConsumer(Consumer consumer, double bablo) {
        throw new UnsupportedOperationException();
    }

    public void addToBalanceConsumer(Consumer consumer, double bablo) {
        throw new UnsupportedOperationException();
    }
}

///./././././././././././././././././././././././././././././/./
//private void addBookkeeperService(BookkeeperService service) {
//
//    if ((service.getNameService() != null && !service.getNameService().equals("")) && (service.getDurationService() != null && !service.getDurationService().equals(""))
//            && (service.getCostServite() != 0.0)) {
//        ref.child("users").child(mAuth.getCurrentUser().getUid()).child("services").child(service.getNameService()).setValue(service);
//    } else {
//        Toast.makeText(getContext(), "fields is empty!!!", Toast.LENGTH_SHORT).show();
//    }
//}

//    private void showListServices(DataSnapshot dataSnapshot) {
//
//TODO
//
//        if (dataSnapshot.exists() && dataSnapshot.getChildren().iterator().hasNext()) {
//            List<BookkeeperService> array = new ArrayList<>();
//            BookkeeperService ie;
////            Iterable<DataSnapshot> localSnap = dataSnapshot.getChildren().iterator().next().
////                    getChildren().iterator().next().getChildren().iterator().next().getChildren();
//            Iterable<DataSnapshot> localSnap = dataSnapshot.child("users").child(mAuth.getCurrentUser().getUid()).child("services").getChildren();
//            for (DataSnapshot dataS : localSnap) {
//                ie = new BookkeeperService(dataS.getValue(BookkeeperService.class).getNameService().toString(),
//                        dataS.getValue(BookkeeperService.class).getDurationService().toString(),
//                        Double.valueOf(dataS.getValue(BookkeeperService.class).getCostServite()),
//                        dataS.getValue(BookkeeperService.class).getDescriptionService().toString());
//                array.add(ie);
//            }
//            ServicesListAdapter adapter = new ServicesListAdapter(getContext(), R.layout.custom_item_service, array);//..//..//..//..//..//
//            listViewServices.setAdapter(adapter);
//        }
//
//
//    }
