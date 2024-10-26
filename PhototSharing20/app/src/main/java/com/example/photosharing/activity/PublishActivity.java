package com.example.photosharing.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.photosharing.R;
import com.example.photosharing.entity.ImageBean;
import com.example.photosharing.databinding.ActivitySubmitBinding;
import com.example.photosharing.net.URLS;
import com.example.photosharing.utils.GlideEngine;
import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.utils.KeyBoardUtil;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.lwkandroid.widget.ngv.DefaultNgvAdapter;
import com.lwkandroid.widget.ngv.INgvImageLoader;
import com.lwkandroid.widget.ngv.NgvChildImageView;
import com.lwkandroid.widget.ngv.NineGridView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishActivity extends AppCompatActivity {

    private ActivitySubmitBinding binding;

    private Button id_btn_1;
    private Button id_btn_2;
    private Button id_btn_3;
    private Button id_btn_4;
    private boolean flag_id_btn_1 = false;
    private boolean flag_id_btn_2 = false;
    private boolean flag_id_btn_3 = false;
    private boolean flag_id_btn_4 = false;

    private EditText share_text;
    private Button at;

    private EditText sub_title;
    private EditText text;
    private TextView texttitle;
    private TextView textcontent;

    private List<File> file = new ArrayList<>();
    private NineGridView mNineGridView;
    private List<String> imagelist = new ArrayList<>();
    private DefaultNgvAdapter<String> ngvAdapter;


    private String imageCode;

    //handle处理toast显示消息
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    ImageBean imageBean = (ImageBean) msg.obj;
                    //获取图片编码信息
                    imageCode = imageBean.getData().getImageCode();
                    break;
                case 2:
                    Toast.makeText(PublishActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 3:
                    Toast.makeText(PublishActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };


    //是否保存草稿
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // 标头隐藏
        Objects.requireNonNull(getSupportActionBar()).hide();

        sub_title = binding.subTitle;
        text = binding.text;

        binding.submitToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 询问是否保存
                showExitDialog("提示", "是否保存草稿再退出？", "是", "否", true);
            }
        });


        //话题按钮的功能-聚焦到话题编辑框，以及增加”#“号


        texttitle = findViewById(R.id.uploadtitle);
        textcontent = findViewById(R.id.uploadcontent);
        mNineGridView = findViewById(R.id.uploadrecyclerView);
        //设置相关参数或属性
        //设置图片分割间距，默认8dp，默认对应attr属性中divider_line_size
        mNineGridView.setDividerLineSize(TypedValue.COMPLEX_UNIT_PX, 12);
        //设置是否开启编辑模式，默认false，对应attr属性中enable_edit_mode
        mNineGridView.setEnableEditMode(true);
        //设置水平方向上有多少列，默认3，对应attr属性中horizontal_child_count
        mNineGridView.setHorizontalChildCount(4);
        //设置“+”图片，对应attr属性中icon_plus_drawable
        mNineGridView.setIconPlusDrawable(R.drawable.loadup);
        //设置“x”图片，对应attr属性中icon_delete_drawable
        mNineGridView.setIconDeleteDrawable(R.drawable.cancel);
        //设置“x”图片尺寸与父容器尺寸的比例，默认0.2f，范围[0,1]，对应attr属性中icon_delete_size_ratio
        mNineGridView.setIconDeleteSizeRatio(0.15f);
        //设置非编辑模式下，只有一张图片时的尺寸，默认都为0，当宽高都非0才生效，且不会超过NineGridView内部可用总宽度，对应attr属性中single_image_width、single_image_height
        mNineGridView.setSingleImageSize(TypedValue.COMPLEX_UNIT_DIP, 150, 200);

        //NineGridView的数据适配器，构造方法中必须设置最大数据容量和图片加载器
        ngvAdapter = new DefaultNgvAdapter<>(100, new GlideDisplayer2());


        //设置点击事件监听器，以处理不同类型的子视图的点击事件
        ngvAdapter.setOnChildClickListener(new DefaultNgvAdapter.OnChildClickedListener<String>() {

            //回调方法，当用户点击“+”号图片时触发
            @Override
            public void onPlusImageClicked(ImageView plusImageView, int dValueToLimited) {
                //点击“+”号图片后的回调
                //plusImageView代表“+”号图片对象，dValueToLimited代表当前可继续添加的图片数量
                openAlbum();//调用 openAlbum() 方法，打开图片相册
            }


            //回调方法，当用户点击某个内容图片时触发
            @Override
            public void onContentImageClicked(int position, String data, NgvChildImageView childImageView) {
                //显示一个短暂的Toast通知，通知用户已删除了哪个位置的图片以及相关数据
                Toast.makeText(PublishActivity.this, "删除position=" + position + "\n" + data, Toast.LENGTH_SHORT).show();
            }


            //回调方法，当用户删除某个图片时触发
            @Override
            public void onImageDeleted(int position, String data) {
                ngvAdapter.removeData(data);
                imagelist.remove(data);
                Toast.makeText(PublishActivity.this, "删除position=" + position + "\n" + data, Toast.LENGTH_SHORT).show();
            }


        });

        //第三步，关联适配器
        mNineGridView.setAdapter(ngvAdapter);

        binding.submitButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publish();
            }
        });
    }

    /**
     * 点击外部事件，关闭键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {//点击editText控件外部
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    assert v != null;
                    KeyBoardUtil.closeKeyboard(v);//软键盘工具类
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        //必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }


    // 弹窗显示内容
    private void showExitDialog(String title, String message, String first, String second, boolean flag) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(second, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (flag) {//不保留
                            finish();
                        }
                    }
                })
                .setNegativeButton(first, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (flag) {//保留
                            save();
                        }
                    }
                })
                .show();
    }


    //第二步，设置适配器
    //创建图片加载器，加载并显示图片
    private static class GlideDisplayer2 implements INgvImageLoader<String> {
        @Override
        public void load(String source, ImageView imageView, int width, int height) {
            Glide.with(imageView.getContext())
                    .load(source)
                    .apply(new RequestOptions().override(width, height))
                    .into(imageView);
        }
    }


    //打开相册获取图片
    private void openAlbum() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(9)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        //本地文件
                        File file1 = new File(result.get(0).getRealPath());
                        file.add(file1);
                        for (LocalMedia localMedia : result) {
                            imagelist.add(localMedia.getRealPath());
                        }

                        //将图片信息传递给适配器
                        ngvAdapter.setDataList(imagelist);
                        postfile(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void postfile(ArrayList<LocalMedia> result) {

        // 创建一个 OkHttpClient 客户端对象，用于执行网络请求
        OkHttpClient client = new OkHttpClient();

        // 创建一个请求体 (RequestBody)，用于传递文件数据
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileList", file.get(0).getName(),
                        // 设置请求体的类型为"multipart/form-data"，并添加文件字段名称和文件名
                        RequestBody.create(MediaType.parse("multipart/form-data"), file.get(0)))
                .build();// 构建请求体

        // 创建一个 HTTP 请求对象用于指定请求的方法、URL、头部信息和请求体
        Request request = new Request.Builder()
                .url("http://47.107.52.7:88/member/photo/image/upload")//设置请求的URL
                .headers(MyHeaders.getHeaders())// 设置请求头部信息
                .post(requestBody)// 使用 POST 请求方法，并指定请求体
                .build();// 构建请求对象


        //使用 OkHttpClient 执行异步网络请求，请求的响应在回调中处理
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            // 网络请求成功，在子线程中执行
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程

                    //从响应中解析出 ImageBean 对象，并使用 Gson 来解析 JSON 数据
                    ImageBean imageBean = new Gson().fromJson(response.body().string(), ImageBean.class);

                    //创建一个消息对象 (Message)，用于向主线程发送消息
                    Message msg = new Message();
                    msg.what = 1;// 设置消息的类型标识
                    msg.obj = imageBean;// 将解析得到的 ImageBean 对象作为消息的数据部分

                    // 使用 Handler 将消息发送到主线程中处理
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void publish() {
        // 创建 Gson 对象，用于将数据对象转换为 JSON 字符串
        Gson gson = new Gson();
        String title = "";
        String text = "";
        Map<String, Object> bodyMap = new HashMap<>();

        // 从 UI 控件中获取标题和内容
        title = binding.subTitle.getText().toString();
        text = binding.text.getText().toString();

        // 检查标题、内容和 imageCode 是否为空
        if (!title.equals("") && !text.equals("") && imageCode != null) {

            // 如果通过验证，将数据添加到 bodyMap 中
            bodyMap.put("content", text);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId", SharedPreferencesUtil.getValue(PublishActivity.this, "id"));
            bodyMap.put("title", title);

            // 使用Gson将对象转换为json字符串
            String json = gson.toJson(bodyMap);

            // 创建请求体 (RequestBody) 用于传递 JSON 数据
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

            // 创建 OkHttpClient 对象，用于执行网络请求
            OkHttpClient client = new OkHttpClient();

            // 创建 HTTP 请求对象 (Request) 用于指定请求的方法、URL、头部信息和请求体
            Request request = new Request.Builder()
                    .url(URLS.ADD_SHARE.getUrl())
                    .headers(MyHeaders.getHeaders())
                    .post(requestBody)//传递请求体
                    .build();


            // 使用 OkHttpClient 执行异步网络请求，请求的响应在回调中处理
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d("publishRes", response.toString());
                        Message msg = new Message();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                }
            });

            //发布失败的情况
        } else {
            Toast.makeText(PublishActivity.this, "标题,内容和图片不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void save() {
        Gson gson = new Gson();
        String title = "";
        String text = "";
        Map<String, Object> bodyMap = new HashMap<>();

        // 从 UI 控件中获取标题和内容
        title = binding.subTitle.getText().toString();
        text = binding.text.getText().toString();

        // 将数据添加到 bodyMap 中
        bodyMap.put("content", text);
        bodyMap.put("imageCode", imageCode);
        bodyMap.put("pUserId", SharedPreferencesUtil.getValue(PublishActivity.this, "id"));
        bodyMap.put("title", title);

        // 使用Gson将对象转换为json字符串
        String json = gson.toJson(bodyMap);

        // MediaType  设置Content-Type 标头中包含的媒体类型值
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Log.d("savePrint", "postData3: " + json);

        // 创建 OkHttpClient 对象，用于执行网络请求
        OkHttpClient client = new OkHttpClient();

        // 创建 HTTP 请求对象 (Request) 用于指定请求的方法、URL、头部信息和请求体
        Request request = new Request.Builder()
                .url(URLS.SAVE.getUrl())
                .headers(MyHeaders.getHeaders())
                .post(requestBody)//// 使用 POST 请求方法，并指定请求体，传递请求体
                .build();


        // 使用 OkHttpClient 执行异步网络请求，请求的响应在回调中处理
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Message msg = new Message();
                    msg.what = 3;// 设置消息的类型标识
                    handler.sendMessage(msg);
                } else {
                    System.out.println(response);
                }
            }
        });
    }
}
