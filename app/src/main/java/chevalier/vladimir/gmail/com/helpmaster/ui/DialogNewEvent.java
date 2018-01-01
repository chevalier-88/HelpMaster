package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.SalaryItem;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

/**
 * Created by chevalier on 17.08.17.
 */

public class DialogNewEvent extends DialogFragment {

    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private Spinner spinnerDay;
    private Spinner spinnerHour;
    private Spinner spinnerMin;
    private AutoCompleteTextView acClientName;
    private EditText etStaffName;
    private Spinner spinnerService;
    private EditText etDiscount;
    private EditText etConsumerPhoneNumber;
    private Button btnAdd;

    private LocalSqliteHelper localDB;
    private List<String> listServices;
    private List<String> listNameRegConsumers;
    private Handler handler;
    private EventItem event;
    private FragmentCurrentEvents fragmentCurrentEvents;
    private FragmentSalary fragmentSalary;

    private static final int SUCCESSFUL_ADD = 1;
    private static final int OPS = 0;

    private Bundle bundle;
    private EventItem eventExist;

    private int flag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        View view = inflater.inflate(R.layout.dialog_new_events, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        localDB = new LocalSqliteHelper(this.getContext());
        listNameRegConsumers = localDB.getListNameConsumers();

        switch (getTargetRequestCode()) {
            case FragmentCurrentEvents.TARGET_CODE_NEW_EVENT:
                makeDialogNewEvent(view);
                break;
            case FragmentCurrentEvents.TARGET_CODE_EXISTS_EVENT:
                makeDialogExistsEvent(view);//TODO
                break;
            case FragmentSalary.TARGET_CODE_EXISTS:
                makeDialogExistsEvent(view);//TODO
                break;
        }
        return view;
    }

