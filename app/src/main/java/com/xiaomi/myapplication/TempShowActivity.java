package com.xiaomi.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import android.widget.RadioButton;

import com.xiaomi.myapplication.fragment.ListChargeTypeFragment;
import com.xiaomi.myapplication.fragment.ListDistanceFragment;
import com.xiaomi.myapplication.fragment.ListPriceFragment;
import com.xiaomi.myapplication.util.CurrentType;


public class TempShowActivity extends AppCompatActivity {
    private String longitude;
    private String latitude;

    private Fragment[] fragments = {null, null, null};
    private RadioButton rbDistance;
    private RadioButton rbChargeType;
    private RadioButton rbPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_show);

        rbDistance = findViewById(R.id.rb_distance);
        rbChargeType = findViewById(R.id.rb_charge_type);
        rbPrice = findViewById(R.id.rb_price);
//        lv = findViewById(R.id.lv_showAbstract);
        // 取初始数据
        Bundle bundle = getIntent().getExtras();
        longitude = bundle.getString("longitude");
        latitude = bundle.getString("latitude");
        CurrentType currentType = (CurrentType) bundle.getSerializable("filterType");

        Log.d("TAG", "当前id "+currentType);
        Log.d("TAG", "onCreate: "+longitude+latitude);
        // 设置默认显示fragment
        initData(currentType);

//        switchFragment(0);
        setViewEventListener();
    }

    // 默认显示选择类型
    private void initData(CurrentType currentType) {
        switch (currentType) {
            case DISTANCE:
                rbDistance.setChecked(true);
                break;
            case CHARGETYPE:
                rbChargeType.setChecked(true);
                break;
            case PRICE:
                rbPrice.setChecked(true);
                break;

        }
        // CurrentType.DISTANCE.ordinal() 返回定义的常量的顺序,从0开始计数
        switchFragment(currentType.ordinal());
    }

    private void setViewEventListener() {

        rbDistance.setOnClickListener(v -> {
            switchFragment(0);
        });
        rbChargeType.setOnClickListener(v -> switchFragment(1));
        rbPrice.setOnClickListener(v -> switchFragment(2));
    }


    private void switchFragment(int fragmentIndex){
        //在Activity中显示Fragment
        //1、获取Fragment管理器 FragmentManager
        FragmentManager fragmentManager=this.getSupportFragmentManager();
        //2、开启fragment事务
        FragmentTransaction transaction=fragmentManager.beginTransaction();


        //懒加载 - 如果需要显示的Fragment为null，就new。并添加到Fragment事务中
        if (fragments[fragmentIndex]==null){
            switch (fragmentIndex){
                case 0://home
                    fragments[fragmentIndex]=new ListDistanceFragment(longitude,latitude);
                    break;
                case 1://order
                    fragments[fragmentIndex]=new ListChargeTypeFragment(longitude,latitude);
                    break;
                case 2://user
                    fragments[fragmentIndex]=new ListPriceFragment(longitude,latitude);
                    break;
            }
            //==添加Fragment对象到Fragment事务中
            //参数：显示Fragment的容器的ID，Fragment对象
            transaction.add(R.id.id_lv_parent,fragments[fragmentIndex]);
        }

        //隐藏其他的Fragment
        for (int i=0;i<fragments.length;i++){
            if (fragmentIndex!=i && fragments[i]!=null){
                //隐藏指定的Fragment
                transaction.hide(fragments[i]);
            }
        }
        //3、把要显示的Fragment的对象添加到FragmentTransaction
        //HomeFragment homeFragment=new HomeFragment();
        //transaction.add(R.id.ll_main_content,homeFragment);

        //4、显示Fragment
        transaction.show(fragments[fragmentIndex]);

        //5、提交事务
        transaction.commit();
    }

}
