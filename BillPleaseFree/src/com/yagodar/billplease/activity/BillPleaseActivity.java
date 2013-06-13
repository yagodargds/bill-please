package com.yagodar.billplease.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.yagodar.billplease.R;
import com.yagodar.billplease.db.BillPleaseDbTableManager;
import com.yagodar.db.DbProvider;
import com.yagodar.db.DbTableManagersHolder;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bill_please_llv);
		
		DbTableManagersHolder.getInstance().addDbTableManager(getResources().getString(R.string.db_name), BillPleaseDbTableManager.getInstance());
		DbProvider.newInstance(this, getResources().getString(R.string.db_name), getResources().getInteger(R.integer.db_version));
	}
	
	public void onButtonClick(View button) {
		if(button != null) {
			switch(button.getId()) {
			case R.id.btn_add_row:
				addBillRow();
				break;
			case R.id.btn_del_row:
				delBillRow(button);
				break;
			case R.id.btn_new_bill:
				delAllBillRows();
				break;
			default:
				break;
			}
		}
	}
	
	private void addBillRow() {
		LinearLayout llBillRows = (LinearLayout) findViewById(R.id.ll_bill_rows);
		if(llBillRows != null) {
			LinearLayout newBillRow = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);
			
			//TODO синхронизация данных/ НЕ РАБОТАЕТ!
			long rowTag = BillPleaseDbTableManager.getInstance().addBillRow("item", 0, 0);
			
			newBillRow.setTag(rowTag);
			for (int i = 0; i < newBillRow.getChildCount(); i++) {
				newBillRow.getChildAt(i).setTag(rowTag);
			}
			
			llBillRows.addView(newBillRow);
		}
	}
	
	private void delBillRow(View delButton) {
		LinearLayout llBillRows = (LinearLayout) findViewById(R.id.ll_bill_rows);
		if(llBillRows != null) {
			llBillRows.removeView(llBillRows.findViewWithTag(delButton.getTag()));
		}
	}
	
	private void delAllBillRows() {
		LinearLayout llBillRows = (LinearLayout) findViewById(R.id.ll_bill_rows);
		if(llBillRows != null) {
			llBillRows.removeAllViews();
		}
	}
}
