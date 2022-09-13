package com.xiaomi.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.util.CurrentType;
import com.xiaomi.myapplication.util.MapUtil;
import com.xiaomi.myapplication.util.MyThread;
import com.xiaomi.myapplication.util.MyToast;


import java.io.IOException;
import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class MainActivity extends AppCompatActivity implements AMapLocationListener {
    private String[] themeName = {"style.data", "style_dark_blue.data", "style_green.data", "style_cartoon.data",
            "style_macaron.data", "style_phantom_black.data", "style_red.data", "style_standard.data"};
    private String[] themeExtraData = {"style_extra.data", "style_dark_blue_extra.data", "style_green_extra.data", "style_cartoon_extra.data",
            "style_macaron_extra.data", "style_phantom_black_extra.data", "style_red_extra.data", "style_standard_extra.data"};
    private String longitude = "0";
    private String latitude = "0";

    private AMap aMap;
    private MapView mapView;

    private boolean textShowFlag = true;
    private boolean transShowFlag = true;
    // 底部搜索框 仅使用一次
    private boolean bottomViewFlag = true;
    private RadioButton rbDistance;
    private RadioButton rbChargeType;
    private RadioButton rbPrice;
    private FloatingActionButton fabChangeTheme;
    private FloatingActionButton fabShowTransport;
    private FloatingActionButton fabOpenList;
    private RelativeLayout rlBottomView;
    private CurrentType currentType = CurrentType.DISTANCE;
    private ChargingStation bottomData;
    private TextView tvNavigation;
    private FloatingActionButton fabMoveCurrent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 隐私合规检查
        mCheckPermission();


        // 显示地图
        mapView = findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        aMap.showMapText(false);
        // 切换主题背景
        setMapBackground(0);

        //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        // 开启定位
        // 定位时间
        int intervalTime = 300 * 1000;
        startLocation(intervalTime);
        // 显示定位蓝点
        showView();
        initView();
        setViewEventListener();

        findViewById(R.id.rg_filter).setVisibility(View.VISIBLE);
        aMap.setOnMarkerClickListener(marker -> {
            bottomData= (ChargingStation) marker.getObject();
            aMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            // 初始化dialog数据
            initDialogData((ChargingStation) marker.getObject());
            // 在点击了marker后 显示list浮动按钮以及底部view
            if (bottomViewFlag) {
                rlBottomView.setVisibility(View.VISIBLE);
                fabOpenList.setVisibility(View.VISIBLE);
                bottomViewFlag = !bottomViewFlag;
            }
            // 底部弹窗
//            bottomDialog((ChargingStation) marker.getObject());
            return true;
        });

    }


    // 切换筛选时会调用一次
    private void showCharge(CurrentType type) {
        currentType = type;
        // 清除地图上所有添加的覆盖物
        aMap.clear();
        FutureTask<List<ChargingStation>> futureTask = new FutureTask<>(new MyThread(longitude,latitude));
        Thread t1 = new Thread(futureTask, "t1");
        t1.start();
        try {
            List<ChargingStation> data = futureTask.get();
            addStationMarker(data);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setViewEventListener() {
        // 悬浮按钮事件
        // 打开主题切换dialog
        fabChangeTheme.setOnClickListener(v -> themeDialog());

        // 切换交通显示
        fabShowTransport.setOnClickListener(v -> {
            String toastShow = transShowFlag ? "开启并更新实时路况" : "关闭实时路况";
            MyToast.showText(MainActivity.this, toastShow, R.drawable.test_icon_1);
            aMap.setTrafficEnabled(transShowFlag);
            transShowFlag = !transShowFlag;
        });

        // 跳转list页面
        fabOpenList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TempShowActivity.class);
            // 获取当前选中的type
            Bundle bundle = new Bundle();
            bundle.putSerializable("filterType", currentType);
            bundle.putString("latitude", latitude);
            bundle.putString("longitude", longitude);
            intent.putExtras(bundle);
            // 记录当前底部显示的电站详情数据,在list显示时移动到所在位置
            startActivity(intent);
        });

