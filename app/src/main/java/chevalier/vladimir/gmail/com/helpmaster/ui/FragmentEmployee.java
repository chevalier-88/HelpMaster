package chevalier.vladimir.gmail.com.helpmaster.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import chevalier.vladimir.gmail.com.helpmaster.R;
import chevalier.vladimir.gmail.com.helpmaster.entities.UserApp;

import static android.app.Activity.RESULT_OK;


public class FragmentEmployee extends Fragment implements View.OnClickListener {

    private ImageView imgEmployee;
    private EditText etName;
    private EditText etSurname;
    private EditText etMail;
    private EditText etPhone;
    private Button btnChange;
    private ProgressDialog progressBar;


    private Handler handlUpdateImg;
    private final static int LOAD_IMG_OK = 101;
    private final static int LOAD_DATA_OK = 102;
    private static final int DOUBLE_CLICK_TIME_DELTA = 400;
    private long LAST_CLICK_TIME = 0L;
    private static final int SELECTED_PICTURE = 100;
    private static final int PIC_CROP = 123;
    private StorageReference mStorageRef;
    private UserApp userApp;

    private Bitmap myBitmap;


    private Uri picUri;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(getContext().getResources().getString(R.string.pref_def_img_path) + R.drawable.no_name));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);

        imgEmployee = (ImageView) view.findViewById(R.id.id_f_e_img);
        imgEmployee.setImageBitmap(myBitmap);

        imgEmployee.setOnClickListener(this);

        etName = (EditText) view.findViewById(R.id.id_f_e_name);
        etName.setText(userApp != null ? userApp.getName() : getContext().getResources().getString(R.string.no_data));
        etName.setOnClickListener(this);

        etSurname = (EditText) view.findViewById(R.id.id_f_e_surname);
        etSurname.setText(userApp != null ? userApp.getSurname() : getContext().getResources().getString(R.string.no_data));
        etSurname.setOnClickListener(this);

        etMail = (EditText) view.findViewById(R.id.id_f_e_mail);
        etMail.setText(userApp != null ? userApp.getEmail() : getContext().getResources().getString(R.string.no_data));
        etMail.setOnClickListener(this);

        etPhone = (EditText) view.findViewById(R.id.id_f_e_phone);
        etPhone.setText(userApp != null ? userApp.getPhone() : getContext().getResources().getString(R.string.no_data));
        etPhone.setOnClickListener(this);

        btnChange = (Button) view.findViewById(R.id.id_f_e_btn);
        btnChange.setOnClickListener(this);

        handlUpdateImg = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case LOAD_IMG_OK:
                        imgEmployee.setImageBitmap(myBitmap);
                        RoundedBitmapDrawable roundedBitmap = getBitmapRoundedForm(myBitmap);
                        if (roundedBitmap != null) {
                            imgEmployee.setImageDrawable(roundedBitmap);
                        }
                        break;
                    case LOAD_DATA_OK:
                        etName.setText(userApp.getName());
                        etSurname.setText(userApp.getSurname());
                        etPhone.setText(userApp.getPhone());
                        etMail.setText(userApp.getEmail());
                        break;
                }
            }
        };
        this.getDataFromFirebase();

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
            }
        } else if (v.getId() == R.id.id_f_e_btn) {
            progressBar = new ProgressDialog(getContext());
            progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressBar.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            progressBar.setCancelable(false);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
            progressBar.setContentView(R.layout.simple_progress_bar);
        }
        LAST_CLICK_TIME = clickTime;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_PICTURE:
                if (data != null) {
                    picUri = data.getData();
                    Intent cropIntent = new Intent(getContext().getResources().getString(R.string.employee_action_adress));
                    cropIntent.setDataAndType(picUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);
                    cropIntent.putExtra("return-data", true);
                    cropIntent.putExtra("path", picUri);
                    startActivityForResult(cropIntent, PIC_CROP);
                }
                break;
            case PIC_CROP:
                progressBar = new ProgressDialog(getContext());
                progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressBar.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                progressBar.setCancelable(false);
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
                progressBar.setContentView(R.layout.simple_progress_bar);

                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    myBitmap = bundle.getParcelable("data");
                    try {
                        File f = new File(getContext().getCacheDir(), "avatar");
                        if (!f.exists()) f.createNewFile();


                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        byte[] bitmapdata = bos.toByteArray();

                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        uploadImage(f);
                    } catch (IOException e) {
                        //TODO
                    }
                } else {
                    //NOP
                }
                break;
        }
    }

    private void uploadImage(File file) {
        if (file.exists()) {
            final Uri updateFileUri = Uri.fromFile(file);
            if (HomeActivity.NAME_CURRENT_USER.length() > 1) {
                StorageReference riversRef = mStorageRef.child(getContext().getResources().getString(R.string.fb_storage_pref)).child(HomeActivity.NAME_CURRENT_USER);

                riversRef.putFile(updateFileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressBar.dismiss();
                                RoundedBitmapDrawable roundedBitmap = getBitmapRoundedForm(myBitmap);
                                if (roundedBitmap != null) {
                                    imgEmployee.setImageDrawable(roundedBitmap);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //TODO
                            }
                        });
            }
        } else {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.uncorredt_file_path), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromFirebase() {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

//                    StorageReference riversRef = mStorageRef.child("images").child(getActivity().getIntent().getStringExtra("NAME_CURRENT_USER"));
                    StorageReference riversRef = mStorageRef.child(getContext().getResources().getString(R.string.fb_storage_pref)).child(HomeActivity.NAME_CURRENT_USER);
                    final File localFile = File.createTempFile(getContext().getResources().getString(R.string.fb_storage_pref), getContext().getResources().getString(R.string.fb_storage_suf));
                    riversRef.getFile(localFile)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    try {
                                        myBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.fromFile(localFile));
                                        handlUpdateImg.sendEmptyMessage(LOAD_IMG_OK);
                                    } catch (IOException e) {

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            try {
                                myBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(getContext().getResources().getString(R.string.pref_def_img_path) + R.drawable.no_name));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        userApp = dataSnapshot.child(getActivity().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~").trim()).child(getContext().getResources().getString(R.string.subbranch_personal_data)).getValue(UserApp.class);
                        userApp = dataSnapshot.child(getActivity().getResources().getString(R.string.branch_users)).child(HomeActivity.MAIL_CURRENT_USER.replace(".", "~").trim()).child(getContext().getResources().getString(R.string.subbranch_personal_data)).getValue(UserApp.class);
                        handlUpdateImg.sendEmptyMessage(LOAD_DATA_OK);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
//NOP
                    }
                });
            }
        });
        th.start();
    }

    private RoundedBitmapDrawable getBitmapRoundedForm(Bitmap bitmap) {
        RoundedBitmapDrawable roundedImageBitmapDrawable = null;

        int bitmapWidthImage = bitmap.getWidth();
        int bitmapHeightImage = bitmap.getHeight();
        int borderWidthHalfImage = 1;

        int bitmapRadiusImage = Math.min(bitmapWidthImage, bitmapHeightImage) / 2;
        int bitmapSquareWidthImage = Math.min(bitmapWidthImage, bitmapHeightImage);
        int newBitmapSquareWidthImage = bitmapSquareWidthImage + borderWidthHalfImage;

        Bitmap roundedImageBitmap = Bitmap.createBitmap(newBitmapSquareWidthImage, newBitmapSquareWidthImage, Bitmap.Config.ARGB_8888);
        Canvas mcanvas = new Canvas(roundedImageBitmap);
        mcanvas.drawColor(Color.RED);
        int i = borderWidthHalfImage + bitmapSquareWidthImage - bitmapWidthImage;
        int j = borderWidthHalfImage + bitmapSquareWidthImage - bitmapHeightImage;

        mcanvas.drawBitmap(bitmap, i, j, null);

        Paint borderImagePaint = new Paint();
        borderImagePaint.setStyle(Paint.Style.STROKE);
        borderImagePaint.setStrokeWidth(borderWidthHalfImage * 2);
        borderImagePaint.setColor(Color.GRAY);
        mcanvas.drawCircle(mcanvas.getWidth() / 2, mcanvas.getWidth() / 2, newBitmapSquareWidthImage / 2, borderImagePaint);

        roundedImageBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), roundedImageBitmap);
        roundedImageBitmapDrawable.setCornerRadius(bitmapRadiusImage);
        roundedImageBitmapDrawable.setAntiAlias(true);
        return roundedImageBitmapDrawable;
    }
}
