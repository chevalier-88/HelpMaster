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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.Serializable;


import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.utils.ConsumerItemAdapter;


public class FragmentConsumer extends Fragment {

    public static final int TARGET_CODE_NEW_CONSUMER = 12345;
    public static final int TARGET_CODE_EXISTS_CONSUMER = 54321;
    private static final int MESSAGE_HANDLER_OK = 200;
    private SwipeMenuListView swipeListConsumers;
    private ConsumerItemAdapter itemAdapter;
    private List<Consumer> listConsumer;
    private Handler handler;

    private ValueEventListener listener;
    private ProgressDialog progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Thread th = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (DataSnapshot dd : dataSnapshot.child(getContext().getResources().getString(R.string.branch_consumers)).getChildren()) {
                            Consumer consumer = dd.getValue(Consumer.class);
                            if (!listConsumer.contains(consumer)) {
                                listConsumer.add(consumer);
                            }
                        }
                        handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_OK));
                    }

                });
                th.start();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //NOP
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

        View view = inflater.inflate(R.layout.fragment_consumers, container, false);

        listConsumer = new LinkedList<Consumer>();
        itemAdapter = new ConsumerItemAdapter(getContext(), listConsumer);
        swipeListConsumers = (SwipeMenuListView) view.findViewById(R.id.id_list_consumers);
        swipeListConsumers.setAdapter(itemAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_HANDLER_OK) {
                    itemAdapter.notifyDataSetChanged();
                    progressBar.dismiss();
                }
            }
        };
        this.makeSwipeComponent();

        FirebaseDatabase.getInstance().getReference().addValueEventListener(listener);


        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.id_fab_visitors);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewConsumer dialog = new DialogNewConsumer();
                dialog.setTargetFragment(FragmentConsumer.this, TARGET_CODE_NEW_CONSUMER);
                dialog.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_consumer));
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().removeEventListener(listener);
    }

    public void addConsumer(Consumer consumer) {
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_consumers)).child(consumer.getName() + " " + consumer.getSurname()).setValue(consumer);
        listConsumer.add(consumer);
        itemAdapter.notifyDataSetChanged();
    }

    public void updateConsumerDescription(int index, Consumer consumer) {
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_consumers)).child(
                listConsumer.get(index).getName() + " " + listConsumer.get(index).getSurname()).removeValue();
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_consumers)).child(
                consumer.getName() + " " + consumer.getSurname()).setValue(consumer);

        listConsumer.set(index, consumer);
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

                SwipeMenuItem callItem = new SwipeMenuItem(getContext().getApplicationContext());
                callItem.setWidth(170);
                callItem.setTitleSize(18);
                callItem.setBackground(R.color.green);
                callItem.setIcon(R.drawable.ic_call);
                menu.addMenuItem(callItem);
            }
        };
        swipeListConsumers.setMenuCreator(creator);

        swipeListConsumers.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        swipeListConsumers.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        Consumer consumerForRemove = (Consumer) swipeListConsumers.getAdapter().getItem(position);
                        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_consumers)).child(consumerForRemove.getName() + " " + consumerForRemove.getSurname()).removeValue();
                        listConsumer.remove(consumerForRemove);
                        itemAdapter.notifyDataSetInvalidated();
                        break;
                    case 1:
                        Consumer consumerForEdit = (Consumer) swipeListConsumers.getAdapter().getItem(position);

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("consumer", (Serializable) consumerForEdit);

                        DialogNewConsumer dialog = new DialogNewConsumer();
                        dialog.setTargetFragment(FragmentConsumer.this, TARGET_CODE_EXISTS_CONSUMER);
                        dialog.setArguments(b);
                        dialog.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_consumer));
                        break;
                    case 2:
                        Consumer consumerForCall = (Consumer) swipeListConsumers.getAdapter().getItem(position);
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(getContext().getResources().getString(R.string.tel) + consumerForCall.getPhoneNumber()));
                        Intent chooser = Intent.createChooser(intent, getContext().getResources().getString(R.string.select_app_for_call));
                        startActivity(chooser);
                        break;
                }

                return false;
            }
        });
    }
}
