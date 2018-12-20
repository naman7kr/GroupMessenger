package com.chat.pcon.groupmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email,password;
    Button cancel,submit;
    TextView register;
    RelativeLayout rlayout;
    FirebaseAuth auth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        onSubmit();
        onCancel();
        onRegister();

        auth = FirebaseAuth.getInstance();
    }
    void init(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        cancel = findViewById(R.id.login_cancel);
        submit = findViewById(R.id.login_submit);
        register = findViewById(R.id.login_register);
        rlayout = findViewById(R.id.login_rel);
        auth = FirebaseAuth.getInstance();
    }
    void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent = new Intent(this,ChatActivity.class);
            startActivity(intent);
            finish();
        }
    }
    void onSubmit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().compareTo("")!=0  && password.getText().toString().compareTo("")!=0) {
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = auth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        });
    }
    void onCancel(){
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setText("");
                password.setText("");
                email.clearFocus();
                password.clearFocus();
            }
        });
    }
    void onRegister(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setText("");
                password.setText("");
                email.clearFocus();
                password.clearFocus();
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
}
