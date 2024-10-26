

package com.example.photosharing.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.photosharing.R;
import com.example.photosharing.activity.ItemDetailActivity;
import com.example.photosharing.adapter.StaggeredGridAdapter;
import com.example.photosharing.entity.ShareBean;
import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.net.MyRequest;
import com.example.photosharing.net.URLS;
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

public class MainRecommendFragment extends Fragment {



    private RecyclerView recyclerView;

    private List<ShareBean.DataBean.RecordsBean> shareList = new ArrayList<>();

//    FragmentRequest request = new FragmentRequest();
   MyRequest request = new MyRequest();

    private int current = 0;

    private int size = 10;

    private int total;

    private StaggeredGridAdapter staggeredGridAdapter;

    private RefreshLayout refreshLayout;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            ShareBean shareBean = (ShareBean) msg.obj;
            if (shareBean.getCode() == 200) {
                switch (msg.what) {
                    case 1: // 初次加载数据
                        shareList.clear();
                        shareList.addAll(shareBean.getData().getRecords());
                        total = shareBean.getData().getTotal();
                        break;
                    case 2: // 加载更多数据
                        refreshLayout.finishLoadMore(true);
                        shareList.addAll(shareBean.getData().getRecords());
                        current = shareBean.getData().getCurrent();
                        total = shareBean.getData().getTotal();
                        break;
                    case 3: // 下拉刷新数据
                        refreshLayout.finishRefresh(true);
                        shareList.clear();
                        shareList.addAll(shareBean.getData().getRecords());
                        total = shareBean.getData().getTotal();
                        break;
                }
                staggeredGridAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), shareBean.getMsg(), Toast.LENGTH_SHORT).show();
            }
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

        View root = inflater.inflate(R.layout.fragment_main_recommend, container, false);

        recyclerView = root.findViewById(R.id.main_recommend_recycler);
        refreshLayout = root.findViewById(R.id.fragment_main_refreshLayout);
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

        staggeredGridAdapter = new StaggeredGridAdapter(getActivity(), shareList);

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

        refreshLayout.setDragRate(1f);
        refreshLayout.setReboundDuration(100);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableScrollContentWhenRefreshed(false);


        // 上拉加载
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

        }
    };

    private void initData() {
        Map<String, String> map = new HashMap<>();
        map.put("current", "0");
        map.put("size", String.valueOf(size));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));
        request.doGet(URLS.GET_FIND.getUrl(),
                MyHeaders.getHeaders(),
                map,
                new Callback() {
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
                    Log.d("ERROR", "MainRecommend ERROR");
                }
            }
        });
    }

    private void refreshData() {
        Map<String, String> map = new HashMap<>();
        map.put("current", "0");
        map.put("size", String.valueOf(size));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));

        request.doGet(URLS.GET_FIND.getUrl(),
                MyHeaders.getHeaders(),
                map,
                new Callback() {
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
                    Log.d("ERROR", "refreshData ERROR");
                }
            }
        });
    }

    private void loadData(int page, int num) {
        Map<String, String> map = new HashMap<>();
        map.put("current", String.valueOf(page));
        map.put("size", String.valueOf(num));
        map.put("userId", SharedPreferencesUtil.getValue(getActivity(), "id"));

        request.doGet(URLS.GET_FIND.getUrl(),
                MyHeaders.getHeaders(),
                map,
                new Callback() {
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
                    handler.sendMessage(msg);
                } else {
                    Log.d("ERROR", "loadData ERROR");
                }
            }
        });
    }
}
