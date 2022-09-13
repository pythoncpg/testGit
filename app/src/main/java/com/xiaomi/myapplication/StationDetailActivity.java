package com.xiaomi.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.util.MapUtil;

import java.io.Serializable;

public class StationDetailActivity extends AppCompatActivity {

    private ChargingStation data;
    private ImageView imageView;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvDistance;
    private TextView tvDriverTime;
    private TextView tvPrice;
    private TextView tvBusinessData;
    private TextView tvChargeStatu;
    private TextView tvUserCount;
    private TextView tvTotalCount;
    private TextView tvNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_detail);

        Bundle extras = getIntent().getExtras();
        data = (ChargingStation) extras.getSerializable("data");
        initView();
        initData(data);
        setViewEventListener();
    }

    private void setViewEventListener() {
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(StationDetailActivity.this,ImageShowActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",data);
            intent.putExtras(bundle);
            startActivity(intent);
        });

        tvNavigation.setOnClickListener(v -> {
            MapUtil mapUtil = new MapUtil(this, data.getLocation(),data.getName());
            mapUtil.newMethod(this);
        });

    }

    private void initData(ChargingStation data) {

        Log.d("TAG", "initData: "+data);
        Log.d("TAG", "chargetype: "+data.getChargeType()+data.getUseCount()+"=="+data.getTotalCount());
//        imageView.setImageResource(data.getPhotoUrl());
        String ivSrc = "http://aos-cdn-image.amap.com/sns/ugccomment/136b14d1-a596-4b62-8080-490d47532444.jpg";
        Log.d("TAG", "photo url:" + data.getPhotoUrl());
        Glide.with(this)
                .load(ivSrc)
                .placeholder(R.drawable.a)
//                .override(300,300)
                .into(imageView);
        tvName.setText(data.getName());
        if (data.getBusinessData().equals(""))
        tvBusinessData.setText("营业时间："+(data.getBusinessData().equals("")?"24小时":data.getBusinessData())+"营业");
        tvAddress.setText(data.getAddress());
        tvDistance.setText("距离当前位置"+data.getDistance()+"米");
        tvDriverTime.setText("驾车时间需"+data.getDrivingTime()+"内");
        if(data.getChargeType().equals("快充")){
            tvChargeStatu.setText(data.getChargeStatus());
            tvUserCount.setText(data.getUseCount()+"");
            tvTotalCount.setText(data.getTotalCount()+"");
        }
        tvPrice.setText(data.getPrice()+"元");

    }

    private void initView() {
        imageView = findViewById(R.id.iv_bk_detail);
        tvName = findViewById(R.id.tv_name_detail);

        tvBusinessData = findViewById(R.id.tv_business_hours_detail);
        tvAddress = findViewById(R.id.tv_address_detail);
        tvDistance = findViewById(R.id.tv_distance_detail);
        tvDriverTime = findViewById(R.id.tv_driver_time_detail);

        RelativeLayout rlChargeType = findViewById(R.id.rl_fast_free);
        tvChargeStatu = findViewById(R.id.tv_charge_status_detail);
        tvUserCount = findViewById(R.id.tv_use_count_detail);
        tvTotalCount = findViewById(R.id.tv_total_count_detail);
        tvPrice = findViewById(R.id.tv_price_detail);

        tvNavigation = findViewById(R.id.bt_navigation);

    }
}