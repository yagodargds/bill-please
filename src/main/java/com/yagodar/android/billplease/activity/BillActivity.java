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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yagodar.android.billplease.R;
import com.yagodar.android.billplease.custom.DbEditText;
import com.yagodar.android.billplease.database.DbManager;
import com.yagodar.android.billplease.database.DbTableBillContract;
import com.yagodar.android.billplease.database.DbTableBillValuesContract;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.android.database.sqlite.custom.AbstractDbEditText;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class BillActivity extends FragmentActivity {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(lastDbEt != null) {
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

        bigValuesScale = getResources().getInteger(R.integer.min_fraction_digits) * getResources().getInteger(R.integer.max_fraction_digits);

        exMotionEvent = NONE_MOTION_EVENT;

        billOnFocusChangeListener = new BillOnFocusChangeListener();
        billOnTouchListener = new BillOnTouchListener();
        billTextWatcher = new BillTextWatcher();
        billOnEditorActionListener = new BillOnEditorActionListener();

        dbTableBillManager = DbManager.getInstance(this).getDbTableManager(DbTableBillContract.getInstance());
        dbTableBillValuesManager = DbManager.getInstance(this).getDbTableManager(DbTableBillValuesContract.getInstance());

        initDefValuesRecordId();

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
                if(llBillRecords.getChildCount() == 0) {
                    findViewById(R.id.chk_all_records).setEnabled(true);
                }

                addBillRecord();
                break;
            case R.id.btn_del_bill_record:
                if(lastDbEt != null && isBillRecordEt(lastDbEt)) {
                    delBillRecord(lastDbEt.getRecordId());

                    if(llBillRecords.getChildCount() == 0) {
                        ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(false);
                        findViewById(R.id.chk_all_records).setEnabled(false);
                    }
                }
                break;
            case R.id.btn_create_new_bill:
                hideFocus();
                (new CreateNewBillDialogFragment()).show(getSupportFragmentManager(), getResources().getString(R.string.btn_lbl_create_new_bill));
                break;
            case R.id.btn_share_bill:
                hideFocus();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                sendIntent.setType(INTENT_TYPE_SHARE);
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.btn_lbl_share_bill)));
                break;
            case R.id.chk_record:
                processCheckingRecord((Long) button.getTag(), ((CheckBox) button).isChecked());
                break;
            case R.id.chk_all_records:
                processCheckingAllRecords(((CheckBox) button).isChecked());
                break;
            case R.id.chk_tax_main:
                toggleTax(((CheckBox) button).isChecked());
                redrawTaxSum(calcSubtotalSum());
                break;
            case R.id.chk_tax_sum_main:
                toggleTax(!((CheckBox) button).isChecked());
                redrawTaxSum(calcSubtotalSum());
                break;
            case R.id.chk_tip_main:
                toggleTip(((CheckBox) button).isChecked());
                redrawTipSum(calcSubtotalSum());
                break;
            case R.id.chk_tip_sum_main:
                toggleTip(!((CheckBox) button).isChecked());
                redrawTipSum(calcSubtotalSum());
                break;
            default:
                break;
        }
    }

    private void setRecordChecked(CheckBox recordCheckBox, boolean isChecked) {
        recordCheckBox.setChecked(isChecked);
        processCheckingRecord((Long) recordCheckBox.getTag(), isChecked);
    }

    private void processCheckingRecord(long recordId, boolean isChecked) {
        if(isChecked) {
            onCheckRecord(recordId);
        }
        else {
            onUncheckRecord(recordId);
        }
    }

    private void onCheckRecord(long recordId) {
        checkedRecordIds.add(recordId);

        if(!((CheckBox) findViewById(R.id.chk_all_records)).isChecked() && checkedRecordIds.size() != 0 && checkedRecordIds.size() == llBillRecords.getChildCount()) {
            ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(true);
        }
    }

    private void onUncheckRecord(long recordId) {
        if(((CheckBox) findViewById(R.id.chk_all_records)).isChecked()) {
            if(checkedRecordIds.size() == llBillRecords.getChildCount()) {//TODO
                ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(false);
            }
        }
        else {
            if(checkedRecordIds.size() != 0 && checkedRecordIds.size() == llBillRecords.getChildCount()) {
                ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(false);
            }
        }

        checkedRecordIds.remove(recordId);
    }

    private void processCheckingAllRecords(boolean isChecked) {
        for(int i = 0; i < llBillRecords.getChildCount(); i++) {
            setRecordChecked((CheckBox) llBillRecords.getChildAt(i).findViewById(R.id.chk_record), isChecked);
        }
    }

    private void toggleTax(boolean isTaxPerMain) {
        setTaxPerMainChecked(isTaxPerMain);
        setTaxSumMainChecked(!isTaxPerMain);
        dbTableBillValuesManager.setColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN, isTaxPerMain);
    }

    private void toggleTip(boolean isTipPerMain) {
        setTipPerMainChecked(isTipPerMain);
        setTipSumMainChecked(!isTipPerMain);
        dbTableBillValuesManager.setColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN, isTipPerMain);
    }

    private void setTaxPerMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tax_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tax), R.drawable.check_on);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tax), R.drawable.check_off);
        }
    }

    private void setTaxSumMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tax_sum_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tax_sum), R.drawable.check_on);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tax_sum), R.drawable.check_off);
        }
    }

    private void setTipPerMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tip_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tip), R.drawable.check_on);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tip), R.drawable.check_off);
        }
    }

    private void setTipSumMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tip_sum_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tip_sum), R.drawable.check_on);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tip_sum), R.drawable.check_off);
        }
    }

    private boolean isBillRecordEt(EditText et) {
        return et.getId() == R.id.et_item_name || et.getId() == R.id.et_cost || et.getId() == R.id.et_share;
    }

    private String getShareText() {
        String shareText = "";

        //app name tag
        shareText += getResources().getString(R.string.app_tag);
        shareText += "\n";

        //records
        shareText += "[" + getResources().getString(R.string.lbl_item_name) + "]";
        shareText += "\n";
        if(llBillRecords.getChildCount() > 1) {
            LinearLayout billRecordLl;
            BigDecimal cost;
            BigInteger share;
            for(int i = 0; i < llBillRecords.getChildCount(); i++) {
                try {
                    billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                    shareText += ((EditText) billRecordLl.findViewById(R.id.et_item_name)).getText();
                    cost = ((DbEditText<BigDecimal>) billRecordLl.findViewById(R.id.et_cost)).getValue();
                    share = ((DbEditText<BigInteger>) billRecordLl.findViewById(R.id.et_share)).getValue();
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_cost) + ":" + decimalFormat.format(cost);
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_share) + ":" + share;
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + decimalFormat.format(cost.divide(new BigDecimal(share), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
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
        shareText += "\t/\t" + ((TextView) findViewById(R.id.et_tax_sum)).getText();
        shareText += "\n";

        //tip
        shareText += "[" + getResources().getString(R.string.lbl_tip) + "]" + ":" + ((EditText) findViewById(R.id.et_tip)).getText();
        shareText += "\t/\t" + ((TextView) findViewById(R.id.et_tip_sum)).getText();
        shareText += "\n";

        //total
        shareText += "[" + getResources().getString(R.string.lbl_total) + "]" + ":" + ((TextView) findViewById(R.id.tv_total_sum)).getText();

        return shareText;
    }

    private void initDefValuesRecordId() {
        if(dbTableBillValuesManager.getAllRecords().size() == 0) {
            valuesRecordId = dbTableBillValuesManager.addRecord();
        }
        else {
            valuesRecordId = dbTableBillValuesManager.getAllRecords().iterator().next().getId();
        }
    }

    private void loadBill() {
        llActionBar = ((LinearLayout) findViewById(R.id.ll_action_bar_icons));

        etHidden = findViewById(R.id.et_hidden);
        etHidden.setOnFocusChangeListener(billOnFocusChangeListener);

        TypedArray resIds = getResources().obtainTypedArray(R.array.apply_touch_listener_res_ids);
        int resId;
        for(int i = 0; i < resIds.length(); i++) {
            resId = resIds.getResourceId(i, 0);
            if(resId != 0) {
                findViewById(resId).setOnTouchListener(billOnTouchListener);
            }
        }
        resIds.recycle();

        for(View view :getViewsByTag(findViewById(android.R.id.content), getResources().getString(R.string.tag_apply_touch_listener))) {
            view.setOnTouchListener(billOnTouchListener);
        }

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_records));
        llBillRecords.removeAllViews();

        checkedRecordIds = new HashSet<Long>();

        for (DbTableManager.DbTableRecord dbRecord : dbTableBillManager.getAllRecords()) {
            drawBillRecord(dbRecord.getId(), false);
        }

        if(llBillRecords.getChildCount() == 0) {
            ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(false);
            findViewById(R.id.chk_all_records).setEnabled(false);
        }

        initBillRecordDefValuesEts(false);
        initTaxTipDefValuesEts();
    }

    private void initBillRecordDefValuesEts(boolean reset) {
        DbEditText dbEtDefItemName = (DbEditText) findViewById(R.id.et_def_item_name);
        DbEditText dbEtDefCost = (DbEditText) findViewById(R.id.et_def_cost);
        DbEditText dbEtDefShare = (DbEditText) findViewById(R.id.et_def_share);

        if (reset) {
            initBillEditText(valuesRecordId, dbEtDefItemName);
            initBillEditText(valuesRecordId, dbEtDefCost);
            initBillEditText(valuesRecordId, dbEtDefShare);
        }
        else {
            initBillEditText(valuesRecordId, dbEtDefItemName, (String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_ITEM_NAME));
            initBillEditText(valuesRecordId, dbEtDefCost, (String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_COST));
            initBillEditText(valuesRecordId, dbEtDefShare, (String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_SHARE));
        }

        dbEtDefItemName.setNextFocusView(View.FOCUS_FORWARD, dbEtDefCost);
        dbEtDefCost.setNextFocusView(View.FOCUS_FORWARD, dbEtDefShare);
    }

    private void initTaxTipDefValuesEts() {
        DbEditText dbEtTax = (DbEditText) findViewById(R.id.et_tax);
        DbEditText dbEtTaxSum = (DbEditText) findViewById(R.id.et_tax_sum);
        DbEditText dbEtTip = (DbEditText) findViewById(R.id.et_tip);
        DbEditText dbEtTipSum = (DbEditText) findViewById(R.id.et_tip_sum);

        initBillEditText(valuesRecordId, dbEtTax);
        initBillEditText(valuesRecordId, dbEtTaxSum);
        initBillEditText(valuesRecordId, dbEtTip);
        initBillEditText(valuesRecordId, dbEtTipSum);

        setTaxPerMainChecked((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN));
        setTaxSumMainChecked(!(Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN));
        setTipPerMainChecked((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN));
        setTipSumMainChecked(!(Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN));

        dbEtTax.setNextFocusView(View.FOCUS_FORWARD, dbEtTaxSum);
        dbEtTaxSum.setNextFocusView(View.FOCUS_FORWARD, dbEtTip);
        dbEtTip.setNextFocusView(View.FOCUS_FORWARD, dbEtTipSum);
    }

    private void addBillRecord() {
        long dbRecordId = dbTableBillManager.addRecord();

        if(dbRecordId != -1) {
            drawBillRecord(dbRecordId, true);
            redrawAllSums();
        }
    }

    private void delBillRecord(long dbRecordId) {
        dbTableBillManager.delRecord(dbRecordId);

        View billRecordLlToDel = llBillRecords.findViewWithTag(dbRecordId);

        DbEditText etShare = (DbEditText) billRecordLlToDel.findViewById(R.id.et_share);
        DbEditText etItemName = (DbEditText) billRecordLlToDel.findViewById(R.id.et_item_name);

        DbEditText backwardEtShare = (DbEditText) etItemName.getNextFocusView(View.FOCUS_BACKWARD);
        final DbEditText forwardEtItemName = (DbEditText) etShare.getNextFocusView(View.FOCUS_FORWARD);

        if(forwardEtItemName != null) {
            forwardEtItemName.setNextFocusView(View.FOCUS_BACKWARD, backwardEtShare);
            postRequestFocus(forwardEtItemName);
        }

        if(backwardEtShare != null) {
            backwardEtShare.setNextFocusView(View.FOCUS_FORWARD, forwardEtItemName);
        }

        llBillRecords.removeView(billRecordLlToDel);

        redrawAllSums();
    }

    private void createNewBill() {
        dbTableBillManager.delAllRecords();
        llBillRecords.removeAllViews();

        checkedRecordIds.clear();
        ((CheckBox) findViewById(R.id.chk_all_records)).setChecked(false);
        findViewById(R.id.chk_all_records).setEnabled(false);

        dbTableBillValuesManager.delAllRecords();
        initDefValuesRecordId();
        initBillRecordDefValuesEts(true);
        initTaxTipDefValuesEts();

        redrawAllSums();
    }

    private void drawBillRecord(long recordId, boolean reset) {
        LinearLayout billRecordLl = (LinearLayout) getLayoutInflater().inflate(R.layout.bill_record_llv, null);

        billRecordLl.setTag(recordId);

        CheckBox recordCheck = (CheckBox) billRecordLl.findViewById(R.id.chk_record);
        recordCheck.setTag(recordId);

        DbEditText etItemName = (DbEditText) billRecordLl.findViewById(R.id.et_item_name);
        DbEditText etCost = (DbEditText) billRecordLl.findViewById(R.id.et_cost);
        DbEditText etShare = (DbEditText) billRecordLl.findViewById(R.id.et_share);

        initBillEditText(recordId, etItemName);
        initBillEditText(recordId, etCost);
        initBillEditText(recordId, etShare);

        if (reset) {
            etItemName.syncValueStr((String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_ITEM_NAME));
            etCost.syncValueStr((String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_COST));
            etShare.syncValueStr((String) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_DEF_SHARE));
        }

        llBillRecords.addView(billRecordLl);

        if(llBillRecords.getChildCount() > 1) {
            DbEditText exEtShare = (DbEditText) llBillRecords.getChildAt(llBillRecords.getChildCount() - 2).findViewById(R.id.et_share);
            exEtShare.setNextFocusView(View.FOCUS_FORWARD, etItemName);
        }

        etItemName.setNextFocusView(View.FOCUS_FORWARD, etCost);
        etCost.setNextFocusView(View.FOCUS_FORWARD, etShare);
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
        BigDecimal subtotalSum = calcSubtotalSum();
        postSetText(((TextView) findViewById(R.id.tv_subtotal_sum)), decimalFormat.format(subtotalSum));

        redrawTaxSum(subtotalSum);
        redrawTipSum(subtotalSum);
    }

    private void redrawTaxSum(BigDecimal subtotalSum) {
        DbEditText<BigDecimal> etTax = ((DbEditText<BigDecimal>) findViewById(R.id.et_tax));
        DbEditText<BigDecimal> etTaxSum = ((DbEditText<BigDecimal>) findViewById(R.id.et_tax_sum));
        BigDecimal taxSum;

        if((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            taxSum = subtotalSum.multiply(etTax.getValue().divide(BigDecimal.valueOf(100.0), bigValuesScale, BIG_VALUES_ROUNDING_MODE));

            if(!etTax.equals(lastDbEt)) {
                etTax.pullFromDb();
            }

            if(!etTaxSum.equals(lastDbEt)) {
                syncDbEditTextValue(etTaxSum, taxSum);
            }
        }
        else {
            taxSum = etTaxSum.getValue();

            if(!etTaxSum.equals(lastDbEt)) {
                etTaxSum.pullFromDb();
            }

            if(!etTax.equals(lastDbEt)) {
                BigDecimal taxPer = BigDecimal.valueOf(0.0);
                if(subtotalSum.compareTo(BigDecimal.valueOf(0.0)) == 1) {
                    taxPer = taxSum.multiply(BigDecimal.valueOf(100.0)).divide(subtotalSum, bigValuesScale, BIG_VALUES_ROUNDING_MODE);
                }

                syncDbEditTextValue(etTax, taxPer);
            }
        }

        postSetText(((TextView) findViewById(R.id.tv_total_sum)), decimalFormat.format(subtotalSum.add(taxSum).add(calcTipSum(subtotalSum))));
    }

    private void redrawTipSum(BigDecimal subtotalSum) {
        DbEditText<BigDecimal> etTip = ((DbEditText<BigDecimal>) findViewById(R.id.et_tip));
        DbEditText<BigDecimal> etTipSum = ((DbEditText<BigDecimal>) findViewById(R.id.et_tip_sum));
        BigDecimal tipSum;

        if((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
            tipSum = subtotalSum.multiply(etTip.getValue().divide(BigDecimal.valueOf(100.0), bigValuesScale, BIG_VALUES_ROUNDING_MODE));

            if(!etTip.equals(lastDbEt)) {
                etTip.pullFromDb();
            }

            if(!etTipSum.equals(lastDbEt)) {
                syncDbEditTextValue(etTipSum, tipSum);
            }
        }
        else {
            tipSum = etTipSum.getValue();

            if(!etTipSum.equals(lastDbEt)) {
                etTipSum.pullFromDb();
            }

            if(!etTip.equals(lastDbEt)) {
                BigDecimal tipPer = BigDecimal.valueOf(0.0);
                if(subtotalSum.compareTo(BigDecimal.valueOf(0.0)) == 1) {
                    tipPer = tipSum.multiply(BigDecimal.valueOf(100.0)).divide(subtotalSum, bigValuesScale, BIG_VALUES_ROUNDING_MODE);
                }

                syncDbEditTextValue(etTip, tipPer);
            }
        }

        postSetText(((TextView) findViewById(R.id.tv_total_sum)), decimalFormat.format(subtotalSum.add(calcTaxSum(subtotalSum)).add(tipSum)));
    }

    private BigDecimal calcSubtotalSum() {
        BigDecimal value = BigDecimal.valueOf(0.0);

        LinearLayout billRecordLl;
        for(int i = 0; i < llBillRecords.getChildCount(); i++) {
            try {
                billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                value = value.add(((DbEditText<BigDecimal>) billRecordLl.findViewById(R.id.et_cost)).getValue().divide(new BigDecimal(((DbEditText<BigInteger>) billRecordLl.findViewById(R.id.et_share)).getValue()), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
            }
            catch(Exception ignored) {}
        }

        return value;
    }

    private BigDecimal calcTaxSum(BigDecimal subtotalSum) {
        BigDecimal taxSum;

        DbEditText<BigDecimal> etTax = ((DbEditText<BigDecimal>) findViewById(R.id.et_tax));
        DbEditText<BigDecimal> etTaxSum = ((DbEditText<BigDecimal>) findViewById(R.id.et_tax_sum));

        if((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            taxSum = subtotalSum.multiply(etTax.getValue().divide(new BigDecimal(100.0), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
        }
        else {
            taxSum = etTaxSum.getValue();
        }

        return taxSum;
    }

    private BigDecimal calcTipSum(BigDecimal subtotalSum) {
        BigDecimal tipSum;

        DbEditText<BigDecimal> etTip = ((DbEditText<BigDecimal>) findViewById(R.id.et_tip));
        DbEditText<BigDecimal> etTipSum = ((DbEditText<BigDecimal>) findViewById(R.id.et_tip_sum));

        if((Boolean) dbTableBillValuesManager.getColumnValue(valuesRecordId, DbTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
            tipSum = subtotalSum.multiply(etTip.getValue().divide(BigDecimal.valueOf(100.0), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
        }
        else {
            tipSum = etTipSum.getValue();
        }

        return tipSum;
    }

    private void initBillEditText(long recordId, AbstractDbEditText et) {
        initBillEditText(recordId, et, null);
    }

    private void initBillEditText(long recordId, AbstractDbEditText et, String defValueStr) {
        if(et != null) {
            et.setRecordId(recordId);
            et.setDefValueStr(defValueStr);
            et.pullFromDb();
            et.setOnFocusChangeListener(billOnFocusChangeListener);
            et.addTextChangedListener(billTextWatcher);
            et.setOnEditorActionListener(billOnEditorActionListener);
        }
    }

    private void syncDbEditTextValue(AbstractDbEditText et) {
        if(et != null) {
            timer.cancel();
            et.syncValue();
            timer.cancel();
        }
    }

    private void syncDbEditTextValue(AbstractDbEditText et, Object value) {
        if(et != null) {
            timer.cancel();
            et.syncValueObj(value);
            timer.cancel();
        }
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

    public static List<View> getViewsByTag(View root, String tag) {
        List<View> result = new LinkedList<View>();

        if (root instanceof ViewGroup) {
            final int childCount = ((ViewGroup) root).getChildCount();
            for (int i = 0; i < childCount; i++) {
                result.addAll(getViewsByTag(((ViewGroup) root).getChildAt(i), tag));
            }
        }

        final Object rootTag = root.getTag();
        // handle null tags, code from Guava's Objects.equal
        if (tag == rootTag || (tag != null && tag.equals(rootTag))) {
            result.add(root);
        }

        return result;
    }

    private class BillOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            switch(view.getId()) {
                case R.id.et_item_name:
                case R.id.et_cost:
                case R.id.et_share:
                    if(hasFocus) {
                        lastDbEt = (DbEditText) view;
                        postSetBackgroundColor(llBillRecords.findViewWithTag(view.getTag()), getResources().getColor(R.color.bill_record_picked));
                    }
                    else {
                        syncDbEditTextValue((DbEditText) view);
                        postSetBackgroundColor(llBillRecords.findViewWithTag(view.getTag()), getResources().getColor(R.color.bill_record_not_picked));
                        redrawAllSums();
                    }
                    break;
                case R.id.et_def_item_name:
                case R.id.et_def_cost:
                case R.id.et_def_share:
                    if(hasFocus) {
                        lastDbEt = (DbEditText) view;
                        postSetBackgroundColor(findViewById(R.id.ll_bill_def_values_record), getResources().getColor(R.color.bill_record_def_values_picked));
                    }
                    else {
                        syncDbEditTextValue((DbEditText) view);
                        postSetBackgroundColor(findViewById(R.id.ll_bill_def_values_record), getResources().getColor(R.color.bill_record_not_picked));
                    }
                    break;
                case R.id.et_tax:
                case R.id.et_tax_sum:
                    if(hasFocus) {
                        int lastDbEtId = 0;
                        if(lastDbEt != null) {
                            lastDbEtId = lastDbEt.getId();
                        }

                        lastDbEt = (DbEditText) view;

                        switch (lastDbEtId) {
                            case R.id.et_tip:
                            case R.id.et_tip_sum:
                                redrawTipSum(calcSubtotalSum());
                                break;
                            default:
                                break;
                        }
                    }
                    else {
                        syncDbEditTextValue((DbEditText) view);
                        redrawTaxSum(calcSubtotalSum());
                    }
                    break;
                case R.id.et_tip:
                case R.id.et_tip_sum:
                    if(hasFocus) {
                        int lastDbEtId = 0;
                        if(lastDbEt != null) {
                            lastDbEtId = lastDbEt.getId();
                        }

                        lastDbEt = (DbEditText) view;

                        switch(lastDbEtId) {
                            case R.id.et_tax:
                            case R.id.et_tax_sum:
                                redrawTaxSum(calcSubtotalSum());
                                break;
                            default:
                                break;
                        }
                    }
                    else {
                        syncDbEditTextValue((DbEditText) view);
                        redrawTipSum(calcSubtotalSum());
                    }
                    break;
                case R.id.et_hidden:
                    if(hasFocus) {
                        hideSoftKeyboard(lastDbEt);

                        int lastDbEtId = 0;
                        if(lastDbEt != null) {
                            lastDbEtId = lastDbEt.getId();
                            lastDbEt = null;
                        }

                        switch(lastDbEtId) {
                            case R.id.et_tax:
                            case R.id.et_tax_sum:
                                redrawTaxSum(calcSubtotalSum());
                                break;
                            case R.id.et_tip:
                            case R.id.et_tip_sum:
                                redrawTipSum(calcSubtotalSum());
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class BillOnTouchListener implements View.OnTouchListener {
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

    private class BillTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            timer.cancel();

            if(lastDbEt != null && lastDbEt.isFocused()) {
                timer = new Timer();
                try {
                    timer.schedule(new PushToDbTimerTask(), getResources().getInteger(R.integer.push_to_db_delay_millisecs));
                }
                catch(Exception ignored) {}
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class BillOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                View nextFocusView = null;

                switch(textView.getId()) {
                    case R.id.et_item_name:
                    case R.id.et_cost:
                    case R.id.et_share:
                    case R.id.et_def_item_name:
                    case R.id.et_def_cost:
                    case R.id.et_def_share:
                    case R.id.et_tip:
                    case R.id.et_tip_sum:
                    case R.id.et_tax:
                    case R.id.et_tax_sum:
                        nextFocusView = ((AbstractDbEditText) textView).getNextFocusView(View.FOCUS_FORWARD);
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
            builder.setView(getActivity().getLayoutInflater().inflate(R.layout.dlg_create_new_bill_llv, null));
            builder.setNegativeButton(R.string.dlg_cancel, onClickListener);
            builder.setPositiveButton(R.string.dlg_confirm, onClickListener);

            return builder.create();
        }

        private class CreateNewBillDialogOnClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
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
    }

    private class PushToDbTimerTask extends TimerTask {
        @Override
        public void run() {
            if(lastDbEt != null) {
                switch(lastDbEt.getId()) {
                    case R.id.et_tax:
                    case R.id.et_tax_sum:
                        lastDbEt.pushToDb();
                        redrawTaxSum(calcSubtotalSum());
                        break;
                    case R.id.et_tip:
                    case R.id.et_tip_sum:
                        lastDbEt.pushToDb();
                        redrawTipSum(calcSubtotalSum());
                        break;
                    default:
                        lastDbEt.pushToDb();
                        redrawAllSums();
                        break;
                }
            }
        }
    }

    private LinearLayout llActionBar;
    private LinearLayout llBillRecords;
    private HashSet<Long> checkedRecordIds;
    private AbstractDbEditText lastDbEt;
    private View etHidden;
    private int exMotionEvent;
    private BillOnFocusChangeListener billOnFocusChangeListener;
    private BillOnTouchListener billOnTouchListener;
    private BillTextWatcher billTextWatcher;
    private BillOnEditorActionListener billOnEditorActionListener;
    private DbTableManager dbTableBillManager;
    private DbTableManager dbTableBillValuesManager;
    private long valuesRecordId;
    private Timer timer;
    private DecimalFormat decimalFormat;
    private int bigValuesScale;

    private static final int NONE_MOTION_EVENT = -1;
    private static final String INTENT_TYPE_SHARE = "text/plain";
    private static final RoundingMode BIG_VALUES_ROUNDING_MODE = RoundingMode.HALF_UP;
}