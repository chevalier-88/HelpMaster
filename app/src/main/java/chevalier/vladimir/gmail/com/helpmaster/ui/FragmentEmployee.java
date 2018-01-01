package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.FlagAccess;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;
import chevalier.vladimir.gmail.com.helpmaster.utils.LocalSqliteHelper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by chevalier on 20.09.17.
 */

public class FragmentEmployee extends Fragment implements View.OnClickListener {

    private ImageView imgEmployee;
    private EditText etName;
    private EditText etSurname;
    private EditText etMail;
    private EditText etPhone;
    private Button btnChange;


    private static final int DOUBLE_CLICK_TIME_DELTA = 400;
    private long LAST_CLICK_TIME = 0L;
    private static final int SELECTED_PICTURE = 1;
    private static String IMAGE_FILE_PATH = "";
    private StorageReference mStorageRef;
    private UserApp userApp;
    private LocalSqliteHelper localSqliteHelper;

    private Handler handlUpdateImg;
    private static int FLAG_HANDLER_MESSAGE_OK = 1;
    private Uri fileImgUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fileImgUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.no_name);
    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        localSqliteHelper = new LocalSqliteHelper(getContext());


        userApp = localSqliteHelper.getCurrentUserApp(FlagAccess.MAIL_CURRENT_USERAPP);
//        read personal date of userApp and set to firebase, agter I can remove functional for db...

        imgEmployee = (ImageView) view.findViewById(R.id.id_f_e_img);
        imgEmployee.setImageURI(fileImgUri);
        imgEmployee.setOnClickListener(this);

        etName = (EditText) view.findViewById(R.id.id_f_e_name);
        etName.setText(userApp.getName());
        etName.setOnClickListener(this);

        etSurname = (EditText) view.findViewById(R.id.id_f_e_surname);
        etSurname.setText(userApp.getSurname());
        etSurname.setOnClickListener(this);

        etMail = (EditText) view.findViewById(R.id.id_f_e_mail);
        etMail.setText(userApp.getEmail());
        etMail.setOnClickListener(this);

        etPhone = (EditText) view.findViewById(R.id.id_f_e_phone);
        etPhone.setText(userApp.getPhone());
        etPhone.setOnClickListener(this);

        btnChange = (Button) view.findViewById(R.id.id_f_e_btn);
        btnChange.setOnClickListener(this);

        handlUpdateImg = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == FLAG_HANDLER_MESSAGE_OK) {
                    imgEmployee.setImageURI(fileImgUri);
                }
            }
        };
        this.getImageFromStorage();

        return view;
    }

    @Override
    public void onClick(View v) {
        long clickTime = System.currentTimeMillis();
        if (clickTime - LAST_CLICK_TIME < DOUBLE_CLICK_TIME_DELTA) {
            switch (v.getId()) {
                case R.id.id_f_e_img:
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECTED_PICTURE);
                    break;
                case R.id.id_f_e_name:
                    etName.setFocusableInTouchMode(true);
                    btnChange.setEnabled(true);
                    btnChange.setVisibility(View.VISIBLE);
                    break;
                case R.id.id_f_e_surname:
                    etSurname.setFocusableInTouchMode(true);
                    btnChange.setEnabled(true);
                    btnChange.setVisibility(View.VISIBLE);
                    break;
                case R.id.id_f_e_mail:
                    etMail.setFocusableInTouchMode(true);
                    btnChange.setEnabled(true);
                    btnChange.setVisibility(View.VISIBLE);
                    break;
                case R.id.id_f_e_phone:
                    etPhone.setFocusableInTouchMode(true);
                    btnChange.setEnabled(true);
                    btnChange.setVisibility(View.VISIBLE);
                    break;
                case R.id.id_f_e_btn:
                    //TODO
                    break;
            }
        }
        LAST_CLICK_TIME = clickTime;
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
                //TODO
                uploadImage(IMAGE_FILE_PATH);
                cursor.close();
                imgEmployee.setImageURI(uri);

            }
        }
    }

    private void uploadImage(String filePath) {
        if (filePath.length() > 1) {
            File imgFile = new File(filePath);

            Uri file = Uri.fromFile(imgFile);

            StorageReference riversRef = mStorageRef.child("images").child(FlagAccess.NAME_USERAPP);

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //TODO
//                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //TODO
                        }
                    });
        } else {
            Toast.makeText(getContext(), "ops, verify path to file!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageFromStorage() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorageReference riversRef = mStorageRef.child("images").child(FlagAccess.NAME_USERAPP);
                    final File localFile = File.createTempFile("images", "jpg");
                    riversRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    fileImgUri = Uri.fromFile(localFile);
                                    handlUpdateImg.sendEmptyMessage(FLAG_HANDLER_MESSAGE_OK);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            fileImgUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.no_name);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        th.start();
    }
}
