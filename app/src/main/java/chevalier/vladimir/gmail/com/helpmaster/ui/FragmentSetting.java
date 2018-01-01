package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

public class FragmentSetting extends Fragment {

    private LocalSqliteHelper sqliteHelper;

    private EditText etExistsEmail;
    private EditText etNewPassword;
    private Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sqliteHelper = new LocalSqliteHelper(getContext());

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        etExistsEmail = (EditText) view.findViewById(R.id.id_f_setting_exists_email);
        etNewPassword = (EditText) view.findViewById(R.id.id_f_setting_new_password);
        btn = (Button) view.findViewById(R.id.id_f_setting_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqliteHelper.updatePasswordCurrentUserApp(FlagAccess.MAIL_CURRENT_USERAPP.trim(), etNewPassword.getText().toString().trim());
            }
        });

        return view;
    }


}
