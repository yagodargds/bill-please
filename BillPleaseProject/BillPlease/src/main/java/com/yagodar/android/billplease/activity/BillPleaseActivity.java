package com.yagodar.android.billplease.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yagodar.android.billplease.R;
import com.yagodar.android.billplease.custom.BillRecordDbEditText;
import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillRecordEtChangingContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillValuesContract;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.DbEditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Timer;
import java.util.TimerTask;

public class BillPleaseActivity extends FragmentActivity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(curDbEt != null) {
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

        timer = new Timer();

        decimalFormat = new DecimalFormat();
        decimalFormat.setMinimumFractionDigits(getResources().getInteger(R.integer.min_fraction_digits));
        decimalFormat.setMaximumFractionDigits(getResources().getInteger(R.integer.max_fraction_digits));
        decimalFormat.setGroupingUsed(false);

        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(custom);

        exMotionEvent = NONE_MOTION_EVENT;

        billPleaseOnFocusChangeListener = new BillPleaseOnFocusChangeListener();
        billPleaseOnTouchListener = new BillPleaseOnTouchListener();
        billPleaseTextWatcher = new BillPleaseTextWatcher();
        billPleaseOnEditorActionListener = new BillPleaseOnEditorActionListener();

        createNewBillDialogFragment = new CreateNewBillDialogFragment();

