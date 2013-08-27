package com.yagodar.android.billplease.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.yagodar.android.billplease.R;
import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillManager;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bill_please_llv);

		llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_rows));
		if(llBillRecords != null) {
			billPleaseOnTouchListener = new BillPleaseOnTouchListener();
            dbBillPleaseTableBillManager = (DbBillPleaseTableBillManager) DbBillPleaseManager.getInstance(this).getDbTableManager(DbBillPleaseTableBillContract.getInstance().getTableName());
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
			addBillRecord();
			break;
		case R.id.btn_del_row:
            if(lastEtOfPickedBillRecord != null && button.getTag().equals(lastEtOfPickedBillRecord.getTag())) {
                hideSoftKeyboard(lastEtOfPickedBillRecord);
                lastEtOfPickedBillRecord = null;
            }

			dbBillPleaseTableBillManager.delRecord((Long) button.getTag());
			llBillRecords.removeView(llBillRecords.findViewWithTag(button.getTag()));
			break;
		case R.id.btn_new_bill:
            hideSoftKeyboard(lastEtOfPickedBillRecord);
            lastEtOfPickedBillRecord = null;

			dbBillPleaseTableBillManager.delAllRecords();
			((LinearLayout) findViewById(R.id.ll_bill_rows)).removeAllViews();
			break;
		default:
			break;
		}
	}

    private void addBillRecord() {
        drawBillRecord(dbBillPleaseTableBillManager.addRecord(),
                getResources().getString(R.string.def_item_name),
                getResources().getString(R.string.draw_def_cost),
                getResources().getString(R.string.draw_def_share));
    }

	private void recoverBill() {
		llBillRecords.removeAllViews();

		for (DbBillPleaseTableBillManager.BillRecord billRowDb : dbBillPleaseTableBillManager.getAllRecords()) {
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

			drawBillRecord(billRowDb.getTag(), itemName, costStr, shareStr);
		}
	}

	private void drawBillRecord(long rowTag, String itemName, String cost, String share) {
		LinearLayout billRowLl = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);

		billRowLl.setTag(rowTag);

		EditText etItem = (EditText) billRowLl.findViewById(R.id.et_item);
		etItem.setText(itemName);
		etItem.setTag(rowTag);
		etItem.setOnTouchListener(billPleaseOnTouchListener);
		etItem.addTextChangedListener(new EtTextWatcher(etItem));

		EditText etCost = (EditText) billRowLl.findViewById(R.id.et_cost);
		etCost.setText(cost);
		etCost.setTag(rowTag);
		etCost.setOnTouchListener(billPleaseOnTouchListener);
		etCost.addTextChangedListener(new EtTextWatcher(etCost));

		EditText etShare = (EditText) billRowLl.findViewById(R.id.et_share);
		etShare.setText(share);
		etShare.setTag(rowTag);
		etShare.setOnTouchListener(billPleaseOnTouchListener);
		etShare.addTextChangedListener(new EtTextWatcher(etShare));

		billRowLl.findViewById(R.id.btn_del_row).setTag(rowTag);

		llBillRecords.addView(billRowLl);
	}

    private void showSoftKeyboard(View view) {
        if(view != null && view.requestFocus()) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftKeyboard(View view) {
        if(view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private class BillPleaseOnTouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean isBillRecordValueChanged = false;
            boolean setBillRecordPicked = false;

            switch(v.getId()) {
                case R.id.et_item:
                    isBillRecordValueChanged = dbBillPleaseTableBillManager.isItemNameChanged((Long) v.getTag());
                    setBillRecordPicked = true;
                    break;
                case R.id.et_cost:
                    isBillRecordValueChanged = dbBillPleaseTableBillManager.isCostChanged((Long) v.getTag());
                    setBillRecordPicked = true;
                    break;
                case R.id.et_share:
                    isBillRecordValueChanged = dbBillPleaseTableBillManager.isShareChanged((Long) v.getTag());
                    setBillRecordPicked = true;
                    break;
                default:
                    break;
            }

            switch(event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if(setBillRecordPicked) {
                        if(lastEtOfPickedBillRecord != null) {
                            llBillRecords.findViewWithTag(lastEtOfPickedBillRecord.getTag()).setBackgroundColor(getResources().getColor(R.color.bill_record_not_picked));
                        }

                        lastEtOfPickedBillRecord = v;
                        llBillRecords.findViewWithTag(v.getTag()).setBackgroundColor(getResources().getColor(R.color.bill_record_picked));
                    }

                    if(!isBillRecordValueChanged) {
                        showSoftKeyboard(v);
                        ((EditText)v).setSelection(0);
                    }

                    break;
                default:
                    break;
            }

            return !isBillRecordValueChanged;
        }
    }

	private class EtTextWatcher implements TextWatcher {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			switch(owner.getId()) {
			case R.id.et_item:
                if(!dbBillPleaseTableBillManager.isItemNameChanged(ownerRowTag)) {
                    dbBillPleaseTableBillManager.setItemNameChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.def_item_name));
                        owner.setSelection(0);

                        dbBillPleaseTableBillManager.setItemNameChanged(ownerRowTag, false);
                    }
                    else {
				        dbBillPleaseTableBillManager.setItemName(ownerRowTag, s.toString());
                    }
                }
				break;
			case R.id.et_cost:
                if(!dbBillPleaseTableBillManager.isCostChanged(ownerRowTag)) {
                    dbBillPleaseTableBillManager.setCostChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_cost));
                        owner.setSelection(0);

                        dbBillPleaseTableBillManager.setCostChanged(ownerRowTag, false);
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

                        dbBillPleaseTableBillManager.setCost(ownerRowTag, value);
                    }
                }
                break;
			case R.id.et_share:
                if(!dbBillPleaseTableBillManager.isShareChanged(ownerRowTag)) {
                    dbBillPleaseTableBillManager.setShareChanged(ownerRowTag, true);

                    owner.setText(s.toString().substring(start, count));
                    owner.setSelection(count);
                }
                else {
                    if(s.length() == 0) {
                        owner.setText(getResources().getString(R.string.draw_def_share));
                        owner.setSelection(0);

                        dbBillPleaseTableBillManager.setShareChanged(ownerRowTag, false);
                    }
                    else {
                        int value = getResources().getInteger(R.integer.def_share);
                        try {
                            value = Integer.parseInt(s.toString());
                        }
                        catch(Exception ignored){}

                        dbBillPleaseTableBillManager.setShare(ownerRowTag, value);
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

	private LinearLayout llBillRecords;
    private View lastEtOfPickedBillRecord;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;

    private DbBillPleaseTableBillManager dbBillPleaseTableBillManager;
}