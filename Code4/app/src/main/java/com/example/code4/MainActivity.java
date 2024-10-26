package com.example.code4;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE_STRING = "com.glriverside.xgqin.code04.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        final EditText etMessage = findViewById(R.id.message);
        Button btSend = findViewById(R.id.send_message);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
//                Toast.makeText(MainActivity.this,
//                        message,Toast.LENGTH_SHORT).show();

                //启动MessageActivity类
                Intent intent = new Intent(MainActivity.this,MessageActivity.class);
                //MainActivity.this：指的是当前的MainActivity的上下文。this关键字在这里表示当前实例。
                // MessageActivity.class：指的是要启动的目标Activity的类。.class表示这是一个类对象。

                intent.putExtra(MESSAGE_STRING,message);
                startActivity(intent);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}