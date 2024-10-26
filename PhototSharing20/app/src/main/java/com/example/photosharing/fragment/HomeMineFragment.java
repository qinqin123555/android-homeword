package com.example.photosharing.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.photosharing.R;
import com.example.photosharing.activity.ItemDetailActivity;
import com.example.photosharing.adapter.StaggeredGridAdapter;
import com.example.photosharing.entity.ShareBean;
import com.example.photosharing.net.FragmentRequest;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.example.photosharing.utils.StaggeredDividerItemDecoration;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeMineFragment extends Fragment {

    private RecyclerView recyclerView;

    private TextView textView;



    private List<ShareBean.DataBean.RecordsBean> shareList = new ArrayList<>();

    FragmentRequest request = new FragmentRequest();

    private int current = 0;

    private int size = 10;

    private int total;

    private StaggeredGridAdapter staggeredGridAdapter;

    private RefreshLayout refreshLayout;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 1: // 接受新数据
                        ShareBean shareBean1 = (ShareBean) msg.obj;
                        shareList.clear();
                        if (shareBean1.getData() != null) {
                            shareList.addAll(shareBean1.getData().getRecords());
                            total = shareBean1.getData().getTotal();
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 2: // 加载更多数据
                        ShareBean shareBean2 = (ShareBean) msg.obj;
                        refreshLayout.finishLoadMore(true);
                        if (shareBean2.getData() != null) {
                            shareList.addAll(shareBean2.getData().getRecords());
                            current = shareBean2.getData().getCurrent();
                            total = shareBean2.getData().getTotal();
                        }
                        break;
                    case 3: // 下拉刷新数据
                        ShareBean shareBean3 = (ShareBean) msg.obj;
                        refreshLayout.finishRefresh(true);
                        shareList.clear();
                        if (shareBean3.getData() != null) {
                            shareList.addAll(shareBean3.getData().getRecords());
                            total = shareBean3.getData().getTotal();
                        }
                        break;
                    case 4: // 删除成功
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        refreshData();
                        break;
                }
                staggeredGridAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_mine, container, false);

        recyclerView = root.findViewById(R.id.home_mine_recycler);
        // 设置RecyclerView 保持固定大小
        recyclerView.setHasFixedSize(true);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                manager.invalidateSpanAssignments(); //防止第一行到顶部有空白区域
            }
        });

        //以下三行去掉 RecyclerView 动画代码，防止闪烁
        ((DefaultItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getItemAnimator().setChangeDuration(0);

        // 设置瀑布流布局2列，垂直方向
        recyclerView.setLayoutManager(manager);

        textView = root.findViewById(R.id.mine_empty);



        refreshLayout = root.findViewById(R.id.fragment_mine_refreshLayout);

        //
        staggeredGridAdapter = new StaggeredGridAdapter(getActivity(), shareList);
        // 删除
        staggeredGridAdapter.setMine(true);

        staggeredGridAdapter.setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener);
        // 创建分割线对象
        recyclerView.addItemDecoration(new StaggeredDividerItemDecoration(this.getActivity(), 10));
        recyclerView.setAdapter(staggeredGridAdapter);
        // 下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if ((current+1)*size >= total) {
                    refreshlayout.setNoMoreData(false);
                    refreshlayout.finishLoadMore(true);//传入false表示加载失败
                } else {
                    loadData(++current, size);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData();
            }
        });

        return root;
    }

    StaggeredGridAdapter.OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = new StaggeredGridAdapter.OnRecyclerViewItemClickListener() {

        @Override
        public void onItemClick(int position) {
            Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("items", (Serializable) shareList);
            intent.putExtra("position", position);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onLongItemClick(int position) {
            ShareBean.DataBean.RecordsBean recordsBean = shareList.get(position);
            new AlertDialog.Builder(getContext())
                    .setTitle("删除操作")
                    .setMessage("是否删除？")
                    .setPositiveButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteMine(recordsBean);
                        }
                    })
                    .show();
        }
    };

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("current", "0");
        map.put("size", String.valueOf(size));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));
        request.getMyself(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ShareBean shareBean = new Gson().fromJson(response.body().string(), ShareBean.class);
                    Message msg = new Message();
                    msg.obj = shareBean;
                    msg.what = 1;
                    handler.sendMessage(msg);
                } else {
                }
            }
        });
    }

    private void refreshData() {
        Map<String, String> map = new HashMap<>();
        map.put("current", "0");
        map.put("size", String.valueOf(size));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));
        request.getMyself(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ShareBean shareBean = new Gson().fromJson(response.body().string(), ShareBean.class);
                    Message msg = new Message();
                    msg.obj = shareBean;
                    msg.what = 3;
                    handler.sendMessage(msg);
                } else {

                }
            }
        });
    }

    private void loadData(int page, int num) {
        Map<String, String> map = new HashMap<>();
        map.put("current", String.valueOf(page));
        map.put("size", String.valueOf(num));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));
        request.getMyself(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ShareBean shareBean = new Gson().fromJson(response.body().string(), ShareBean.class);
                    Message msg = new Message();
                    msg.obj = shareBean;
                    msg.what = 2;
                    handler.sendMessageDelayed(msg, 2000);
                } else {
                }
            }
        });
    }

    private void deleteMine(ShareBean.DataBean.RecordsBean recordsBean) {
        Map<String, String> map = new HashMap<>();
        map.put("shareId", recordsBean.getId());
        map.put("userId", SharedPreferencesUtil.getValue(getContext() ,"id"));
        System.out.println(map);
        request.deleteShow(map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println(response);
                    Message msg = new Message();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }
        });
    }

}
