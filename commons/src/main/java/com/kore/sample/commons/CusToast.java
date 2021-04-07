package com.kore.sample.commons;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kore.sample.commons.base.BaseApp;

public class CusToast {
    public static void show(String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void showLong(String msg) {
        show(msg, Toast.LENGTH_LONG);
    }

    private static void show(String massage, int showLength) {
        Context context = BaseApp.INSTANCE;
        View view = LayoutInflater.from(context).inflate(R.layout.common_cus_toast, null);

        TextView title = (TextView) view.findViewById(R.id.toast_tv);
        title.setText(massage);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 70);
        toast.setDuration(showLength);

        toast.setView(view);
        toast.show();
    }
}