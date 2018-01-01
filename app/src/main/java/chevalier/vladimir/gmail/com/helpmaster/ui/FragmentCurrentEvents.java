package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.utils.EventItemAdapter;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

public class FragmentCurrentEvents extends Fragment {

    private static final int MESSAGE_HENDLER = 1;
    public static final int TARGET_CODE_NEW_EVENT = 12345;
    public static final int TARGET_CODE_EXISTS_EVENT = 54321;

    private List<EventItem> eventList;
//    private MyEventItemAdapter itemAdapter;
    private EventItemAdapter itemAdapter;
    private SwipeMenuListView listView;
    private Handler handler;
    private LocalSqliteHelper sqliteHelper;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_current_events, container, false);
        sqliteHelper = new LocalSqliteHelper(this.getContext());


        listView = (SwipeMenuListView) view.findViewById(R.id.id_events);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_HENDLER) {
                    itemAdapter = new EventItemAdapter(getContext(), eventList);
//                    itemAdapter = new MyEventItemAdapter();
                    listView.setAdapter(itemAdapter);
                }
            }
        }

        ;

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eventList = sqliteHelper.getListEvents() != null ? sqliteHelper.getListEvents() : new LinkedList<EventItem>();
                handler.sendMessage(handler.obtainMessage(MESSAGE_HENDLER));
            }
        });
        th.start();
        this.

                makeSwipeComponent();

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                DialogNewEvent dialogFragment = new DialogNewEvent();
                dialogFragment.setTargetFragment(FragmentCurrentEvents.this, TARGET_CODE_NEW_EVENT);
                dialogFragment.show(getFragmentManager(), "add event:");

            }
        });
        return view;
    }


    public void addEvent(EventItem event) {
        eventList.add(event);
        itemAdapter.notifyDataSetInvalidated();
    }

    public void updateEvent(int index, EventItem event) {
        if(eventList!=null){eventList.set(index, event);
        itemAdapter.notifyDataSetChanged();}
//        listView.setAdapter(itemAdapter);
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
                        sqliteHelper.deleteEvent(event.getDate().toString(), event.getConsumer());
                        eventList.remove(event);
                        itemAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        DialogNewEvent dialogFragment = new DialogNewEvent();
                        dialogFragment.setTargetFragment(FragmentCurrentEvents.this, TARGET_CODE_EXISTS_EVENT);
                        dialogFragment.setFlag(200);
                        dialogFragment.show(getFragmentManager(), "add event:");

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("event", (Serializable) event);
                        dialogFragment.setArguments(b);
                        break;
                    case 2:
                        if (event.getPhoneConsumer().toString().equals("-")) {
                            Toast.makeText(getContext(), "Sorry, but this consumer didn't provide a phone number :-(", Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Intent chooser = Intent.createChooser(intent, getString(R.string.select_app_for_call));
                            intent.setData(Uri.parse("tel:" + event.getPhoneConsumer()));
                            startActivity(chooser);
                        }
                        break;
                }
                return false;
            }
        });
//    }
    }

//    class MyEventItemAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return eventList.size();
//        }
//
//        @Override
//        public EventItem getItem(int position) {
//            return eventList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            // menu type count
//            return 3;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            // current menu type
//            return position % 3;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = View.inflate(getContext(),
//                        R.layout.custom_item_event, null);
//                new ViewHolder(convertView);
//            }
//            ViewHolder holder = (ViewHolder) convertView.getTag();
//            EventItem item = getItem(position);
//            holder.tvDate.setText(item.getDate());
//            holder.tvService.setText(item.getService());
//            holder.tvCostumer.setText(item.getConsumer());
//
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView tvDate;
//            TextView tvService;
//            TextView tvCostumer;
//
//            public ViewHolder(View view) {
//                tvDate = (TextView) view.findViewById(R.id.id_cie_date);
//                tvService = (TextView) view.findViewById(R.id.id_cie_service);
//                tvCostumer = (TextView) view.findViewById(R.id.id_cie_consumer);
//                view.setTag(this);
//            }
//        }
//    }
//
//    private int dp2px(int dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }
}