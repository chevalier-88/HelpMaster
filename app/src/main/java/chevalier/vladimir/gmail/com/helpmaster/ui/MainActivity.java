package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

public class MainActivity extends FragmentActivity {


    LocalSqliteHelper sqliteHelper;
    FragmentSignUp fragmentSignUp;
    FragmentSignIn fragmentSignIn;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.WelcomWindowTheme);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqliteHelper = new LocalSqliteHelper(this);

        fragmentSignIn = new FragmentSignIn();
        fragmentSignUp = new FragmentSignUp();

        fragmentTransaction = getFragmentManager().beginTransaction();

        if (!sqliteHelper.doesDatabaseExists(this, this.getResources().getString(R.string.database_name))) {
            fragmentTransaction.add(R.id.id_main_Container, fragmentSignUp);
            fragmentTransaction.commit();
        } else {
            if (!FlagAccess.checkNetWorkAccess(this.getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "ops, check your connection with internet \n now read only mode!", Toast.LENGTH_SHORT).show();
            }
            fragmentTransaction.add(R.id.id_main_Container, fragmentSignIn);
            fragmentTransaction.commit();

        }

    }
}

