package com.yagodar.android.bill_please.activity.bill_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;

/**
 * Created by yagodar on 18.06.2015.
 */
public class BillListAdapter extends BaseAdapter {

    public BillListAdapter(Context context, View.OnClickListener onClickListener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        if(BillList.getInstance().isLoaded()) {
            return BillList.getInstance().getCount();
        } else {
            return 0;
        }
    }

    @Override
    public Bill getItem(int position) {
        return BillList.getInstance().getBill(position);
    }

    @Override
    public long getItemId(int position) {
        return BillList.getInstance().getBill(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.bill_row, null);

            viewHolder = new ViewHolder();

            viewHolder.numberView = (TextView) convertView.findViewById(R.id.bill_number);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.bill_name);
            viewHolder.taxValView = (TextView) convertView.findViewById(R.id.bill_tax_val);
            viewHolder.taxTypeView = (TextView) convertView.findViewById(R.id.bill_tax_type);
            viewHolder.tipValView = (TextView) convertView.findViewById(R.id.bill_tip_val);
            viewHolder.tipTypeView = (TextView) convertView.findViewById(R.id.bill_tip_type);

            viewHolder.editButton = (Button) convertView.findViewById(R.id.bill_edit_button);
            viewHolder.editButton.setOnClickListener(onClickListener);

            viewHolder.updateButton = (Button) convertView.findViewById(R.id.bill_update_button);
            viewHolder.updateButton.setOnClickListener(onClickListener);

            viewHolder.removeButton = (Button) convertView.findViewById(R.id.bill_remove_button);
            viewHolder.removeButton.setOnClickListener(onClickListener);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bill bill = getItem(position);

        viewHolder.numberView.setText(String.valueOf(position + 1));
        viewHolder.nameView.setText(bill.getName());
        viewHolder.taxValView.setText(bill.getFormattedTaxVal());
        viewHolder.taxTypeView.setText(bill.getTaxType().toString());
        viewHolder.tipValView.setText(bill.getFormattedTipVal());
        viewHolder.tipTypeView.setText(bill.getTipType().toString());

        Bundle buttonArgs = new Bundle();
        buttonArgs.putLong(Bill.BILL_ID_KEY, bill.getId());

        viewHolder.editButton.setTag(buttonArgs);
        viewHolder.updateButton.setTag(buttonArgs);
        viewHolder.removeButton.setTag(buttonArgs);

        return convertView;
    }

    private static class ViewHolder {
        public TextView numberView;
        public TextView nameView;
        public TextView taxValView;
        public TextView taxTypeView;
        public TextView tipValView;
        public TextView tipTypeView;
        public Button editButton;
        public Button updateButton;
        public Button removeButton;
    }

    private final LayoutInflater layoutInflater;
    private final View.OnClickListener onClickListener;
}
