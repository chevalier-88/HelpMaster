package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;


public class FragmentSignUp extends Fragment {

    private ValueEventListener listener;


    private EditText etName;
    private EditText etSurname;
    private EditText etPhone;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfPassword;


    private SharedPreferences sp;
    private static final String PERSONAL_DATA = "PERSONAL_DATA";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseApp.getApps(getActivity()).isEmpty())
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    sp = getActivity().getSharedPreferences(PERSONAL_DATA, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    String userName = etName.getText().toString().trim();
                    String userSurname = etSurname.getText().toString().trim();
                    String userPhone = etPhone.getText().toString().trim();
                    String userEmail = etEmail.getText().toString().trim();

                    editor.putString(getActivity().getResources().getString(R.string.key_name_current_user), userName + "" + userSurname);
                    editor.putString(getActivity().getResources().getString(R.string.key_phone_current_user), userPhone);
                    editor.putString(getActivity().getResources().getString(R.string.key_mail_current_user), userEmail);
                    editor.putInt(getActivity().getResources().getString(R.string.sort_events), getActivity().getResources().getInteger(R.integer.default_sorted_key));
                    editor.putBoolean(getActivity().getResources().getString(R.string.notification_mode), getActivity().getResources().getBoolean(R.bool.default_notification_mode));
                    editor.commit();

                } catch (
                        Exception ioe) {
                    FirebaseDatabase.getInstance().getReference().child(getActivity().getResources().getString(R.string.branch_users)).removeValue();
                } finally {
                    Toast.makeText(getActivity().getApplicationContext(), getActivity().getResources().getString(R.string.rest), Toast.LENGTH_SHORT).show();
                    restartApp();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                FirebaseDatabase.getInstance().getReference().child(getActivity().getResources().getString(R.string.branch_users)).removeValue();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        etName = (EditText) view.findViewById(R.id.id_s_up_name);
        etSurname = (EditText) view.findViewById(R.id.id_s_up_surname);
        etPhone = (EditText) view.findViewById(R.id.id_s_up_phone);
        etEmail = (EditText) view.findViewById(R.id.id_s_up_e_mail);
        etPassword = (EditText) view.findViewById(R.id.id_s_up_password);
        etConfPassword = (EditText) view.findViewById(R.id.id_s_up_confirm_password);

        view.findViewById(R.id.id_s_up_btn_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccessToInternet(getActivity())) {
                    if (formValidation()) {

                        FirebaseDatabase.getInstance().getReference().child(getActivity().getResources().getString(R.string.branch_users)).child(etEmail.getText().toString().replace(".", "~")).child("personal data").setValue(getUserApp());
                        FirebaseDatabase.getInstance().getReference().addValueEventListener(listener);
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_no_connection), Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().removeEventListener(listener);
    }

    private void restartApp() {
        Intent mStartActivity = new Intent(getActivity(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getActivity(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);

    }


    private UserApp getUserApp() {
        UserApp result = new UserApp();
        result.setName(etName.getText().toString().trim());
        result.setSurname(etSurname.getText().toString().trim());
        result.setPhone(etPhone.getText().toString().trim());
        result.setEmail(etEmail.getText().toString().trim());
        result.setPassword(etPassword.getText().toString().trim());
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
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    private boolean isAccessToInternet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info == null ? false : info.isConnectedOrConnecting());
    }
}