    private void makeDialogNewEvent(View v) {
        fragmentCurrentEvents = (FragmentCurrentEvents) getTargetFragment();
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESSFUL_ADD:

                        fragmentCurrentEvents.addEvent(getEvent());
                        handler = null;
                        dismiss();
                        break;
                    case OPS:
                        Toast.makeText(getActivity(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        spinnerYear = (Spinner) v.findViewById(R.id.id_dialog_spinner_year);
        spinnerYear.setAdapter(this.getYearsList());
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerYear.getSelectedItem().toString().equals("year")) {
                    spinnerMonth.setEnabled(true);
                } else {
                    spinnerMonth.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth = (Spinner) v.findViewById(R.id.id_dialog_spinner_month);
        spinnerMonth.setEnabled(false);
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerMonth.getSelectedItem().toString().equals("month")) {
                    spinnerDay.setAdapter(DialogNewEvent.this.getDaysList());
                    spinnerDay.setEnabled(true);
                } else {
                    spinnerDay.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDay = (Spinner) v.findViewById(R.id.id_dialog_spinner_day);

        spinnerHour = (Spinner) v.findViewById(R.id.id_dialog_spinner_hour);
        spinnerHour.setAdapter(this.getHoursList());

        spinnerMin = (Spinner) v.findViewById(R.id.id_dialog_spinner_minutes);
        spinnerMin.setAdapter(this.getMinutesList());

        etDiscount = (EditText) v.findViewById(R.id.id_dialog_et_discount);
        etConsumerPhoneNumber = (EditText) v.findViewById(R.id.id_dialog_et_phone_number);
        ArrayAdapter<String> adapterConsumersName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listNameRegConsumers);
        acClientName = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_client_name);
        acClientName.setAdapter(adapterConsumersName);
        acClientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Consumer tmpConsumer = localDB.getConsumer(acClientName.getAdapter().getItem(position).toString().trim());
                etDiscount.setText("" + tmpConsumer.getDiscount());
                etConsumerPhoneNumber.setText("" + tmpConsumer.getPhoneNumber());
            }
        });
        etStaffName = (EditText) v.findViewById(R.id.id_dialog_staff_name);
        etStaffName.setText(FlagAccess.NAME_USERAPP);

        spinnerService = (Spinner) v.findViewById(R.id.id_dialog_spinner_service);
        spinnerService.setAdapter(this.getServicesList());

        btnAdd = (Button) v.findViewById(R.id.id_dialog_event_btn);
        btnAdd.setEnabled(spinnerService.getCount() > 1 ? true : false);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    event = getEvent();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (event != null) {
                                localDB.writeNewEvent(event);
                                handler.sendMessage(handler.obtainMessage(SUCCESSFUL_ADD));
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

    private void makeDialogExistsEvent(View v) {
        bundle = getArguments();
        eventExist = (EventItem) bundle.getSerializable("event");

        String[] strFullDate = eventExist.getDate().split("_");
        String date = strFullDate[0].trim();
        String time = strFullDate[1].trim();
        switch (this.getFlag()) {
            case 200:
                fragmentCurrentEvents = (FragmentCurrentEvents) getTargetFragment();
                break;
            case 100:
                fragmentSalary = (FragmentSalary) getTargetFragment();
                break;
        }

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SUCCESSFUL_ADD:
                        if (fragmentCurrentEvents != null)
                            fragmentCurrentEvents.updateEvent(bundle.getInt("index"), event);
                        if (fragmentSalary != null) {
                            SalaryItem item = new SalaryItem();
                            item.setDate(event.getDate());
                            item.setServiceName(event.getService());
                            item.setConsumerName(event.getConsumer());
                            item.setSum((double) localDB.getService(event.getService()).getCostService());
                            fragmentSalary.updateAdapter(bundle.getInt("index"), item);
                        }
                        handler = null;
                        dismiss();
                        break;
                    case OPS:
                        Toast.makeText(getActivity(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        };


        spinnerYear = (Spinner) v.findViewById(R.id.id_dialog_spinner_year);
        spinnerYear.setAdapter(this.getYearsList());
        spinnerYear.setSelection(((ArrayAdapter<String>) spinnerYear.getAdapter()).getPosition(date.split("-")[0].trim()));

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerYear.getSelectedItem().toString().equals("year")) {
                    spinnerMonth.setEnabled(true);
                } else {
                    spinnerMonth.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth = (Spinner) v.findViewById(R.id.id_dialog_spinner_month);
        spinnerMonth.setSelection(((ArrayAdapter<String>) spinnerMonth.getAdapter()).getPosition(date.split("-")[1].trim()));

        spinnerDay = (Spinner) v.findViewById(R.id.id_dialog_spinner_day);
        spinnerDay.setAdapter(DialogNewEvent.this.getDaysList());
        spinnerDay.setSelection(((ArrayAdapter<Integer>) spinnerDay.getAdapter()).getPosition(Integer.valueOf(date.split("-")[2].trim())));


        spinnerHour = (Spinner) v.findViewById(R.id.id_dialog_spinner_hour);
        spinnerHour.setAdapter(this.getHoursList());
        spinnerHour.setSelection(((ArrayAdapter<Integer>) spinnerHour.getAdapter()).getPosition(Integer.valueOf(time.split(":")[0].trim())));

        spinnerMin = (Spinner) v.findViewById(R.id.id_dialog_spinner_minutes);
        spinnerMin.setAdapter(this.getMinutesList());
        spinnerMin.setSelection(((ArrayAdapter<Integer>) spinnerMin.getAdapter()).getPosition(Integer.valueOf(time.split(":")[1].trim())));


        etDiscount = (EditText) v.findViewById(R.id.id_dialog_et_discount);
        etDiscount.setText("" + eventExist.getDiscount());
        etConsumerPhoneNumber = (EditText) v.findViewById(R.id.id_dialog_et_phone_number);
        etConsumerPhoneNumber.setText(eventExist.getPhoneConsumer());
        ArrayAdapter<String> adapterConsumersName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listNameRegConsumers);
        acClientName = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_client_name);
        acClientName.setAdapter(adapterConsumersName);
        acClientName.setText(eventExist.getConsumer().toString().trim());
        acClientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Consumer tmpConsumer = localDB.getConsumer(acClientName.getAdapter().getItem(position).toString().trim());
                etDiscount.setText("" + tmpConsumer.getDiscount());

                etConsumerPhoneNumber.setText("" + tmpConsumer.getPhoneNumber());


            }
        });

        etStaffName = (EditText) v.findViewById(R.id.id_dialog_staff_name);
        etStaffName.setText(eventExist.getStaff().toString().trim());

        spinnerService = (Spinner) v.findViewById(R.id.id_dialog_spinner_service);
        spinnerService.setAdapter(this.getServicesList());
        spinnerService.setSelection(((ArrayAdapter<String>) spinnerService.getAdapter()).getPosition(eventExist.getService().toString().trim()));

        btnAdd = (Button) v.findViewById(R.id.id_dialog_event_btn);
        btnAdd.setEnabled(spinnerService.getCount() > 1 ? true : false);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formValidation()) {
                    event = getEvent();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (event != null) {
                                localDB.updateExistsEvent(eventExist, event);
                                handler.sendMessage(handler.obtainMessage(SUCCESSFUL_ADD));
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

    private EventItem getEvent() {
        EventItem result = new EventItem();
        String date = spinnerYear.getSelectedItem().toString() + " - " +
                spinnerMonth.getSelectedItem().toString() + " - " + spinnerDay.getSelectedItem().toString();
        String time = spinnerHour.getSelectedItem().toString() + " : " + spinnerMin.getSelectedItem().toString();

        result.setDate(date + " _ " + time);
        result.setConsumer(acClientName.getText().toString().trim());
        result.setPhoneConsumer(etConsumerPhoneNumber.getText().toString().trim().length() == 0 ? "-" : etConsumerPhoneNumber.getText().toString().trim());
        result.setStaff(etStaffName.getText().toString().trim());
        result.setService(spinnerService.getSelectedItem().toString());
        result.setIdService((int) spinnerService.getSelectedItemId());
        if (etDiscount.getText().toString().trim().equals("")) {
            result.setDiscount(0);
        } else {
            result.setDiscount(Integer.parseInt(etDiscount.getText().toString().trim()));
        }
        return result;
    }

    private ArrayAdapter getYearsList() {
        List<String> listYears = new LinkedList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        listYears.add("year");
        listYears.add("" + (currentYear - 1));
        listYears.add("" + currentYear);
        listYears.add("" + (currentYear + 1));
        listYears.add("" + (currentYear + 2));
        ArrayAdapter<String> result = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listYears);
        return result;
    }


    private ArrayAdapter getDaysList() {
        Integer[] array = null;
        if (spinnerYear.getSelectedItem().toString().equals("year") || spinnerMonth.getSelectedItem().toString().equals("month")) {
            Toast.makeText(getActivity(), "Please, selected year and month", Toast.LENGTH_SHORT).show();
        } else {
            Integer year = Integer.valueOf(spinnerYear.getSelectedItem().toString());
            if (((year % 4) == 0) && (spinnerMonth.getSelectedItemId() == 2)) {
                array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
            } else {
                if (spinnerMonth.getSelectedItemId() == 2) {
                    array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
                } else if ((spinnerMonth.getSelectedItemId() == 1) || (spinnerMonth.getSelectedItemId() == 3) ||
                        (spinnerMonth.getSelectedItemId() == 5) || (spinnerMonth.getSelectedItemId() == 7) ||
                        (spinnerMonth.getSelectedItemId() == 8) || (spinnerMonth.getSelectedItemId() == 10) ||
                        (spinnerMonth.getSelectedItemId() == 12)) {
                    array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31};
                } else if ((spinnerMonth.getSelectedItemId() == 4) || (spinnerMonth.getSelectedItemId() == 6) ||
                        (spinnerMonth.getSelectedItemId() == 9) || (spinnerMonth.getSelectedItemId() == 11)) {
                    array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
                }
            }
        }
        List<Integer> listDays = Arrays.asList(array);


        ArrayAdapter<Integer> result = new ArrayAdapter<Integer>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listDays);
        return result;
    }

    private ArrayAdapter getHoursList() {
        List<Integer> listHours = new ArrayList<>();
        listHours.add(8);
        listHours.add(9);
        listHours.add(10);
        listHours.add(11);
        listHours.add(12);
        listHours.add(13);
        listHours.add(14);
        listHours.add(15);
        listHours.add(16);
        listHours.add(17);
        listHours.add(18);
        listHours.add(19);
        listHours.add(20);
        listHours.add(21);
        ArrayAdapter<Integer> result = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listHours);
        return result;

    }

    private ArrayAdapter getMinutesList() {
        List<Integer> listMinutes = new ArrayList<>();
        listMinutes.add(0);
        listMinutes.add(5);
        listMinutes.add(10);
        listMinutes.add(15);
        listMinutes.add(20);
        listMinutes.add(25);
        listMinutes.add(30);
        listMinutes.add(35);
        listMinutes.add(40);
        listMinutes.add(45);
        listMinutes.add(50);
        listMinutes.add(55);
        ArrayAdapter<Integer> result = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, listMinutes);
        return result;

    }

    private ArrayAdapter getServicesList() {
        if (localDB == null)
            localDB = new LocalSqliteHelper(getContext());
        listServices = localDB.getListServicesName();
        ArrayAdapter<String> result = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listServices);

        return result;
    }

    private boolean formValidation() {
        if (!spinnerYear.getSelectedItem().toString().equals("year") &&
                !spinnerMonth.getSelectedItem().toString().equals("month") &&
                !acClientName.getText().toString().equals("") &&
                !spinnerService.getSelectedItem().toString().equals(getContext().getResources().getString(R.string.select_service))) {
            return true;
        } else {
            return false;
        }

    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
