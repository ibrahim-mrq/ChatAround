package com.chat_tracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chat_tracker.Interface.UserInterface;
import com.chat_tracker.Model.User;
import com.chat_tracker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ProductViewHolder> {

    private ArrayList<String> list;
    private Context context;

    public GalleryAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_gallery_design, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        final String users = list.get(position);
        holder.onBind(users);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.customGallery_img);
        }

        public void onBind(String user) {
            Picasso.get().load(user).fit()
                    .placeholder(R.drawable.ic_image)
                    .into(img);
        }
    }
}
