package com.xiaomi.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.xiaomi.myapplication.Bean.ChargingStation;
import com.xiaomi.myapplication.MainActivity;
import com.xiaomi.myapplication.R;
import com.xiaomi.myapplication.StationDetailActivity;
import com.xiaomi.myapplication.adapter.ListInfoAdapter;
import com.xiaomi.myapplication.util.MyThread;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ListChargeTypeFragment extends ListFragment {
    private String longtitude;
    private String latitude;

    private List<ChargingStation> data;

    public ListChargeTypeFragment() {
    }

    public ListChargeTypeFragment(String longtitude, String latitude) {
        this.longtitude = longtitude;
        this.latitude = latitude;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charge_type_list, container, false);
        if (data ==null){
            FutureTask<List<ChargingStation>> futureTask = new FutureTask<>(new MyThread(longtitude,latitude));
            Thread t1 = new Thread(futureTask,"t1");
            t1.start();
            try {
                data = futureTask.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Adapter adapter = new ListInfoAdapter(data,getContext());
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getData());
        ListView lv = view.findViewById(android.R.id.list);
        lv.setAdapter((ListAdapter) adapter);
        return view;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        Intent intent = new Intent(getActivity(), StationDetailActivity.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("data", data.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
        super.onListItemClick(l, v, position, id);
    }
}
