package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.SalaryItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;
import chevalier.vladimir.gmail.com.helpmaster.utils.SalaryItemAdapter;


public class FragmentSalary extends Fragment {


    private SwipeMenuListView showListSalary;
    private Spinner spinnerMonths;
    private SalaryItemAdapter listAdapter;
    private List<SalaryItem> listSalaryItems;
    private LocalSqliteHelper sqliteHelper;
    private TextView footer;
    private Handler handler;
    private int MESSAGE_HANDLER = 0;

    private List<String> months;

    public static final int TARGET_CODE_EXISTS = 5555;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = inflater.inflate(R.layout.fragment_salary, container, false);
        months = Arrays.asList(getContext().getResources().getStringArray(R.array.Months));

        sqliteHelper = new LocalSqliteHelper(getContext());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, this.getListMonths());
        spinnerMonths = (Spinner) view.findViewById(R.id.id_f_s_spinner_months);
        spinnerMonths.setAdapter(spinnerAdapter);
        spinnerMonths.setSelection(this.getListMonths().size() - 1);
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == MESSAGE_HANDLER) {
                            listAdapter = new SalaryItemAdapter(getContext(), listSalaryItems);
                            showListSalary.setAdapter(listAdapter);
                        }
                    }
                };
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        listSalaryItems = sqliteHelper.getListEvents() != null ? FragmentSalary.this.getListSalaryItems(sqliteHelper.getListEvents()) : new LinkedList<SalaryItem>();
                        handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER));
                    }
                });
                th.start();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            String eventDate = item.getDate();
            String date = eventDate.split("_")[0].trim();
            int eventDateYear = Integer.parseInt(date.split("-")[0].trim());
            int eventDateMonth = months.indexOf(date.split("-")[1].trim());
            int eventDateDay = Integer.parseInt(date.split("-")[2].trim());


            if (eventDateYear <= currentYear) {
                int selectedMonth = months.indexOf(spinnerMonths.getSelectedItem().toString().trim());
                if ((eventDateMonth == selectedMonth) && (selectedMonth < currentMonth)) {

                    SalaryItem salaryItem = new SalaryItem();
                    salaryItem.setDate(item.getDate());
                    salaryItem.setServiceName(item.getService());
                    salaryItem.setConsumerName(item.getConsumer());
//                    salaryItem.setSum(this.getBabloForSalary(sqliteHelper.getService(item.getService()), sqliteHelper.getConsumer(item.getConsumer())));
                    salaryItem.setSum(this.getBabloForSalarys(sqliteHelper.getService(item.getService()), item));
                    result.add(salaryItem);

                } else if ((eventDateMonth == selectedMonth) && (selectedMonth == currentMonth)) {
                    if (eventDateDay <= currentDay) {
                        SalaryItem salaryItem = new SalaryItem();
                        salaryItem.setDate(item.getDate());
                        salaryItem.setServiceName(item.getService());
                        salaryItem.setConsumerName(item.getConsumer());
//                        salaryItem.setSum(this.getBabloForSalary(sqliteHelper.getService(item.getService()), sqliteHelper.getConsumer(item.getConsumer())));
                        salaryItem.setSum(this.getBabloForSalarys(sqliteHelper.getService(item.getService()), item));
                        result.add(salaryItem);
                    }
                }
            }
        }
        return result;
    }

    //    private double getBabloForSalary(Service service, Consumer consumer) {hjhjhjkhjkhjkhj// need receive discount value from event
    private double getBabloForSalarys(Service service, EventItem event) {
        double result = 0;
        double costService = service.getCostService();
        double firstCostService = service.getFirstCostService();
        int consumerDiscount = (event == null ? 0 : event.getDiscount());
        result = ((costService * (1 - (double) consumerDiscount / (double) 100)) - firstCostService) / 2;
        return result;
    }

    public void updateAdapter(int index, SalaryItem event) {

        listSalaryItems.set(index, event);
        listAdapter.notifyDataSetChanged();
    }

    private void getDataForFooter() {
        final Handler handlerFooter = new Handler() {
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
                for (SalaryItem item : listSalaryItems) {
                    i += item.getSum();
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

                switch (index) {
                    case 0:
                        sqliteHelper.deleteEvent(item.getDate().toString().trim(), item.getConsumerName().toString().trim());
                        listSalaryItems.remove(item);
                        double a = Double.parseDouble(String.valueOf(footer.getText()));
                        double b = Double.parseDouble(item.getSum().toString().trim());
                        footer.setText("" + (a - b));
                        listAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        DialogNewEvent dialogFragment = new DialogNewEvent();
                        dialogFragment.setTargetFragment(FragmentSalary.this, TARGET_CODE_EXISTS);
                        dialogFragment.setFlag(100);
                        dialogFragment.show(getFragmentManager(), "exists event:");

                        Bundle bdl = new Bundle();
                        bdl.putInt("index", position);
                        EventItem event = sqliteHelper.getEvent(item.getDate(), item.getConsumerName());

                        bdl.putSerializable("event", (Serializable) event);
                        dialogFragment.setArguments(bdl);
                        break;
                }
                return false;
            }
        });
    }
}
