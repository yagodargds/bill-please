package com.yagodar.billplease.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.yagodar.billplease.R;
import com.yagodar.billplease.db.BillPleaseDbTableManager;
import com.yagodar.billplease.db.BillPleaseDbTableManager.BillRow;
import com.yagodar.db.DbProvider;
import com.yagodar.db.DbTableManagersHolder;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bill_please_llv);
		
		DbTableManagersHolder.getInstance().addDbTableManager(getResources().getString(R.string.db_name), BillPleaseDbTableManager.getInstance());
		DbProvider.newInstance(this, getResources().getString(R.string.db_name), getResources().getInteger(R.integer.db_version));
		
		recoverBill();
	}
	
	public void onButtonClick(View button) {
		LinearLayout llBillRows = ((LinearLayout) findViewById(R.id.ll_bill_rows));
		if(llBillRows != null) {
			switch(button.getId()) {
			case R.id.btn_add_row:
				addNewBillRow();
				break;
			case R.id.btn_del_row:
				BillPleaseDbTableManager.getInstance().delBillRow((Long) button.getTag());
				llBillRows.removeView(llBillRows.findViewWithTag((Long) button.getTag()));
				break;
			case R.id.btn_new_bill:
				BillPleaseDbTableManager.getInstance().delAllBillRows();
				((LinearLayout) findViewById(R.id.ll_bill_rows)).removeAllViews();
				break;
			default:
				break;
			}
		}
	}

	public void onEditTextClick(View editText) {
		switch(editText.getId()) {
		case R.id.et_item:
		case R.id.et_cost:
		case R.id.et_share:
			((EditText) editText).setText("");
			break;
		default:
			break;
		}
	}
	
	private void addNewBillRow() {
		LinearLayout llBillRows = (LinearLayout) findViewById(R.id.ll_bill_rows);
		if(llBillRows != null) {
			LinearLayout newBillRow = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);
			
			long rowTag = BillPleaseDbTableManager.getInstance().addBillRow(((EditText) newBillRow.findViewById(R.id.et_item)).getText().toString(), getResources().getInteger(R.integer.def_cost), getResources().getInteger(R.integer.def_share));
			
			newBillRow.setTag(rowTag);
			for (int i = 0; i < newBillRow.getChildCount(); i++) {
				newBillRow.getChildAt(i).setTag(rowTag);
			}
			
			llBillRows.addView(newBillRow);
		}
	}
	
	private void recoverBill() {
		LinearLayout llBillRows = (LinearLayout) findViewById(R.id.ll_bill_rows);
		if(llBillRows != null) {
			llBillRows.removeAllViews();

			EtOnTouchListener etOnTouchListener = new EtOnTouchListener();
			
			for (BillRow billRowDb : BillPleaseDbTableManager.getInstance().getBillRows()) {
				LinearLayout billRowLl = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);

				billRowLl.setTag(billRowDb.getRowTag());

				EditText etItem = (EditText) billRowLl.findViewById(R.id.et_item);
				etItem.setText(billRowDb.getItemName());
				etItem.setTag(billRowDb.getRowTag());
				etItem.setOnTouchListener(etOnTouchListener);
				etItem.addTextChangedListener(new EtTextWatcher(R.id.et_item, billRowDb.getRowTag()));

				EditText etCost = (EditText) billRowLl.findViewById(R.id.et_cost);
				if(billRowDb.getCost() != getResources().getInteger(R.integer.def_cost)) {
					etCost.setText("" + billRowDb.getCost());
				}
				etCost.setTag(billRowDb.getRowTag());
				etCost.setOnTouchListener(etOnTouchListener);
				etCost.addTextChangedListener(new EtTextWatcher(R.id.et_cost, billRowDb.getRowTag()));

				EditText etShare = (EditText) billRowLl.findViewById(R.id.et_share);
				if(billRowDb.getShare() != getResources().getInteger(R.integer.def_share)) {
					etShare.setText("" + billRowDb.getShare());
				}
				etShare.setTag(billRowDb.getRowTag());
				etShare.setOnTouchListener(etOnTouchListener);
				etShare.addTextChangedListener(new EtTextWatcher(R.id.et_share, billRowDb.getRowTag()));

				billRowLl.findViewById(R.id.btn_del_row).setTag(billRowDb.getRowTag());
				
				llBillRows.addView(billRowLl);
			}
		}
	}
	
	private class EtOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(v.getId()) {
			case R.id.et_item:
			case R.id.et_cost:
			case R.id.et_share:
				((EditText)v).setText("");
				((EditText)v).requestFocus();
				break;
			default:
				break;
			}

			return true;
		}
	}
	
	private class EtTextWatcher implements TextWatcher {
		@Override
		public void afterTextChanged(Editable s) {}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			switch(ownerResId) {
			case R.id.et_item:
				BillPleaseDbTableManager.getInstance().setBillRowItemName(ownerRowTag, s.toString());
				break;
			case R.id.et_cost:
				int cost = getResources().getInteger(R.integer.def_cost);
				try {
					cost = Integer.parseInt(s.toString());
				}
				catch(Exception e){}
				
				BillPleaseDbTableManager.getInstance().setBillRowCost(ownerRowTag, cost);
				break;
			case R.id.et_share:
				int share = getResources().getInteger(R.integer.def_share);
				try {
					share = Integer.parseInt(s.toString());
				}
				catch(Exception e){}
				
				BillPleaseDbTableManager.getInstance().setBillRowShare(ownerRowTag, share);
				break;
			default:
				break;
			}
		}
		
		public EtTextWatcher(int ownerResId, long ownerRowTag) {
			this.ownerResId = ownerResId;
			this.ownerRowTag = ownerRowTag;
		}
		
		private int ownerResId;
		private long ownerRowTag;
	}
}
