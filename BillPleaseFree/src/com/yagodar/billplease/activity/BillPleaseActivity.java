package com.yagodar.billplease.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

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
		
		llBillRows = ((LinearLayout) findViewById(R.id.ll_bill_rows));
		if(llBillRows != null) {
			etOnTouchListener = new EtOnTouchListener();
			DbTableManagersHolder.getInstance().addDbTableManager(getResources().getString(R.string.db_name), BillPleaseDbTableManager.getInstance());
			DbProvider.newInstance(this, getResources().getString(R.string.db_name), getResources().getInteger(R.integer.db_version));
			recoverBill();
		}
		else {
			finish();
		}
	}
	
	public void onButtonClick(View button) {
		switch(button.getId()) {
		case R.id.btn_add_row:
			addNewBillRow();
			break;
		case R.id.btn_del_row:
			BillPleaseDbTableManager.getInstance().delBillRow((Long) button.getTag());
			llBillRows.removeView(llBillRows.findViewWithTag(button.getTag()));
			break;
		case R.id.btn_new_bill:
			BillPleaseDbTableManager.getInstance().delAllBillRows();
			((LinearLayout) findViewById(R.id.ll_bill_rows)).removeAllViews();
			break;
		default:
			break;
		}
	}

    private void addNewBillRow() {
		String defItemName = getResources().getString(R.string.def_item_name);
		drawBillRow(BillPleaseDbTableManager.getInstance().addBillRow(	defItemName, 
																		Double.parseDouble(getResources().getString(R.string.def_cost_double)), 
																		getResources().getInteger(R.integer.def_share)), 
					defItemName, 
					getResources().getString(R.string.draw_def_cost), 
					getResources().getString(R.string.draw_def_share));
	}

	private void recoverBill() {
		llBillRows.removeAllViews();
		
		for (BillRow billRowDb : BillPleaseDbTableManager.getInstance().getBillRows()) {
			String itemName = getResources().getString(R.string.def_item_name);
			String costStr = getResources().getString(R.string.draw_def_cost);
			String shareStr = getResources().getString(R.string.draw_def_share);
			
			if(billRowDb.isItemNameChanged()) {
				itemName = billRowDb.getItemName();
			}
			
			if(billRowDb.isCostChanged()) {
				costStr = String.valueOf(billRowDb.getCost());
			}

			if(billRowDb.isShareChanged()) {
				shareStr = String.valueOf(billRowDb.getShare());
			}
			
			drawBillRow(billRowDb.getRowTag(), itemName, costStr, shareStr);
		}
	}
	
	private void drawBillRow(long rowTag, String itemName, String cost, String share) {
		LinearLayout billRowLl = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);

		billRowLl.setTag(rowTag);

		EditText etItem = (EditText) billRowLl.findViewById(R.id.et_item);
		etItem.setText(itemName);
		etItem.setTag(rowTag);
		etItem.setOnTouchListener(etOnTouchListener);
		etItem.addTextChangedListener(new EtTextWatcher(etItem));

		EditText etCost = (EditText) billRowLl.findViewById(R.id.et_cost);
		etCost.setText(cost);
		etCost.setTag(rowTag);
		etCost.setOnTouchListener(etOnTouchListener);
		etCost.addTextChangedListener(new EtTextWatcher(etCost));

		EditText etShare = (EditText) billRowLl.findViewById(R.id.et_share);
		etShare.setText(share);
		etShare.setTag(rowTag);
		etShare.setOnTouchListener(etOnTouchListener);
		etShare.addTextChangedListener(new EtTextWatcher(etShare));

		billRowLl.findViewById(R.id.btn_del_row).setTag(rowTag);
		
		llBillRows.addView(billRowLl);
	}
	
	private class EtOnTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			boolean valueChanged = false;
			
			switch(v.getId()) {
			case R.id.et_item:
				valueChanged = BillPleaseDbTableManager.getInstance().isBillRowItemNameChanged((Long) v.getTag());
				break;
			case R.id.et_cost:
				valueChanged = BillPleaseDbTableManager.getInstance().isBillRowCostChanged((Long) v.getTag());
				break;
			case R.id.et_share:
				valueChanged = BillPleaseDbTableManager.getInstance().isBillRowShareChanged((Long) v.getTag());
				break;
			default:
				break;
			}
			
			if(!valueChanged) {
                v.requestFocus();
                ((EditText)v).setSelection(0);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput((EditText) v, 0);
                return true;
			}
            else {
                return false;
            }
		}
	}
	
	private class EtTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			switch(owner.getId()) {
			case R.id.et_item:
                if(!BillPleaseDbTableManager.getInstance().isBillRowItemNameChanged(ownerRowTag)) {
                    BillPleaseDbTableManager.getInstance().setBillRowItemNameChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.def_item_name));
                        owner.setSelection(0);

                        BillPleaseDbTableManager.getInstance().setBillRowItemNameChanged(ownerRowTag, false);
                    }
                    else {
				        BillPleaseDbTableManager.getInstance().setBillRowItemName(ownerRowTag, s.toString());
                    }
                }
				break;
			case R.id.et_cost:
                if(!BillPleaseDbTableManager.getInstance().isBillRowCostChanged(ownerRowTag)) {
                    BillPleaseDbTableManager.getInstance().setBillRowCostChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_cost));
                        owner.setSelection(0);

                        BillPleaseDbTableManager.getInstance().setBillRowCostChanged(ownerRowTag, false);
                    }
                    else {
                        double value = 0.0;
                        try {
                            value = Double.parseDouble(s.toString());
                        }
                        catch(Exception e){
                            try {
                                value = Double.parseDouble(getResources().getString(R.string.def_cost_double));
                            }
                            catch(Exception ignored){}
                        }

                        BillPleaseDbTableManager.getInstance().setBillRowCost(ownerRowTag, value);
                    }
                }
                break;
			case R.id.et_share:
                if(!BillPleaseDbTableManager.getInstance().isBillRowShareChanged(ownerRowTag)) {
                    BillPleaseDbTableManager.getInstance().setBillRowShareChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_share));
                        owner.setSelection(0);

                        BillPleaseDbTableManager.getInstance().setBillRowShareChanged(ownerRowTag, false);
                    }
                    else {
                        int value = getResources().getInteger(R.integer.def_share);
                        try {
                            value = Integer.parseInt(s.toString());
                        }
                        catch(Exception ignored){}

                        BillPleaseDbTableManager.getInstance().setBillRowShare(ownerRowTag, value);
                    }
                }
                break;
			default:
				break;
			}
		}

        @Override
        public void afterTextChanged(Editable s) {}
		
		public EtTextWatcher(EditText owner) {
			this.owner = owner;
			this.ownerRowTag = (Long) owner.getTag();
		}
		
		private EditText owner;
		private long ownerRowTag;
	}
	
	private LinearLayout llBillRows;
	private EtOnTouchListener etOnTouchListener; 
}