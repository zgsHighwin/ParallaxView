package com.zgs.parallax.view.adapter;

import android.view.View;
import android.widget.TextView;

import com.zgs.parallax.view.R;

import java.util.List;

/**
 * User: zgsHighwin
 * Email: 799174081@qq.com Or 799174081@gmail.com
 * Description:
 * Create-Time: 2017/4/8 19:35
 */
public  class MyAdapter extends BaseParallaxAdapter<String> {


    public MyAdapter(List<String> list) {
        super(R.layout.item_text, list);
    }

    @Override
    public void convertView(ParallaxHolder holder, String s) {
        ((TextView) holder.itemView.findViewById(R.id.text1)).setText(s);
    }

}
