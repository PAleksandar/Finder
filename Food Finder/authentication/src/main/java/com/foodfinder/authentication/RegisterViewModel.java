package com.foodfinder.authentication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.foodfinder.acount.Account;
import com.foodfinder.acount.Position;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class RegisterViewModel extends ViewModel {

    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 1;
    public static final int registerRequestCode=91;
    private ProgressDialog progressDialog;
    private String imageUri="";
    byte[] imageBytes=null;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private Context mContext;
    private Activity mActivity;

    private MutableLiveData<Date> birthdayDate;

    public MutableLiveData<Date> getBirthdayDate() {
        if (birthdayDate == null) {
            birthdayDate = new MutableLiveData<Date>();
        }
        return birthdayDate;
    }

    public void initializeViewModel(Context context, Activity activity)
    {
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mContext=context;
        mActivity=activity;

    }

    public Date getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        Date date=new Date(mYear-1900,mMonth,mDay);
        return  date;
    }

    public void selectDate()
    {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        birthdayDate.setValue(new Date(year-1900,monthOfYear,dayOfMonth));
                       // String s= DateFormat.format("yyyy.MM.dd", birthdayDate.getValue()).toString();
                       // userBirthday.setText(s);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public View.OnClickListener getAddDateListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               selectDate();
            }
        };
    }

    private void selectPicture() {
        try{
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mActivity.startActivityForResult(i, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
        }catch(Exception exp){
            Log.i("Error",exp.toString());
        }
    }

    public View.OnClickListener getAddImageListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectPicture();
            }
        };
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == SELECT_PICTURE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = mContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            try {
                final InputStream imageStream = mContext.getContentResolver().openInputStream(selectedImage);
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

    public void registerUser(final boolean isDriver,final String Email, final String Password, final String UserName, final String LastName, final String UserPhone) {

        progressDialog=new ProgressDialog(mContext);
        progressDialog.setMessage("please wait");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Authentication Successfully completed", Toast.LENGTH_LONG).show();

                    saveImage(isDriver,Email, Password, UserName, LastName, UserPhone);

                } else {
                    Toast.makeText(mContext, "Authentication failed User already exits", Toast.LENGTH_LONG).show();
                }

            }


        });




    }

    private void saveImage(final boolean isDriver, final String Email, final String Password, final String UserName, final String LastName, final String UserPhone)
    {
        String uid = "fake_user";
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        byte[] bmp=imageBytes;
        final StorageReference ref=mStorageRef.child("profileImages").child(uid+".jpg");
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
                    saveUser(isDriver,Email, Password, UserName, LastName, UserPhone);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private boolean validation(String Email, String Password, String UserName, String LastName, String UserPhone)
    {
        boolean test;

        if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(UserName) || TextUtils.isEmpty(LastName) || TextUtils.isEmpty(UserPhone))
        {
            test=false;
        }
        else {
            test=true;
        }

        return test;
    }

    private void saveUser(boolean isDriver,String Email, String Password, String UserName, String LastName, String UserPhone)
    {
        if(!validation(Email, Password, UserName, LastName, UserPhone))
        {
            Toast.makeText(mContext, "Nisu popunjena sva polja", Toast.LENGTH_LONG).show();
            return;
        }

        String uid = "";
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        Account account=new Account();
        account.setUserId(uid);
        account.setUserName(UserName);
        account.setLastName(LastName);
        account.setEmail(Email);
        account.setPassword(Password);
        account.setPhone(UserPhone);
        account.setBirthday(birthdayDate.getValue());
        account.setProfileImage(imageUri);
        account.setActive(true);
        account.setDriver(isDriver);
        account.setPlace(new Position(4.5,3.5));

        List<String> friends=new ArrayList<String>();
        account.setFriends(friends);

        List<Float> ranks=new ArrayList<Float>();
        ranks.add(new Float(3.4));
        ranks.add(new Float(2.5));
        account.setRanks(ranks);


        DatabaseReference ref = mRef.child("users").child(uid);
        ref.setValue(account).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Write was successful!
                // ...
                Toast.makeText(mContext, "Registration Successfully completed", Toast.LENGTH_LONG).show();
                startNavigationActivity();


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        // ...
                        Toast.makeText(mContext, "Registration failed ", Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void startNavigationActivity()
    {
        Bundle conData = new Bundle();
        conData.putString("results", "Thanks Thanks");
        Intent intent = new Intent();
        intent.putExtras(conData);
        mActivity.setResult(RESULT_OK, intent);
        mActivity.finish();
    }

    public View.OnClickListener getCustomerRegisterListener(final String Email, final String Password, final String UserName, final String LastName, final String UserPhone)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser(false,Email, Password, UserName, LastName, UserPhone);
            }
        };
    }

    public View.OnClickListener getDelivererRegisterListener(final String Email, final String Password, final String UserName, final String LastName, final String UserPhone)
    {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser(true,Email, Password, UserName, LastName, UserPhone);
            }
        };
    }

    public void stopProgresDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
