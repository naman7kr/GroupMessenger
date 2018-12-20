package com.chat.pcon.groupmessenger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {
    Button register;
    TextInputEditText name,email,password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onRegister();
    }
    void init(){
        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_btn);
    }
    void onRegister(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
