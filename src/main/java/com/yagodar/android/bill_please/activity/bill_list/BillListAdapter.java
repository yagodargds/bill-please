package com.yagodar.android.bill_please.activity.bill_list;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.custom.adapter.AbsRecyclerViewAdapter;
import com.yagodar.essential.model.ListModel;

/**
 * Created by yagodar on 18.06.2015.
 */
public class BillListAdapter extends AbsRecyclerViewAdapter<Bill, BillListAdapter.ViewHolder> {

    public BillListAdapter(Context context, View.OnClickListener onClickListener, ListModel<Bill> listModel) {
        super(context, onClickListener, listModel);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getLayoutInflater().inflate(R.layout.bill_row, parent, false), getOnClickListener());
    }



    public static class ViewHolder extends AbsRecyclerViewAdapter.AbsViewHolder<Bill> {

        private TextView mTextViewNumber;
        private TextView mTextViewName;
        private TextView mTextViewTaxVal;
        private TextView mTextViewTaxType;
        private TextView mTextViewTipVal;
        private TextView mTextViewTipType;
        private Button mButtonEdit;
        private Button mButtonRemove;

        public ViewHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);

            mTextViewNumber = (TextView) itemView.findViewById(R.id.bill_number);
            mTextViewName = (TextView) itemView.findViewById(R.id.bill_et_name);
            mTextViewTaxVal = (TextView) itemView.findViewById(R.id.bill_tax_val);
            mTextViewTaxType = (TextView) itemView.findViewById(R.id.bill_tax_type);
            mTextViewTipVal = (TextView) itemView.findViewById(R.id.bill_tip_val);
            mTextViewTipType = (TextView) itemView.findViewById(R.id.bill_tip_type);

            mButtonEdit = (Button) itemView.findViewById(R.id.bill_edit_button);
            mButtonEdit.setOnClickListener(onClickListener);

            mButtonRemove = (Button) itemView.findViewById(R.id.bill_remove_button);
            mButtonRemove.setOnClickListener(onClickListener);
        }

        @Override
        public void onBind(Bill model, int position) {
            mTextViewNumber.setText(String.valueOf(position + 1));
            mTextViewName.setText(model.getName());
            mTextViewTaxVal.setText(model.getFormattedTaxVal());
            mTextViewTaxType.setText(model.getTaxType().toString());
            mTextViewTipVal.setText(model.getFormattedTipVal());
            mTextViewTipType.setText(model.getTipType().toString());

            Bundle buttonArgs = new Bundle();
            buttonArgs.putLong(BaseColumns._ID, model.getId());

            mButtonEdit.setTag(buttonArgs);
            mButtonRemove.setTag(buttonArgs);

            setEnabled(true); //TODO моргает
        }

        public void setEnabled(boolean value) {
            mButtonEdit.setEnabled(value);
            mButtonRemove.setEnabled(value);
        }
    }

}
