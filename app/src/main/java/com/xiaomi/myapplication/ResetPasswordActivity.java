package com.xiaomi.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

//        点击跳转注册activity
        findViewById(R.id.text_to_register).setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
//        点击跳转登录activity
        findViewById(R.id.text_to_login).setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}