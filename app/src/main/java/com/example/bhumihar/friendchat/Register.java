package com.example.bhumihar.friendchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button register_btn ,sign_btn  ;
    EditText email_et ,password_et ;
    ProgressBar progressBar ;

    private FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        intilize();

        Firebase.setAndroidContext(this);
        firebaseauth = FirebaseAuth.getInstance();

        register_btn.setOnClickListener(this);
        sign_btn.setOnClickListener(this);
    }

    private void intilize() {

        register_btn = (Button)findViewById(R.id.sign_up_button);
        sign_btn = (Button)findViewById(R.id.sign_in_button);
        email_et = (EditText)findViewById(R.id.email);
        password_et = (EditText)findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.sign_up_button:
                register();
                break;

            case R.id.sign_in_button:
                Intent Login_Intent = new Intent(Register.this ,MainActivity.class);
                Login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Login_Intent);
                finish();
                break;
        }

    }

    private void register() {
        String email = email_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseauth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Toast.makeText(Register.this, "User Created With Email:onComplete:" , Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful())
                        {
                            sendVerificationEmail();
                            Intent Login_Intent = new Intent(Register.this ,MainActivity.class);
                            Login_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(Login_Intent);
                            finish();

                        }else {

                            Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void sendVerificationEmail() {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user!=null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(Register.this, "Signup successful.Verification email sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
