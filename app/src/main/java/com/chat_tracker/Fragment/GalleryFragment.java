package com.chat_tracker.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat_tracker.Activity.MapsActivity;
import com.chat_tracker.Adapter.GalleryAdapter;
import com.chat_tracker.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private View v;
    private ArrayList<String> list = new ArrayList<>();
    private GalleryAdapter adapter;
    private RecyclerView rv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_gallery, container, false);

        rv = v.findViewById(R.id.galleryF_rv);

        list.add("https://lh3.googleusercontent.com/proxy/yb4FQX-7CnvjnzqLvIvb6AvZ2rZMAwKgjbensOAyD5rzNIUcLc7HxBiXcTyCHXLEgW1wxRlI1QSIBTJl1gXSD6Dil7DvnKrWYh0JIWcHIW1Z");
        list.add("https://static.remove.bg/remove-bg-web/a6c5f1017e9c0bdc648aad9debd2f40a17d45814/assets/start-0e837dcc57769db2306d8d659f53555feb500b3c5d456879b9c843d1872e7baa.jpg");
        list.add("https://cdn.arstechnica.net/wp-content/uploads/2016/02/5718897981_10faa45ac3_b-640x624.jpg");
        list.add("https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png");
        list.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        list.add("https://filedn.com/ltOdFv1aqz1YIFhf4gTY8D7/ingus-info/BLOGS/Photography-s0tocks3/stock-photography-slider.jpg");
        list.add("https://www.stevebloom.com/images/b/002732-SB2.jpg");

        list.add("https://lh3.googleusercontent.com/proxy/yb4FQX-7CnvjnzqLvIvb6AvZ2rZMAwKgjbensOAyD5rzNIUcLc7HxBiXcTyCHXLEgW1wxRlI1QSIBTJl1gXSD6Dil7DvnKrWYh0JIWcHIW1Z");
        list.add("https://static.remove.bg/remove-bg-web/a6c5f1017e9c0bdc648aad9debd2f40a17d45814/assets/start-0e837dcc57769db2306d8d659f53555feb500b3c5d456879b9c843d1872e7baa.jpg");
        list.add("https://cdn.arstechnica.net/wp-content/uploads/2016/02/5718897981_10faa45ac3_b-640x624.jpg");
        list.add("https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png");
        list.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        list.add("https://filedn.com/ltOdFv1aqz1YIFhf4gTY8D7/ingus-info/BLOGS/Photography-stocks3/stock-photography-slider.jpg");
        list.add("https://www.stevebloom.com/images/b/002732-SB2.jpg");

        list.add("https://lh3.googleusercontent.com/proxy/yb4FQX-7CnvjnzqLvIvb6AvZ2rZMAwKgjbensOAyD5rzNIUcLc7HxBiXcTyCHXLEgW1wxRlI1QSIBTJl1gXSD6Dil7DvnKrWYh0JIWcHIW1Z");
        list.add("https://static.remove.bg/remove-bg-web/a6c5f1017e9c0bdc648aad9debd2f40a17d45814/assets/start-0e837dcc57769db2306d8d659f53555feb500b3c5d456879b9c843d1872e7baa.jpg");
        list.add("https://cdn.arstechnica.net/wp-content/uploads/2016/02/5718897981_10faa45ac3_b-640x624.jpg");
        list.add("https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png");
        list.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        list.add("https://filedn.com/ltOdFv1aqz1YIFhf4gTY8D7/ingus-info/BLOGS/Photography-stocks3/stock-photography-slider.jpg");
        list.add("https://www.stevebloom.com/images/b/002732-SB2.jpg");

        list.add("https://lh3.googleusercontent.com/proxy/yb4FQX-7CnvjnzqLvIvb6AvZ2rZMAwKgjbensOAyD5rzNIUcLc7HxBiXcTyCHXLEgW1wxRlI1QSIBTJl1gXSD6Dil7DvnKrWYh0JIWcHIW1Z");
        list.add("https://static.remove.bg/remove-bg-web/a6c5f1017e9c0bdc648aad9debd2f40a17d45814/assets/start-0e837dcc57769db2306d8d659f53555feb500b3c5d456879b9c843d1872e7baa.jpg");
        list.add("https://cdn.arstechnica.net/wp-content/uploads/2016/02/5718897981_10faa45ac3_b-640x624.jpg");
        list.add("https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png");
        list.add("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__340.jpg");
        list.add("https://filedn.com/ltOdFv1aqz1YIFhf4gTY8D7/ingus-info/BLOGS/Photography-stocks3/stock-photography-slider.jpg");
        list.add("https://www.stevebloom.com/images/b/002732-SB2.jpg");

        adapter = new GalleryAdapter(getActivity(), list);

        rv.setAdapter(adapter);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter.notifyDataSetChanged();

        return v;

    }
}