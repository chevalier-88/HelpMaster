package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;

/**
 * Created by chevalier on 19.08.17.
 */

public class DialogNewService extends DialogFragment {

    private EditText serviceName;
    private EditText serviceDuration;
    private EditText serviceCost;
    private EditText serviceFirstCost;
    private EditText serviceDescription;
    private Button btnAdd;
    private FragmentServices fragmentServices;
    private Handler handler;
    private Service service;
    private Bundle b;

    private static final int ADD_SERVICE = 1;
    private static final int OPS = 2;

    private String oldNameService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        View view = inflater.inflate(R.layout.dialog_new_service, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        switch (getTargetRequestCode()) {
            case FragmentServices.TARGET_CODE_NEW_SERVICE:
                this.makeDialogNewService(view);
                break;
            case FragmentServices.TARGET_CODE_EXISTS_SERVICE:
                this.makeDialogExistsService(view);
                break;
        }


        return view;
    }

    @SuppressLint("HandlerLeak")
    private void makeDialogNewService(View v) {
        serviceName = (EditText) v.findViewById(R.id.id_dialog_service_name);
        serviceDuration = (EditText) v.findViewById(R.id.id_dialog_service_duration);
        serviceCost = (EditText) v.findViewById(R.id.id_dialog_service_cost);
        serviceFirstCost = (EditText) v.findViewById(R.id.id_dialog_service_first_cost);
        serviceDescription = (EditText) v.findViewById(R.id.id_dialog_service_description);
        fragmentServices = (FragmentServices) getTargetFragment();
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADD_SERVICE:
                        fragmentServices.addServiceDescription(service);
                        handler = null;
                        dismiss();
                        break;
                    case OPS:
                        Toast.makeText(getActivity(), getContext().getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        };
        btnAdd = (Button) v.findViewById(R.id.id_dialog_service_btn);
        btnAdd.setText(R.string.id_dialog_service_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            //                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {

                if (formValidation()) {
                    service = getService();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (service != null) {
                                handler.sendMessage(handler.obtainMessage(ADD_SERVICE));
//                                handler.sendEmptyMessage(ADD_SERVICE);
                            } else {
                                handler.sendMessage(handler.obtainMessage(OPS));
                            }
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(getContext(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    private void makeDialogExistsService(View v) {

        b = getArguments();
        Service s = (Service) b.getSerializable("service");
        oldNameService = s.getNameService();

        serviceName = (EditText) v.findViewById(R.id.id_dialog_service_name);
        serviceName.setText(s.getNameService());
        serviceDuration = (EditText) v.findViewById(R.id.id_dialog_service_duration);
        serviceDuration.setText("" + s.getDurationService());
        serviceCost = (EditText) v.findViewById(R.id.id_dialog_service_cost);
        serviceCost.setText("" + s.getCostService());
        serviceFirstCost = (EditText) v.findViewById(R.id.id_dialog_service_first_cost);
        serviceFirstCost.setText("" + (double) s.getFirstCostService());
        serviceDescription = (EditText) v.findViewById(R.id.id_dialog_service_description);
        serviceDescription.setText(s.getDescriptionService());
        fragmentServices = (FragmentServices) getTargetFragment();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADD_SERVICE:
                        fragmentServices.updateServiceDescription(b.getInt("index"), service);
                        handler = null;
                        dismiss();
                        break;
                    case OPS:
                        Toast.makeText(getActivity(), getContext().getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        };
        btnAdd = (Button) v.findViewById(R.id.id_dialog_service_btn);
        btnAdd.setText(R.string.id_dialog_service_btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            //                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {

                if (formValidation()) {
                    service = getService();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (service != null) {
//---------------------- > replaice for firebase --> sqliteHelper.editService(oldNameService, service);// if service was changed--------------------------------------------
                                handler.sendMessage(handler.obtainMessage(ADD_SERVICE));
                            } else {
                                handler.sendMessage(handler.obtainMessage(OPS));
                            }
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(getContext(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Service getService() {
        Service s = null;
        String sName = serviceName.getText().toString().trim().length() > 0 ? serviceName.getText().toString().trim() : null;
        Integer sDuration = serviceDuration.getText().toString().trim().length() > 0 ? Integer.valueOf(serviceDuration.getText().toString().trim()) : null;
        Integer sCost = serviceCost.getText().toString().trim().length() > 0 ? Integer.valueOf(serviceCost.getText().toString().trim()) : null;
        Double sFirstCost = serviceFirstCost.getText().toString().trim().length() > 0 ? Double.valueOf(serviceFirstCost.getText().toString().trim()) : null;
        String sDescription = serviceDescription.getText().toString().trim().length() > 0 ? serviceDescription.getText().toString() : null;

        if (sName != null && sDuration != null && sCost != null && sDescription != null && sFirstCost != null) {
            s = new Service();
            s.setNameService(sName);
            s.setDurationService(sDuration);
            s.setCostService(sCost);
            s.setFirstCostService(sFirstCost);
            s.setDescriptionService(sDescription);
        }
        return s;
    }

    private boolean formValidation() {
        if ((serviceName.getText().toString().length() > 0) &&
                (serviceDuration.getText().toString().length() > 0) &&
                (serviceCost.getText().toString().length() > 0) &&
                (serviceFirstCost.getText().toString().length() > 0) &&
                (serviceDescription.getText().toString().length() > 0)) {
            return true;
        } else {
            return false;
        }
    }
}
