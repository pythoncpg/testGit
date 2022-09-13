package com.xiaomi.myapplication.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.myapplication.Bean.ChargingStation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyThread implements Callable<List<ChargingStation>> {
    private String longitude;
    private String latitude;

    public MyThread() {
    }

    public MyThread(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }


    @Override
    public List<ChargingStation> call() throws Exception {
        // 异步请求数据
        Log.d("TAG"," longtitude:++++"+latitude+"latitude:"+latitude);
        List<ChargingStation> list = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient();
//            RequestBody requestBody = new FormBody.Builder()
//                    .add("longitude", "116.473168")
//                    .add("latitude", "39.993015")
//                    .build();

            Request request;
            // 默认get请求
            request = new Request.Builder()
                    .url("http://mini18.g.mi.com/mi-auto-api/info?longitude="+longitude+"&latitude="+latitude)
                    .build();
            Response response = client.newCall(request).execute();
            Log.d("TAG", "返回数据 :"+response);
            JSONObject jsonObject = new JSONObject(response.body().string());
            String lists = jsonObject.getString("pois");
            Log.d("TAG", "body数据 :"+lists);
            // 通过gson解析json
            Gson gson = new Gson();
            list = gson.fromJson(lists,new TypeToken<List<ChargingStation>>(){}.getType());
            Log.d("TAG", "尺寸"+list.size()+"\n"+"数据:"+list.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}


