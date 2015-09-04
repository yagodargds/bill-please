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

        public TextView textViewNumber;
        public TextView textViewName;
        public TextView textViewTaxVal;
        public TextView textViewTaxType;
        public TextView textViewTipVal;
        public TextView textViewTipType;
        public Button buttonEdit;
        public Button buttonRemove;

        public ViewHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);

            textViewNumber = (TextView) itemView.findViewById(R.id.bill_number);
            textViewName = (TextView) itemView.findViewById(R.id.bill_et_name);
            textViewTaxVal = (TextView) itemView.findViewById(R.id.bill_tax_val);
            textViewTaxType = (TextView) itemView.findViewById(R.id.bill_tax_type);
            textViewTipVal = (TextView) itemView.findViewById(R.id.bill_tip_val);
            textViewTipType = (TextView) itemView.findViewById(R.id.bill_tip_type);

            buttonEdit = (Button) itemView.findViewById(R.id.bill_edit_button);
            buttonEdit.setOnClickListener(onClickListener);

            buttonRemove = (Button) itemView.findViewById(R.id.bill_remove_button);
            buttonRemove.setOnClickListener(onClickListener);
        }

        @Override
        public void onBind(Bill model, int position) {
            textViewNumber.setText(String.valueOf(position + 1));
            textViewName.setText(model.getName());
            textViewTaxVal.setText(model.getFormattedTaxVal());
            textViewTaxType.setText(model.getTaxType().toString());
            textViewTipVal.setText(model.getFormattedTipVal());
            textViewTipType.setText(model.getTipType().toString());

            Bundle buttonArgs = new Bundle();
            buttonArgs.putLong(BaseColumns._ID, model.getId());

            buttonEdit.setTag(buttonArgs);
            buttonRemove.setTag(buttonArgs);
        }

    }

}
