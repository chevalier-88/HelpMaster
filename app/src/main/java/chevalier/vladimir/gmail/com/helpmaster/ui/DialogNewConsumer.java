package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.Consumer;

import static android.app.Activity.RESULT_OK;


public class DialogNewConsumer extends DialogFragment {


    private FragmentConsumer fragmentConsumer;

    private ImageView imgPhoto;
    private EditText etName;
    private EditText etSurmane;
    private EditText etPhoneNumber;
    private EditText etBalance;
    private EditText etDiscount;
    private EditText etDescription;
    private Button btnAdd;

    private Handler handlerDialog;
    private Bundle bundle;


    private static final int FLAG_HANDLER_MESSAGE_OK = 1;
    private static final int FLAG_HANDLER_MESSAGE_OPS = 0;
    private static final int DOUBLE_CLICK_TIME_DELTA = 400;
    private static final int SELECTED_PICTURE = 1;
    private long lastClickTime = 0L;
    private String IMAGE_FILE_PATH = "android.resource://chevalier.vladimir.gmail.com.helpmaster/" + R.drawable.no_name;
    private Consumer consumer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        View view = inflater.inflate(R.layout.dialog_new_consumer, container, false);
        getDialog().setCanceledOnTouchOutside(false);

        switch (getTargetRequestCode()) {
            case FragmentConsumer.TARGET_CODE_NEW_CONSUMER:
                makeDialogNewConsumer(view);
                break;
            case FragmentConsumer.TARGET_CODE_EXISTS_CONSUMER:
                makeDialogExistsConsumer(view);//TODO
                break;
        }

        return view;
    }

    @SuppressLint("HandlerLeak")
    private void makeDialogNewConsumer(View v) {

        fragmentConsumer = (FragmentConsumer) getTargetFragment();


        handlerDialog = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_HANDLER_MESSAGE_OK:
                        fragmentConsumer.addConsumer(consumer);
                        handlerDialog = null;
                        dismiss();
                        break;
                    case FLAG_HANDLER_MESSAGE_OPS:
                        Toast.makeText(getContext(), R.string.msg_no_correct_fields, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        imgPhoto = (ImageView) v.findViewById(R.id.id_dialog_consumer_img);
//        imgPhoto.setImageResource(R.drawable.unidentified);
        imgPhoto.setImageURI(Uri.parse(IMAGE_FILE_PATH));
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECTED_PICTURE);
                }
                lastClickTime = clickTime;
            }
        });

        etName = (EditText) v.findViewById(R.id.id_dialog_consumer_name);
        etSurmane = (EditText) v.findViewById(R.id.id_dialog_consumer_surname);
        etPhoneNumber = (EditText) v.findViewById(R.id.id_dialog_consumer_phone_number);
        etBalance = (EditText) v.findViewById(R.id.id_dialog_consumer_balance);
//        etBalance.setText(getContext().getResources().getString(R.string.consumer_dialog_default_balance));
        etDiscount = (EditText) v.findViewById(R.id.id_dialog_consumer_discount);
//        etDiscount.setText(getContext().getResources().getString(R.string.consumer_dialog_default_discount));
        etDescription = (EditText) v.findViewById(R.id.id_dialog_consumer_description);
