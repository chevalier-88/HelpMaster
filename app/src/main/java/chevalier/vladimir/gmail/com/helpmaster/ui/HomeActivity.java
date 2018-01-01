package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import chevalier.vladimir.gmail.com.helpmaster.R;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private NavigationView navigationView;
    private Toolbar toolbar = null;
    private FragmentCurrentEvents fragmentCurrentEvents;
    private FragmentServices fragmentServices;
    private FragmentConsumer fragmentConsumer;
    private FragmentEmployee fragmentEmployee;
    private FragmentSalary fragmentSalary;
    private FragmentSetting fragmentSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.id_container, fragmentCurrentEvents);
        transaction.commit();
        setTitle(getResources().getString(R.string.nav_events));

    }


    @Override
    public void onBackPressed() {
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
                transaction.replace(R.id.id_container, fragmentCurrentEvents, "Events");
                setTitle(getResources().getString(R.string.nav_events));
                break;
            case R.id.id_nav_services:
                transaction.replace(R.id.id_container, fragmentServices, "Services");
                setTitle(getResources().getString(R.string.nav_services));
                break;
            case R.id.id_nav_employees:
                transaction.replace(R.id.id_container, fragmentEmployee, "Employees");
                setTitle(getResources().getString(R.string.nav_employees));
                break;
            case R.id.id_nav_consumers:
                transaction.replace(R.id.id_container, fragmentConsumer, "Consumers");
                setTitle(getResources().getString(R.string.nav_consumers));
                break;
            case R.id.id_nav_salary:
                transaction.replace(R.id.id_container, fragmentSalary, "Salary");
                setTitle(getString(R.string.nav_salary));
                break;
            case R.id.id_nav_settings:
//                transaction.replace(R.id.id_container, fragmentSetting);
                setTitle(getResources().getString(R.string.nav_settings));
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

}
