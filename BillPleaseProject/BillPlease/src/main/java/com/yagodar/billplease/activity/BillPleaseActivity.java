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
import com.yagodar.billplease.db.DbBillPleaseManager;
import com.yagodar.billplease.db.DbBillPleaseTablePersonalBillContract;
import com.yagodar.billplease.db.DbBillPleaseTablePersonalBillManager;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bill_please_llv);

		llBillRows = ((LinearLayout) findViewById(R.id.ll_bill_rows));
		if(llBillRows != null) {
			etOnTouchListener = new EtOnTouchListener();
            dbBillPleaseTablePersonalBillManager = (DbBillPleaseTablePersonalBillManager) DbBillPleaseManager.getInstance(this).getDbTableManager(DbBillPleaseTablePersonalBillContract.getInstance().getTableName());
			recoverBill();
		}
		else {
			finish();
		}
	}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //Skipped. Not in need. Everything is redraws in onCreate(). P.S. If use it, may be bugs with draw EditText views.
    }

	public void onButtonClick(View button) {
		switch(button.getId()) {
		case R.id.btn_add_row:
			addNewBillRow();
			break;
		case R.id.btn_del_row:
			dbBillPleaseTablePersonalBillManager.delPersonalBillRecord((Long) button.getTag());
			llBillRows.removeView(llBillRows.findViewWithTag(button.getTag()));
			break;
		case R.id.btn_new_bill:
			dbBillPleaseTablePersonalBillManager.delAllPersonalBillRecords();
			((LinearLayout) findViewById(R.id.ll_bill_rows)).removeAllViews();
			break;
		default:
			break;
		}
	}

    private void addNewBillRow() {
        drawBillRow(dbBillPleaseTablePersonalBillManager.addPersonalBillRecord(),
                getResources().getString(R.string.def_item_name),
                getResources().getString(R.string.draw_def_cost),
                getResources().getString(R.string.draw_def_share));
    }

	private void recoverBill() {
		llBillRows.removeAllViews();

		for (DbBillPleaseTablePersonalBillManager.PersonalBillRecord billRowDb : dbBillPleaseTablePersonalBillManager.getAllPersonalBillRecords()) {
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

			drawBillRow(billRowDb.getTag(), itemName, costStr, shareStr);
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
				valueChanged = dbBillPleaseTablePersonalBillManager.isPersonalBillRecordItemNameChanged((Long) v.getTag());
				break;
			case R.id.et_cost:
				valueChanged = dbBillPleaseTablePersonalBillManager.isPersonalBillRecordCostChanged((Long) v.getTag());
				break;
			case R.id.et_share:
				valueChanged = dbBillPleaseTablePersonalBillManager.isPersonalBillRecordShareChanged((Long) v.getTag());
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
                if(!dbBillPleaseTablePersonalBillManager.isPersonalBillRecordItemNameChanged(ownerRowTag)) {
                    dbBillPleaseTablePersonalBillManager.setPersonalBillRecordItemNameChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.def_item_name));
                        owner.setSelection(0);

                        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordItemNameChanged(ownerRowTag, false);
                    }
                    else {
				        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordItemName(ownerRowTag, s.toString());
                    }
                }
				break;
			case R.id.et_cost:
                if(!dbBillPleaseTablePersonalBillManager.isPersonalBillRecordCostChanged(ownerRowTag)) {
                    dbBillPleaseTablePersonalBillManager.setPersonalBillRecordCostChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_cost));
                        owner.setSelection(0);

                        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordCostChanged(ownerRowTag, false);
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

                        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordCost(ownerRowTag, value);
                    }
                }
                break;
			case R.id.et_share:
                if(!dbBillPleaseTablePersonalBillManager.isPersonalBillRecordShareChanged(ownerRowTag)) {
                    dbBillPleaseTablePersonalBillManager.setPersonalBillRecordShareChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_share));
                        owner.setSelection(0);

                        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordShareChanged(ownerRowTag, false);
                    }
                    else {
                        int value = getResources().getInteger(R.integer.def_share);
                        try {
                            value = Integer.parseInt(s.toString());
                        }
                        catch(Exception ignored){}

                        dbBillPleaseTablePersonalBillManager.setPersonalBillRecordShare(ownerRowTag, value);
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

    private DbBillPleaseTablePersonalBillManager dbBillPleaseTablePersonalBillManager;
}