package com.chat.pcon.groupmessenger;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout name_wrapper,email_wrapper,pass_wrapper;
    Button register;
    TextInputEditText name,email,password;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    ProgressDialog dialog;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final String TAG = "RegisterActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        onRegister();
    }
    void init(){
        name = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_btn);
        name_wrapper = findViewById(R.id.register_name_wrapper);
        email_wrapper = findViewById(R.id.register_email_wrapper);
        pass_wrapper = findViewById(R.id.register_pass_wrapper);

        mFirestore = FirebaseFirestore.getInstance();


    }
    void setDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("...Signing you up");
        dialog.setCancelable(false);
    }
    boolean checkInputErrors(){
        String username = name.getText().toString();
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        if(username.length()<=3){
            name_wrapper.setError("Name must have atleast 4 characters");
            return false;
        }
        if(!pattern.matcher(mail).matches()){
            email_wrapper.setError("Not a valid Email");
            return false;
        }
        if(pass.length()<=5){
            pass_wrapper.setError("Password must have atleast 6 characters");
            return false;
        }
        name_wrapper.setErrorEnabled(false);
        email_wrapper.setErrorEnabled(false);
        pass_wrapper.setErrorEnabled(false);
        return true;
    }
    void onRegister(){

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkInputErrors()) {
                    dialog.show();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            //set User details in firestore database
            UserInfo info = new UserInfo(
                    name.getText().toString(),
                    user.getEmail(),
                    getRandomColor(),
                    user.getUid()
            );
            mFirestore.collection("user").document(mAuth.getUid()).set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Log.d(TAG,"Data Saved Successfully");
                    mAuth.signOut();
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Log.e(TAG, "Data was not saved Successfully");
                }
            });
        }

    }
    String getRandomColor(){
        String [] colors = getResources().getStringArray(R.array.colors);
        return colors[new Random().nextInt(colors.length)];
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

    @Override
    protected void onStart() {
        super.onStart();
        setDialog();
    }
}
