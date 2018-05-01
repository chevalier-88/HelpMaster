package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.SalaryItem;
import chevalier.vladimir.gmail.com.helpmaster.utils.SalaryItemAdapter;


public class FragmentSalary extends Fragment {


    private SwipeMenuListView showListSalary;
    private Spinner spinnerMonths;
    private SalaryItemAdapter salaryItemAdapter;
    private List<SalaryItem> listSalaryItems;
    private List<EventItem> listEventItem;


    private ValueEventListener listener;
    private TextView footer;
    private Handler handler;
    private static int MESSAGE_HANDLER_OK = 200;
    private static int MESSAGE_HANDLER_COMPLETE = 201;

    private List<String> months;

    public static final int TARGET_CODE_EXISTS = 5555;

    private ProgressDialog progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listEventItem = new ArrayList<>();

        if (FirebaseApp.getApps(getContext()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Thread th = new Thread(new Runnable() {
                    //                th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (dataSnapshot.child(FragmentSalary.this.getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).getChildrenCount() > 0) {
                            for (DataSnapshot d : dataSnapshot.child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).getChildren()) {
                                EventItem event = d.getValue(EventItem.class);
                                listEventItem.add(event);
                            }
                            Collections.sort(listEventItem);
                            handler.sendEmptyMessage(MESSAGE_HANDLER_COMPLETE);
                        } else {
                            //NOP
                        }

                    }
                });
                th.start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//NOP
            }
        };
    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressBar = new ProgressDialog(getContext());
        progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        progressBar.setCancelable(false);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();
        progressBar.setContentView(R.layout.simple_progress_bar);

        View view = inflater.inflate(R.layout.fragment_salary, container, false);

        months = Arrays.asList(getContext().getResources().getStringArray(R.array.Months));

        listSalaryItems = new ArrayList<>();
        salaryItemAdapter = new SalaryItemAdapter(getContext(), listSalaryItems);

        showListSalary = (SwipeMenuListView) view.findViewById(R.id.id_fragment_list_salary);
        showListSalary.setAdapter(salaryItemAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == MESSAGE_HANDLER_OK) {
                    salaryItemAdapter.notifyDataSetChanged();
                } else if (msg.what == MESSAGE_HANDLER_COMPLETE) {
                    progressBar.dismiss();
                    spinnerMonths.setSelection(FragmentSalary.this.getListMonths().size() - 1);
                }

            }
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, this.getListMonths());
        spinnerMonths = (Spinner) view.findViewById(R.id.id_f_s_spinner_months);
        spinnerMonths.setAdapter(spinnerAdapter);

        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listSalaryItems.size() > 0) listSalaryItems.clear();
                listSalaryItems.addAll(getListSalaryItems(listEventItem));
                handler.sendEmptyMessage(MESSAGE_HANDLER_OK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//NOP
            }
        });

        footer = (TextView) getActivity().getLayoutInflater().inflate(R.layout.footer_list_salary, null);
        footer.setPadding(10, 10, 20, 10);

        showListSalary = (SwipeMenuListView) view.findViewById(R.id.id_fragment_list_salary);

        showListSalary.addFooterView(footer);
        showListSalary.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0 && (view.getLastVisiblePosition() == listSalaryItems.size())) {
                    FragmentSalary.this.getDataForFooter();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((totalItemCount >= 1) && (visibleItemCount == totalItemCount))
                    FragmentSalary.this.getDataForFooter();
            }
        });

        this.makeSwipeComponent();
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(listener);

        return view;
    }

    private List<String> getListMonths() {
        List<String> result = new LinkedList<>();
        String[] months = getContext().getResources().getStringArray(R.array.Months);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        result.add(months[currentMonth - 2]);
        result.add(months[currentMonth - 1]);
        result.add(months[currentMonth]);
        return result;
    }

    private List<SalaryItem> getListSalaryItems(List<EventItem> events) {

        List<SalaryItem> result = new LinkedList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        for (EventItem item : events) {

            String date = item.getDate().split(" ")[0].trim();
            int eventDateYear = Integer.parseInt(date.split("-")[0].trim());
            int eventDateMonth = Integer.parseInt(date.split("-")[1]);
            int eventDateDay = Integer.parseInt(date.split("-")[2].trim());

            if (eventDateYear <= currentYear) {

                int selectedMonth = Arrays.asList(getContext().getResources().getStringArray(R.array.Months)).indexOf(spinnerMonths.getSelectedItem().toString().trim());

                if ((eventDateMonth == selectedMonth) && (selectedMonth < currentMonth)) {
                    SalaryItem salaryItem = new SalaryItem();
                    salaryItem.setDate(item.getDate());
                    salaryItem.setServiceName(item.getService());
                    salaryItem.setConsumerName(item.getConsumer());
                    salaryItem.setSum(((item.getCost() * (1 - (double) item.getDiscount() / (double) 100)) - item.getPrimeCost()) / 2);
                    result.add(salaryItem);
                } else if ((eventDateMonth == selectedMonth) && (selectedMonth == currentMonth)) {
                    if (eventDateDay <= currentDay) {
                        SalaryItem salaryItem = new SalaryItem();
                        salaryItem.setDate(item.getDate());
                        salaryItem.setServiceName(item.getService());
                        salaryItem.setConsumerName(item.getConsumer());
                        salaryItem.setSum((double) item.getCost());
                        salaryItem.setSum(((item.getCost() * (1 - (double) item.getDiscount() / (double) 100)) - item.getPrimeCost()) / 2);
                        result.add(salaryItem);
                    }
                }
            }
        }
        return result;
    }

    private EventItem getEvent(SalaryItem item) {
        EventItem result = null;
        for (EventItem i : listEventItem) {
            if ((i.getDate().equals(item.getDate())) && (i.getConsumer().equals(item.getConsumerName())) && (i.getService().equals(item.getServiceName()))) {
                result = i;
            }
        }
        return result;
    }

    public void updateAdapter(int index, EventItem event) {
        SalaryItem item = new SalaryItem();
        item.setDate(event.getDate());
        item.setConsumerName(event.getConsumer());
        item.setServiceName(event.getService());
        item.setSum(((event.getCost() * (1 - (double) event.getDiscount() / (double) 100)) - event.getPrimeCost()) / 2);
        listSalaryItems.set(index, item);

        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).removeValue();
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).setValue(event);
        salaryItemAdapter.notifyDataSetChanged();
    }


    private void getDataForFooter() {
        @SuppressLint("HandlerLeak") final Handler handlerFooter = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bu = msg.getData();
                footer.setText("" + Math.round(bu.getDouble("sum") * 100) / 100D);


            }
        };
        Thread threadFooter = new Thread(new Runnable() {
            @Override
            public void run() {
                double i = 0;
                if (listSalaryItems.size() > 0) {
                    for (SalaryItem item : listSalaryItems) {
                        i += item.getSum();
                    }
                }
                Message message = new Message();
                Bundle b = new Bundle();
                b.putDouble("sum", i);
                message.setData(b);
                handlerFooter.sendMessage(message);
            }
        });
        threadFooter.start();
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

            }
        };
        showListSalary.setMenuCreator(creator);

        showListSalary.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        showListSalary.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                SalaryItem item = (SalaryItem) showListSalary.getAdapter().getItem(position);
                EventItem event = FragmentSalary.this.getEvent(item);
                switch (index) {
                    case 0:
                        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~")).child(getContext().getResources().getString(R.string.subbranch_events)).child(event.getDate() + " " + event.getConsumer()).removeValue();
                        listSalaryItems.remove(item);
                        double a = Double.parseDouble(String.valueOf(footer.getText()));
                        double b = Double.parseDouble(item.getSum().toString().trim());
                        footer.setText("" + (a - b));
                        salaryItemAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        DialogNewEvent dialogFragment = new DialogNewEvent();
                        dialogFragment.setTargetFragment(FragmentSalary.this, TARGET_CODE_EXISTS);
                        dialogFragment.setFlag(100);
                        dialogFragment.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_exist_event));

                        Bundle bdl = new Bundle();
                        bdl.putInt("index", position);
                        bdl.putSerializable("event", (Serializable) event);
                        dialogFragment.setArguments(bdl);
                        break;
                }
                return false;
            }
        });
    }


//    @Override
//    public void onStop() {
//        super.onStop();
//        spinnerMonths.setSelection(0);
//        listEventItem.clear();
//        listSalaryItems.clear();
//    }
}
