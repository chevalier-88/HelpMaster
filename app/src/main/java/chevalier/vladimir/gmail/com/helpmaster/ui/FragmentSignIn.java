package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;


public class FragmentSignIn extends Fragment {

    public static final String TAG = "verify_message";

    private LocalSqliteHelper localSqliteHandler;
    private LinearLayout signInForm;
    private EditText fieldMail;
    private EditText fieldPassword;
    private Button btnSignIn;
    private ImageView imgLogo;

    public static final int ANIM_ITEM_DURATION = 1000;
    private static boolean status = false;

    //
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseDatabase database;
//    private DatabaseReference ref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //TODO
                    Log.i(TAG, "user is exists and signIn");
                } else {
                    Log.i(TAG, "user is null, and ist signOut");
                    //TODO
                }

            }
        };

//        database = FirebaseDatabase.getInstance();
//        ref = database.getReference();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        status = true;
        localSqliteHandler = new LocalSqliteHelper(getActivity());


        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInForm = (LinearLayout) view.findViewById(R.id.id_s_i_form);

        imgLogo = (ImageView) view.findViewById(R.id.id_s_i_img_logo);

        fieldMail = (EditText) view.findViewById(R.id.id_s_i_sign_in);
        fieldPassword = (EditText) view.findViewById(R.id.id_s_i_e_t_password);

        btnSignIn = (Button) view.findViewById(R.id.id_s_i_btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            UserApp currentUserApp;

            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick for button");
                if (FlagAccess.checkNetWorkAccess(getActivity())) {
                    Log.i(TAG, "checkNetWork...");
                    if (validationForm()){
                        Log.i(TAG, "check form");
                        mAuth.signInWithEmailAndPassword(fieldMail.getText().toString().trim(), fieldPassword.getText().toString().trim()).addOnCompleteListener(
                                getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()&& ((currentUserApp = localSqliteHandler.getUserApp(fieldMail.getText().toString(), fieldPassword.getText().toString())) != null)) {
                                            FlagAccess.NAME_USERAPP = currentUserApp.getName() + " " + currentUserApp.getSurname();
                                            FlagAccess.MAIL_CURRENT_USERAPP = fieldMail.getText().toString();

                                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                            Log.i(TAG, "operation is succesfull");
                                        } else {
                                            fieldMail.setError("verify");
                                            fieldPassword.setError("verify");
                                        }
                                    }
                                }
                        );
                    }
                } else {
//                    if (validationForm()) {
////                        UserApp currentUserApp;
//                        if ((currentUserApp = localSqliteHandler.getUserApp(fieldMail.getText().toString(), fieldPassword.getText().toString())) != null) {
//                            FlagAccess.NAME_USERAPP = currentUserApp.getName() + " " + currentUserApp.getSurname();
//                            FlagAccess.MAIL_CURRENT_USERAPP = fieldMail.getText().toString();
//
//
//                            Intent intent = new Intent(getActivity(), HomeActivity.class);
//                            startActivity(intent);
//                            getActivity().finish();
//                        } else {
//                            Toast.makeText(getActivity(), "user with such data don't exists!!!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                    Toast.makeText(getActivity(), "ops, check your connection with internet \n now you can only read!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (status) {
            status = false;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int heightDisplay = displaymetrics.heightPixels;
            int heightLogoImageView = imgLogo.getHeight();
            int translateLogoImageView = (heightDisplay / 3) - (heightLogoImageView);

            ViewCompat.animate(imgLogo).translationY(-translateLogoImageView).scaleX(0.8f).scaleY(0.8f).setDuration(ANIM_ITEM_DURATION).start();
            ViewCompat.animate(signInForm).alpha(1.0f).setStartDelay(ANIM_ITEM_DURATION).setDuration(ANIM_ITEM_DURATION).start();
        }
    }

    private boolean validationForm() {
        boolean result;
        if ((fieldMail.getText() != null && fieldMail.getText().toString().length() > 0) &&
                (fieldPassword.getText() != null && fieldPassword.getText().toString().length() > 0)) {
            result = true;
        } else {
            Toast.makeText(getActivity(), "ops!!! \n check your field and try again", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "call method stop in fragmentSignIn");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}
