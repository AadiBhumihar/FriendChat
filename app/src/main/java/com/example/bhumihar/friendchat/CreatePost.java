package com.example.bhumihar.friendchat;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CreatePost extends AppCompatActivity implements View.OnClickListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth ;
    private FirebaseUser user ;

    EditText input ;
    ImageView imgv ;
    ImageButton camera_btn , Gallery_btn ;
    Button post_btn ;

    private Intent Camera_Intent;
    private int CameraData = 10;
    private int start_code ;
    private int SELECT_PHOTO = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        intilize();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getCurrentUser();

        camera_btn.setOnClickListener(this);
        Gallery_btn.setOnClickListener(this);
        post_btn.setOnClickListener(this);
    }

    private void intilize() {
        input = (EditText)findViewById(R.id.edit_post);
        imgv = (ImageView)findViewById(R.id.image_choose);
        camera_btn = (ImageButton)findViewById(R.id.open_cam);
        Gallery_btn = (ImageButton)findViewById(R.id.open_gallery);
        post_btn = (Button)findViewById(R.id.done_post);
    }




    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {

            selectImage() ;
        }

    }

    private void checkPermission() {

        if(ContextCompat.checkSelfPermission(CreatePost.this , android.Manifest.permission.CAMERA) +
                (ContextCompat.checkSelfPermission(CreatePost.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                + (ContextCompat.checkSelfPermission(CreatePost.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreatePost.this,
                    android.Manifest.permission.CAMERA) ||(ActivityCompat.shouldShowRequestPermissionRationale(CreatePost.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) || (ActivityCompat.shouldShowRequestPermissionRationale(CreatePost.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) )
            {

            } else {

                ActivityCompat.requestPermissions(CreatePost.this,
                        new String[]{android.Manifest.permission.CAMERA , android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }

        } else
        {
            ActivityCompat.requestPermissions(CreatePost.this,
                    new String[]{android.Manifest.permission.CAMERA , android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                          MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE :
                if (grantResults.length>0  && permissions[0]== android.Manifest.permission.CAMERA && permissions[1]== android.Manifest.permission.READ_EXTERNAL_STORAGE
                        && permissions[2]== android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                {

                }else {
                    selectImage() ;
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void selectImage() {

        if (start_code==100) {
            Camera_Intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Camera_Intent, CameraData);

        } else if (start_code==101) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.open_cam :
                start_code = 100 ;
                checkAndroidVersion();
                break;

            case R.id.open_gallery :
                start_code = 101 ;
                checkAndroidVersion();
                break;

            case R.id.done_post :
                createpost();
                break;
        }

    }

    private void createpost() {

        String message = input.getText().toString() ;
        Bitmap bitmap = null;
        try {
            BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();
            bitmap = drawable.getBitmap();
            //bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
        }catch (NullPointerException ex) {

        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();
        String encodedImage = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        if (TextUtils.isEmpty(message) && (TextUtils.isEmpty(encodedImage))) {
            Toast.makeText(this, "Empty Post", Toast.LENGTH_SHORT).show();

        } else if ((TextUtils.isEmpty(encodedImage)) && (!TextUtils.isEmpty(message))) {
            ChatMessage chatmessage = new ChatMessage(user.getUid() ,message ,user.getDisplayName());
            DatabaseReference userdata = databaseReference.child("posts");
            userdata.push().setValue(chatmessage);

        } else if ((!TextUtils.isEmpty(encodedImage)) && (TextUtils.isEmpty(message))) {
            ChatMessage chatmessage = new ChatMessage(user.getUid() , user.getDisplayName());
            chatmessage.setImagebitmap(encodedImage);
            DatabaseReference userdata = databaseReference.child("posts");
            userdata.push().setValue(chatmessage);

        } else if ((!TextUtils.isEmpty(encodedImage)) && (!TextUtils.isEmpty(message))) {
            ChatMessage chatmessage = new ChatMessage(user.getUid() ,message ,encodedImage ,user.getDisplayName());
            DatabaseReference userdata = databaseReference.child("posts");
            userdata.push().setValue(chatmessage);
        }
        Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();

    }




    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CameraData) {

                Bundle extra = data.getExtras();
                Bitmap bmp = (Bitmap) extra.get("data");
                imgv.setImageBitmap(bmp);

            } else if (requestCode == SELECT_PHOTO) {


                Uri Imageuri = data.getData();
                String path = getPath(Imageuri);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                imgv.setImageURI(Imageuri);

            }

        }




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


}
