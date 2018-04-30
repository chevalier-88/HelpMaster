package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;


public class FragmentSignIn extends Fragment {


    private LinearLayout signInForm;
    private EditText fieldMail;
    private EditText fieldPassword;
    private ImageView imgLogo;

    public static final int ANIM_ITEM_DURATION = 1000;
    private static boolean status = false;

    private ValueEventListener listener;
    private UserApp userFromFirebase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isAccessToInternet(getActivity())) {
                    if (validationForm()) {
                        userFromFirebase = dataSnapshot.child(getActivity().getResources().getString(R.string.branch_users)).
                                child(fieldMail.getText().toString().trim().
                                        replace(".", "~")).
                                child(getActivity().getResources().getString(R.string.subbranch_personal_data)).getValue(UserApp.class);
                    }
                    if (fieldMail.getText().toString().trim().equals(userFromFirebase.getEmail().toString()) &&
                            fieldPassword.getText().toString().trim().equals(userFromFirebase.getPassword().toString())) {

                        Intent intent = new Intent(getActivity().getBaseContext(), HomeActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        fieldMail.setError(getActivity().getResources().getString(R.string.msg_no_correct_fields));
                        fieldPassword.setError(getActivity().getResources().getString(R.string.msg_no_correct_fields));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        status = true;

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        signInForm = (LinearLayout) view.findViewById(R.id.id_s_i_form);

        imgLogo = (ImageView) view.findViewById(R.id.id_s_i_img_logo);

        fieldMail = (EditText) view.findViewById(R.id.id_s_i_sign_in);
        fieldPassword = (EditText) view.findViewById(R.id.id_s_i_e_t_password);

        view.findViewById(R.id.id_s_i_btn_signin).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isAccessToInternet(getActivity())) {
                    if (validationForm()) {
                        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(listener);
                    }
                } else {
//NOP
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().removeEventListener(listener);
    }

    private boolean validationForm() {
        boolean result;
        if ((fieldMail.getText() != null && fieldMail.getText().toString().length() > 0) &&
                (fieldPassword.getText() != null && fieldPassword.getText().toString().length() > 0)) {
            result = true;
        } else {
            Toast.makeText(getActivity(), this.getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    private boolean isAccessToInternet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info == null ? false : info.isConnectedOrConnecting());
    }

}


