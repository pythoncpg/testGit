package com.xiaomi.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.R;

import java.io.Serializable;
import java.util.List;

// 充电桩简要适配器
public class AbstractInfoAdapter extends BaseAdapter {
    private List<ChargingStation> data;
    private Context context;
    private int resource;

    public AbstractInfoAdapter(List<ChargingStation> data, Context context) {
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
        MViewHolder mViewHolder ;
        if (convertView == null) {
            mViewHolder = new MViewHolder();
            convertView =LayoutInflater.from(context).inflate(R.layout.abstract_info_item_view, parent,false);
            mViewHolder.imageView = convertView.findViewById(R.id.iv_list_bk);
            mViewHolder.name = convertView.findViewById(R.id.tv_list_name);
            mViewHolder.distance = convertView.findViewById(R.id.tv_list_distance);
            mViewHolder.address = convertView.findViewById(R.id.tv_list_address);
            mViewHolder.price = convertView.findViewById(R.id.tv_price);
            convertView.setTag(mViewHolder);
        }else {
            mViewHolder = (MViewHolder) convertView.getTag();
        }
//        mViewHolder.imageView.setImageResource();
        mViewHolder.name.setText(data.get(position).getName());
        mViewHolder.distance.setText(data.get(position).getDistance()+"米");
        mViewHolder.address.setText(data.get(position).getAddress());
        mViewHolder.price.setText(data.get(position).getPrice()+"元");

        return convertView;
    }

    private class MViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView distance;
        private TextView address;
        private TextView price;
    }
}
