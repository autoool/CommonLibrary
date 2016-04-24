package com.techidea.commondemo.adapter;

import android.support.v7.widget.RecyclerView;

import com.techidea.commonlibrary.adapter.BaseRecyclerAdapter;
import com.techidea.commonlibrary.adapter.BaseRecyclerHolder;
import com.techidea.commondemo.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by zhangchao on 2016/4/24.
 */
public class RecyclerviewGridAdapter extends BaseRecyclerAdapter<PayItem> {

    private List<PayItem> mDatas;

    public RecyclerviewGridAdapter(RecyclerView v, Collection<PayItem> datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
        this.mDatas = new ArrayList<>(datas);
    }

    @Override
    public void convert(BaseRecyclerHolder holder, PayItem item, int position, boolean isScrolling) {
        holder.setText(R.id.textview_pay_name, item.getName());
        holder.setImageResource(R.id.imageview_pay, item.getId());
    }
}
