package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.utils.HelpMasterReceiver;
import chevalier.vladimir.gmail.com.helpmaster.utils.HelpMasterServiceNotification;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSQLiteStorage;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private NavigationView navigationView;
    private Toolbar toolbar = null;
    private FragmentCurrentEvents fragmentCurrentEvents;
    private FragmentServices fragmentServices;
    private FragmentConsumer fragmentConsumer;
    private FragmentEmployee fragmentEmployee;
    private FragmentSalary fragmentSalary;
    private FragmentSetting fragmentSetting;

    private static final String PERSONAL_DATA = "PERSONAL_DATA";
    public static String MAIL_CURRENT_USER = null;
    public static String NAME_CURRENT_USER = null;
    public static Integer SORTED_FLAG = null;
    public static Boolean NOTIFICATION_MODE = null;
    private SharedPreferences sharedPreferences;
    private static String BROADCAST_ACTION = "android.intent.action.BOOT_COMPLETED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        sharedPreferences = getSharedPreferences(PERSONAL_DATA, Context.MODE_PRIVATE);
        MAIL_CURRENT_USER = sharedPreferences.getString(getBaseContext().getResources().getString(R.string.key_mail_current_user), "");
        NAME_CURRENT_USER = sharedPreferences.getString(getBaseContext().getResources().getString(R.string.key_name_current_user), "");
        SORTED_FLAG = sharedPreferences.getInt(getBaseContext().getResources().getString(R.string.sort_events), 3);
        NOTIFICATION_MODE = sharedPreferences.getBoolean(getBaseContext().getResources().getString(R.string.notification_mode), true);


        fragmentCurrentEvents = new FragmentCurrentEvents();
        fragmentServices = new FragmentServices();
        fragmentConsumer = new FragmentConsumer();
        fragmentEmployee = new FragmentEmployee();
        fragmentSalary = new FragmentSalary();
        fragmentSetting = new FragmentSetting();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View head = navigationView.getHeaderView(0);
        ((TextView) head.findViewById(R.id.id_user_name)).setText(HomeActivity.NAME_CURRENT_USER);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.id_container, fragmentCurrentEvents);
        transaction.commit();
        transaction.addToBackStack(getResources().getString(R.string.nav_events));
        setTitle(getResources().getString(R.string.nav_events));


        Intent i = new Intent();
        i.setClass(this, HelpMasterReceiver.class);
        this.sendBroadcast(i);
        IntentFilter filter = new IntentFilter(BROADCAST_ACTION);
        HelpMasterReceiver br = new HelpMasterReceiver();
        registerReceiver(br, filter);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            setTitle(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName().toString());
        } else {
            getSupportFragmentManager().popBackStack();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.id_nav_events:
                transaction.replace(R.id.id_container, fragmentCurrentEvents, getResources().getString(R.string.nav_events));
                setTitle(getResources().getString(R.string.nav_events));
                transaction.addToBackStack(getResources().getString(R.string.nav_events));
                break;
            case R.id.id_nav_services:
                transaction.replace(R.id.id_container, fragmentServices, getResources().getString(R.string.nav_services));
                setTitle(getResources().getString(R.string.nav_services));
                transaction.addToBackStack(getResources().getString(R.string.nav_services));
                break;
            case R.id.id_nav_employees:
                transaction.replace(R.id.id_container, fragmentEmployee, getResources().getString(R.string.nav_employees));
                setTitle(getResources().getString(R.string.nav_employees));
                transaction.addToBackStack(getResources().getString(R.string.nav_employees));
                break;
            case R.id.id_nav_consumers:
                transaction.replace(R.id.id_container, fragmentConsumer, getResources().getString(R.string.nav_consumers));
                setTitle(getResources().getString(R.string.nav_consumers));
                transaction.addToBackStack(getResources().getString(R.string.nav_consumers));
                break;
            case R.id.id_nav_salary:
                transaction.replace(R.id.id_container, fragmentSalary, getString(R.string.nav_salary));
                setTitle(getString(R.string.nav_salary));
                transaction.addToBackStack(getString(R.string.nav_salary));
                break;
            case R.id.id_nav_settings:
                transaction.replace(R.id.id_container, fragmentSetting, getResources().getString(R.string.nav_settings));
                setTitle(getResources().getString(R.string.nav_settings));
                transaction.addToBackStack(getResources().getString(R.string.nav_settings));
                break;
            case R.id.id_nav_info:
                setTitle(getResources().getString(R.string.nav_about));
                break;
        }
        transaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

