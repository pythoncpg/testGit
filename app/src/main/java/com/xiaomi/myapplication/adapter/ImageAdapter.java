package com.xiaomi.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiaomi.myapplication.Bean.PhotoUrl;
import com.xiaomi.myapplication.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ItemCardViewHolder> {
    private Context context;
    private PhotoUrl photoUrl;

    public ImageAdapter(Context context, PhotoUrl photoUrl) {
        this.context = context;
        this.photoUrl = photoUrl;
    }

    @NonNull
    @Override
    public ItemCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取item view
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_picture,null);
        // 获取viewholder
        ItemCardViewHolder viewHolder = new ItemCardViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemCardViewHolder holder, int position) {
        String ivSrc = "http://aos-cdn-image.amap.com/sns/ugccomment/136b14d1-a596-4b62-8080-490d47532444.jpg";
        Log.d("TAG", "photo url:" + photoUrl.getUrl().get(position));
        Glide.with(context)
                .load(ivSrc)
                .placeholder(R.drawable.a)
//                .override(300,300)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return photoUrl.getUrl().size();
    }

    public class ItemCardViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ItemCardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_r1);
        }
    }

}
