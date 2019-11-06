package com.chat.tntchatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.tntchatapp.MessageActivity;
import com.chat.tntchatapp.Models.Users;
import com.chat.tntchatapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> {


    Context context;
    ArrayList<Users> user_list;

    public UserAdapter(Context context, ArrayList<Users> user_list) {
        this.context = context;
        this.user_list = user_list;
    }

    @NonNull
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.UserAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull UserAdapterViewHolder holder, int position) {
        final Users users=user_list.get(position);
        holder.userName.setText(users.getUserName());
        if(users.getImageUrl().equals("default"))
            holder.user_image.setImageResource(R.drawable.images);
        else
            Glide.with(context).load(users.getImageUrl()).into(holder.user_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userId",users.getId());
                context.startActivity(intent);
            }
        });

        if(users.getStatus().equals("online"))
            holder.status.setImageResource(R.color.green_400);
        else
            holder.status.setImageResource(R.color.grey_40);
    }





    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class UserAdapterViewHolder extends  RecyclerView.ViewHolder{
        public TextView userName;
        public CircleImageView user_image;
        public  CircleImageView status;
        public UserAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            user_image=itemView.findViewById(R.id.user_image);
            userName=itemView.findViewById(R.id.user_name);
            status=itemView.findViewById(R.id.status);
        }
    }
}
