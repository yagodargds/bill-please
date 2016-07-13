package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.custom.adapter.AbsRecyclerViewAdapter;
import com.yagodar.essential.model.ListModel;

/**
 * Created by yagodar on 18.06.2015.
 */
public class BillListAdapter extends AbsRecyclerViewAdapter<Bill, BillListAdapter.ViewHolder> {
    public BillListAdapter(Context context,
                           View.OnClickListener onClickListener,
                           View.OnLongClickListener onLongClickListener,
                           ListModel<Bill> listModel) {
        super(context, onClickListener, onLongClickListener, listModel);
    }

    @Override
    protected ViewHolder onCreateViewHolder(View itemView, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        return new ViewHolder(itemView, onClickListener, onLongClickListener);
    }

    @Override
    protected View createItemView(ViewGroup parent, int viewType) {
        return getLayoutInflater().inflate(R.layout.bill_row, parent, false);
    }

    public static class ViewHolder extends AbsRecyclerViewAdapter.AbsViewHolder<Bill> {
        private TextView mName;
        private TextView mOrders;
        private TextView mTotal;
        private TextView mDate;

        public ViewHolder(View itemView, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
            super(itemView);

            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);

            mName = (TextView) itemView.findViewById(R.id.bill_name);
            mOrders = (TextView) itemView.findViewById(R.id.bill_orders);
            mTotal = (TextView) itemView.findViewById(R.id.bill_total);
            mDate = (TextView) itemView.findViewById(R.id.bill_date);
        }

        @Override
        public void onBind(Bill model, int position) {
            itemView.setTag(model.getTag());
            mName.setText(model.getName());
            mOrders.setText(String.valueOf(model.getCount()));
            mTotal.setText(model.getFormattedTotal());
        }
    }
}
