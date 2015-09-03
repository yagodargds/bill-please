package com.yagodar.android.bill_please.activity.bill_list;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;
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
public class BillListAdapter extends AbsRecyclerViewAdapter<Bill> {

    public BillListAdapter(Context context, View.OnClickListener onClickListener, ListModel<Bill> listModel) {
        super(context, onClickListener, listModel);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.bill_row, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewNumber = (TextView) convertView.findViewById(R.id.bill_number);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.bill_et_name);
            viewHolder.textViewTaxVal = (TextView) convertView.findViewById(R.id.bill_tax_val);
            viewHolder.textViewTaxType = (TextView) convertView.findViewById(R.id.bill_tax_type);
            viewHolder.textViewTipVal = (TextView) convertView.findViewById(R.id.bill_tip_val);
            viewHolder.textViewTipType = (TextView) convertView.findViewById(R.id.bill_tip_type);

            viewHolder.buttonEdit = (Button) convertView.findViewById(R.id.bill_edit_button);
            viewHolder.buttonEdit.setOnClickListener(getOnClickListener());

            viewHolder.buttonRemove = (Button) convertView.findViewById(R.id.bill_remove_button);
            viewHolder.buttonRemove.setOnClickListener(getOnClickListener());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bill bill = getItem(position);

        viewHolder.textViewNumber.setText(String.valueOf(position + 1));
        viewHolder.textViewName.setText(bill.getName());
        viewHolder.textViewTaxVal.setText(bill.getFormattedTaxVal());
        viewHolder.textViewTaxType.setText(bill.getTaxType().toString());
        viewHolder.textViewTipVal.setText(bill.getFormattedTipVal());
        viewHolder.textViewTipType.setText(bill.getTipType().toString());

        Bundle buttonArgs = new Bundle();
        buttonArgs.putLong(BaseColumns._ID, bill.getId());

        viewHolder.buttonEdit.setTag(buttonArgs);
        viewHolder.buttonRemove.setTag(buttonArgs);

        return convertView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public ViewHolder(View v) {
            super(v);
        }
    }

    private static class ViewHolder {
        public TextView textViewNumber;
        public TextView textViewName;
        public TextView textViewTaxVal;
        public TextView textViewTaxType;
        public TextView textViewTipVal;
        public TextView textViewTipType;
        public Button buttonEdit;
        public Button buttonRemove;
    }

}
