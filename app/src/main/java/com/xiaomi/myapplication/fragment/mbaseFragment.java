package com.xiaomi.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.R;
import com.xiaomi.myapplication.StationDetailActivity;
import com.xiaomi.myapplication.adapter.ListInfoAdapter;
import com.xiaomi.myapplication.util.MyThread;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


public class mbaseFragment extends ListFragment implements AbsListView.OnScrollListener {
    private List<ChargingStation> data;
    private View footerView;
    private View view;
    private ProgressBar pb;
    private TextView tvLoad;
    private ListView lv;
    private int lastVisibleIndex;
    private int totalCout;
    private View footerNoMoreView;
    private Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        initData();
        initView(inflater,container);
        setListeners();
        return view;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_distance_list, container, false);
        footerView = inflater.inflate(R.layout.view_bottom_load,null);
        footerNoMoreView = inflater.inflate(R.layout.view_bottom_no_more,null);
        adapter = new ListInfoAdapter(data,getContext());
        lv = view.findViewById(android.R.id.list);
        lv.setAdapter((ListAdapter) adapter);
        lv.addFooterView(footerView);

        pb = footerView.findViewById(R.id.pb_load);
        tvLoad = footerView.findViewById(R.id.tv_load_text);
    }


    private void initData(int viewPage) {
        if (data ==null){
            FutureTask<List<ChargingStation>> futureTask = new FutureTask<>(new MyThread());
            Thread t1 = new Thread(futureTask,"t1");
            t1.start();
            try {
                data = futureTask.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        Intent intent = new Intent(getActivity(), StationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
//        super.onListItemClick(l, v, position, id);
    }

    public void setListeners(){
        // 当数据超过10条时，添加滑动监听
        if (data.size()==10) lv.setOnScrollListener(this);
        // 否则移除底部view
        else lv.removeFooterView(footerView);
    }

    /**
     * 滑动状态改变监听
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.e("TAG", "lastVisibleIndex = " + lastVisibleIndex);
        Log.e("TAG", "adapter.getCount() = " + adapter.getCount());
        // 滑到底部后自动加载，判断listView已经停止滚动并且最后可视的条目等于adapter的条目
        // 注意这里在listView设置好adpter后，加了一个底部加载布局。
        // 所以判断条件为：lastVisibleIndex == adapter.getCount()
        if (scrollState == SCROLL_STATE_IDLE
                && lastVisibleIndex == adapter.getCount()) {
            /**
             * 这里也要设置为可见，是因为当你真正从网络获取数据且获取失败的时候。
             * 我在失败的方法里面，隐藏了底部的加载布局并提示用户加载失败。所以再次监听的时候需要
             * 继续显示隐藏的控件。因为我模拟的获取数据，失败的情况这里不给出。实际中简单的加上几句代码就行了。
             */
            pb.setVisibility(View.VISIBLE);
            tvLoad.setVisibility(View.VISIBLE);
            loadMoreData();// 加载更多数据
        }
    }

    private void loadMoreData() {
        int start;int end;
        // 获取当前adapter中的总条目数
        int count = adapter.getCount();
        // 判断新加载数据与数据总数
        if (count+10<totalCout){
            start=count;
            end = start+10;
//            initData(start,end);
        }
    }


    /**
     * 滑动监听
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引
        lastVisibleIndex = firstVisibleItem+visibleItemCount-1;
        // 当adapter中的所有条目数相等时，移除底部view
        if(totalItemCount == totalCout+1){
            lv.removeFooterView(footerView);
            lv.addFooterView(footerNoMoreView);
            // 滑动到底部后自动加载，
        }
    }
}

