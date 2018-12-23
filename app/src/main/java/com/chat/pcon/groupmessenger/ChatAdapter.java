package com.chat.pcon.groupmessenger;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<MyViewHolder> {
    List<MessageInfo> infos;
    Context context;
    int lastAnimatedPosition = -1;
    public ChatAdapter(List<MessageInfo> infos){
        this.infos = infos;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        this.context = viewGroup.getContext();
        if(viewType==0) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_msg_send, viewGroup, false);
        }else{
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_msg_receive,viewGroup,false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        final MessageInfo info = infos.get(position);

        myViewHolder.name.setText(info.name);
        myViewHolder.msgBody.setText(info.msg);

        myViewHolder.msgHead.setText(String.valueOf(info.name.charAt(0)));
        GradientDrawable drawable = (GradientDrawable) myViewHolder.msgHead.getBackground();
        drawable.setColor(Color.parseColor(info.color));

        myViewHolder.msgHead.setTextColor(Color.WHITE);
        myViewHolder.msgHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("uid", info.uid);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(infos.get(position).type == false)
            return 0;
        else
            return 1;

    }

}
class MyViewHolder extends RecyclerView.ViewHolder{
    TextView msgHead,name,msgBody;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        msgHead = itemView.findViewById(R.id.msg_head);
        msgBody = itemView.findViewById(R.id.msg_body);
        name = itemView.findViewById(R.id.msg_name);
    }
}