package com.chat_tracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chat_tracker.Activity.Login.ProfileActivity;
import com.chat_tracker.Interface.UserInterface;
import com.chat_tracker.Model.User;
import com.chat_tracker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ProductViewHolder> {

    private ArrayList<User> list;
    private UserInterface listener;
    private Context context;

    public UserAdapter(ArrayList<User> list, Context context, UserInterface listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_design, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        final User users = list.get(position);
        holder.onBind(users);

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.add(users);
                notifyDataSetChanged();
            }
        });

        holder.imgPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.privateMessages(users);
            }
        });
        holder.imgDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.directChat(users);
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context , ProfileActivity.class)
                .putExtra("name" , users.getName())
                .putExtra("token" , users.getToken())
                .putExtra("img" , users.getImage())
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgAdd, imgDirect, imgPrivate;
        private TextView tv_name;
        private CircleImageView img;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.customUser_tv_name);
            img = itemView.findViewById(R.id.customUser_img);

            imgAdd = itemView.findViewById(R.id.customUser_img_add);
            imgDirect = itemView.findViewById(R.id.customUser_img_direct);
            imgPrivate = itemView.findViewById(R.id.customUser_img_private);
        }

        public void onBind(User user) {
            tv_name.setText(user.getName());
            if (!user.getImage().isEmpty())
                Picasso.get().load(user.getImage()).placeholder(R.drawable.user).into(img);
        }
    }
}
