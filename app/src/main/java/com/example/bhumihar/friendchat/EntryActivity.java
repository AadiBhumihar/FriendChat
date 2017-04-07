package com.example.bhumihar.friendchat;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class EntryActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener
        ,EmojiconsFragment.OnEmojiconBackspaceClickedListener{

    EditText SubmitText ;
    ImageButton EmojiButton ;
    Button Save ,Cancel ;
    String label ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        SubmitText = (EditText)findViewById(R.id.entryedit);
        EmojiButton = (ImageButton)findViewById(R.id.emojibtn);
        Save = (Button)findViewById(R.id.savebtn);
        Cancel = (Button)findViewById(R.id.cancelbtn);

        Bundle p = getIntent().getExtras();
        label = p.getString("intent_key");
        Toolbar toolbar = (Toolbar)findViewById(R.id.SubmitToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(label);
        }


        EmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEmojiconFragment(false);
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Login_Intent = new Intent(EntryActivity.this ,User_Profile.class);
                Login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Login_Intent);
                finish();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TextValue = SubmitText.getText().toString() ;
                if (TextUtils.isEmpty(TextValue)) {
                    Toast.makeText(EntryActivity.this, "Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (label.contains("Enter Username")) {
                        UserData.Username = TextValue;
                    } else {
                        UserData.Status = TextValue;
                    }

                    Intent Login_Intent = new Intent(EntryActivity.this, User_Profile.class);
                    Login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(Login_Intent);
                    finish();

                }
            }
        });
    }


    private void setEmojiconFragment(boolean b) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.EmojiFrame , EmojiconsFragment.newInstance(b))
                .commit() ;
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(SubmitText,emojicon);
    }


    @Override
    public void onEmojiconBackspaceClicked(View v) {
       EmojiconsFragment.backspace(SubmitText);
    }



}
