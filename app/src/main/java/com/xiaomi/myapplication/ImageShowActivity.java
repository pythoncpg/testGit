package com.xiaomi.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.Bean.PhotoUrl;
import com.xiaomi.myapplication.adapter.ImageAdapter;

public class ImageShowActivity extends AppCompatActivity {
    private ChargingStation data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);
//        Bundle extras = getIntent().getExtras();
//        data = (ChargingStation) extras.getSerializable("data");
        initView();
    }

    private void initView() {
        RecyclerView rv =  findViewById(R.id.rv_show_picture);
        // 设置网格管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rv.setLayoutManager(gridLayoutManager);

        PhotoUrl photoUrl = new PhotoUrl();

        photoUrl.addTitle("F");
        photoUrl.addUrl("http://aos-cdn-image.amap.com/sns/ugccomment/a1f50792-9f16-44e3-a616-9751c48f22eb.jpeg");
        photoUrl.addTitle("A");
        photoUrl.addUrl("http://aos-cdn-image.amap.com/sns/ugccomment/136b14d1-a596-4b62-8080-490d47532444.jpg");

        ImageAdapter imageAdapter = new ImageAdapter(this,photoUrl);
        rv.setAdapter(imageAdapter);


    }
}