//        etDescription.setText(getContext().getResources().getString(R.string.consumer_dialog_default_description));
        btnAdd = (Button) v.findViewById(R.id.id_dialog_consumer_btn);
        btnAdd.setText(getContext().getResources().getString(R.string.btn_add));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            //TODO handler, when you press button, start new thread with handlermessage... and send data to db...
            @Override
            public void onClick(View v) {
                if (DialogNewConsumer.this.formValidation()) {
                    consumer = getConsumer();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (consumer != null) {
                                handlerDialog.sendMessage(handlerDialog.obtainMessage(FLAG_HANDLER_MESSAGE_OK));
                            } else {
                                handlerDialog.sendMessage(handlerDialog.obtainMessage(FLAG_HANDLER_MESSAGE_OPS));
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private void makeDialogExistsConsumer(View v) {
        bundle = getArguments();
        Consumer existConsumer = (Consumer) bundle.getSerializable("consumer");
//        existNameConsumer = existConsumer.getName();
//        existSurnameConsumer = existConsumer.getSurname();
//        existPhoneNumberConsumer = existConsumer.getPhoneNumber();

        imgPhoto = (ImageView) v.findViewById(R.id.id_dialog_consumer_img);
        if (new File(existConsumer.getPathToPhoto()).exists()) {
            imgPhoto.setImageURI(Uri.parse(existConsumer.getPathToPhoto()));
            IMAGE_FILE_PATH = existConsumer.getPathToPhoto();
        } else {
            imgPhoto.setImageURI(Uri.parse(IMAGE_FILE_PATH));
        }
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECTED_PICTURE);
                }
                lastClickTime = clickTime;
            }
        });

        etName = (EditText) v.findViewById(R.id.id_dialog_consumer_name);
        etName.setText(existConsumer.getName());
        etSurmane = (EditText) v.findViewById(R.id.id_dialog_consumer_surname);
        etSurmane.setText(existConsumer.getSurname());
        etPhoneNumber = (EditText) v.findViewById(R.id.id_dialog_consumer_phone_number);
        etPhoneNumber.setText(existConsumer.getPhoneNumber());
        etBalance = (EditText) v.findViewById(R.id.id_dialog_consumer_balance);
        etBalance.setText("" + (int) existConsumer.getBalance());
        etDiscount = (EditText) v.findViewById(R.id.id_dialog_consumer_discount);
        etDiscount.setText("" + existConsumer.getDiscount());
        etDescription = (EditText) v.findViewById(R.id.id_dialog_consumer_description);
        etDescription.setText(existConsumer.getDescription());


        fragmentConsumer = (FragmentConsumer) getTargetFragment();

        handlerDialog = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FLAG_HANDLER_MESSAGE_OK:
                        fragmentConsumer.updateConsumerDescription(bundle.getInt("index"), consumer);
                        handlerDialog = null;
                        dismiss();
                        break;
                    case FLAG_HANDLER_MESSAGE_OPS:
                        Toast.makeText(getActivity(), getContext().getResources().getString(R.string.msg_no_correct_fields), Toast.LENGTH_SHORT).show();
                        break;
                }


            }
        };
        btnAdd = (Button) v.findViewById(R.id.id_dialog_consumer_btn);
        btnAdd.setText(getContext().getResources().getString(R.string.btn_add));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            //                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {

                if (formValidation()) {
                    consumer = getConsumer();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (consumer != null) {
                                handlerDialog.sendMessage(handlerDialog.obtainMessage(FLAG_HANDLER_MESSAGE_OK));
                            } else {
                                handlerDialog.sendMessage(handlerDialog.obtainMessage(FLAG_HANDLER_MESSAGE_OPS));
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

    private boolean formValidation() {
        if ((!etName.getText().toString().equals("")) &&
                (!etSurmane.getText().toString().equals("")) && (!etBalance.getText().toString().equals("")) && (!etDiscount.getText().toString().equals(""))) {
            return true;
        }
        return false;
    }

    private Consumer getConsumer() {
        Consumer result = new Consumer();
        result.setName(etName.getText().toString().trim());
        result.setSurname(etSurmane.getText().toString().trim());
        result.setPhoneNumber(etPhoneNumber.getText().toString().trim());
        result.setBalance(Double.parseDouble(etBalance.getText().toString().trim()));
        result.setDiscount(Integer.parseInt(etDiscount.getText().toString().trim()));
        result.setDescription((etDescription.getText().toString().length() > 0 ? etDescription.getText().toString().toString() : ""));
        result.setPathToPhoto(IMAGE_FILE_PATH);
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECTED_PICTURE) {

                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor =
                        getActivity().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(projection[0]);
                IMAGE_FILE_PATH = cursor.getString(columnIndex);
                cursor.close();
                imgPhoto.setImageURI(uri);

            }
        }
    }
}