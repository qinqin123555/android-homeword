package com.example.photosharing.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.example.photosharing.R;
import com.example.photosharing.entity.PersonBean;
import com.example.photosharing.net.Code;
import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.net.MyRequest;
import com.example.photosharing.net.URLS;
import com.example.photosharing.utils.KeyBoardUtil;
import com.example.photosharing.utils.MergeURLUtil;
import com.example.photosharing.utils.SharedPreferencesUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private Boolean eyeSwitch = false;
    private String account = "";
    private String password = "";
    Map<String, String> loginInfo = new HashMap<>();


    private Handler handler;


    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 按钮
        final Button btnLogin = findViewById(R.id.login);
        // 注册按钮
        final TextView btnRegister = findViewById(R.id.button_register);

        // 获取图片资源
        // account
        final ImageView accountView = findViewById(R.id.account_img);
        final ImageView accountClose = findViewById(R.id.account_close);

        // pwd
        final ImageView passwordView = findViewById(R.id.password_img);
        final ImageView passwordClose = findViewById(R.id.password_close);
        final ImageView passwordEye = findViewById(R.id.password_eye);

        // input
        EditText accountText = findViewById(R.id.login_account);
        EditText passwordText = findViewById(R.id.login_password);

        // clear
        clearEditText(accountText, accountClose);
        clearEditText(passwordText, passwordClose);

        //  监听listen
        accountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int num, int num1, int num2) {
                accountClose.setVisibility(View.GONE);
                accountView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int num, int num1, int num2) {
                accountClose.setVisibility(View.VISIBLE);
                accountView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    accountClose.setVisibility(View.VISIBLE);
                    accountView.setVisibility(View.GONE);
                    account = editable.toString();
                } else {
                    accountClose.setVisibility(View.GONE);
                    accountView.setVisibility(View.VISIBLE);
                }
            }
        });

        // 对密码输入框输入时监听
        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordClose.setVisibility(View.GONE);
                passwordView.setVisibility(View.VISIBLE);
                passwordEye.setVisibility(View.GONE);
                eyeSwitch = false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordClose.setVisibility(View.VISIBLE);
                passwordView.setVisibility(View.GONE);
                passwordEye.setVisibility(View.VISIBLE);
            }

            @Override

            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    passwordClose.setVisibility(View.VISIBLE);
                    passwordView.setVisibility(View.GONE);
                    passwordEye.setVisibility(View.VISIBLE);
                    eyeSwitch = false;
                    password = editable.toString();
                } else {
                    passwordClose.setVisibility(View.GONE);
                    passwordView.setVisibility(View.VISIBLE);
                    passwordEye.setVisibility(View.GONE);
                }
            }
        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                PersonBean personBean = (PersonBean) msg.obj;

                if (personBean.getCode() == Code.SUCCESS) {
                    Map<String, Object> map = JSON.parseObject(JSON.toJSONString(personBean.getData()));
                    if (SharedPreferencesUtil.putMap(LoginActivity.this, map)) {
                        Toast.makeText(LoginActivity.this, "图跃，分享你的生活", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, NavBottomActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("ERROR", "ERROR Login");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, personBean.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // 登录逻辑
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!account.equals("") && !password.equals("")) {
                    loginInfo.put("username", account);
                    loginInfo.put("password", password);


                    MyRequest MyRequest = new MyRequest();
                    MyRequest.doPost(MergeURLUtil.merge(URLS.LOGIN.getUrl(), loginInfo),
                            MyHeaders.getHeaders(),
                            new HashMap<>(),
                            new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    if (response.isSuccessful()) {
                                        PersonBean personBean = new Gson().fromJson(response.body().string(), PersonBean.class);
                                        Message msg = new Message();
                                        msg.obj = personBean;
                                        handler.sendMessage(msg);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // 眼睛展示功能
        passwordEye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        passwordEye.setImageResource(R.drawable.open_eye);
                        passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        passwordEye.setImageResource(R.drawable.closed_eye);
                        passwordText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        passwordText.setTypeface(Typeface.DEFAULT);
                        break;
                    }
                    default: break;
                }

                // 光标判断
                CharSequence charSequence = passwordText.getText();
                if (charSequence != null) {
                    Spannable spannable = (Spannable) charSequence;
                    Selection.setSelection(spannable, charSequence.length());
                };
                return true;
            }
        });
    }

    // 点击删除EditText内容功能
    private void clearEditText(EditText editText, ImageView closeView) {
        closeView.setOnClickListener(v -> editText.setText(""));
    }

  // 点击外部事件，关闭键盘
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
}
