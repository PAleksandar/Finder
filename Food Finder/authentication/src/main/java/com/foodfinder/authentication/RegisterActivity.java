package com.foodfinder.authentication;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.foodfinder.acount.Account;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 1;

    EditText userEmail, userPassword, userName, userPhone, userBirthday;
    Button DelivererRegisterButton;
    Button CustomRegisterButton;
    Button addImageButton;
    ProgressDialog  progressDialog;
    Date birthdayDate;
    String imageUri="";
    byte[] imageBytes=null;


    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;

    public static final int registerRequestCode=91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setActionBar();
        setStatusBar();
        initializeComponent();
        setCurrentDate();

        DelivererRegisterButton.setOnClickListener(delivererRegisterListener);
        CustomRegisterButton.setOnClickListener(customerRegisterListener);
        addImageButton.setOnClickListener(addImageListener);
        userBirthday.setOnClickListener(addDateListener);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void setCurrentDate()
    {

        birthdayDate= getCurrentDate();
        userBirthday.setText(DateFormat.format("yyyy.MM.dd",birthdayDate).toString());
    }

    private Date getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        Date date=new Date(mYear-1900,mMonth,mDay);
        return  date;
    }

    private void selectDate()
    {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {


                        //  Log.d("Datum", "godina: "+year+", mesec: "+monthOfYear+", dan: "+dayOfMonth);
                        birthdayDate=new Date(year-1900,monthOfYear,dayOfMonth);
                        String s= DateFormat.format("yyyy.MM.dd", birthdayDate).toString();
                        userBirthday.setText(s);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void selectPicture() {
        try{
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
        }catch(Exception exp){
            Log.i("Error",exp.toString());
        }
    }

    private void saveImage(final boolean isDriver)
    {
        String uid = "fake_user";
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        byte[] bmp=imageBytes;
        final StorageReference ref=mStorageRef.child("profileImages").child(uid);
        UploadTask uploadTask = ref.putBytes(bmp);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageUri=downloadUri.toString();
                    Log.d("!!!!!!!!!! URI", "onComplete: !!!!!!!!!!!!!!!!!!!!!!! "+downloadUri);
                    saveUser(isDriver);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            try {
                final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                Bitmap imageBtm = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                imageBtm.compress(Bitmap.CompressFormat.JPEG,50, baos);
                imageBytes=baos.toByteArray();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

    }

    View.OnClickListener delivererRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString();
            String Password = userPassword.getText().toString();

            registerUser(Email, Password, true);
        }
    };

    View.OnClickListener customerRegisterListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String Email = userEmail.getText().toString();
            String Password = userPassword.getText().toString();

            registerUser(Email, Password, false);
        }
    };

    View.OnClickListener addImageListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            selectPicture();
        }
    };

    View.OnClickListener addDateListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            selectDate();
        }
    };

    private void initializeComponent()
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        userEmail = (EditText) findViewById(R.id.email_register);
        userPassword = (EditText) findViewById(R.id.password_register);
        userName = (EditText) findViewById(R.id.user_name_register);
        userPhone = (EditText) findViewById(R.id.phone_register);
        userBirthday = (EditText) findViewById(R.id.birthday_register);

        DelivererRegisterButton = (Button) findViewById(R.id.button_deliverer_reg);
        CustomRegisterButton = (Button) findViewById(R.id.button_custom_reg);
        addImageButton=(Button) findViewById(R.id.button_add_image);
    }

    private void setActionBar()
    {
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();
    }

    private void setStatusBar()
    {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(this.getResources().getColor(R.color.grey));
        }
    }

    private boolean validation()
    {
        boolean test;

        String Email = userEmail.getText().toString().trim();
        String Password = userPassword.getText().toString().trim();
        String UserName = userName.getText().toString().trim();
        String UserPhone = userPhone.getText().toString().trim();

        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(UserName) || TextUtils.isEmpty(UserPhone))
        {
            test=false;
        }
        else {
            test=true;
        }

        return test;
    }

    private void saveUser(boolean isDriver)
    {
        if(!validation())
        {
            Toast.makeText(RegisterActivity.this, "Nisu popunjena sva polja", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = "";
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //saveImage();

        String Email = userEmail.getText().toString();
        String Password = userPassword.getText().toString();
        String UserName = userName.getText().toString();
        String UserPhone = userPhone.getText().toString();


        Account account=new Account();
        account.setUserId(uid);
        account.setUserName(UserName);
        account.setEmail(Email);
        account.setPassword(Password);
        account.setPhone(UserPhone);
        account.setBirthday(birthdayDate);
        account.setProfileImage(imageUri);
        account.setActive(true);
        account.setDriver(isDriver);

        DatabaseReference ref = mRef.child("users").child(uid);
        ref.setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
                Toast.makeText(RegisterActivity.this, "Registration Successfully completed", Toast.LENGTH_LONG).show();
                startNavigationActivity();


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Toast.makeText(RegisterActivity.this, "Registration failed ", Toast.LENGTH_LONG).show();

                    }
                });;
    }

    private void startNavigationActivity()
    {
        Bundle conData = new Bundle();
        conData.putString("results", "Thanks Thanks");
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void registerUser(String email, String password, final boolean isDriver) {

        progressDialog=new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("please wait");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Authentication Successfully completed", Toast.LENGTH_LONG).show();

                    saveImage(isDriver);
                   // saveUser(isDriver);



                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed User already exits", Toast.LENGTH_LONG).show();
                }

            }


        });




    }
}
