package com.xiaomi.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        点击跳转登录activity
        findViewById(R.id.text_to_login).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
//        点击跳转密码重置activity
        findViewById(R.id.text_to_resetPassword).setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }
}