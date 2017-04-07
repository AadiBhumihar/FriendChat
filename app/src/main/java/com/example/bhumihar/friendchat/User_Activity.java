package com.example.bhumihar.friendchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User_Activity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth ;
    int tyu =0;
    FirebaseUser user ;

    private FirebaseListAdapter<ChatMessage> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Log.e("UserId","UID:"+user.getUid());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_activity);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayShowTitleEnabled(false);
            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();
            ImageView imageView = (ImageView)findViewById(R.id.profile_pic);
            TextView textView = (TextView)findViewById(R.id.textViewUsername);
            imageView.setImageURI(photoUrl);
            textView.setText(name);
        }

        displaychatmessage();

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(User_Activity.this ,CreatePost.class));
            }
        });



    }



    private void displaychatmessage() {

        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        final DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference();;
        DatabaseReference userdata = databaseReference.child("posts");

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, userdata) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
                final ImageView userimage = (ImageView)v.findViewById(R.id.user_image);
                ImageView messageImage = (ImageView)v.findViewById(R.id.post_image);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));

                try {
                    String encodedImage = model.getImagebitmap();
                    byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    messageImage.setImageBitmap(decodedByte);
                } catch (NullPointerException ex) {
                   ex.printStackTrace();
                }


                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("photos").child(model.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.exists()){
                           try {
                               Object p_data = dataSnapshot.child("encoded_image").getValue();
                               photodata p = (photodata) p_data;
                               String encodedImage = p.getEncoded_image();
                               byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                               Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                               userimage.setImageBitmap(decodedByte);
                           }catch (NullPointerException ex) {
                              ex.printStackTrace();
                           }

                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        listOfMessages.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater() ;
        menuInflater.inflate(R.menu.menu,menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.Logout :
                LogOut();
                break;

            case R.id.profile :
                if(verifyemail())
                {

                    startActivity(new Intent(User_Activity.this ,User_Profile.class));
                }else {
                    Toast.makeText(this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Name_a :
                startActivity(new Intent(User_Activity.this ,Data_activity.class));
                break;

            case R.id.photopost :
                startActivity(new Intent(User_Activity.this ,Photopost.class));
                break;
        }
        return true ;
    }


    private void LogOut() {

        MainActivity.auth.signOut();
        Intent Login_Intent = new Intent(User_Activity.this ,MainActivity.class);
        Login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(Login_Intent);
        finish();
     }

    private boolean verifyemail() {

        if (user == null) {
            Toast.makeText(this, "User Null", Toast.LENGTH_SHORT).show();
        } else {
            boolean emailVerified = user.isEmailVerified();
             Log.e("Verifcation",String.valueOf(emailVerified));
            if(emailVerified) {
                Toast.makeText(this, "EmailVerified :Yes", Toast.LENGTH_SHORT).show();
                return true ;
            }
        }

        return false ;

    }
}
