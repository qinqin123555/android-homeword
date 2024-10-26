package com.example.photosharing.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.photosharing.R;
import com.example.photosharing.fragment.HomeFragment;
import com.example.photosharing.fragment.MainFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class NavBottomActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener  {

    private BottomNavigationView navigationView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_vavigation);
        navigationView = findViewById(R.id.nav_bottom);
//      不加的话什么都没有
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setSelectedItemId(R.id.navigation_main);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
//           首页,新建MainFragment片段，MainFragment片段中有推荐和关注片段
            case R.id.navigation_main:
                fragmentTransaction.replace(R.id.home_fragment, new MainFragment()).commit();
                return true;
//          发布，点击发布之后跳转到发布活动页面
            case R.id.navigation_publish: {
                Intent intent = new Intent(NavBottomActivity.this, PublishActivity.class);
                startActivity(intent);
                return false;
            }
//          主页，新建HomeFragment片段
            case R.id.navigation_home:
                fragmentTransaction.replace(R.id.home_fragment, new HomeFragment()).commit();
                return true;
        }
        return true;
    }
}
