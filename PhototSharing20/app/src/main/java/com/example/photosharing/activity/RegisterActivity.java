package com.example.photosharing.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photosharing.R;
import com.example.photosharing.databinding.ActivityRegisterBinding;
import com.example.photosharing.net.Code;
import com.example.photosharing.net.MyHeaders;
import com.example.photosharing.net.MyRequest;
import com.example.photosharing.entity.MyResponse;
import com.example.photosharing.net.URLS;
import com.example.photosharing.utils.KeyBoardUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String account;
    private String password;
    Map<String, String> registerInfo = new HashMap<>();


    private Handler handler;

    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final ImageView accountView = binding.accountImg;
        final ImageView accountClose = binding.accountClose;
        final ImageView passwordView = binding.passwordImg;
        final ImageView passwordClose = binding.passwordClose;
        final ImageView passwordEye = binding.passwordEye;

        final ImageView surePasswordClose = binding.surePasswordClose;

        // 系统toolbar隐藏
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding.registerAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                accountClose.setVisibility(View.GONE);
                accountView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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

        binding.registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordClose.setVisibility(View.GONE);
                passwordView.setVisibility(View.VISIBLE);
                passwordEye.setVisibility(View.GONE);
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
                } else {
                    passwordClose.setVisibility(View.GONE);
                    passwordView.setVisibility(View.VISIBLE);
                    passwordEye.setVisibility(View.GONE);
                }
            }
        });

        binding.sureRegisterPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                surePasswordClose.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                surePasswordClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    surePasswordClose.setVisibility(View.VISIBLE);
                    password = editable.toString();
                } else {
                    surePasswordClose.setVisibility(View.GONE);
                }
            }
        });

        // 注册逻辑
        binding.register.setOnClickListener(view -> {
            String account = binding.registerAccount.getText().toString().trim();
            String password = binding.registerPassword.getText().toString().trim();
            String confirmPassword = binding.sureRegisterPassword.getText().toString().trim();

            if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "请填写账号和密码", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "两次密码不匹配，请重新输入", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> registerInfo = new HashMap<>();
            registerInfo.put("username", account);
            registerInfo.put("password", password);
            Log.d("WARMING", registerInfo.toString());

            MyRequest MyRequest = new MyRequest();

            MyRequest.doPost(URLS.REGISTER.getUrl(),
                    MyHeaders.getHeaders(),
                    registerInfo,
                    new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                MyResponse MyResponse = new Gson().fromJson(response.body().string(), MyResponse.class);
                                Message msg = new Message();
                                msg.obj = MyResponse;
                                handler.sendMessage(msg);
                            }
                        }
                    });
        });

        // 密码可见
        binding.passwordEye.setOnClickListener(v -> {
            int inputType = binding.registerPassword.getInputType();
            if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                binding.registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                passwordEye.setImageResource(R.drawable.closed_eye);


            } else {
                binding.registerPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordEye.setImageResource(R.drawable.open_eye);
            }
            // 光标判断
            CharSequence charSequence = binding.registerPassword.getText();
            if (charSequence != null) {
                Spannable spannable = (Spannable) charSequence;
                Selection.setSelection(spannable, charSequence.length());
            }
        });

        clearEditText(binding.registerAccount, accountClose);
        clearEditText(binding.registerPassword, passwordClose);
        clearEditText(binding.sureRegisterPassword, surePasswordClose);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                MyResponse responseBody = (MyResponse) msg.obj;

                if (responseBody.getCode() == Code.SUCCESS) {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }


    // 点击删除EditText内容功能
    private void clearEditText(EditText editText, ImageView closeView) {
        closeView.setOnClickListener(v -> editText.setText(""));
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
}
