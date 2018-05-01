package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.ui.HomeActivity;

public class HelpMasterServiceNotification extends Service {
    private static int NOTIFICATION_ID = 0;
    private List<EventItem> listEventItem;
    private static int counter = 0;

    private boolean startFlag = true;
    private ExecutorService executor;

    private Thread valueEventThread;
    private ValueEventListener valueEventListener;
    private Thread childEventThread;
    private ChildEventListener childEventListener;

    private Handler handler;
    private static final int HANDLER_WHAT_VALUE_EVENT = 1;
    private static final int HANDLER_WHAT_CHILD_EVENT = 2;

    private SharedPreferences sSharedPreferences;

    private static final String sPERSONAL_DATA = "PERSONAL_DATA";
    public static String sMAIL_CURRENT_USER = null;
    public static String sNAME_CURRENT_USER = null;
    public static Integer sSORTED_FLAG = null;
    public static Boolean sNOTIFICATION_MODE = null;

    private LocalSQLiteStorage db;

    public HelpMasterServiceNotification() {
        if (FirebaseApp.getApps(getBaseContext()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        listEventItem = new LinkedList<>();
        executor = Executors.newFixedThreadPool(2);
        sSharedPreferences = getSharedPreferences(sPERSONAL_DATA, Context.MODE_PRIVATE);
        sMAIL_CURRENT_USER = sSharedPreferences.getString(getBaseContext().getResources().getString(R.string.key_mail_current_user), "");
        sNAME_CURRENT_USER = sSharedPreferences.getString(getBaseContext().getResources().getString(R.string.key_name_current_user), "");
        sSORTED_FLAG = sSharedPreferences.getInt(getBaseContext().getResources().getString(R.string.sort_events), 3);
        sNOTIFICATION_MODE = sSharedPreferences.getBoolean(getBaseContext().getResources().getString(R.string.notification_mode), true);
        db = new LocalSQLiteStorage(getBaseContext());
    }

    @SuppressLint("HandlerLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLER_WHAT_VALUE_EVENT:
                        FirebaseDatabase.getInstance().getReference().
                                child(getBaseContext().getResources().getString(R.string.branch_users)).
                                child(sMAIL_CURRENT_USER.replace(".", "~")).
                                child(getBaseContext().getResources().getString(R.string.subbranch_events)).
                                addValueEventListener(valueEventListener);
                        executor.execute(startChildEventListener());
                        break;
                    case HANDLER_WHAT_CHILD_EVENT:
                        FirebaseDatabase.getInstance().getReference().
                                child(getBaseContext().getResources().getString(R.string.branch_users)).
                                child(sMAIL_CURRENT_USER.replace(".", "~")).
                                child(getBaseContext().getResources().getString(R.string.subbranch_events)).
                                addChildEventListener(childEventListener);
                        break;
                }
            }
        };
        executor.execute(startValueEvntListener());
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Thread startValueEvntListener() {
        valueEventThread = new Thread(new Runnable() {
            @Override
            public void run() {
                valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (startFlag) {
                            listEventItem.clear();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                listEventItem.add(d.getValue(EventItem.class));
                            }

                            List<EventItem> dbList = db.readTableEventItems();

                            if (dbList.size() > 0) {
                                if ((dbList.size() == listEventItem.size()) && (!dbList.containsAll(listEventItem))) {
                                    db.updateEvent(listEventItem);
                                    for (EventItem i : listEventItem) {
                                        if (!dbList.contains(i)) {
                                            createNotification(getBaseContext().getResources().getString(R.string.msg_notif_change), i.getDate(), i.getService(), i.getConsumer());
                                        }
                                    }
                                } else if (dbList.size() < listEventItem.size()) {
                                    for (EventItem i : listEventItem) {
                                        if (!dbList.contains(i)) {
                                            db.insertEventItem(i);
                                            createNotification(getBaseContext().getResources().getString(R.string.msg_notif_add), i.getDate(), i.getService(), i.getConsumer());
                                        }
                                    }
                                } else if (dbList.size() > listEventItem.size()) {
                                    for (EventItem i : dbList) {
                                        if (!listEventItem.contains(i)) {
                                            db.deleteEventItem(i);
                                            if (getSharedPreferences(sPERSONAL_DATA, Context.MODE_PRIVATE).getBoolean(getBaseContext().getResources().getString(R.string.notification_mode), true))
                                                createNotification(getBaseContext().getResources().getString(R.string.msg_notif_delete), i.getDate(), i.getService(), i.getConsumer());
                                        }
                                    }
                                }
                            } else {
                                db.insertEventItems(listEventItem);
                            }
                            startFlag = false;
                            handler.sendEmptyMessage(HANDLER_WHAT_CHILD_EVENT);
                        } else {
                            listEventItem.clear();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                listEventItem.add(d.getValue(EventItem.class));
                            }
                            List<EventItem> dbListEvents = db.readTableEventItems();
                            if ((dbListEvents.size() == listEventItem.size()) && (!dbListEvents.containsAll(listEventItem))) {
                                List<EventItem> tmpListEvent = db.readTableEventItems();
                                db.updateEvent(listEventItem);
                                for (EventItem i : listEventItem) {
                                    if (!tmpListEvent.contains(i)) {
                                        createNotification(getBaseContext().getResources().getString(R.string.msg_notif_change), i.getDate(), i.getService(), i.getConsumer());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //NOP
                    }
                };
                handler.sendEmptyMessage(HANDLER_WHAT_VALUE_EVENT);
            }
        });
        return valueEventThread;
    }

    private Thread startChildEventListener() {
        childEventThread = new Thread(new Runnable() {
            @Override
            public void run() {
                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (listEventItem.size() > counter) {
                            counter++;
                        } else {
                            listEventItem.add(dataSnapshot.getValue(EventItem.class));
                            db.insertEventItem(dataSnapshot.getValue(EventItem.class));
                            counter++;
                            String date = dataSnapshot.getValue(EventItem.class).getDate();
                            String service = dataSnapshot.getValue(EventItem.class).getService();
                            String consumer = dataSnapshot.getValue(EventItem.class).getConsumer();
                            if (getSharedPreferences(sPERSONAL_DATA, Context.MODE_PRIVATE).getBoolean(getBaseContext().getResources().getString(R.string.notification_mode), true))
                                createNotification(getBaseContext().getResources().getString(R.string.msg_notif_add), date, service, consumer);

                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//NOP
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if (getSharedPreferences(sPERSONAL_DATA, Context.MODE_PRIVATE).getBoolean(getBaseContext().getResources().getString(R.string.notification_mode), true)) {
                            String date = dataSnapshot.getValue(EventItem.class).getDate();
                            String service = dataSnapshot.getValue(EventItem.class).getService();
                            String consumer = dataSnapshot.getValue(EventItem.class).getConsumer();
                            createNotification(getBaseContext().getResources().getString(R.string.msg_notif_delete), date, service, consumer);
                            listEventItem.remove(dataSnapshot.getValue(EventItem.class));
                            db.deleteEventItem(dataSnapshot.getValue(EventItem.class));
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }

                ;
            }
        }

        );
        return childEventThread;
    }

    private void createNotification(String title, String date, String service, String consumer) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setVibrate(new long[]{100, 100, 100}).setLights(Color.WHITE, 1000, 1000)
                .setContentIntent(pendingIntent).setStyle(new NotificationCompat.InboxStyle().addLine(date).addLine(service).addLine(consumer));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());

    }

}