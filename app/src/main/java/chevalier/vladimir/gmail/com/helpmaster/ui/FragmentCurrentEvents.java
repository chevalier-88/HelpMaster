package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.utils.EventItemAdapter;

public class FragmentCurrentEvents extends Fragment {

    private static final int MESSAGE_HENDLER_OK = 200;
    public static final int TARGET_CODE_NEW_EVENT = 201;
    public static final int TARGET_CODE_EXISTS_EVENT = 202;

    private List<EventItem> eventList;

    private EventItemAdapter itemAdapter;
    private SwipeMenuListView listView;
    private Handler handler;


    private View view;

    private ValueEventListener listener;

    private ProgressDialog progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventList = new LinkedList<>();

        if (FirebaseApp.getApps(getContext()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<EventItem> items = new LinkedList<>();
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (dataSnapshot.child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).getChildrenCount() > 0) {
                            for (DataSnapshot d : dataSnapshot.child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).getChildren()) {
                                EventItem event = d.getValue(EventItem.class);
                                if (!items.contains(event)) {
                                    items.add(event);
                                }

                            }
                            eventList.clear();
                            eventList.addAll(filterListEvent(items, HomeActivity.SORTED_FLAG));
                            handler.sendMessage(handler.obtainMessage(MESSAGE_HENDLER_OK));
                        } else {
                            //TODO
                            handler.sendMessage(handler.obtainMessage(MESSAGE_HENDLER_OK));
                        }
                    }

                });
                th.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progressBar = new ProgressDialog(getContext());
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        progressBar.setContentView(R.layout.simple_progress_bar);

        view = inflater.inflate(R.layout.fragment_current_events, container, false);

        itemAdapter = new EventItemAdapter(getContext(), eventList);

        listView = (SwipeMenuListView) view.findViewById(R.id.id_events);
        listView.setAdapter(itemAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_HENDLER_OK) {
                    itemAdapter.notifyDataSetChanged();
                    progressBar.dismiss();
                }
            }
        };

        this.makeSwipeComponent();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(listener);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                DialogNewEvent dialogFragment = new DialogNewEvent();
                dialogFragment.setTargetFragment(FragmentCurrentEvents.this, TARGET_CODE_NEW_EVENT);
                dialogFragment.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_event));
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().removeEventListener(listener);
    }

    public void addEvent(EventItem event) {
        if (HomeActivity.NAME_CURRENT_USER.equals(event.getStaff())) {
            FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).setValue(event);
            switch (HomeActivity.SORTED_FLAG) {
                case 1:

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);

                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                        Date firstDayOfWeek = dateFormat.parse(dateFormat.format(calendar.getTime()));

                        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                        Date lastDayOfWeek = dateFormat.parse(dateFormat.format(calendar.getTime()));

                        Date eventDay = dateFormat.parse(event.getDate());
                        if (((firstDayOfWeek.compareTo(eventDay)) < 0) && (eventDay.compareTo(lastDayOfWeek) < 0)) {
//                            eventList.add(event);
                            ((LinkedList<EventItem>) eventList).addFirst(event);
                            itemAdapter.notifyDataSetInvalidated();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    if ((Calendar.getInstance().get(Calendar.MONTH) + 1) == Integer.parseInt(event.getDate().split(" ")[0].trim().split("-")[1].trim())) {
                        eventList.add(event);
                        itemAdapter.notifyDataSetInvalidated();
                    }
                    break;
                case 3:
                    eventList.add(event);
                    itemAdapter.notifyDataSetInvalidated();
                    break;
            }
        } else {
            //TODO
        }
    }

    public void updateEvent(int index, EventItem event, String key) {
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(key).removeValue();
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).setValue(event);
        eventList.set(index, event);
        itemAdapter.notifyDataSetChanged();
    }

    private void makeSwipeComponent() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext().getApplicationContext());
                deleteItem.setWidth(170);
                deleteItem.setTitleSize(18);
                deleteItem.setBackground(R.color.red);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);

                SwipeMenuItem editItem = new SwipeMenuItem(
                        getContext().getApplicationContext());
                editItem.setWidth(170);
                editItem.setTitleSize(18);
                editItem.setBackground(R.color.blue);
                editItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(editItem);

                SwipeMenuItem callItem = new SwipeMenuItem(
                        getContext().getApplicationContext());
                callItem.setWidth(170);
                callItem.setTitleSize(18);
                callItem.setBackground(R.color.green);
                callItem.setIcon(R.drawable.ic_call);
                menu.addMenuItem(callItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                EventItem event = (EventItem) listView.getAdapter().getItem(position);

                switch (index) {
                    case 0:
                        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).removeValue();
                        eventList.remove(event);
                        itemAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        DialogNewEvent dialogFragment = new DialogNewEvent();
                        dialogFragment.setTargetFragment(FragmentCurrentEvents.this, TARGET_CODE_EXISTS_EVENT);
                        dialogFragment.setFlag(200);
                        dialogFragment.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_event));

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("event", (Serializable) event);
                        dialogFragment.setArguments(b);
                        break;
                    case 2:
                        if (event.getPhoneConsumer().toString().equals("-")) {
                            Toast.makeText(getContext(), getContext().getResources().getString(R.string.unsupport_call_consumer), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Intent chooser = Intent.createChooser(intent, getString(R.string.select_app_for_call));
                            intent.setData(Uri.parse(getContext().getResources().getString(R.string.tel) + event.getPhoneConsumer()));
                            startActivity(chooser);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private List<EventItem> filterListEvent(List<EventItem> events, int filter) {
        List<EventItem> result = new LinkedList<>();

        switch (filter) {

            case 1:

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setFirstDayOfWeek(Calendar.MONDAY);

                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    Date firstDayOfWeek = dateFormat.parse(dateFormat.format(calendar.getTime()));

                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
                    Date lastDayOfWeek = dateFormat.parse(dateFormat.format(calendar.getTime()));
                    for (EventItem item : events) {
                        Date eventDay = dateFormat.parse(item.getDate());
                        if (((firstDayOfWeek.compareTo(eventDay)) <= 0) && (eventDay.compareTo(lastDayOfWeek) <= 0)) {
                            result.add(item);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();

                }
                break;

            case 2:
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                for (EventItem item : events) {
                    if (currentMonth == Integer.parseInt(item.getDate().split(" ")[0].trim().split("-")[1].trim())) {
                        result.add(item);
                    }
                }

                break;
            case 3:
                result.clear();
                result.addAll(events);
                break;
        }
        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }
}