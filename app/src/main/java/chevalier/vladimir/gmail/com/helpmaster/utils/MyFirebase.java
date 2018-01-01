package chevalier.vladimir.gmail.com.helpmaster.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;
import chevalier.vladimir.gmail.com.helpmaster.ui.HomeActivity;

/**
 * Created by chevalier on 22.11.17.
 */

public class MyFirebase {
    private static MyFirebase myFirebase;

    private MyFirebase() {
        if (FirebaseApp.getApps(mContext).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if (mAuth == null) mAuth = FirebaseAuth.getInstance();
        if (mAuthListener == null) mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (user == null) user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }

            }
        };

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = mAuth.getCurrentUser();
    }

    public static MyFirebase getInstance(Context context) {
        mContext = context;
        if (myFirebase == null) myFirebase = new MyFirebase();
        return myFirebase;
    }


    public static final String TAG = "verify_message";

    private static Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private UserApp currentUserApp;

    public void signIn(final String mail, String password, final Activity activity) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    String i = mail;
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //TODO if all is successful forward to HomeActivity
//                            if (task.isSuccessful()&& ((currentUserApp = localSqliteHandler.getUserApp(fieldMail.getText().toString(), fieldPassword.getText().toString())) != null)) {
                                FlagAccess.NAME_USERAPP = currentUserApp.getName() + " " + currentUserApp.getSurname();
                                FlagAccess.MAIL_CURRENT_USERAPP = mail;

                                Intent intent = new Intent(activity, HomeActivity.class);
                                activity.startActivity(intent);
                                activity.finish();
                                Log.i(TAG, "operation is succesfull");
//                            } else {
//                                fieldMail.setError("verify");
//                                fieldPassword.setError("verify");
//                            }
                        }

                    }
                });
    }
}
