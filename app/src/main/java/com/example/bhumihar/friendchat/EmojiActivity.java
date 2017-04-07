package com.example.bhumihar.friendchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class EmojiActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener
        ,EmojiconsFragment.OnEmojiconBackspaceClickedListener{

    EmojiconEditText EmojiEdit ;
    EmojiconTextView EmojiText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emoji);

        EmojiEdit = (EmojiconEditText)findViewById(R.id.emojiedit);
        EmojiText = (EmojiconTextView)findViewById(R.id.emojitext);

        EmojiEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EmojiText.setText(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setEmojiconFragment(false);


    }

    private void setEmojiconFragment(boolean b) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojiframe ,EmojiconsFragment.newInstance(b))
                .commit() ;
    }


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(EmojiEdit,emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(EmojiEdit);

    }
}
