package chevalier.vladimir.gmail.com.helpmaster.ui;


import android.annotation.SuppressLint;
import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;
import chevalier.vladimir.gmail.com.helpmaster.entities.EventItem;
import chevalier.vladimir.gmail.com.helpmaster.entities.Service;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;

/**
 * Created by chevalier on 17.08.17.
 */

public class DialogNewEvent extends DialogFragment {

    private Spinner spinnerYear;
    private Spinner spinnerMonth;
    private Spinner spinnerDay;
    private Spinner spinnerHour;
    private Spinner spinnerMin;
    private AutoCompleteTextView autoCompleteConsumer;


    private AutoCompleteTextView autoCompleteStaffName;

    private Spinner spinnerService;
    private EditText etDiscount;
    private EditText autoCompleteAdapterConsumers;
    private Button btnAdd;


    private List<String> listServicesName;
    private List<Service> listServices;
    private List<String> listNameRegConsumers;
    private List<String> listStaffName;
    private Handler handler;


    private ArrayAdapter<String> servicesAdapter;
    private FragmentCurrentEvents fragmentCurrentEvents;
    private FragmentSalary fragmentSalary;

    private static final int MESSAGE_HANDLER_EVENT_COMPLETE = 1;
    private static final int MESSAGE_HANDLER_SERVICES_READY = 2;
    private static final int MESSAGE_HANDLER_FAIL = -1;

    private Bundle bundle;

    private int flag;


//    private ValueEventListener valueEvnetConsumer;
//    private ValueEventListener valueEventUsers;
//    private ValueEventListener valueEventRegconsumer;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listNameRegConsumers = this.getNameRegConsumer();
        listStaffName = this.getStaffName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);


        listServicesName = new ArrayList<>();
        listServicesName.add("услуга");
        listServices = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.child("services").getChildren()) {
                    Service service = d.getValue(Service.class);
                    listServices.add(service);
                    listServicesName.add(service.getNameService());
                }
                handler.sendEmptyMessage(MESSAGE_HANDLER_SERVICES_READY);
//                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //NOP
            }
        });

//        listNameRegConsumers = this.getNameRegConsumer();
//        listStaffName = this.getStaffName();//==========================++++++++++++++++++++++++++++++++++++
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
//        getDialog().getWindow().setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//        );
        View view = inflater.inflate(R.layout.dialog_new_events, container, false);
        getDialog().setCanceledOnTouchOutside(false);

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


    @SuppressLint("HandlerLeak")
    private void makeDialogNewEvent(View v) {
        fragmentCurrentEvents = (FragmentCurrentEvents) getTargetFragment();

        servicesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listServicesName);

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_HANDLER_EVENT_COMPLETE:
                        EventItem eventItem = (EventItem) msg.obj;
                        fragmentCurrentEvents.addEvent(eventItem);
                        handler = null;
                        dismiss();
                        break;
                    case MESSAGE_HANDLER_FAIL:
                        Toast.makeText(getActivity(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_HANDLER_SERVICES_READY:
                        servicesAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };


        spinnerYear = (Spinner) v.findViewById(R.id.id_dialog_spinner_year);
        spinnerYear.setAdapter(this.getYearsList());
        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerYear.getSelectedItem().toString().equals(getResources().getString(R.string.year))) {
                    spinnerMonth.setEnabled(true);
                    if (formValidation()) btnAdd.setEnabled(true);
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
                if (!spinnerMonth.getSelectedItem().toString().equals(getResources().getString(R.string.month))) {
                    spinnerDay.setAdapter(DialogNewEvent.this.getDaysList());
                    spinnerDay.setEnabled(true);
                    if (formValidation()) btnAdd.setEnabled(true);
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
        autoCompleteAdapterConsumers = (EditText) v.findViewById(R.id.id_dialog_et_phone_number);
        ArrayAdapter<String> adapterConsumersName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listNameRegConsumers);
        autoCompleteConsumer = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_client_name);
        autoCompleteConsumer.setAdapter(adapterConsumersName);
        autoCompleteConsumer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteConsumer.setSelection(0);
                getConsumerByKey(autoCompleteConsumer.getAdapter().getItem(position).toString().trim());
                if (formValidation()) btnAdd.setEnabled(true);
            }
        });
//        etStaffName = (EditText) v.findViewById(R.id.id_dialog_staff_name);
//        etStaffName.setText(getActivity().getIntent().getStringExtra("NAME_CURRENT_USER"));

        ArrayAdapter<String> adapterStaffName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listStaffName);
        autoCompleteStaffName = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_staff_name);
        autoCompleteStaffName.setText(HomeActivity.NAME_CURRENT_USER);
        autoCompleteStaffName.setAdapter(adapterStaffName);
        autoCompleteStaffName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteConsumer.setSelection(0);
