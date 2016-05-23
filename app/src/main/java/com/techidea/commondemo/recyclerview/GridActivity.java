package com.techidea.commondemo.recyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.techidea.commondemo.R;
import com.techidea.commonlibrary.adapter.DividerGridItemDecoration;
import com.techidea.commondemo.adapter.PayItem;
import com.techidea.commondemo.adapter.RecyclerviewGridAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zhangchao on 2016/4/24.
 */
public class GridActivity extends AppCompatActivity {

    @Bind(R.id.recyclerview_grid)
    RecyclerView mRecyclerViewGrid;

    private RecyclerviewGridAdapter mRecyclerviewGridAdapter;

    private List<PayItem> mPayItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        ButterKnife.bind(this);
        initData();

        mRecyclerviewGridAdapter = new RecyclerviewGridAdapter(mRecyclerViewGrid, mPayItemList, R.layout.view_pay_item);
        mRecyclerViewGrid.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerViewGrid.setLayoutManager(new GridLayoutManager(this, 4));

        mRecyclerViewGrid.setAdapter(mRecyclerviewGridAdapter);
    }

    private void initData() {
        mPayItemList = new ArrayList<>();
        mPayItemList.add(new PayItem(R.drawable.ali, "支付宝"));
        mPayItemList.add(new PayItem(R.drawable.weixin, "微信"));
        mPayItemList.add(new PayItem(R.drawable.card, "银行卡"));
        mPayItemList.add(new PayItem(R.drawable.cash, "现金"));
        mPayItemList.add(new PayItem(R.drawable.discount, "折扣"));
    }
}
