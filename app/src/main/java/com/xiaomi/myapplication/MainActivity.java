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
    // ??????????????? ???????????????
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
        // ??????????????????
        mCheckPermission();


        // ????????????
        mapView = findViewById(R.id.mv_map);
        mapView.onCreate(savedInstanceState);// ?????????????????????
        aMap = mapView.getMap();
        aMap.showMapText(false);
        // ??????????????????
        setMapBackground(0);

        //???????????????????????????MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        // ????????????
        // ????????????
        int intervalTime = 300 * 1000;
        startLocation(intervalTime);
        // ??????????????????
        showView();
        initView();
        setViewEventListener();

        findViewById(R.id.rg_filter).setVisibility(View.VISIBLE);
        aMap.setOnMarkerClickListener(marker -> {
            bottomData= (ChargingStation) marker.getObject();
            aMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            // ?????????dialog??????
            initDialogData((ChargingStation) marker.getObject());
            // ????????????marker??? ??????list????????????????????????view
            if (bottomViewFlag) {
                rlBottomView.setVisibility(View.VISIBLE);
                fabOpenList.setVisibility(View.VISIBLE);
                bottomViewFlag = !bottomViewFlag;
            }
            // ????????????
//            bottomDialog((ChargingStation) marker.getObject());
            return true;
        });

    }


    // ??????????????????????????????
    private void showCharge(CurrentType type) {
        currentType = type;
        // ???????????????????????????????????????
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
        // ??????????????????
        // ??????????????????dialog
        fabChangeTheme.setOnClickListener(v -> themeDialog());

        // ??????????????????
        fabShowTransport.setOnClickListener(v -> {
            String toastShow = transShowFlag ? "???????????????????????????" : "??????????????????";
            MyToast.showText(MainActivity.this, toastShow, R.drawable.test_icon_1);
            aMap.setTrafficEnabled(transShowFlag);
            transShowFlag = !transShowFlag;
        });

        // ??????list??????
        fabOpenList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TempShowActivity.class);
            // ?????????????????????type
            Bundle bundle = new Bundle();
            bundle.putSerializable("filterType", currentType);
            bundle.putString("latitude", latitude);
            bundle.putString("longitude", longitude);
            intent.putExtras(bundle);
            // ?????????????????????????????????????????????,???list??????????????????????????????
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
        // ???????????????
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
        // ???????????????????????? ??????
        // ??????view
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_dialog_change_theme, null);

        Window window = changeThemeDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setContentView(view);

        WindowManager.LayoutParams lp = window.getAttributes();
        //?????????????????????????????????????????????????????????????????????????????????
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.y = 20;//??????Dialog?????????????????????
        // ??????????????????
        window.setAttributes(lp);
        changeThemeDialog.show();
        // ??????view????????????????????????
        initDialogEventListener(view);

    }

    private void initDialogEventListener(View view) {
        // ????????????
        view.findViewById(R.id.bt_style_1).setOnClickListener(v -> setMapBackground(0));
        view.findViewById(R.id.bt_style_2).setOnClickListener(v -> setMapBackground(1));
        view.findViewById(R.id.bt_style_3).setOnClickListener(v -> setMapBackground(2));
        view.findViewById(R.id.bt_style_4).setOnClickListener(v -> setMapBackground(3));
        view.findViewById(R.id.bt_style_5).setOnClickListener(v -> setMapBackground(4));
        view.findViewById(R.id.bt_style_6).setOnClickListener(v -> setMapBackground(5));
        view.findViewById(R.id.bt_style_7).setOnClickListener(v -> setMapBackground(6));
        view.findViewById(R.id.bt_style_8).setOnClickListener(v -> setMapBackground(7));
        view.findViewById(R.id.ib_show_text).setOnClickListener(v -> {
            // ??????????????????
            aMap.showMapText(textShowFlag);
            textShowFlag = !textShowFlag;
        });
    }


    // ???????????????view-????????????
    private void initDialogData(ChargingStation data) {
        Log.d("TAG", "??????view????????????" + data.toString());
        rlBottomView.setVisibility(View.VISIBLE);
        ImageView iv = rlBottomView.findViewById(R.id.iv_bk);
        Log.d("TAG", "??????????????????" + data.toString());
        // ??????????????????
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
        Log.d("TAG", "??????" + data.getDistance());
        tvDistance.setText(data.getDistance() + "???");

        TextView tvAddress = rlBottomView.findViewById(R.id.tv_address);
        tvAddress.setText(data.getAddress());
        TextView tvPrice = rlBottomView.findViewById(R.id.tv_price);
        tvPrice.setText(data.getPrice() + "???");

    }

    private void mCheckPermission() {
        // ??????????????????
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
            // ????????????
            markerOption.position(new LatLng(Double.parseDouble(arr[1]),
                    Double.parseDouble(arr[0])));
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.drawable.charge_station_1dp)));
            Marker marker = aMap.addMarker(markerOption);
            marker.setObject(station);
        }
    }


    private void showView() {
        // ??????????????????????????????????????????????????????????????????
        MyLocationStyle locationStyle = new MyLocationStyle();
        // ??????????????????
//        locationStyle.myLocationIcon(BitmapDescriptor myLocationIcon);
        //????????????????????????????????????????????????????????????????????????????????????????????????
        locationStyle.strokeColor(Color.argb(0, 0, 0, 0));// ???????????????????????????
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// ???????????????????????????
        locationStyle.strokeWidth(0);
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.getUiSettings().setLogoBottomMargin(-100);//????????????LOG????????????

        // ????????????????????????????????????????????????
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationStyle(locationStyle);
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// ????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true??????????????????????????????????????????false??????????????????????????????????????????????????????false

        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));//????????????????????????

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
        // getMyLocation??????
//        if (aMap.getMyLocation()==null) Log.d("TAG", "null");
//        myLatlng =new LatLng(aMap.getMyLocation().getLongitude(),aMap.getMyLocation().getLatitude());
        try {
            // ???????????????
            AMapLocationClient locationClient = new AMapLocationClient(this);
            // ????????????
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            // ????????????????????????
            locationOption.setNeedAddress(true);
            // ??????????????????
            locationClient.setLocationListener(this);
            // ????????????
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // ??????????????????
//            locationOption.setInterval(intervalTime);

            // ????????????
            locationOption.setOnceLocation(true);
            locationOption.setOnceLocationLatest(true);
            // ??????????????????
            locationClient.setLocationOption(locationOption);
            // ????????????
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

    // ??????
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // ???????????????
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // ????????????
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //?????????????????????????????????????????????
                amapLocation.getLocationType();//??????????????????????????????????????????????????????????????????????????????
                amapLocation.getLatitude();//????????????
                amapLocation.getLongitude();//????????????
                amapLocation.getAccuracy();//??????????????????
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//????????????
                amapLocation.getAddress();//???????????????option?????????isNeedAddress???false??????????????????????????????????????????????????????????????????GPS??????????????????????????????
                amapLocation.getCountry();//????????????
                amapLocation.getProvince();//?????????
                amapLocation.getCity();//????????????
                amapLocation.getDistrict();//????????????
                amapLocation.getStreet();//????????????
                amapLocation.getStreetNum();//?????????????????????
                amapLocation.getCityCode();//????????????
                amapLocation.getAdCode();//????????????
                amapLocation.getAoiName();//????????????????????????AOI??????
                Log.d("TAG", "???????????????" + df.format(date) + "???????????????" + amapLocation.getLongitude() + "???????????????" +
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
                //??????????????????ErrCode???????????????errInfo???????????????????????????????????????
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


}