//                getStaffName(autoCompleteStaffName.getAdapter().getItem(position).toString().trim());
                if (formValidation()) btnAdd.setEnabled(true);
            }
        });


        spinnerService = (Spinner) v.findViewById(R.id.id_dialog_spinner_service);
        spinnerService.setAdapter(servicesAdapter);
        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (formValidation()) btnAdd.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAdd = (Button) v.findViewById(R.id.id_dialog_event_btn);
        btnAdd.setEnabled(spinnerService.getCount() > 1 ? true : false);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (formValidation()) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EventItem obj = null;
                            if ((obj = getEvent()) != null) {
//                                handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_EVENT_COMPLETE));

//                                Bundle bundle = new Bundle();
//                                bundle.putParcelable("eventItem", (Parcelable) obj);

                                Message msg = new Message();
                                msg.obj = obj;
                                msg.what = MESSAGE_HANDLER_EVENT_COMPLETE;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_FAIL));
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
    private void makeDialogExistsEvent(View v) {
        bundle = getArguments();
        final EventItem eventExist = (EventItem) bundle.getSerializable("event");


        String[] strFullDate = eventExist.getDate().split(" ");
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

        servicesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listServicesName);
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_HANDLER_EVENT_COMPLETE:
                        if (fragmentCurrentEvents != null)
                            fragmentCurrentEvents.updateEvent(bundle.getInt("index"), getEvent(), eventExist.getDate() + " " + eventExist.getConsumer());
                        if (fragmentSalary != null) {
//                            SalaryItem item = new SalaryItem();
//                            EventItem event = getEvent();
//                            item.setDate(event.getDate());
//                            item.setServiceName(event.getService());
//                            item.setConsumerName(event.getConsumer());
//                            item.setSum((double) getService(event.getService()).getCostService());

//                            SalaryItem item = new SalaryItem();
//                            EventItem event = getEvent();
//                            item.setDate(item.getDate());
//                            item.setServiceName(event.getService());
//                            item.setConsumerName(event.getConsumer());
//                            item.setSum(((event.getCost() * (1 - (double) event.getDiscount() / (double) 100)) - event.getPrimeCost()) / 2);
//                            fragmentSalary.updateAdapter(bundle.getInt("index"), item);

                            EventItem event = getEvent();
                            fragmentSalary.updateAdapter(bundle.getInt("index"), event);
                        }
                        handler = null;
                        dismiss();
                        break;
                    case MESSAGE_HANDLER_FAIL:
                        Toast.makeText(getActivity(), R.string.msg_no_correct_fields, Toast.LENGTH_LONG).show();
                        break;
                    case MESSAGE_HANDLER_SERVICES_READY:
                        servicesAdapter.notifyDataSetChanged();
                        spinnerService.setSelection(((ArrayAdapter<String>) spinnerService.getAdapter()).getPosition(eventExist.getService().trim()));
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
                if (!spinnerYear.getSelectedItem().toString().equals(getResources().getString(R.string.year))) {
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
//        spinnerMonth.setSelection(((ArrayAdapter<String>) spinnerMonth.getAdapter()).getPosition(date.split("-")[1].trim()));
//        spinnerMonth.setSelection(((ArrayAdapter<String>) spinnerMonth.getAdapter()).getPosition(getResources().getStringArray(R.array.Months)[Integer.parseInt(date.split("-")[1].trim())]));
        spinnerMonth.setSelection(Integer.parseInt(date.split("-")[1].trim()));

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
        autoCompleteAdapterConsumers = (EditText) v.findViewById(R.id.id_dialog_et_phone_number);
        autoCompleteAdapterConsumers.setText(eventExist.getPhoneConsumer());
        ArrayAdapter<String> adapterConsumersName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listNameRegConsumers);
        autoCompleteConsumer = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_client_name);
        autoCompleteConsumer.setText(eventExist.getConsumer().toString().trim());
        autoCompleteConsumer.setAdapter(adapterConsumersName);
        autoCompleteConsumer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteConsumer.setSelection(0);
                getConsumerByKey(autoCompleteConsumer.getAdapter().getItem(position).toString().trim());
//                if (formValidation()) btnAdd.setEnabled(true);
            }
        });


//        etStaffName = (EditText) v.findViewById(R.id.id_dialog_staff_name);
//        etStaffName.setText(eventExist.getStaff().toString().trim());


        ArrayAdapter<String> adapterStaffName = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listStaffName);
        autoCompleteStaffName = (AutoCompleteTextView) v.findViewById(R.id.id_dialog_staff_name);
//        autoCompleteStaffName.setText(getActivity().getIntent().getStringExtra("NAME_CURRENT_USER"));
        autoCompleteStaffName.setText(HomeActivity.NAME_CURRENT_USER);
        autoCompleteStaffName.setAdapter(adapterStaffName);
        autoCompleteStaffName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteConsumer.setSelection(0);
