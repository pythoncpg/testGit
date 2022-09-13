package com.xiaomi.myapplication.util;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaomi.myapplication.R;

/*
 * 系统自身的toast采用队列,MyToast需要顶掉当前Toast,显示最新的toast
 * */
public class MyToast extends Toast {
    private final static int ICON_HIDE = -1;
    private final static int ICON_SHOW = 1;

    private static MyToast toast;
    private static ImageView iv;
    private static TextView tv;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public MyToast(Context context) {
        super(context);
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }

    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 初始化toast
     *
     * @param context
     * @param text
     */
    public static void initToast(Context context, CharSequence text) {
        cancelToast();
        toast = new MyToast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.view_my_toast, null);
        // 设置资源
        iv = view.findViewById(R.id.iv_toast_img);
        tv = view.findViewById(R.id.tv_toast_text);
        tv.setText(text);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
    }

    /**
     * 显示toast
     * @param context
     * @param text
     * @param imgType
     */
    private static void showToast(Context context, CharSequence text, int imgType, int id) {
        initToast(context, text);
        // 判断是否显示icon
        if (imgType == ICON_HIDE) iv.setVisibility(View.GONE);
        else {
            iv.setImageResource(R.drawable.test_icon);
            ObjectAnimator.ofFloat(iv, "rotationY", 0, 360).setDuration(1700).start();
        }
        toast.show();
    }
    public static void showText(Context context,CharSequence text){
        showToast(context,text,ICON_HIDE,ICON_HIDE);

    }
    public static void showText(Context context,CharSequence text,int id){
        showToast(context,text,ICON_SHOW,id);
    }
}
