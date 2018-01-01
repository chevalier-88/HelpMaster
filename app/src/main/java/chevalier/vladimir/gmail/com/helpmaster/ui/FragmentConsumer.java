package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.utils.ConsumerItemAdapter;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentConsumer extends Fragment {

    public static final int TARGET_CODE_NEW_CONSUMER = 12345;
    public static final int TARGET_CODE_EXISTS_CONSUMER = 54321;
    private static final int MESSAGE_HANDLER_OK = 1;

    private SwipeMenuListView listViewConsumers;
    private ConsumerItemAdapter itemAdapter;
    private List<Consumer> listConsumer;
    private LocalSqliteHelper localSqliteHelper;
    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consumers, container, false);

        listViewConsumers = (SwipeMenuListView) view.findViewById(R.id.id_list_consumers);


        localSqliteHelper = new LocalSqliteHelper(getContext());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_HANDLER_OK) {
                    itemAdapter = new ConsumerItemAdapter(getActivity(), listConsumer);
                    listViewConsumers.setAdapter(itemAdapter);
                }
            }
        };
        this.makeSwipeComponent();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listConsumer = localSqliteHelper.getListConsumers() != null ? localSqliteHelper.getListConsumers() : new ArrayList<Consumer>();
                handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_OK));
            }
        });
        th.start();


        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.id_fab_visitors);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewConsumer dialog = new DialogNewConsumer();
                dialog.setTargetFragment(FragmentConsumer.this, TARGET_CODE_NEW_CONSUMER);
                dialog.show(getFragmentManager(), "add visitor:");
            }
        });
        return view;
    }

    public void addConsumer(Consumer consumer) {
        listConsumer.add(consumer);
        itemAdapter.notifyDataSetInvalidated();
    }

    public void updateConsumerDescription(int index, Consumer consumer) {
        listConsumer.set(index, consumer);
        itemAdapter.notifyDataSetChanged();
//        listViewConsumers.setAdapter(itemAdapter);
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

                SwipeMenuItem callItem = new SwipeMenuItem(getContext().getApplicationContext());
                callItem.setWidth(170);
                callItem.setTitleSize(18);
                callItem.setBackground(R.color.green);
                callItem.setIcon(R.drawable.ic_call);
                menu.addMenuItem(callItem);
            }
        };
        listViewConsumers.setMenuCreator(creator);

        listViewConsumers.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listViewConsumers.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Consumer selectedConsumer = (Consumer) listViewConsumers.getAdapter().getItem(position);
                switch (index) {
                    case 0:
                        localSqliteHelper.deleteConsumer(selectedConsumer.getName(), selectedConsumer.getSurname());
                        listConsumer.remove(selectedConsumer);
                        itemAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        DialogNewConsumer dialog = new DialogNewConsumer();
                        dialog.setTargetFragment(FragmentConsumer.this, TARGET_CODE_EXISTS_CONSUMER);
                        dialog.show(getFragmentManager(), "add visitor:");

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("consumer", (Serializable) selectedConsumer);
                        dialog.setArguments(b);
                        break;
                    case 2:
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + selectedConsumer.getPhoneNumber()));
                        Intent chooser = Intent.createChooser(intent, getContext().getResources().getString(R.string.select_app_for_call));
                        startActivity(chooser);
                        break;
                }

                return false;
            }
        });
    }
}
