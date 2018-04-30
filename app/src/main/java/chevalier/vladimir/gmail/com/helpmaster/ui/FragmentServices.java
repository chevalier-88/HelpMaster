package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;
import chevalier.vladimir.gmail.com.helpmaster.utils.ServiceItemAdapter;


public class FragmentServices extends Fragment {

    private SwipeMenuListView listServices;

    private ServiceItemAdapter adapter;
    private List<Service> fullListService;

    private Handler handler;
    private int HANDLER_MESSAGE_OK = 200;
    public static final int TARGET_CODE_NEW_SERVICE = 12345;
    public static final int TARGET_CODE_EXISTS_SERVICE = 54321;


    private ValueEventListener listener;

    private ProgressDialog progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(getContext()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        listener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataSnapshot.child(getContext().getResources().getString(R.string.branch_services)).getChildrenCount() > 0) {
                            for (DataSnapshot d : dataSnapshot.child(getContext().getResources().getString(R.string.branch_services)).getChildren()) {
                                Service service = d.getValue(Service.class);
                                if (!fullListService.contains(service)) {
                                    fullListService.add(service);
                                }
                            }
                            handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_OK));
                        } else {
                            //NOP
                        }
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

        View view = inflater.inflate(R.layout.fragment_services, container, false);

        fullListService = new LinkedList<>();
        adapter = new ServiceItemAdapter(getActivity(), fullListService);
        listServices = (SwipeMenuListView) view.findViewById(R.id.id_fragment_list_services);
        listServices.setAdapter(adapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_MESSAGE_OK) {
                    adapter.notifyDataSetChanged();
                    progressBar.dismiss();
                }

            }
        };
        this.makeSwipeComponent();

        FirebaseDatabase.getInstance().getReference().addValueEventListener(listener);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.id_fab_services);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewService dialog = new DialogNewService();
                dialog.setTargetFragment(FragmentServices.this, TARGET_CODE_NEW_SERVICE);
                dialog.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_service));
            }
        });


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().removeEventListener(listener);
    }

    public void addServiceDescription(Service service) {

        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_services)).child(service.getNameService()).setValue(service);
        fullListService.add(service);
        adapter.notifyDataSetChanged();

    }

    public void updateServiceDescription(int index, Service service) {

        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_services)).child(fullListService.get(index).getNameService()).removeValue();
        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_services)).child(service.getNameService()).setValue(service);

        fullListService.set(index, service);
        adapter.notifyDataSetChanged();
    }


    private void makeSwipeComponent() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem editItem = new SwipeMenuItem(
                        getContext().getApplicationContext());
                editItem.setWidth(170);
                editItem.setTitleSize(18);
                editItem.setBackground(R.color.blue);
                editItem.setIcon(R.drawable.ic_edit);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getContext().getApplicationContext());
                deleteItem.setWidth(170);
                deleteItem.setTitleSize(18);
                deleteItem.setBackground(R.color.red);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        listServices.setMenuCreator(creator);

        listServices.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listServices.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()

        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        DialogNewService dialog = new DialogNewService();
                        dialog.setTargetFragment(FragmentServices.this, TARGET_CODE_EXISTS_SERVICE);
                        dialog.show(getFragmentManager(), getContext().getResources().getString(R.string.tg_btn_add_service));

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("service", (Serializable) listServices.getAdapter().getItem(position));
                        dialog.setArguments(b);
                        break;
                    case 1:
                        Service tmpService = (Service) listServices.getAdapter().getItem(position);
                        FirebaseDatabase.getInstance().getReference().child(getContext().getResources().getString(R.string.branch_services)).child(tmpService.getNameService()).removeValue();
                        fullListService.remove(tmpService);
                        adapter.notifyDataSetInvalidated();
                        break;
                }
                return false;
            }
        });
    }

}