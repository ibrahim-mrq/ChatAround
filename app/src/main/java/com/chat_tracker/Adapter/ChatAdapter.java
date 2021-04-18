package com.chat_tracker.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chat_tracker.Model.Chat;
import com.chat_tracker.R;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ONE_TYPE = 1;
    private static final int TWO_TYPE = 2;
    private ArrayList<Chat> chatList;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private String token1;

    public ChatAdapter(ArrayList<Chat> chatList, Context context, String token1) {
        this.chatList = chatList;
        ChatAdapter.context = context;
        this.token1 = token1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == ONE_TYPE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat_one, parent, false);
            return new ChatOneViewHolder(v);
        } else if (viewType == TWO_TYPE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat_two, parent, false);
            return new ChatTwoViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if (holder instanceof ChatOneViewHolder) {
            ChatOneViewHolder chatOneViewHolder = (ChatOneViewHolder) holder;
            chatOneViewHolder.onBind(chat);
        } else {
            ChatTwoViewHolder chatTwoViewHolder = (ChatTwoViewHolder) holder;
            chatTwoViewHolder.onBind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getToken1().equals(token1)) {
            return ONE_TYPE;
        } else {
            return TWO_TYPE;
        }
    }

    public static class ChatOneViewHolder extends RecyclerView.ViewHolder {
        Chat chat;
        TextView tv_mag;

        public ChatOneViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_mag = itemView.findViewById(R.id.tv_itemChatOne_mag);
        }

        public void onBind(final Chat chat) {
            this.chat = chat;
            tv_mag.setText(chat.getMagContent());
        }
    }

    static class ChatTwoViewHolder extends RecyclerView.ViewHolder {

        TextView tv_mag;
        TextView tv_userSender;

        public ChatTwoViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_mag = itemView.findViewById(R.id.tv_itemChatTwo_mag);
            tv_userSender = itemView.findViewById(R.id.tv_itemChatTwo_userSender);
//            tv_userSender.setVisibility(View.GONE);
        }

        public void onBind(final Chat chat) {
            tv_mag.setText(chat.getMagContent());
            tv_userSender.setText(chat.getUserSender());
        }
    }

}
