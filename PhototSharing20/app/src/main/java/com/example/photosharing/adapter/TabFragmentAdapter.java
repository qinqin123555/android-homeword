package com.example.photosharing.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class TabFragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList;

    private final String[] mTitle;

    public TabFragmentAdapter(FragmentManager fragmentManager, List<Fragment> fragments, String[] title){
        super(fragmentManager);
        mFragmentList = fragments;
        mTitle= title;

    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }
}

