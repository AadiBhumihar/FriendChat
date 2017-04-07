package com.example.bhumihar.friendchat;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class User_Profile extends AppCompatActivity implements View.OnClickListener{

    private static final int SELECT_PHOTO = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000;
    
    ImageView ProfileImage;
    ImageButton ProfileButton ,UserButton ,StatusButton ,BdayButton ;
    TextView UserText ,StatusText ,BdayText ;
    Uri Imageuri ;
    Button uploadbutton ;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    private StorageReference mStorageRef;

    FirebaseUser user ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        firebaseAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar)findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.left1);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("Profile");
            actionBar.setDisplayShowTitleEnabled(true);

        }

         intilize();

        
        if ((!UserData.Username.isEmpty()) && (!UserData.Status.isEmpty())) {
            UserText.setText(UserData.Username);
            StatusText.setText(UserData.Status);
        } else if ((!UserData.Username.isEmpty()) && (UserData.Status.isEmpty())) {
            UserText.setText(UserData.Username);
        } else if ((UserData.Username.isEmpty()) && (!UserData.Status.isEmpty())) {
            StatusText.setText(UserData.Status);
        }


        ProfileButton.setOnClickListener(this);
        UserButton.setOnClickListener(this);
        StatusButton.setOnClickListener(this);
        BdayButton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);

    }

    private void intilize() {
        ProfileImage = (ImageView)findViewById(R.id.imageset);
        ProfileButton = (ImageButton)findViewById(R.id.imagebutton);
        UserText = (TextView)findViewById(R.id.username);
        UserButton =(ImageButton)findViewById(R.id.useredit);
        StatusText = (TextView)findViewById(R.id.status);
        StatusButton = (ImageButton)findViewById(R.id.statusedit);
        BdayText = (TextView)findViewById(R.id.bday);
        BdayButton = (ImageButton)findViewById(R.id.bdayedit);
        uploadbutton = (Button)findViewById(R.id.tickbutton);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.imagebutton :
                showFileChooser() ;
                break;

            case R.id.useredit :
                StartIntent("Enter Username");
                break;

            case R.id.statusedit:
                StartIntent("Add New Status");
                break;

            case R.id.bdayedit:

                break;
            case R.id.tickbutton :
                uploaddata();
                break;
        }
    }


    private void StartIntent(String Label) {
        Intent entry_intent = new Intent(User_Profile.this, EntryActivity.class);
        Bundle mBundle3 = new Bundle();
        mBundle3.putString("intent_key", Label);
        entry_intent.putExtras(mBundle3);
        startActivity(entry_intent);

    }


    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= 23){

            if (ContextCompat.checkSelfPermission(User_Profile.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(User_Profile.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {

                    ActivityCompat.requestPermissions(User_Profile.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }else{
                ActivityCompat.requestPermissions(User_Profile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        }
        else
        {

            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {

                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Imageuri = data.getData();
                    String path = getPath(Imageuri);
                    Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                    Bitmap bitmap ;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Imageuri);
                        ProfileImage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri,projection,null,null,null);
        if (cursor == null)
            return null;
        int column_index =  cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void uploaddata() {

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        ProfileData profile = new ProfileData() ;
        profile.setUsername(UserText.getText().toString());
        profile.setStatus(StatusText.getText().toString());

        DatabaseReference userdata = databaseReference.child("user");
        userdata.child(user.getUid()).setValue(profile);

        //displaying a success toast
        Toast.makeText(this, "Information Saved...", Toast.LENGTH_LONG).show();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(UserText.getText().toString())
                .setPhotoUri(Imageuri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(User_Profile.this, "User profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        photopost();
    }


    private void photopost() {

        BitmapDrawable ImageDrawable = (BitmapDrawable)ProfileImage.getDrawable();
        Bitmap ImageBitmap = ImageDrawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //getting the current logged in user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        DatabaseReference userdata = databaseReference.child("photos");
        userdata.child(user.getUid()).setValue(new photodata(encodedImage));



    }




}
