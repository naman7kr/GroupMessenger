package com.chat.pcon.groupmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rView;
    EditText msg;
    ImageView send;
    ChatAdapter mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    List<MessageInfo> infos=new ArrayList<>();

    private static final String TAG = "ChatActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        setAdapter();
        rView.setItemAnimator(new MyItemAnimator(this));
        onSend();
        onReceiveMsg();
    }

    void init(){
        rView = findViewById(R.id.chat_rview);
        msg = findViewById(R.id.chat_msg);
        send = findViewById(R.id.chat_send_btn);
        mAdapter = new ChatAdapter(infos);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }


    void setAdapter(){
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        rView.setLayoutManager(manager);
        rView.setAdapter(mAdapter);
    }
    void onSend(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(msg.getText().toString().compareTo("")!=0){
                    SharedPreferences pref = getSharedPreferences("user_info",MODE_PRIVATE);
                    UserInfo uInfo = new UserInfo(
                            pref.getString("name","No Name"),
                            pref.getString("email","No Email"),
                            pref.getString("color","#fff"),
                            pref.getString("uid","null")
                    );
                    MessageInfo mInfo = new MessageInfo(
                            msg.getText().toString(),
                            uInfo.uid,
                            uInfo.name,
                            Timestamp.now(),
                            false,
                            uInfo.color
                    );
                    infos.add(0,mInfo);
                    mAdapter.notifyItemInserted(0);
                    rView.scrollToPosition(0);

                    mFirestore.collection("room").add(mInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG,"msg successfully added to firestore");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG,"msg cannot be added to firestore");
                        }
                    });
                }
                msg.setText("");

            }
        });
    }

    void onReceiveMsg(){

        mFirestore.collection("room")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.e(TAG,"Listen Failed"+e);
                    return;
                }
                if(snapshot!=null ) {
                    List<DocumentChange> changes=snapshot.getDocumentChanges();
                    if(infos.isEmpty()){
                     for(DocumentChange change:changes){
                         MessageInfo mInfo = change.getDocument().toObject(MessageInfo.class);
                         if(mInfo.uid.compareTo(mAuth.getUid())==0){
                             mInfo.type = false;
                         }else{
                             mInfo.type = true;
                         }
                         infos.add(mInfo);
                         mAdapter.notifyDataSetChanged();
                     }

                    }else {
                        if (!changes.isEmpty()) {
                            DocumentChange dc = changes.get(0);
                            MessageInfo mInfo = dc.getDocument().toObject(MessageInfo.class);

                            if(mInfo.uid.compareTo(mAuth.getUid())!=0) {
                                mInfo.type = true;
                                infos.add(0, mInfo);
                                mAdapter.notifyItemInserted(0);
                                rView.scrollToPosition(0);
                                Log.e(TAG, "Message Successfully received");
                            }
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.chat,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                SharedPreferences preferences = getSharedPreferences("user_info",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                mAuth.signOut();
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            case  R.id.menu_profile:
                Intent intent = new Intent(this,ProfileActivity.class);
                intent.putExtra("uid",mAuth.getUid());
                startActivity(intent);
        }
        return true;
    }
}
