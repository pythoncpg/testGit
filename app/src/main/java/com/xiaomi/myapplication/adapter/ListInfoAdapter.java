package com.xiaomi.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.R;

import java.util.List;

public class ListInfoAdapter extends BaseAdapter {
    private List<ChargingStation> data;
    private Context context;

    public ListInfoAdapter(List<ChargingStation> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MListViewHold mListViewHold;
        if(convertView==null){
            mListViewHold = new MListViewHold();
            convertView= LayoutInflater.from(context).inflate(R.layout.abstract_info_item_view,null,false);
            mListViewHold.imageView = convertView.findViewById(R.id.iv_bk);
            mListViewHold.name=convertView.findViewById(R.id.tv_name);
            mListViewHold.distance=convertView.findViewById(R.id.tv_distance);
            mListViewHold.address=convertView.findViewById(R.id.tv_address);
            mListViewHold.price=convertView.findViewById(R.id.tv_price);

            mListViewHold.businessHours=convertView.findViewById(R.id.tv_business_hours);

            // 剩余量
            mListViewHold.rl_show_fast_remain = convertView.findViewById(R.id.rl_show_fast_remain);
            mListViewHold.tv_charge_status=convertView.findViewById(R.id.tv_charge_status);
            mListViewHold.tv_use_count=convertView.findViewById(R.id.tv_use_count);
            mListViewHold.tv_total_count=convertView.findViewById(R.id.tv_total_count);

            mListViewHold.rl_show_slow_remain = convertView.findViewById(R.id.rl_show_slow_remain);
            mListViewHold.tv_charge_status_slow=convertView.findViewById(R.id.tv_charge_status_slow);
            mListViewHold.tv_use_count_slow=convertView.findViewById(R.id.tv_use_count_slow);
            mListViewHold.tv_total_count_slow=convertView.findViewById(R.id.tv_total_count_slow);

            mListViewHold.parkingInfo=convertView.findViewById(R.id.tv_parkingInfo);
            convertView.setTag(mListViewHold);

        }else{
            mListViewHold = (MListViewHold) convertView.getTag();
        }
        Log.d("TAG", "getView: "+data.get(position));
        String ivSrc = "http://aos-cdn-image.amap.com/sns/ugccomment/136b14d1-a596-4b62-8080-490d47532444.jpg";
        Log.d("TAG", "photo url:" + data.get(position).getPhotoUrl());
        Glide.with(context)
                .load(ivSrc)
                .placeholder(R.drawable.a)
//                .override(300,300)
                .into(mListViewHold.imageView);

        mListViewHold.name.setText(data.get(position).getName());
        mListViewHold.distance.setText(data.get(position).getDistance()+"米");
        mListViewHold.address.setText(data.get(position).getAddress());
        mListViewHold.price.setText(data.get(position).getPrice()+"元");
        mListViewHold.businessHours.setText(data.get(position).getBusinessData().equals("")?"24小时":data.get(position).getBusinessData());
        Log.d("TAG", "chargeType: +"+data.get(position).getChargeType());
        Log.d("TAG", "===============");

        if (data.get(position).getChargeType().equals("快充")){
            mListViewHold.rl_show_slow_remain.setVisibility(View.GONE);
            mListViewHold.rl_show_fast_remain.setVisibility(View.VISIBLE);
            mListViewHold.tv_charge_status.setText(data.get(position).getChargeStatus());
            mListViewHold.tv_use_count.setText(data.get(position).getUseCount()+"");
            mListViewHold.tv_total_count.setText(data.get(position).getTotalCount()+"");
        }else {
            mListViewHold.rl_show_fast_remain.setVisibility(View.GONE);
            mListViewHold.rl_show_slow_remain.setVisibility(View.VISIBLE);
            mListViewHold.tv_charge_status_slow.setText(data.get(position).getChargeStatus());

            mListViewHold.tv_use_count_slow.setText(data.get(position).getUseCount()+"");
            mListViewHold.tv_total_count_slow.setText(data.get(position).getTotalCount()+"");
        }

        mListViewHold.parkingInfo.setText(data.get(position).getParkingInfo());
        return convertView;
    }
    class MListViewHold{
        private ImageView imageView;
        private TextView name;
        private TextView distance;
        private TextView address;
        private TextView price;
        private TextView businessHours;
        private RelativeLayout rl_show_fast_remain;
        private TextView tv_charge_status;
        private TextView tv_use_count;
        private TextView tv_total_count;

        private RelativeLayout rl_show_slow_remain;
        private TextView tv_charge_status_slow;
        private TextView tv_use_count_slow;
        private TextView tv_total_count_slow;
        private TextView parkingInfo;


    }
}
