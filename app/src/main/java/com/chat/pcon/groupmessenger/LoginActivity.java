package com.chat.pcon.groupmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email,password;
    TextInputLayout email_wrapper;
    TextInputLayout pass_wrapper;
    Button cancel,submit;
    TextView register;
    RelativeLayout rlayout;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    private static final String TAG = "LoginActivity";
    ProgressDialog dialog;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        onSubmit();
        onCancel();
        onRegister();

        mAuth = FirebaseAuth.getInstance();
    }
    void init(){
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        cancel = findViewById(R.id.login_cancel);
        submit = findViewById(R.id.login_submit);
        register = findViewById(R.id.login_register);
        rlayout = findViewById(R.id.login_rel);
        email_wrapper = findViewById(R.id.login_email_wrapper);
        pass_wrapper = findViewById(R.id.login_password_wrapper);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }
    void setDialog(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("...Signing you in");
        dialog.setCancelable(false);
    }
    void updateUI(final FirebaseUser user, boolean flag){
        if(user!=null){
            if (flag == false) {
                //store user info in shared preferences
                mFirestore.collection("user").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        dialog.dismiss();
                        DocumentSnapshot snapshot = task.getResult();
                        UserInfo info = snapshot.toObject(UserInfo.class);
                        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("name", info.name);
                        editor.putString("email", info.email);
                        editor.putString("color", info.color);
                        editor.putString("uid", user.getUid());
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }else{
                dialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                startActivity(intent);
                finish();
            }

        }

    }

    boolean checkInputErrors(){
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        if(!pattern.matcher(mail).matches()){
            email_wrapper.setError("Not a valid Email");
            return false;
        }
        if(pass.length()<=5){
            pass_wrapper.setError("Not a valid Password");
            return false;
        }
        email_wrapper.setErrorEnabled(false);
        pass_wrapper.setErrorEnabled(false);
        return true;
    }

    void onSubmit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInputErrors()) {
                    dialog.show();
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user,false);
                                    } else {
                                        dialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null,false);
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
        setDialog();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser,true);
    }
}