//                getStaffName(autoCompleteStaffName.getAdapter().getItem(position).toString().trim());
                if (formValidation()) btnAdd.setEnabled(true);
            }
        });


        spinnerService = (Spinner) v.findViewById(R.id.id_dialog_spinner_service);
        spinnerService.setAdapter(servicesAdapter);
        spinnerService.setSelection(((ArrayAdapter<String>) spinnerService.getAdapter()).getPosition(eventExist.getService().trim()));
        btnAdd = (Button) v.findViewById(R.id.id_dialog_event_btn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (formValidation()) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (getEvent() != null) {
                                handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_EVENT_COMPLETE));
                            } else {
                                handler.sendMessage(handler.obtainMessage(MESSAGE_HANDLER_FAIL));
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
//        String date = spinnerYear.getSelectedItem().toString() + " - " +
//                spinnerMonth.getSelectedItem().toString() + " - " + spinnerDay.getSelectedItem().toString();
        String day = ((Integer.parseInt(spinnerDay.getSelectedItem().toString())) < 10) ? "0" + spinnerDay.getSelectedItem().toString() : spinnerDay.getSelectedItem().toString();
        String date = spinnerYear.getSelectedItem().toString() + "-" +
                spinnerMonth.getSelectedItemPosition() + "-" + day;
        String time = spinnerHour.getSelectedItem().toString() + ":" + spinnerMin.getSelectedItem().toString();


        result.setDate(date + " " + time);
        result.setConsumer(autoCompleteConsumer.getText().toString().trim());
        result.setPhoneConsumer(autoCompleteAdapterConsumers.getText().toString().trim().length() == 0 ? "-" : autoCompleteAdapterConsumers.getText().toString().trim());
//        result.setStaff(etStaffName.getText().toString().trim());
        result.setStaff(autoCompleteStaffName.getText().toString().trim());
        result.setService(spinnerService.getSelectedItem().toString());
        result.setIdService((int) spinnerService.getSelectedItemId());
        if (etDiscount.getText().toString().trim().equals("")) {
            result.setDiscount(0);
        } else {
            result.setDiscount(Integer.parseInt(etDiscount.getText().toString().trim()));
        }
        Service service = listServices.get(spinnerService.getSelectedItemPosition() - 1);
        result.setCost(service.getCostService());
        result.setStatus(false);
        result.setPrimeCost(service.getFirstCostService());
        return result;
    }

    private ArrayAdapter getYearsList() {
        List<String> listYears = new LinkedList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        listYears.add(getResources().getString(R.string.year));
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
        if (spinnerYear.getSelectedItem().toString().equals(getResources().getString(R.string.year)) || spinnerMonth.getSelectedItem().toString().equals(getResources().getString(R.string.month))) {
            Toast.makeText(getActivity(), getContext().getResources().getString(R.string.msg_select_year_month), Toast.LENGTH_SHORT).show();
        } else {
            Integer year = Integer.valueOf(spinnerYear.getSelectedItem().toString());
            if (((year % 4) == 0) && (spinnerMonth.getSelectedItemId() == 2)) {
                array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29};
            } else {
                if (spinnerMonth.getSelectedItemId() == 2) {
                    array = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28};
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


    private List<String> getNameRegConsumer() {

        listNameRegConsumers = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((getActivity() != null) && (getActivity().getResources().getString(R.string.branch_consumers) != null)) {
                    for (DataSnapshot d : dataSnapshot.child(getActivity().getResources().getString(R.string.branch_consumers)).getChildren()) {//-----------------------------------------------
                        Consumer c = d.getValue(Consumer.class);
                        listNameRegConsumers.add(c.getName() + " " + c.getSurname());
                    }
                }
                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return listNameRegConsumers;
    }

    private List<String> getStaffName() {

        listStaffName = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {//users/mail/personaldata/item
                if ((getActivity() != null) && (getContext().getResources().getString(R.string.branch_users) != null)) {
                    for (DataSnapshot d : dataSnapshot.child(getContext().getResources().getString(R.string.branch_users)).getChildren()) {
                        UserApp user = d.child(getContext().getResources().getString(R.string.subbranch_personal_data)).getValue(UserApp.class);

                        listStaffName.add(user.getName() + " " + user.getSurname());
                    }
                }
                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return listStaffName;
    }


    private void getConsumerByKey(final String key) {

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Consumer result = dataSnapshot.child(getContext().getResources().getString(R.string.branch_consumers)).child(key).getValue(Consumer.class);
                if ((getActivity() != null) && (getActivity().getBaseContext().getResources().getString(R.string.branch_consumers) != null)) {
                    Consumer result = dataSnapshot.child(getActivity().getBaseContext().getResources().getString(R.string.branch_consumers)).child(key).getValue(Consumer.class);
                    etDiscount.setText("" + result.getDiscount());
                    autoCompleteAdapterConsumers.setText("" + result.getPhoneNumber());
                }
                if (formValidation()) btnAdd.setEnabled(true);
                FirebaseDatabase.getInstance().getReference().removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean formValidation() {
        if (!spinnerYear.getSelectedItem().toString().equals(getResources().getString(R.string.year)) &&
                !spinnerMonth.getSelectedItem().toString().equals(getResources().getString(R.string.month)) &&
                !autoCompleteConsumer.getText().toString().equals("") &&
                !spinnerService.getSelectedItem().toString().equals(getContext().getResources().getString(R.string.event_service))) {
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
