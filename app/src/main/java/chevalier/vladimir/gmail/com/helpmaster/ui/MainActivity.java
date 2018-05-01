package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.utils.HelpMasterReceiver;

public class MainActivity extends FragmentActivity {


    private FragmentSignUp fragmentSignUp;
    private FragmentSignIn fragmentSignIn;
    private FragmentTransaction fragmentTransaction;

    private SharedPreferences sp;
    private static final String PERSONAL_DATA = "PERSONAL_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.WelcomWindowTheme);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fragmentSignIn = new FragmentSignIn();
        fragmentSignUp = new FragmentSignUp();

        fragmentTransaction = getFragmentManager().beginTransaction();

        sp = getBaseContext().getSharedPreferences(PERSONAL_DATA, Context.MODE_PRIVATE);
        if (sp.getAll().size() > 0) {
            if (!isAccessToInternet(this.getApplicationContext())) {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.msg_no_connection), Toast.LENGTH_LONG).show();
            }
            fragmentTransaction.add(R.id.id_main_Container, fragmentSignIn);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else {
            fragmentTransaction.add(R.id.id_main_Container, fragmentSignUp);
            fragmentTransaction.commit();
        }

    }

    private boolean isAccessToInternet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info == null ? false : info.isConnectedOrConnecting());
    }
}

