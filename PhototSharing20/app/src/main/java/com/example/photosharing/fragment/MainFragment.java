package com.example.photosharing.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.photosharing.R;
import com.example.photosharing.adapter.TabFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    private ViewPager pager;
    private TabLayout tabLayout;
    private final String[] table =  {"推荐", "关注"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 创建视图并将其与 fragment_main.xml 关联
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // 获取 ViewPager 和 TabLayout 控件的引用
        pager = view.findViewById(R.id.main_page);
        tabLayout = view.findViewById(R.id.main_tab_layout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 创建包含两个子 Fragment 的列表
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MainRecommendFragment()); // 推荐
        fragmentList.add(new MainFollowFragment()); // 关注

        // 创建适配器，用于将子 Fragment 与 ViewPager 关联
        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, table);

        // 设置适配器给 ViewPager
        pager.setAdapter(tabFragmentAdapter);

        // 将 TabLayout 与 ViewPager 建立关联
        tabLayout.setupWithViewPager(pager);
    }
}
