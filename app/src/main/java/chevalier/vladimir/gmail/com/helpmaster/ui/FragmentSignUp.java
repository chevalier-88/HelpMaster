package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;


public class FragmentSignUp extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseDatabase database;
//    private DatabaseReference ref;

    private EditText etName;
    private EditText etSurname;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfPassword;


    private LocalSqliteHelper localHelper;


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
//                    Log.i(TAG, "user is exists and signIn");
                } else {
//                    Log.i(TAG, "user is null, and ist signOut");
                    //TODO
                }

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        localHelper = new LocalSqliteHelper(getActivity());

        etName = (EditText) view.findViewById(R.id.id_s_up_name);
        etSurname = (EditText) view.findViewById(R.id.id_s_up_surname);
        etPhone = (EditText) view.findViewById(R.id.id_s_up_phone);
        etEmail = (EditText) view.findViewById(R.id.id_s_up_e_mail);
        etPassword = (EditText) view.findViewById(R.id.id_s_up_password);
        etConfPassword = (EditText) view.findViewById(R.id.id_s_up_confirm_password);

        Button btnSignUp = (Button) view.findViewById(R.id.id_s_up_btn_reg);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FlagAccess.checkNetWorkAccess(getActivity())) {
                    if (formValidation()) {
                        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Ops, \n user with such data can not be registration \n maybe such user already registered", Toast.LENGTH_LONG).show();
                                        } else {
                                            localHelper.writeNewUserApp(getUserApp());
                                            restartApp();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Ops, \n please verify your data \n and try again or close application", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Ops, \n please verify your connection to internet \n and try again or close application", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void restartApp() {
        Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        mStartActivity.setFlags(mStartActivity.FLAG_ACTIVITY_CLEAR_TASK);
//        mStartActivity.addFlags(mStartActivity.FLAG_ACTIVITY_NEW_TASK);
        System.exit(0);

    }


    private UserApp getUserApp() {
        UserApp result = new UserApp();
        result.setName(etName.getText().toString());
        result.setSurname(etSurname.getText().toString());
        result.setPhone(etPhone.getText().toString());
        result.setEmail(etEmail.getText().toString());
        result.setPassword(etPassword.getText().toString());
        return result;
    }

    private boolean formValidation() {
        boolean result;
        if ((etName.getText() != null && etName.getText().toString().length() > 0) &&
                (etSurname.getText() != null && etSurname.getText().toString().length() > 0) &&
                (etPhone.getText() != null && etPhone.getText().toString().length() > 0) &&
                (etEmail.getText() != null && etEmail.getText().toString().length() > 0) &&
                (etPassword.getText() != null && etPassword.getText().toString().length() > 0) &&
                (etConfPassword.getText() != null && etConfPassword.getText().toString().length() > 0) &&
                (etPassword.getText().toString().equals(etConfPassword.getText().toString()))) {
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
//        Log.i(TAG, "call method stop in fragmentSignIn");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


