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
        private TextView mTextViewName;
        private TextView mTextViewTaxVal;
        private TextView mTextViewTaxType;
        private TextView mTextViewTipVal;
        private TextView mTextViewTipType;

        public ViewHolder(View itemView, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
            super(itemView);

            itemView.setOnClickListener(onClickListener);
            itemView.setOnLongClickListener(onLongClickListener);

            mTextViewName = (TextView) itemView.findViewById(R.id.bill_et_name);
            mTextViewTaxVal = (TextView) itemView.findViewById(R.id.bill_tax_val);
            mTextViewTaxType = (TextView) itemView.findViewById(R.id.bill_tax_type);
            mTextViewTipVal = (TextView) itemView.findViewById(R.id.bill_tip_val);
            mTextViewTipType = (TextView) itemView.findViewById(R.id.bill_tip_type);
        }

        @Override
        public void onBind(Bill model, int position) {
            itemView.setTag(model.getTag());
            mTextViewName.setText(model.getName());
            mTextViewTaxVal.setText(model.getFormattedTaxVal());
            mTextViewTaxType.setText(model.getTaxType().toString());
            mTextViewTipVal.setText(model.getFormattedTipVal());
            mTextViewTipType.setText(model.getTipType().toString());
        }
    }
}
