package com.kore.sample;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kore.jigsaw.anno.router.Autowired;
import com.kore.jigsaw.anno.router.Route;
import com.kore.jigsaw.core.router.JRouter;

/**
 * @author koreq
 * @date 2021-04-16
 * @description
 */
@Route(path = "/about", desc = "关于页面")
public class AboutActivity extends Activity {

    @Autowired
    String mSource = "default";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JRouter.get().inject(this);

        setContentView(R.layout.act_about);
        TextView tvInfo = (TextView) findViewById(R.id.tv_info);
        String showText = "source: " + mSource;
        tvInfo.setText(showText);
    }
}
