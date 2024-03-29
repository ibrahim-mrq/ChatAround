package com.chat_tracker.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.chat_tracker.Model.TabClass;

import java.util.ArrayList;

public class TabAdapter extends FragmentStatePagerAdapter {


    ArrayList<TabClass> tabs;

    public TabAdapter(FragmentManager fm, ArrayList<TabClass> tabs) {
        super(fm);
        this.tabs = tabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return tabs.get(position).getFragment();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getName();

    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
