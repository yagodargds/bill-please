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
import com.yagodar.billplease.db.BillPleaseDbManager;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bill_please_llv);


		llBillRows = ((LinearLayout) findViewById(R.id.ll_bill_rows));
		if(llBillRows != null) {
			etOnTouchListener = new EtOnTouchListener();
            billPleaseDbManager = new BillPleaseDbManager(this);
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
			billPleaseDbManager.delPersonalBillRecord((Long) button.getTag());
			llBillRows.removeView(llBillRows.findViewWithTag(button.getTag()));
			break;
		case R.id.btn_new_bill:
			billPleaseDbManager.delAllPersonalBillRecords();
			((LinearLayout) findViewById(R.id.ll_bill_rows)).removeAllViews();
			break;
		default:
			break;
		}
	}

    private void addNewBillRow() {
        drawBillRow(billPleaseDbManager.addPersonalBillRecord(),
                getResources().getString(R.string.def_item_name),
                getResources().getString(R.string.draw_def_cost),
                getResources().getString(R.string.draw_def_share));
    }

	private void recoverBill() {
		llBillRows.removeAllViews();

		for (BillPleaseDbManager.PersonalBillRecord billRowDb : billPleaseDbManager.getAllPersonalBillRecords()) {
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
				valueChanged = billPleaseDbManager.isPersonalBillRecordItemNameChanged((Long) v.getTag());
				break;
			case R.id.et_cost:
				valueChanged = billPleaseDbManager.isPersonalBillRecordCostChanged((Long) v.getTag());
				break;
			case R.id.et_share:
				valueChanged = billPleaseDbManager.isPersonalBillRecordShareChanged((Long) v.getTag());
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
                if(!billPleaseDbManager.isPersonalBillRecordItemNameChanged(ownerRowTag)) {
                    billPleaseDbManager.setPersonalBillRecordItemNameChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.def_item_name));
                        owner.setSelection(0);

                        billPleaseDbManager.setPersonalBillRecordItemNameChanged(ownerRowTag, false);
                    }
                    else {
				        billPleaseDbManager.setPersonalBillRecordItemName(ownerRowTag, s.toString());
                    }
                }
				break;
			case R.id.et_cost:
                if(!billPleaseDbManager.isPersonalBillRecordCostChanged(ownerRowTag)) {
                    billPleaseDbManager.setPersonalBillRecordCostChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_cost));
                        owner.setSelection(0);

                        billPleaseDbManager.setPersonalBillRecordCostChanged(ownerRowTag, false);
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

                        billPleaseDbManager.setPersonalBillRecordCost(ownerRowTag, value);
                    }
                }
                break;
			case R.id.et_share:
                if(!billPleaseDbManager.isPersonalBillRecordShareChanged(ownerRowTag)) {
                    billPleaseDbManager.setPersonalBillRecordShareChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_share));
                        owner.setSelection(0);

                        billPleaseDbManager.setPersonalBillRecordShareChanged(ownerRowTag, false);
                    }
                    else {
                        int value = getResources().getInteger(R.integer.def_share);
                        try {
                            value = Integer.parseInt(s.toString());
                        }
                        catch(Exception ignored){}

                        billPleaseDbManager.setPersonalBillRecordShare(ownerRowTag, value);
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

    private BillPleaseDbManager billPleaseDbManager;
}