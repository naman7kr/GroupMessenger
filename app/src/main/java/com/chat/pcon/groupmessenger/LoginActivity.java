package com.chat.pcon.groupmessenger;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email,password;
    Button cancel,submit;
    TextView register;
    RelativeLayout rlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        onSubmit();
        onCancel();
        onRegister();
    }
    void init(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        cancel = findViewById(R.id.login_cancel);
        submit = findViewById(R.id.login_submit);
        register = findViewById(R.id.login_register);
        rlayout = findViewById(R.id.login_rel);
    }
    void onSubmit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);
                //finish();
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
}
