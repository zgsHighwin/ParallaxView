package com.zgs.parallax.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.zgs.parallax.view.adapter.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> mList;
    private ParallaxRecyclerView mRecyclerView;
    private View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = new ArrayList<>();
        init();
        mRecyclerView = (ParallaxRecyclerView) findViewById(R.id.prv);

        MyAdapter baseParallaxAdapter = new MyAdapter(mList);

        LayoutInflater headerInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //初始化布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //设置布局管理器
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //拿到header view
        mHeaderView = headerInflater.inflate(getResources().getIdentifier("view_header", "layout", getPackageName()), mRecyclerView, false);


        //添加header view
        baseParallaxAdapter.addHeaderView(mHeaderView);
        //添加footer view
        View footView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(getResources()
                                .getIdentifier("view_footer", "layout", getPackageName())
                        , mRecyclerView
                        , false);
        baseParallaxAdapter.addFooterView(footView);

        //设置适配器
        mRecyclerView.setAdapter(baseParallaxAdapter);
    }

    /**
     * 初始化数据
     */
    private void init() {
        String[] sCheeseStrings = Cheeses.sCheeseStrings;
        for (int i = 0; i < sCheeseStrings.length; i++) {
            mList.add(sCheeseStrings[i]);
        }
    }
}