//        fabMoveCurrent.setOnClickListener(v -> {
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(Double.parseDouble(longitude),Double.parseDouble(latitude))));
//        });
        tvNavigation.setOnClickListener(v -> {
            MapUtil mapUtil = new MapUtil(this, bottomData.getLocation(),bottomData.getName());
            mapUtil.newMethod(this);
//            mapUtil.isAvilible(this,MapUtil.GAODE_PACKAGE_NAME);
        });


        rbDistance.setOnClickListener(v -> showCharge(CurrentType.DISTANCE));
        rbChargeType.setOnClickListener(v -> showCharge(CurrentType.CHARGETYPE));
        rbPrice.setOnClickListener(v -> showCharge(CurrentType.PRICE));
        // 跳转详情页
        rlBottomView.setOnClickListener(v -> {
            Intent intent =new Intent(MainActivity.this,StationDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data",bottomData);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }


    private void initView() {
        rbDistance = findViewById(R.id.rb_distance);
        rbChargeType = findViewById(R.id.rb_charge_type);
        rbPrice = findViewById(R.id.rb_price);
        fabChangeTheme = findViewById(R.id.fab_change_theme);
        fabShowTransport = findViewById(R.id.fab_show_transportation);
        tvNavigation = findViewById(R.id.bt_navigation);

//        fabMoveCurrent = findViewById(R.id.fab_move_current);
        fabOpenList = findViewById(R.id.fab_open_list);
        rbDistance.setChecked(true);
        rlBottomView = findViewById(R.id.rl_bottom_view);
    }

    private void themeDialog() {
        Dialog changeThemeDialog = new Dialog(this, R.style.bottom_dialog_style);
        // 设置点击外部消失 默认
        // 获取view
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_dialog_change_theme, null);

        Window window = changeThemeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes();
        //如果没有这行代码，弹框的内容会自适应，而不会充满父控件
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.y = 20;//设置Dialog距离底部的距离
        // 属性设置窗体
        window.setAttributes(lp);
        changeThemeDialog.show();
        // 设置view内的组件点击事件
        initDialogEventListener(view);

    }

    private void initDialogEventListener(View view) {
        // 切换主题
        view.findViewById(R.id.bt_style_1).setOnClickListener(v -> setMapBackground(0));
        view.findViewById(R.id.bt_style_2).setOnClickListener(v -> setMapBackground(1));
        view.findViewById(R.id.bt_style_3).setOnClickListener(v -> setMapBackground(2));
        view.findViewById(R.id.bt_style_4).setOnClickListener(v -> setMapBackground(3));
        view.findViewById(R.id.bt_style_5).setOnClickListener(v -> setMapBackground(4));
        view.findViewById(R.id.bt_style_6).setOnClickListener(v -> setMapBackground(5));
        view.findViewById(R.id.bt_style_7).setOnClickListener(v -> setMapBackground(6));
        view.findViewById(R.id.bt_style_8).setOnClickListener(v -> setMapBackground(7));
        view.findViewById(R.id.ib_show_text).setOnClickListener(v -> {
            // 切换文字显示
            aMap.showMapText(textShowFlag);
            textShowFlag = !textShowFlag;
        });
    }


    // 初始化底部view-设置数据
    private void initDialogData(ChargingStation data) {
        Log.d("TAG", "底部view显示数据" + data.toString());
        rlBottomView.setVisibility(View.VISIBLE);
        ImageView iv = rlBottomView.findViewById(R.id.iv_bk);
        Log.d("TAG", "初始卡片数据" + data.toString());
        // 设置图片资源
        ImageView ivPhotoUrl = rlBottomView.findViewById(R.id.iv_bk);
        String ivSrc = "http://aos-cdn-image.amap.com/sns/ugccomment/136b14d1-a596-4b62-8080-490d47532444.jpg";
        Log.d("TAG", "photo url:" + data.getPhotoUrl());
        Glide.with(this)
                .load(ivSrc)
                .placeholder(R.drawable.a)
//                .override(300,300)
                .into(ivPhotoUrl);

        TextView tvName = rlBottomView.findViewById(R.id.tv_name);
        tvName.setText(data.getName());

        TextView tvDistance = rlBottomView.findViewById(R.id.tv_distance);
        Log.d("TAG", "距离" + data.getDistance());
        tvDistance.setText(data.getDistance() + "米");

        TextView tvAddress = rlBottomView.findViewById(R.id.tv_address);
        tvAddress.setText(data.getAddress());
        TextView tvPrice = rlBottomView.findViewById(R.id.tv_price);
        tvPrice.setText(data.getPrice() + "元");

    }

    private void mCheckPermission() {
        // 隐私合规检查
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, 266);
                }
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == 266) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 266);
                    return;
                }
            }
        }
    }


    private void addStationMarker(List<ChargingStation> list) {
        MarkerOptions markerOption = new MarkerOptions();
        ChargingStation station;
        String[] arr;
        for (int i = 0; i < list.size(); i++) {
            station = list.get(i);
            arr = station.getLocation().split(",");
            Log.d("TAG", "addStationMarker: "+arr[0]+arr[1]);
            Log.d("TAG", longitude+"=="+latitude);
            // 设置位置
            markerOption.position(new LatLng(Double.parseDouble(arr[1]),
                    Double.parseDouble(arr[0])));
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.charge_station_1dp)));
            Marker marker = aMap.addMarker(markerOption);
            marker.setObject(station);
        }
    }


    private void showView() {
        // 如果要设置定位的默认状态，可以在此处进行设置
        MyLocationStyle locationStyle = new MyLocationStyle();
        // 设置蓝点图标
//        locationStyle.myLocationIcon(BitmapDescriptor myLocationIcon);
        //设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的填充颜色。
        locationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        locationStyle.strokeWidth(0);
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.getUiSettings().setLogoBottomMargin(-100);//下移高德LOG到屏幕外

        // 去掉高德地图右下角隐藏的缩放按钮
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationStyle(locationStyle);
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));//地图默认缩放比例

    }

    private void setMapBackground(int index) {
        byte[] buffer1 = null;
        byte[] buffer2 = null;
        InputStream is1 = null;
        InputStream is2 = null;
        try {
            is1 = getAssets().open(themeName[index]);
            int lenght1 = is1.available();
            buffer1 = new byte[lenght1];
            is1.read(buffer1);
            is2 = getAssets().open(themeExtraData[index]);
            int lenght2 = is2.available();
            buffer2 = new byte[lenght2];
            is2.read(buffer2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is1 != null)
                    is1.close();
                if (is2 != null)
                    is2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CustomMapStyleOptions customMapStyleOptions = new CustomMapStyleOptions();
        customMapStyleOptions.setStyleData(buffer1);
        customMapStyleOptions.setStyleExtraData(buffer2);
        aMap.setCustomMapStyle(customMapStyleOptions);
    }

    private void startLocation(int intervalTime) {
        // getMyLocation为空
//        if (aMap.getMyLocation()==null) Log.d("TAG", "null");
//        myLatlng =new LatLng(aMap.getMyLocation().getLongitude(),aMap.getMyLocation().getLatitude());
        try {
            // 定位发起端
            AMapLocationClient locationClient = new AMapLocationClient(this);
            // 地位参数
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            // 设置返回地址信息
            locationOption.setNeedAddress(true);
            // 设置定位监听
            locationClient.setLocationListener(this);
            // 设置精度
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 设置时间间隔
//            locationOption.setInterval(intervalTime);

            // 单次定位
            locationOption.setOnceLocation(true);
            locationOption.setOnceLocationLatest(true);
            // 设置定位参数
            locationClient.setLocationOption(locationOption);
            // 开启定位
            locationClient.startLocation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    // 暂停
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // 重载时调用
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // 销毁地图
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                Log.d("TAG", "获取时间：" + df.format(date) + "当前经度：" + amapLocation.getLongitude() + "当前纬度：" +
                        amapLocation.getLatitude());

                longitude = "" + amapLocation.getLongitude();
                latitude = "" + amapLocation.getLatitude();
                Log.d("TAG", " longtitude:====" + longitude + "latitude:" + latitude);
                FutureTask<List<ChargingStation>> futureTask = new FutureTask<>(new MyThread(longitude, latitude));
                Thread t1 = new Thread(futureTask, "t1");
                t1.start();
                try {
                    addStationMarker(futureTask.get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


}

