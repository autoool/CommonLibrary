package com.techidea.commondemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import com.techidea.commondemo.menu.ArcMenuActivity;
import com.techidea.commondemo.adapter.GridViewAdapter;
import com.techidea.commondemo.adapter.PayItem;
import com.techidea.commondemo.menu.RayMenuActivity;
import com.techidea.commondemo.recyclerview.GridActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.gridview)
    GridView mGridView;

    private List<PayItem> mPayItemList;
    private GridViewAdapter mGridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        mGridViewAdapter = new GridViewAdapter(this, mPayItemList, R.layout.view_pay_item);
        mGridView.setAdapter(mGridViewAdapter);
    }

    private void initData() {
        mPayItemList = new ArrayList<>();
        mPayItemList.add(new PayItem(R.drawable.ali, "支付宝"));
        mPayItemList.add(new PayItem(R.drawable.weixin, "微信"));
        mPayItemList.add(new PayItem(R.drawable.card, "银行卡"));
        mPayItemList.add(new PayItem(R.drawable.cash, "现金"));
        mPayItemList.add(new PayItem(R.drawable.discount, "折扣"));
    }

    @OnClick(R.id.button_recycler_grid)
    void buttonGrid() {
        startActivity(new Intent(this, GridActivity.class));
    }

    @OnClick(R.id.button_arcmenu)
    void buttonArcMenu() {
        startActivity(new Intent(this, ArcMenuActivity.class));
    }

    @OnClick(R.id.button_raymenu)
    void buttonRayMenu() {
        startActivity(new Intent(this, RayMenuActivity.class));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
