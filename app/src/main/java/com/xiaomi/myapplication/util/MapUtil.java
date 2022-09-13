package com.xiaomi.myapplication.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MapUtil {
    public static String GAODE_PACKAGE_NAME="com.autonavi.minimap";
    public MapUtil(Context context, String location,String name) {
        String[] arr = location.split(",");
        this.context = context;
        this.targetLongtitude = arr[0];
        this.targetLatitude = arr[1];
        this.name = name;
    }

    private Context context;
    private String targetLongtitude;
    private String targetLatitude;
    private String name;
    // 检测是否安装对应程序
    public  void isAvilible(Context context,String packageName){
        // 获取packageManager
        PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序信息
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        // 存储包名
        List<String> packageNames=new ArrayList<>();
        //
        if (installedPackages!=null){
            for (int i = 0; i < installedPackages.size(); i++) {
                packageNames.add(installedPackages.get(i).packageName);
            }
        }

        for (int i = 0; i < packageNames.size(); i++) {
            Log.d("TAG", "名称" + packageNames.get(i));

        }
        if( packageNames.contains(packageName)){
            openGaodeMap();
        }else{
            MyToast.showText(context,"您并未安装高德地图，是否前去下载");
            Uri uri = Uri.parse("market://search?q=高德地图");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            goToMarket.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            context.startActivity(goToMarket);

        }
    }


    public void openGaodeMap(){
        Intent intent= new Intent("android.intent.action.VIEW", android.net.Uri.parse(
                "androidamap://route?sourceApplication=appName&slat=&slon=&sname=我的位置&dlat="+ targetLatitude +"&dlon="+ targetLongtitude+"&dname=目的地&dev=0&t=0"
              ));
        context.startActivity(intent);
    }

    public void newMethod(Context context){
        try {
            String uri = String.format("amapuri://route/plan/?dlat=%s&dlon=%s&dname=%s&dev=0&t=0",
                    targetLatitude, targetLongtitude,name);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse(uri));
            intent.setPackage("com.autonavi.minimap");
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            MyToast.showText(context,"请安装地图");
            Uri uri = Uri.parse("market://search?q=高德地图");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            goToMarket.setClassName("com.tencent.android.qqdownloader", "com.tencent.pangu.link.LinkProxyActivity");
            context.startActivity(goToMarket);
        }
    }
}