        dbBillPleaseManager = DbBillPleaseManager.getInstance(this);
        dbBillPleaseTableBillManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillContract.getInstance());
        dbBillPleaseTableBillRecordEtChangingManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());
        dbBillPleaseTableBillValuesManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillValuesContract.getInstance());

        loadBill();

        redrawAllSums();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //Skipped. Not in need. Everything is redraws in onCreate(). P.S. If use it, may be bugs with draw EditText views.
    }

    public void onButtonClick(View button) {
        switch(button.getId()) {
            case R.id.btn_add_new_bill_record:
                addBillRecord();

                if(curDbEt != null && curDbEt instanceof BillRecordDbEditText) {
                    llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_del_bill_record:
                if(curDbEt != null && curDbEt instanceof BillRecordDbEditText) {
                    delBillRecord(curDbEt.getDbRecordId());
                }
                break;
            case R.id.btn_create_new_bill:
                hideFocus();
                createNewBillDialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.btn_lbl_create_new_bill));
                break;
            case R.id.btn_share_bill:
                hideFocus();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                sendIntent.setType(INTENT_TYPE_SHARE);
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.btn_lbl_share_bill)));
                break;
            default:
                break;
        }
    }

    private String getShareText() {
        String shareText = "";

        //app name tag
        shareText += "#" + getResources().getString(R.string.app_name);
        shareText += "\n";

        //records
        shareText += "[" + getResources().getString(R.string.lbl_item_name) + "]";
        shareText += "\n";
        if(llBillRecords.getChildCount() > 1) {
            LinearLayout billRecordLl;
            double cost;
            int share;
            for(int i = 0; i < llBillRecords.getChildCount() - 1; i++) {
                try {
                    billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                    shareText += ((EditText) billRecordLl.findViewById(R.id.et_item_name)).getText();
                    cost = ((BillRecordDbEditText<Double>) billRecordLl.findViewById(R.id.et_cost)).getDbValue();
                    share = ((BillRecordDbEditText<Integer>) billRecordLl.findViewById(R.id.et_share)).getDbValue();
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_cost) + ":" + decimalFormat.format(cost);
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_share) + ":" + share;
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + decimalFormat.format(cost / (double) share);
                    shareText += "\n";
                }
                catch(Exception ignored) {}
            }
        }
        else {
            shareText += getResources().getString(R.string.share_no_items);
            shareText += "\n";
        }

        //subtotal
        shareText += "[" + getResources().getString(R.string.lbl_subtotal) + "]" + ":" + ((TextView) findViewById(R.id.tv_subtotal_sum)).getText();
        shareText += "\n";

        //tax
        shareText += "[" + getResources().getString(R.string.lbl_tax) + "]" + ":" + ((EditText) findViewById(R.id.et_tax)).getText();
        shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + ((TextView) findViewById(R.id.et_tax_sum)).getText();
        shareText += "\n";

        //tip
        shareText += "[" + getResources().getString(R.string.lbl_tip) + "]" + ":" + ((EditText) findViewById(R.id.et_tip)).getText();
        shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + ((TextView) findViewById(R.id.et_tip_sum)).getText();
        shareText += "\n";

        //total
        shareText += "[" + getResources().getString(R.string.lbl_total) + "]" + ":" + ((TextView) findViewById(R.id.tv_total_sum)).getText();

        return shareText;
    }

	private void loadBill() {
        llActionBar = ((LinearLayout) findViewById(R.id.ll_action_bar_icons));

        etHidden = findViewById(R.id.et_hidden);
        etHidden.setOnFocusChangeListener(billPleaseOnFocusChangeListener);

        TypedArray resIds = getResources().obtainTypedArray(R.array.apply_touch_listener_res_ids);
        int resId;
        for(int i = 0; i < resIds.length(); i++) {
            resId = resIds.getResourceId(i, 0);
            if(resId != 0) {
                findViewById(resId).setOnTouchListener(billPleaseOnTouchListener);
            }
        }
        resIds.recycle();

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_records));

		llBillRecords.removeAllViews();

		for (DbTableBaseManager.DbTableRecord dbRecord : dbBillPleaseTableBillManager.getAllRecords()) {
			drawBillRecord(dbRecord.getId());
		}

        boolean isValuesNew = false;
        long valuesRecordId;
        if(dbBillPleaseTableBillValuesManager.getAllRecords().size() == 0) {
            valuesRecordId = dbBillPleaseTableBillValuesManager.addRecord();
            isValuesNew = true;
        }
        else {
            valuesRecordId = dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId();
        }

        DbEditText<Double> dbEtTax = (DbEditText) findViewById(R.id.et_tax);
        dbEtTax.setDbRecordId(valuesRecordId);
        dbEtTax.initDbManagerBase(dbBillPleaseTableBillValuesManager, DbBillPleaseTableBillValuesContract.COLUMN_NAME_TAX_PER);
        if(isValuesNew) {
            dbEtTax.setDbValue(Double.parseDouble(getResources().getString(R.string.def_tax_double)));
        }
        dbEtTax.pullFromDb();
        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            postSetBackgroundResource(findViewById(R.id.tv_tax_main), R.drawable.check_box_checked_rect);
        }
        dbEtTax.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTax.addTextChangedListener(billPleaseTextWatcher);
        dbEtTax.setOnEditorActionListener(billPleaseOnEditorActionListener);

        DbEditText<Double> dbEtTaxSum = (DbEditText) findViewById(R.id.et_tax_sum);
        dbEtTaxSum.setDbRecordId(valuesRecordId);
        dbEtTaxSum.initDbManagerBase(dbBillPleaseTableBillValuesManager, DbBillPleaseTableBillValuesContract.COLUMN_NAME_TAX_SUM);
        dbEtTaxSum.pullFromDb();
        if(!(Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            postSetBackgroundResource(findViewById(R.id.tv_tax_sum_main), R.drawable.check_box_checked_rect);
        }
        dbEtTaxSum.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTaxSum.addTextChangedListener(billPleaseTextWatcher);
        dbEtTaxSum.setOnEditorActionListener(billPleaseOnEditorActionListener);

        DbEditText<Double> dbEtTip = (DbEditText) findViewById(R.id.et_tip);
        dbEtTip.setDbRecordId(valuesRecordId);
        dbEtTip.initDbManagerBase(dbBillPleaseTableBillValuesManager, DbBillPleaseTableBillValuesContract.COLUMN_NAME_TIP_PER);
        if(isValuesNew) {
            dbEtTip.setDbValue(Double.parseDouble(getResources().getString(R.string.def_tip_double)));
        }
        dbEtTip.pullFromDb();
        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
            postSetBackgroundResource(findViewById(R.id.tv_tip_main), R.drawable.check_box_checked_rect);
        }
        dbEtTip.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTip.addTextChangedListener(billPleaseTextWatcher);
        dbEtTip.setOnEditorActionListener(billPleaseOnEditorActionListener);

        DbEditText<Double> dbEtTipSum = (DbEditText) findViewById(R.id.et_tip_sum);
        dbEtTipSum.setDbRecordId(valuesRecordId);
        dbEtTipSum.initDbManagerBase(dbBillPleaseTableBillValuesManager, DbBillPleaseTableBillValuesContract.COLUMN_NAME_TIP_SUM);
        dbEtTipSum.pullFromDb();
        if(!(Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
            postSetBackgroundResource(findViewById(R.id.tv_tip_sum_main), R.drawable.check_box_checked_rect);
        }
        dbEtTipSum.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTipSum.addTextChangedListener(billPleaseTextWatcher);
        dbEtTipSum.setOnEditorActionListener(billPleaseOnEditorActionListener);

        dbEtTax.setNextFocusView(View.FOCUS_FORWARD, dbEtTaxSum);

        dbEtTaxSum.setNextFocusView(View.FOCUS_BACKWARD, dbEtTax);
        dbEtTaxSum.setNextFocusView(View.FOCUS_FORWARD, dbEtTip);

        dbEtTip.setNextFocusView(View.FOCUS_BACKWARD, dbEtTaxSum);
        dbEtTip.setNextFocusView(View.FOCUS_FORWARD, dbEtTipSum);

        dbEtTipSum.setNextFocusView(View.FOCUS_BACKWARD, dbEtTip);

        if(llBillRecords.getChildCount() == 0) {
            createNewBillDialogFragment.show(getSupportFragmentManager(), getResources().getString(R.string.btn_lbl_create_new_bill));
        }
        else {
            findViewById(R.id.btn_add_new_bill_record).setVisibility(View.VISIBLE);
        }
	}

    private void addBillRecord() {
        long dbRecordId = dbBillPleaseManager.addNewBillRecord();

        if(dbRecordId != -1) {
            int defShare = (Integer) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_DEF_SHARE);
            if(defShare > getResources().getInteger(R.integer.min_share)) {
                dbBillPleaseTableBillManager.setColumnValue(dbRecordId, DbBillPleaseTableBillContract.COLUMN_NAME_SHARE, defShare);
                dbBillPleaseTableBillRecordEtChangingManager.setColumnValue(dbRecordId * R.id.et_share, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED, true);
            }

            drawBillRecord(dbRecordId);
        }
    }

    private void delBillRecord(long dbRecordId) {
        dbBillPleaseManager.delBillRecord(dbRecordId);

        View billRecordLlToDel = llBillRecords.findViewWithTag(dbRecordId);

        BillRecordDbEditText share = (BillRecordDbEditText) billRecordLlToDel.findViewById(R.id.et_share);
        BillRecordDbEditText itemName = (BillRecordDbEditText) billRecordLlToDel.findViewById(R.id.et_item_name);

        BillRecordDbEditText exEtShare = (BillRecordDbEditText) itemName.getNextFocusView(View.FOCUS_BACKWARD);
        final BillRecordDbEditText postEtItemName = (BillRecordDbEditText) share.getNextFocusView(View.FOCUS_FORWARD);

        if(postEtItemName != null) {
            postEtItemName.setNextFocusView(View.FOCUS_BACKWARD, exEtShare);
            postRequestFocus(postEtItemName);
        }

        if(exEtShare != null) {
            exEtShare.setNextFocusView(View.FOCUS_FORWARD, postEtItemName);
        }

        llBillRecords.removeView(billRecordLlToDel);

        redrawAllSums();
    }

    private void createNewBill() {
        dbBillPleaseManager.delAllBillRecords();
        llBillRecords.removeAllViews();
        addBillRecord();
        findViewById(R.id.btn_add_new_bill_record).setVisibility(View.VISIBLE);
        redrawAllSums();
    }

	private void drawBillRecord(long recordId) {
		LinearLayout billRecordLl = (LinearLayout) getLayoutInflater().inflate(R.layout.bill_record_llv, null);

		billRecordLl.setTag(recordId);

        BillRecordDbEditText<String> etItemName = (BillRecordDbEditText) billRecordLl.findViewById(R.id.et_item_name);
        etItemName.setDbRecordId(recordId);
        etItemName.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME);
        etItemName.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etItemName.pullFromDb();
        etItemName.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etItemName.addTextChangedListener(billPleaseTextWatcher);
        etItemName.setOnEditorActionListener(billPleaseOnEditorActionListener);

        BillRecordDbEditText<Double> etCost = (BillRecordDbEditText) billRecordLl.findViewById(R.id.et_cost);
        etCost.setDbRecordId(recordId);
        etCost.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_COST);
        etCost.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etCost.pullFromDb();
        etCost.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etCost.addTextChangedListener(billPleaseTextWatcher);
        etCost.setOnEditorActionListener(billPleaseOnEditorActionListener);

        BillRecordDbEditText<Integer> etShare = (BillRecordDbEditText) billRecordLl.findViewById(R.id.et_share);
        etShare.setDbRecordId(recordId);
        etShare.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_SHARE);
        etShare.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etShare.pullFromDb();
        etShare.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etShare.addTextChangedListener(billPleaseTextWatcher);
        etShare.setOnEditorActionListener(billPleaseOnEditorActionListener);

        llBillRecords.addView(billRecordLl);

        if(llBillRecords.getChildCount() > 1) {
            BillRecordDbEditText<Integer> exEtShare = (BillRecordDbEditText) llBillRecords.getChildAt(llBillRecords.getChildCount() - 2).findViewById(R.id.et_share);

            exEtShare.setNextFocusView(View.FOCUS_FORWARD, etItemName);

            etItemName.setNextFocusView(View.FOCUS_BACKWARD, exEtShare);
        }

        etItemName.setNextFocusView(View.FOCUS_FORWARD, etCost);

        etCost.setNextFocusView(View.FOCUS_BACKWARD, etItemName);
        etCost.setNextFocusView(View.FOCUS_FORWARD, etShare);

        etShare.setNextFocusView(View.FOCUS_BACKWARD, etCost);
	}

    private void hideSoftKeyboard(View view) {
        if(view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void hideFocus() {
        postRequestFocus(etHidden);
    }

    private void redrawAllSums() {
        double subtotalSum = calcSubtotalSum();
        postSetText(((TextView) findViewById(R.id.tv_subtotal_sum)), decimalFormat.format(subtotalSum));

        redrawTaxTipSums(subtotalSum);
    }

    private void redrawTaxTipSums(double subtotalSum) {
        DbEditText<Double> etTax = ((DbEditText<Double>) findViewById(R.id.et_tax));
        DbEditText<Double> etTaxSum = ((DbEditText<Double>) findViewById(R.id.et_tax_sum));
        double taxSum = 0.0;

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            taxSum = subtotalSum * (etTax.getDbValue() / 100.0);

            if(!etTax.equals(curDbEt)) {
                etTax.pullFromDb();
                etTax.resetInputRegistered();
                timer.cancel();
            }

            if(!etTaxSum.equals(curDbEt)) {
                etTaxSum.setDbValue(taxSum);
                etTaxSum.pullFromDb();
                etTaxSum.resetInputRegistered();
                timer.cancel();
            }
        }
        else {
            taxSum = etTaxSum.getDbValue();

            if(!etTaxSum.equals(curDbEt)) {
                etTaxSum.pullFromDb();
                etTaxSum.resetInputRegistered();
                timer.cancel();
            }

            if(!etTax.equals(curDbEt)) {
                double taxPer = 0.0;
                if(subtotalSum > 0.0) {
                    taxPer = (taxSum * 100.0) / subtotalSum;
                }

                etTax.setDbValue(taxPer);
                etTax.pullFromDb();
                etTax.resetInputRegistered();
                timer.cancel();
            }
        }

        DbEditText<Double> etTip = ((DbEditText<Double>) findViewById(R.id.et_tip));
        DbEditText<Double> etTipSum = ((DbEditText<Double>) findViewById(R.id.et_tip_sum));
        double tipSum = 0.0;

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
            tipSum = subtotalSum * (etTip.getDbValue() / 100.0);

            if(!etTip.equals(curDbEt)) {
                etTip.pullFromDb();
                etTip.resetInputRegistered();
                timer.cancel();
            }

            if(!etTipSum.equals(curDbEt)) {
                etTipSum.setDbValue(tipSum);
                etTipSum.pullFromDb();
                etTipSum.resetInputRegistered();
                timer.cancel();
            }
        }
        else {
            tipSum = etTipSum.getDbValue();

            if(!etTipSum.equals(curDbEt)) {
                etTipSum.pullFromDb();
                etTipSum.resetInputRegistered();
                timer.cancel();
            }

            if(!etTip.equals(curDbEt)) {
                double tipPer = 0.0;
                if(subtotalSum > 0.0) {
                    tipPer = (tipSum * 100.0) / subtotalSum;
                }

                etTip.setDbValue(tipPer);
                etTip.pullFromDb();
                etTip.resetInputRegistered();
                timer.cancel();
            }
        }

        postSetText(((TextView) findViewById(R.id.tv_total_sum)), decimalFormat.format(subtotalSum + taxSum + tipSum));
    }

    private double calcSubtotalSum() {
        double value = 0.0;

        LinearLayout billRecordLl;
        for(int i = 0; i < llBillRecords.getChildCount(); i++) {
            try {
                billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                value += ((BillRecordDbEditText<Double>) billRecordLl.findViewById(R.id.et_cost)).getDbValue() / (double) ((BillRecordDbEditText<Integer>) billRecordLl.findViewById(R.id.et_share)).getDbValue();
            }
            catch(Exception ignored) {}
        }

        return value;
    }

    private void postSetText(final TextView textView, final String text) {
        try {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(text);
                }
            });
        }
        catch(Exception ignored) {}
    }

    private void postRequestFocus(final View view) {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.requestFocus();
                }
            });
        }
        catch(Exception ignored) {}
    }

    private void postSetBackgroundColor(final View view, final int color) {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundColor(color);
                }
            });
        }
        catch(Exception ignored) {}
    }

    private void postSetBackgroundResource(final View view, final int resId) {
        try {
            view.post(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundResource(resId);
                }
            });
        }
        catch(Exception ignored) {}
    }

    private class BillPleaseOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            switch(view.getId()) {
                case R.id.et_item_name:
                case R.id.et_cost:
                case R.id.et_share:
                    if(hasFocus) {
                        postSetBackgroundColor(llBillRecords.findViewWithTag(view.getTag()), getResources().getColor(R.color.bill_record_picked));

                        if(llBillRecords.indexOfChild(llBillRecords.findViewWithTag(view.getTag())) != llBillRecords.getChildCount() - 1) {
                            llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.VISIBLE);
                        }
                        else {
                            llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.GONE);
                        }

                        curDbEt = (BillRecordDbEditText) view;
                    }
                    else {
                        timer.cancel();

                        ((BillRecordDbEditText) view).pushToDb();

                        if(view.getId() == R.id.et_cost) {
                            double defCost = Double.parseDouble(getResources().getString(R.string.min_cost_double));
                            if(((BillRecordDbEditText<Double>) view).getDbValue() < defCost) {
                                ((BillRecordDbEditText<Double>) view).setDbValue(defCost);
                            }
                        }
                        else if(view.getId() == R.id.et_share) {
                            int defShare = getResources().getInteger(R.integer.min_share);
                            if(((BillRecordDbEditText<Integer>) view).getDbValue() < defShare) {
                                ((BillRecordDbEditText<Integer>) view).setDbValue(defShare);
                            }
                        }

                        ((BillRecordDbEditText) view).pullFromDb();
                        ((BillRecordDbEditText) view).resetInputRegistered();

                        timer.cancel();

                        postSetBackgroundColor(llBillRecords.findViewWithTag(view.getTag()), getResources().getColor(R.color.bill_record_not_picked));

                        if(llBillRecords.findViewWithTag(view.getTag()).equals(llBillRecords.getChildAt(llBillRecords.getChildCount() - 1)) && ((BillRecordDbEditText) view).isChanged()) {
                            addBillRecord();
                        }

                        llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.GONE);

                        redrawAllSums();
                    }
                    break;
                case R.id.et_tax:
                case R.id.et_tax_sum:
                case R.id.et_tip:
                case R.id.et_tip_sum:
                    if(hasFocus) {
                        curDbEt = (DbEditText) view;
                    }
                    else {
                        timer.cancel();

                        ((DbEditText) view).pushToDb();
                        ((DbEditText) view).pullFromDb();
                        ((DbEditText) view).resetInputRegistered();

                        timer.cancel();

                        redrawTaxTipSums(calcSubtotalSum());
                    }
                    break;
                case R.id.et_hidden:
                    if(hasFocus) {
                        hideSoftKeyboard(curDbEt);
                        curDbEt = null;
                        redrawTaxTipSums(calcSubtotalSum());
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
                case R.id.tv_tax_main:
                    postSetBackgroundResource(view, R.drawable.check_box_checked_rect);
                    postSetBackgroundResource(findViewById(R.id.tv_tax_sum_main), R.drawable.check_box_not_checked_rect);
                    dbBillPleaseTableBillValuesManager.setColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN, true);
                    break;
                case R.id.tv_tax_sum_main:
                    postSetBackgroundResource(view, R.drawable.check_box_checked_rect);
                    postSetBackgroundResource(findViewById(R.id.tv_tax_main), R.drawable.check_box_not_checked_rect);
                    dbBillPleaseTableBillValuesManager.setColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN, false);
                    break;
                case R.id.tv_tip_main:
                    postSetBackgroundResource(view, R.drawable.check_box_checked_rect);
                    postSetBackgroundResource(findViewById(R.id.tv_tip_sum_main), R.drawable.check_box_not_checked_rect);
                    dbBillPleaseTableBillValuesManager.setColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN, true);
                    break;
                case R.id.tv_tip_sum_main:
                    postSetBackgroundResource(view, R.drawable.check_box_checked_rect);
                    postSetBackgroundResource(findViewById(R.id.tv_tip_main), R.drawable.check_box_not_checked_rect);
                    dbBillPleaseTableBillValuesManager.setColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN, false);
                    break;
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

    private class BillPleaseOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                View nextFocusView = null;

                switch(textView.getId()) {
                    case R.id.et_item_name:
                    case R.id.et_cost:
                    case R.id.et_share:
                    case R.id.et_tip:
                    case R.id.et_tip_sum:
                    case R.id.et_tax:
                    case R.id.et_tax_sum:
                        nextFocusView = ((DbEditText) textView).getNextFocusView(View.FOCUS_FORWARD);
                        break;
                    default:
                        break;
                }

                if(nextFocusView != null) {
                    postRequestFocus(nextFocusView);
                    return true;
                }
            }

            return false;
        }
    }

    private class CreateNewBillDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.btn_lbl_create_new_bill);

            dlgCreateNewBillLlv = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dlg_create_new_bill_llv, null);

            if(llBillRecords.getChildCount() > 0) {
                dlgCreateNewBillLlv.findViewById(R.id.tv_current_bill_will_be_deleted).setVisibility(View.VISIBLE);
                builder.setNegativeButton(R.string.dlg_cancel, onClickListener);
            }

            builder.setView(dlgCreateNewBillLlv);

            builder.setPositiveButton(R.string.dlg_confirm, onClickListener);

            return builder.create();
        }

        private class CreateNewBillDialogOnClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int defShare = getResources().getInteger(R.integer.min_share);
                        try {
                            defShare = Integer.parseInt(((EditText) dlgCreateNewBillLlv.findViewById(R.id.et_def_share)).getText().toString());
                        }
                        catch(Exception ignored) {}

                        dbBillPleaseTableBillValuesManager.setColumnValue(dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId(), DbBillPleaseTableBillValuesContract.COLUMN_NAME_DEF_SHARE, defShare);

                        createNewBill();

                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        }

        private DialogInterface.OnClickListener onClickListener = new CreateNewBillDialogOnClickListener();
        private LinearLayout dlgCreateNewBillLlv;
    }

    private class PushToDbTimerTask extends TimerTask {
        @Override
        public void run() {
            if(curDbEt != null) {
                curDbEt.pushToDb();
                redrawAllSums();
            }
        }
    }

    private LinearLayout llActionBar;
	private LinearLayout llBillRecords;
    private DbEditText curDbEt;
    private View etHidden;
    private int exMotionEvent;
    private BillPleaseOnFocusChangeListener billPleaseOnFocusChangeListener;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;
    private BillPleaseTextWatcher billPleaseTextWatcher;
    private BillPleaseOnEditorActionListener billPleaseOnEditorActionListener;
    private CreateNewBillDialogFragment createNewBillDialogFragment;
    private DbBillPleaseManager dbBillPleaseManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillRecordEtChangingManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillValuesManager;
    private Timer timer;
    private DecimalFormat decimalFormat;

    private static final int NONE_MOTION_EVENT = -1;
    private static final String INTENT_TYPE_SHARE = "text/plain";
}