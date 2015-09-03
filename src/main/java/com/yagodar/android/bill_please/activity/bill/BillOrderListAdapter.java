package com.yagodar.android.bill_please.activity.bill;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.adapter.AbsListViewAdapter;
import com.yagodar.essential.model.ListModel;

/**
 * Created by yagodar on 24.06.2015.
 */
public class BillOrderListAdapter extends AbsListViewAdapter<BillOrder> {

    public BillOrderListAdapter(Context context, View.OnClickListener onClickListener, ListModel<BillOrder> listModel) {
        super(context, onClickListener, listModel);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.bill_order_row, null);

            viewHolder = new ViewHolder();

            viewHolder.textViewNumber = (TextView) convertView.findViewById(R.id.bill_order_number);
            viewHolder.textViewName = (TextView) convertView.findViewById(R.id.bill_order_name);
            viewHolder.textViewCost = (TextView) convertView.findViewById(R.id.bill_order_cost);
            viewHolder.textViewShare = (TextView) convertView.findViewById(R.id.bill_order_share);
            viewHolder.textViewSubtotal = (TextView) convertView.findViewById(R.id.bill_order_subtotal);

            viewHolder.buttonEdit = (Button) convertView.findViewById(R.id.bill_order_edit_button);
            viewHolder.buttonEdit.setOnClickListener(getOnClickListener());

            viewHolder.buttonRemove = (Button) convertView.findViewById(R.id.bill_order_remove_button);
            viewHolder.buttonRemove.setOnClickListener(getOnClickListener());

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BillOrder billOrder = getItem(position);

        viewHolder.textViewNumber.setText(String.valueOf(position + 1));
        viewHolder.textViewName.setText(billOrder.getName());
        viewHolder.textViewCost.setText(billOrder.getFormattedCost());
        viewHolder.textViewShare.setText(billOrder.getFormattedShare());
        viewHolder.textViewSubtotal.setText(billOrder.getFormattedSubtotal());

        Bundle buttonArgs = new Bundle();
        buttonArgs.putLong(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, getListModel().getId());
        buttonArgs.putLong(BaseColumns._ID, billOrder.getId());

        viewHolder.buttonEdit.setTag(buttonArgs);
        viewHolder.buttonRemove.setTag(buttonArgs);

        return convertView;
    }

    private static class ViewHolder {
        public TextView textViewNumber;
        public TextView textViewName;
        public TextView textViewCost;
        public TextView textViewShare;
        public TextView textViewSubtotal;
        public Button buttonEdit;
        public Button buttonRemove;
    }

}
