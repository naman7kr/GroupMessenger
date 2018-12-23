package com.chat.pcon.groupmessenger;


import com.google.firebase.Timestamp;

public class MessageInfo {
    public String msg;
    public String uid;
    public String name;
    Timestamp timestamp;
    public String color;
    public boolean type=true; //if false then sent msg and true then received msg
    public MessageInfo(){}

    public MessageInfo(String msg,String uid, String name,Timestamp timestamp,boolean type,String color){
        this.msg = msg;
        this.uid = uid;
        this.name = name;
        this.timestamp = timestamp;
        this.type = type;
        this.color = color;
    }
}
