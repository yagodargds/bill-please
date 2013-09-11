package com.yagodar.android.billplease.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.yagodar.android.billplease.R;
import com.yagodar.android.billplease.custom.BillRecordEditText;
import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillRecordEtChangingContract;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.DbEditText;

import java.util.Timer;
import java.util.TimerTask;

public class BillPleaseActivity extends Activity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(exDbEt != null) {
                hideFocus();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_please_llv);

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_rows));
        if(llBillRecords != null) {
            etHidden = findViewById(R.id.et_hidden);
            dbEtTax = (DbEditText) findViewById(R.id.et_tax);
            dbEtTip = (DbEditText) findViewById(R.id.et_tip);

            billPleaseOnFocusChangeListener = new BillPleaseOnFocusChangeListener();
            etHidden.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
            dbEtTax.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
            dbEtTip.setOnFocusChangeListener(billPleaseOnFocusChangeListener);

            billPleaseTextWatcher = new BillPleaseTextWatcher();
            dbEtTax.addTextChangedListener(billPleaseTextWatcher);
            dbEtTip.addTextChangedListener(billPleaseTextWatcher);

            billPleaseOnTouchListener = new BillPleaseOnTouchListener();
            TypedArray resIds = getResources().obtainTypedArray(R.array.not_et_btn_res_ids);
            int resId;
            for(int i = 0; i < resIds.length(); i++) {
                resId = resIds.getResourceId(i, 0);
                if(resId != 0) {
                    findViewById(resId).setOnTouchListener(billPleaseOnTouchListener);
                }
            }
            resIds.recycle();

            exMotionEvent = NONE_MOTION_EVENT;

            dbBillPleaseManager = DbBillPleaseManager.getInstance(this);
            dbBillPleaseTableBillManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillContract.getInstance());
            dbBillPleaseTableBillRecordEtChangingManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());

            recoverBill();

            hideFocus();

            timer = new Timer();
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
            if(exDbEt != null && button.getTag().equals(exDbEt.getTag())) {
                hideSoftKeyboard(exDbEt);
                hideFocus();
            }

            delBillRecord((Long) button.getTag());
			break;
		case R.id.btn_new_bill:
            if(exDbEt != null) {
                hideSoftKeyboard(exDbEt);
                hideFocus();
            }

            delAllBillRecords();
			break;
		default:
			break;
		}
	}

    private void addBillRecord() {
        long dbRecordId = dbBillPleaseManager.addNewBillRecord();

        if(dbRecordId != -1) {
            drawBillRecord(dbRecordId);
        }
    }

	private void recoverBill() {
		llBillRecords.removeAllViews();

		for (DbTableBaseManager.DbTableRecord dbRecord : dbBillPleaseTableBillManager.getAllRecords()) {
			drawBillRecord(dbRecord.getId());
		}
	}

    private void delBillRecord(long dbRecordId) {
        dbBillPleaseManager.delBillRecord(dbRecordId);
        llBillRecords.removeView(llBillRecords.findViewWithTag(dbRecordId));
    }

    private void delAllBillRecords() {
        dbBillPleaseManager.delAllBillRecords();
        llBillRecords.removeAllViews();
    }

	private void drawBillRecord(long recordId) {
		LinearLayout billRecordLl = (LinearLayout) getLayoutInflater().inflate(R.layout.app_row_llv, null);

		billRecordLl.setTag(recordId);

        BillRecordEditText<String> etItemName = (BillRecordEditText) billRecordLl.findViewById(R.id.et_item_name);
        etItemName.setDbRecordId(recordId);
        etItemName.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME);
        etItemName.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etItemName.pullFromDb();
        etItemName.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etItemName.addTextChangedListener(billPleaseTextWatcher);

        BillRecordEditText<Double> etCost = (BillRecordEditText) billRecordLl.findViewById(R.id.et_cost);
        etCost.setDbRecordId(recordId);
        etCost.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_COST);
        etCost.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etCost.pullFromDb();
        etCost.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etCost.addTextChangedListener(billPleaseTextWatcher);

        BillRecordEditText<Integer> etShare = (BillRecordEditText) billRecordLl.findViewById(R.id.et_share);
        etShare.setDbRecordId(recordId);
        etShare.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_SHARE);
        etShare.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etShare.pullFromDb();
        etShare.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etShare.addTextChangedListener(billPleaseTextWatcher);

		billRecordLl.findViewById(R.id.btn_del_row).setTag(recordId);

		llBillRecords.addView(billRecordLl);
	}

    private void hideSoftKeyboard(View view) {
        if(view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void hideFocus() {
        etHidden.requestFocus();
    }

    private class BillPleaseOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            switch(view.getId()) {
                case R.id.et_item_name:
                case R.id.et_cost:
                case R.id.et_share:
                    if(hasFocus) {
                        llBillRecords.findViewWithTag(view.getTag()).setBackgroundColor(getResources().getColor(R.color.bill_record_picked));

                        if(!((BillRecordEditText) view).isChanged()) {
                            ((BillRecordEditText) view).setText("");
                        }

                        exDbEt = (BillRecordEditText) view;
                    }
                    else {
                        timer.cancel();
                        ((BillRecordEditText) view).pushToDb();
                        ((BillRecordEditText) view).pullFromDb();
                        ((BillRecordEditText) view).resetInputRegistered();
                        llBillRecords.findViewWithTag(view.getTag()).setBackgroundColor(getResources().getColor(R.color.bill_record_not_picked));
                    }
                    break;
                case R.id.et_tax:
                case R.id.et_tip:
                    if(hasFocus) {
                        exDbEt = (DbEditText) view;
                    }
                    else {
                        timer.cancel();
                        ((DbEditText) view).pushToDb();
                        ((DbEditText) view).pullFromDb();
                        ((DbEditText) view).resetInputRegistered();
                    }
                    break;
                case R.id.et_hidden:
                    if(hasFocus) {
                        hideSoftKeyboard(exDbEt);
                        exDbEt = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class BillPleaseOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch(view.getId()) {
                case R.id.sv_bill_rows:
                    if(exMotionEvent == MotionEvent.ACTION_DOWN && event.getAction() == MotionEvent.ACTION_UP) {
                        hideFocus();
                    }
                    break;
                default:
                    hideFocus();
                    break;
            }

            exMotionEvent = event.getAction();

            return false;
        }
    }

    private class BillPleaseTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            timer.cancel();
            timer = new Timer();

            try {
                timer.schedule(new PushToDbTimerTask(), getResources().getInteger(R.integer.push_to_db_delay_millisecs));
            }
            catch(Exception ignored) {}
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class PushToDbTimerTask extends TimerTask {
        @Override
        public void run() {
            if(exDbEt != null) {
                exDbEt.pushToDb();
            }
        }
    }

	private LinearLayout llBillRecords;
    private DbEditText exDbEt;
    private View etHidden;
    private DbEditText dbEtTax;
    private DbEditText dbEtTip;
    private int exMotionEvent;
    private BillPleaseOnFocusChangeListener billPleaseOnFocusChangeListener;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;
    private BillPleaseTextWatcher billPleaseTextWatcher;
    private DbBillPleaseManager dbBillPleaseManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillRecordEtChangingManager;
    private Timer timer;

    private static final int NONE_MOTION_EVENT = -1;
}