package com.chat.tntchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chat.tntchatapp.Models.Chats;
import com.chat.tntchatapp.PhotoActivity;
import com.chat.tntchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {
    Context context;
    ArrayList<Chats> msg_list;
    FirebaseUser firebaseUser;

    public  static  final  int  MSG_TYPE_LEFT=0;
    public  static  final  int  MSG_TYPE_RIGHT=1;

    public MessageAdapter(Context context, ArrayList<Chats> msg_list) {
        this.context = context;
        this.msg_list = msg_list;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.MessageAdapterViewHolder(view);
        }
        else {
            View view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.MessageAdapterViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageAdapterViewHolder holder, int position) {
        final Chats chats=msg_list.get(position);
        if(chats.getMessage()!="") {
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(chats.getMessage());
        }
        else
            holder.message.setVisibility(View.GONE);
        if(chats.getImage_uri()!="") {
            holder.msg_img.setVisibility(View.VISIBLE);
            Glide.with(context).load(chats.getImage_uri()).into(holder.msg_img);
            holder.msg_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,PhotoActivity.class);
                    intent.putExtra("url",chats.getImage_uri());
                    context.startActivity(intent);
                }
            });
        }
        else {
            holder.msg_img.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return msg_list.size();
    }

    public class MessageAdapterViewHolder extends  RecyclerView.ViewHolder{
        public TextView message;
        public ImageView msg_img;
        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            message=itemView.findViewById(R.id.message);
            msg_img=itemView.findViewById(R.id.msg_img);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(msg_list.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }
}
