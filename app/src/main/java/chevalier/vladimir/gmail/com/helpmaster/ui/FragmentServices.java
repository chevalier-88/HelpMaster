package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;
import chevalier.vladimir.gmail.com.helpmaster.utils.ServiceItemAdapter;


public class FragmentServices extends Fragment {

    private SwipeMenuListView listServices;

    private ServiceItemAdapter adapter;
    private List<Service> fullListService;
    private LocalSqliteHelper sqliteHelper;
    private Handler handler;
    private int FLAG = 0;
    public static final int TARGET_CODE_NEW_SERVICE = 12345;
    public static final int TARGET_CODE_EXISTS_SERVICE = 54321;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private Boolean ooo = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseApp.getApps(getContext()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        sqliteHelper = new LocalSqliteHelper(getContext());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ooo = true;
        fullListService = new LinkedList<>();
        adapter = new ServiceItemAdapter(getActivity(), fullListService);
        listServices = (SwipeMenuListView) view.findViewById(R.id.id_fragment_list_services);
        listServices.setAdapter(adapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == FLAG) {
                    adapter.notifyDataSetChanged();
//                    adapter = new ServiceItemAdapter(getActivity(), fullListService);
//                    listServices.setAdapter(adapter);
                }

            }
        };
        this.makeSwipeComponent();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (DataSnapshot d : dataSnapshot.child(user.getUid()).child("services").getChildren()) {
                            Service service = d.getValue(Service.class);
                            if (!fullListService.contains(service)) {
                                fullListService.add(service);
                                handler.sendMessage(handler.obtainMessage(FLAG));
                                try {
                                    TimeUnit.MILLISECONDS.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                th.start();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++ method onCancelled +++++++++++++++++++++++++++++++++");
            }
        });

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.id_fab_services);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewService dialog = new DialogNewService();
                dialog.setTargetFragment(FragmentServices.this, TARGET_CODE_NEW_SERVICE);
                dialog.show(getFragmentManager(), "add service:");
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        return view;
    }

    public void addServiceDescription(Service service) {

//        sqliteHelper.writeNewService(service);
        myRef.child(user.getUid()).child("services").child(service.getNameService()).setValue(service);
        fullListService.add(service);
        adapter.notifyDataSetInvalidated();
    }

    public void updateServiceDescription(int index, Service service) {

//        Map<String, Object> updateService = new HashMap<>();
//        updateService.put(fullListService.get(index).getNameService(), service);
//        myRef.child(user.getUid()).child("services").updateChildren(updateService);
        myRef.child(user.getUid()).child("services").child(fullListService.get(index).getNameService()).removeValue();
        myRef.child(user.getUid()).child("services").child(service.getNameService()).setValue(service);

        fullListService.set(index, service);
        adapter.notifyDataSetChanged();
    }

    // business for swipe:
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
                        dialog.show(getFragmentManager(), "edit service:");

                        Bundle b = new Bundle();
                        b.putInt("index", position);
                        b.putSerializable("service", (Serializable) listServices.getAdapter().getItem(position));
                        dialog.setArguments(b);
                        break;
                    case 1:
                        Service tmpService = (Service) listServices.getAdapter().getItem(position);
                        myRef.child(user.getUid()).child("services").child(tmpService.getNameService()).removeValue();
                        fullListService.remove(tmpService);
                        adapter.notifyDataSetInvalidated();
                        break;
                }
                return false;
            }
        });
    }
}