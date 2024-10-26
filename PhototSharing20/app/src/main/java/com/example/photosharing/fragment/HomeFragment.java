package com.example.photosharing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.photosharing.R;
import com.example.photosharing.activity.LoginActivity;
import com.example.photosharing.adapter.TabFragmentAdapter;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ViewPager pager;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_personal_description;
    private ImageView iv_sex;


    private final String[] table = {"我的", "喜欢"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        pager = view.findViewById(R.id.home_page);
        tabLayout = view.findViewById(R.id.home_tab_layout);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // 设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        tv_personal_description = view.findViewById(R.id.personal_description);
        TextView tv_id = view.findViewById(R.id.profile_user_id);
        TextView tv_subscription = view.findViewById(R.id.subscription_number);
        TextView tv_fan = view.findViewById(R.id.fan_number);
        TextView tv_thumbsup = view.findViewById(R.id.thumbsup_number);
        iv_sex = view.findViewById(R.id.sex);
        TextView account_home = view.findViewById(R.id.account_home);

        Toolbar toolbar = view.findViewById(R.id.toolbar);


        Map<String, String> map = SharedPreferencesUtil.getMap(this.getActivity());

        tv_id.setText("id:" + map.get("id"));

        account_home.setText(map.get("username"));
        tv_personal_description.setText(map.get("introduce"));
        return view;
    }


    private void refreshData() {
        // 在这里执行数据刷新的逻辑
        Map<String, String> map = SharedPreferencesUtil.getMap(this.getActivity());
        tv_personal_description.setText(map.get("introduce"));

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeMineFragment());
        fragmentList.add(new HomeLikeFragment());

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(getActivity().getSupportFragmentManager(),
                fragmentList, table);

        pager.setAdapter(tabFragmentAdapter);
        tabLayout.setupWithViewPager(pager);

        // 滚动到顶部
        if (pager.getChildAt(0) instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) pager.getChildAt(0);
            if (recyclerView.getLayoutManager() != null) {
                recyclerView.getLayoutManager().scrollToPosition(0);
            }
        }


        // 刷新完成
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeMineFragment());
        fragmentList.add(new HomeLikeFragment());

        TabFragmentAdapter tabFragmentAdapter = new TabFragmentAdapter(getActivity().getSupportFragmentManager(), fragmentList, table);

        pager.setAdapter(tabFragmentAdapter);
        tabLayout.setupWithViewPager(pager);
    }
}

