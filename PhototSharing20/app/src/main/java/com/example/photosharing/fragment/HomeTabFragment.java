package com.example.photosharing.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photosharing.R;

public class HomeTabFragment extends Fragment {

    private TextView titleTv;

    private String mTitle;

    public HomeTabFragment(String title) {
        mTitle = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_tab, container, false);
        titleTv = view.findViewById(R.id.tv_title);
        titleTv.setText(mTitle);
        return view;
    }
}
