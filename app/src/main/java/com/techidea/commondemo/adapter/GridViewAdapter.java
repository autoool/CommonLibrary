package com.techidea.commondemo.adapter;

import android.content.Context;
import com.techidea.commonlibrary.adapter.CommonAdapter;
import com.techidea.commonlibrary.adapter.CommonViewHolder;
import com.techidea.commondemo.R;

import java.util.List;

/**
 * Created by zhangchao on 2016/4/24.
 */
public class GridViewAdapter extends CommonAdapter<PayItem> {

    public GridViewAdapter(Context context, List<PayItem> datas, int itemLayoutId) {
        super(context, datas, itemLayoutId);
    }

    @Override
    public void convert(CommonViewHolder viewHolder, PayItem item) {
        viewHolder.setImageResource(R.id.imageview_pay, item.getId());
        viewHolder.setText(R.id.textview_pay_name, item.getName());
    }
}
