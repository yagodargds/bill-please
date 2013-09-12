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
import android.widget.TextView;

import com.yagodar.android.billplease.R;
import com.yagodar.android.billplease.custom.BillRecordEditText;
import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillRecordEtChangingContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillTaxTipContract;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.DbEditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

        try {
            timer = new Timer();

            decimalFormat = new DecimalFormat();
            decimalFormat.setMinimumFractionDigits(getResources().getInteger(R.integer.min_fraction_digits));
            decimalFormat.setMaximumFractionDigits(getResources().getInteger(R.integer.max_fraction_digits));

            DecimalFormatSymbols custom = new DecimalFormatSymbols();
            custom.setDecimalSeparator('.');
            decimalFormat.setDecimalFormatSymbols(custom);

            exMotionEvent = NONE_MOTION_EVENT;

            billPleaseOnFocusChangeListener = new BillPleaseOnFocusChangeListener();
            billPleaseOnTouchListener = new BillPleaseOnTouchListener();
            billPleaseTextWatcher = new BillPleaseTextWatcher();

            dbBillPleaseManager = DbBillPleaseManager.getInstance(this);
            dbBillPleaseTableBillManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillContract.getInstance());
            dbBillPleaseTableBillRecordEtChangingManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());
            dbBillPleaseTableBillTaxTipManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillTaxTipContract.getInstance());

            loadBill();

            hideFocus();

            redrawAllSums();
        }
        catch (Exception ignored) {
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

	private void loadBill() {
        etHidden = findViewById(R.id.et_hidden);
        etHidden.setOnFocusChangeListener(billPleaseOnFocusChangeListener);

        TypedArray resIds = getResources().obtainTypedArray(R.array.not_et_btn_res_ids);
        int resId;
        for(int i = 0; i < resIds.length(); i++) {
            resId = resIds.getResourceId(i, 0);
            if(resId != 0) {
                findViewById(resId).setOnTouchListener(billPleaseOnTouchListener);
            }
        }
        resIds.recycle();

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_rows));

		llBillRecords.removeAllViews();

		for (DbTableBaseManager.DbTableRecord dbRecord : dbBillPleaseTableBillManager.getAllRecords()) {
			drawBillRecord(dbRecord.getId());
		}

        boolean isTaxTipNew = false;
        long taxTipRecordId;
        if(dbBillPleaseTableBillTaxTipManager.getAllRecords().size() == 0) {
            taxTipRecordId = dbBillPleaseTableBillTaxTipManager.addRecord();
            isTaxTipNew = true;
        }
        else {
            taxTipRecordId = dbBillPleaseTableBillTaxTipManager.getAllRecords().iterator().next().getId();
        }

        DbEditText<Double> dbEtTax = (DbEditText) findViewById(R.id.et_tax);
        dbEtTax.setDbRecordId(taxTipRecordId);
        dbEtTax.initDbManagerBase(dbBillPleaseTableBillTaxTipManager, DbBillPleaseTableBillTaxTipContract.COLUMN_NAME_TAX);
        if(isTaxTipNew) {
            dbEtTax.setDbValue(Double.parseDouble(getResources().getString(R.string.def_tax_double)));
        }
        dbEtTax.pullFromDb();
        dbEtTax.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTax.addTextChangedListener(billPleaseTextWatcher);

        DbEditText<Double> dbEtTip = (DbEditText) findViewById(R.id.et_tip);
        dbEtTip.setDbRecordId(taxTipRecordId);
        dbEtTip.initDbManagerBase(dbBillPleaseTableBillTaxTipManager, DbBillPleaseTableBillTaxTipContract.COLUMN_NAME_TIP);
        if(isTaxTipNew) {
            dbEtTax.setDbValue(Double.parseDouble(getResources().getString(R.string.def_tip_double)));
        }
        dbEtTip.pullFromDb();
        dbEtTip.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTip.addTextChangedListener(billPleaseTextWatcher);
	}

    private void delBillRecord(long dbRecordId) {
        dbBillPleaseManager.delBillRecord(dbRecordId);
        llBillRecords.removeView(llBillRecords.findViewWithTag(dbRecordId));
        redrawAllSums();
    }

    private void delAllBillRecords() {
        dbBillPleaseManager.delAllBillRecords();
        llBillRecords.removeAllViews();
        redrawAllSums();
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

    private void redrawAllSums() {
        double subtotalSum = calcSubtotalSum();
        ((TextView) findViewById(R.id.tv_subtotal_sum)).setText(String.valueOf(decimalFormat.format(subtotalSum)));

        double taxSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tax)).getDbValue() / 100.0 );
        ((TextView) findViewById(R.id.tv_tax_sum)).setText(String.valueOf(decimalFormat.format(taxSum)));

        double tipSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tip)).getDbValue() / 100.0 );
        ((TextView) findViewById(R.id.tv_tip_sum)).setText(String.valueOf(decimalFormat.format(tipSum)));

        ((TextView) findViewById(R.id.tv_total_sum)).setText(String.valueOf(decimalFormat.format(subtotalSum + taxSum + tipSum)));
    }

    private void redrawTaxTipSums() {
        double subtotalSum = calcSubtotalSum();

        double taxSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tax)).getDbValue() / 100.0 );
        ((TextView) findViewById(R.id.tv_tax_sum)).setText(String.valueOf(decimalFormat.format(taxSum)));

        double tipSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tip)).getDbValue() / 100.0 );
        ((TextView) findViewById(R.id.tv_tip_sum)).setText(String.valueOf(decimalFormat.format(tipSum)));

        ((TextView) findViewById(R.id.tv_total_sum)).setText(String.valueOf(decimalFormat.format(subtotalSum + taxSum + tipSum)));
    }

    private double calcSubtotalSum() {
        double value = 0.0;

        try {
            LinearLayout billRecordLl;
            for(int i = 0; i < llBillRecords.getChildCount(); i++) {
                billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                value += ((BillRecordEditText<Double>) billRecordLl.findViewById(R.id.et_cost)).getDbValue() / (double) ((BillRecordEditText<Integer>) billRecordLl.findViewById(R.id.et_share)).getDbValue();
            }


        }
        catch(Exception ignored) {}

        return value;
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

                        if(view.getId() == R.id.et_cost) {
                            double defCost = Double.parseDouble(getResources().getString(R.string.min_cost_double));
                            if(((BillRecordEditText<Double>) view).getDbValue() < defCost) {
                                ((BillRecordEditText<Double>) view).setDbValue(defCost);
                            }
                        }
                        else if(view.getId() == R.id.et_share) {
                            int defShare = getResources().getInteger(R.integer.min_share);
                            if(((BillRecordEditText<Integer>) view).getDbValue() < defShare) {
                                ((BillRecordEditText<Integer>) view).setDbValue(defShare);
                            }
                        }

                        ((BillRecordEditText) view).pullFromDb();
                        ((BillRecordEditText) view).resetInputRegistered();

                        timer.cancel();

                        llBillRecords.findViewWithTag(view.getTag()).setBackgroundColor(getResources().getColor(R.color.bill_record_not_picked));

                        redrawAllSums();
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

                        timer.cancel();

                        redrawTaxTipSums();
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
    private int exMotionEvent;
    private BillPleaseOnFocusChangeListener billPleaseOnFocusChangeListener;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;
    private BillPleaseTextWatcher billPleaseTextWatcher;
    private DbBillPleaseManager dbBillPleaseManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillRecordEtChangingManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillTaxTipManager;
    private Timer timer;
    private DecimalFormat decimalFormat;

    private static final int NONE_MOTION_EVENT = -1;
}