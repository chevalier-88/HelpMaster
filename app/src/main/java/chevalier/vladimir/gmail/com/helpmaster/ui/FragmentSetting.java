package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import chevalier.vladimir.gmail.com.helpmaster.R;

public class FragmentSetting extends Fragment implements View.OnClickListener {

    private RadioButton rbtnWeek;
    private RadioButton rbtnMonth;
    private RadioButton rbtnAll;
    private CheckBox chbNotification;
    private Button btnSave;


    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private static final String PERSONAL_DATA = "PERSONAL_DATA";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        editor = getActivity().getSharedPreferences(PERSONAL_DATA, Context.MODE_PRIVATE).edit();


        rbtnWeek = (RadioButton) view.findViewById(R.id.id_r_b_week);
        rbtnWeek.setOnClickListener(this);
        rbtnMonth = (RadioButton) view.findViewById(R.id.id_r_b_month);
        rbtnMonth.setOnClickListener(this);
        rbtnAll = (RadioButton) view.findViewById(R.id.id_r_b_all);
        rbtnAll.setOnClickListener(this);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup1);
        rg.check(rg.getChildAt(HomeActivity.SORTED_FLAG - 1).getId());

        chbNotification = (CheckBox) view.findViewById(R.id.id_ch_b_notif);
        chbNotification.setChecked(HomeActivity.NOTIFICATION_MODE);
        chbNotification.setOnClickListener(this);

        btnSave = (Button) view.findViewById(R.id.id_btn_save);
        btnSave.setOnClickListener(this);
        btnSave.setVisibility(View.INVISIBLE);

        return view;
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.id_r_b_week:
                editor.putInt(getContext().getResources().getString(R.string.sort_events), 1);
                HomeActivity.SORTED_FLAG = 1;
                break;
            case R.id.id_r_b_month:
                editor.putInt(getContext().getResources().getString(R.string.sort_events), 2);
                HomeActivity.SORTED_FLAG = 2;
                break;
            case R.id.id_r_b_all:
                editor.putInt(getContext().getResources().getString(R.string.sort_events), 3);
                HomeActivity.SORTED_FLAG = 3;
                break;
            case R.id.id_ch_b_notif:
                editor.putBoolean(getContext().getResources().getString(R.string.notification_mode), chbNotification.isChecked());
                break;
            case R.id.id_btn_save:
                break;
        }
        editor.commit();
    }
}
