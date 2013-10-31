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
import com.yagodar.android.billplease.custom.BillDbEditText;
import com.yagodar.android.billplease.custom.BillValuesDbEditText;
import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillContract;
import com.yagodar.android.billplease.database.DbBillPleaseTableBillValuesContract;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.AbstractDbEditText;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BillPleaseActivity extends FragmentActivity {
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

        billPleaseOnFocusChangeListener = new BillPleaseOnFocusChangeListener();
        billPleaseOnTouchListener = new BillPleaseOnTouchListener();
        billPleaseTextWatcher = new BillPleaseTextWatcher();
        billPleaseOnEditorActionListener = new BillPleaseOnEditorActionListener();

        createNewBillDialogFragment = new CreateNewBillDialogFragment();

        dbBillPleaseTableBillManager = DbBillPleaseManager.getInstance(this).getDbTableManager(DbBillPleaseTableBillContract.getInstance());
        dbBillPleaseTableBillValuesManager = DbBillPleaseManager.getInstance(this).getDbTableManager(DbBillPleaseTableBillValuesContract.getInstance());

        if(dbBillPleaseTableBillValuesManager.getAllRecords().size() == 0) {
            valuesRecordId = dbBillPleaseTableBillValuesManager.addRecord();
        }
        else {
            valuesRecordId = dbBillPleaseTableBillValuesManager.getAllRecords().iterator().next().getId();
        }

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

                if(lastDbEt != null && isBillRecordEt(lastDbEt)) {
                    llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_del_bill_record:
                if(lastDbEt != null && isBillRecordEt(lastDbEt)) {
                    delBillRecord(lastDbEt.getRecordId());
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

    private void toggleTax(boolean isTaxPerMain) {
        setTaxPerMainChecked(isTaxPerMain);
        setTaxSumMainChecked(!isTaxPerMain);
        dbBillPleaseTableBillValuesManager.setColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN, isTaxPerMain);
    }

    private void toggleTip(boolean isTipPerMain) {
        setTipPerMainChecked(isTipPerMain);
        setTipSumMainChecked(!isTipPerMain);
        dbBillPleaseTableBillValuesManager.setColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN, isTipPerMain);
    }

    private void setTaxPerMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tax_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tax), R.drawable.check_box_checked_rect);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tax), R.drawable.check_box_not_checked_rect);
        }
    }

    private void setTaxSumMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tax_sum_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tax_sum), R.drawable.check_box_checked_rect);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tax_sum), R.drawable.check_box_not_checked_rect);
        }
    }

    private void setTipPerMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tip_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tip), R.drawable.check_box_checked_rect);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tip), R.drawable.check_box_not_checked_rect);
        }
    }

    private void setTipSumMainChecked(boolean isChecked) {
        ((CheckBox) findViewById(R.id.chk_tip_sum_main)).setChecked(isChecked);
        if(isChecked) {
            postSetBackgroundResource(findViewById(R.id.ll_tip_sum), R.drawable.check_box_checked_rect);
        }
        else {
            postSetBackgroundResource(findViewById(R.id.ll_tip_sum), R.drawable.check_box_not_checked_rect);
        }
    }

    private boolean isBillRecordEt(EditText et) {
        return et.getId() == R.id.et_item_name || et.getId() == R.id.et_cost || et.getId() == R.id.et_share;
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
            BigDecimal cost;
            BigInteger share;
            for(int i = 0; i < llBillRecords.getChildCount() - 1; i++) {
                try {
                    billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                    shareText += ((EditText) billRecordLl.findViewById(R.id.et_item_name)).getText();
                    cost = ((BillDbEditText<BigDecimal>) billRecordLl.findViewById(R.id.et_cost)).getValue();
                    share = ((BillDbEditText<BigInteger>) billRecordLl.findViewById(R.id.et_share)).getValue();
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

        for(View view :getViewsByTag(findViewById(android.R.id.content), getResources().getString(R.string.apply_touch_listener_tag))) {
            view.setOnTouchListener(billPleaseOnTouchListener);
        }

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_records));

        llBillRecords.removeAllViews();

        for (DbTableBaseManager.DbTableRecord dbRecord : dbBillPleaseTableBillManager.getAllRecords()) {
            drawBillRecord(dbRecord.getId());
        }

        BillValuesDbEditText dbEtTax = (BillValuesDbEditText) findViewById(R.id.et_tax);
        initBillEditText(valuesRecordId, dbEtTax);
        setTaxPerMainChecked((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN));

        BillValuesDbEditText dbEtTaxSum = (BillValuesDbEditText) findViewById(R.id.et_tax_sum);
        initBillEditText(valuesRecordId, dbEtTaxSum);
        setTaxSumMainChecked(!(Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN));

        BillValuesDbEditText dbEtTip = (BillValuesDbEditText) findViewById(R.id.et_tip);
        initBillEditText(valuesRecordId, dbEtTip);
        setTipPerMainChecked((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN));

        BillValuesDbEditText dbEtTipSum = (BillValuesDbEditText) findViewById(R.id.et_tip_sum);
        initBillEditText(valuesRecordId, dbEtTipSum);
        setTipSumMainChecked(!(Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN));

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
        long dbRecordId = dbBillPleaseTableBillManager.addRecord();

        if(dbRecordId != -1) {
            drawBillRecord(dbRecordId);
        }
    }

    private void delBillRecord(long dbRecordId) {
        dbBillPleaseTableBillManager.delRecord(dbRecordId);

        View billRecordLlToDel = llBillRecords.findViewWithTag(dbRecordId);

        BillDbEditText etShare = (BillDbEditText) billRecordLlToDel.findViewById(R.id.et_share);
        BillDbEditText etItemName = (BillDbEditText) billRecordLlToDel.findViewById(R.id.et_item_name);

        BillDbEditText backwardEtShare = (BillDbEditText) etItemName.getNextFocusView(View.FOCUS_BACKWARD);
        final BillDbEditText forwardEtItemName = (BillDbEditText) etShare.getNextFocusView(View.FOCUS_FORWARD);

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
        dbBillPleaseTableBillManager.delAllRecords();
        llBillRecords.removeAllViews();
        addBillRecord();
        findViewById(R.id.btn_add_new_bill_record).setVisibility(View.VISIBLE);
        redrawAllSums();
    }

    private void drawBillRecord(long recordId) {
        LinearLayout billRecordLl = (LinearLayout) getLayoutInflater().inflate(R.layout.bill_record_llv, null);

        billRecordLl.setTag(recordId);

        BillDbEditText etItemName = (BillDbEditText) billRecordLl.findViewById(R.id.et_item_name);
        initBillEditText(recordId, etItemName);

        BillDbEditText etCost = (BillDbEditText) billRecordLl.findViewById(R.id.et_cost);
        initBillEditText(recordId, etCost);

        BillDbEditText etShare = (BillDbEditText) billRecordLl.findViewById(R.id.et_share);
        initBillEditText(recordId, etShare, (String) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_DEF_SHARE));

        llBillRecords.addView(billRecordLl);

        if(llBillRecords.getChildCount() > 1) {
            BillDbEditText exEtShare = (BillDbEditText) llBillRecords.getChildAt(llBillRecords.getChildCount() - 2).findViewById(R.id.et_share);

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
        BigDecimal subtotalSum = calcSubtotalSum();
        postSetText(((TextView) findViewById(R.id.tv_subtotal_sum)), decimalFormat.format(subtotalSum));

        redrawTaxSum(subtotalSum);
        redrawTipSum(subtotalSum);
    }

    private void redrawTaxSum(BigDecimal subtotalSum) {
        BillValuesDbEditText<BigDecimal> etTax = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tax));
        BillValuesDbEditText<BigDecimal> etTaxSum = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tax_sum));
        BigDecimal taxSum;

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
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
        BillValuesDbEditText<BigDecimal> etTip = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tip));
        BillValuesDbEditText<BigDecimal> etTipSum = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tip_sum));
        BigDecimal tipSum;

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
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
                value = value.add(((BillDbEditText<BigDecimal>) billRecordLl.findViewById(R.id.et_cost)).getValue().divide(new BigDecimal(((BillDbEditText<BigInteger>) billRecordLl.findViewById(R.id.et_share)).getValue()), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
            }
            catch(Exception ignored) {}
        }

        return value;
    }

    private BigDecimal calcTaxSum(BigDecimal subtotalSum) {
        BigDecimal taxSum;

        BillValuesDbEditText<BigDecimal> etTax = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tax));
        BillValuesDbEditText<BigDecimal> etTaxSum = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tax_sum));

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TAX_PER_MAIN)) {
            taxSum = subtotalSum.multiply(etTax.getValue().divide(new BigDecimal(100.0), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
        }
        else {
            taxSum = etTaxSum.getValue();
        }

        return taxSum;
    }

    private BigDecimal calcTipSum(BigDecimal subtotalSum) {
        BigDecimal tipSum;

        BillValuesDbEditText<BigDecimal> etTip = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tip));
        BillValuesDbEditText<BigDecimal> etTipSum = ((BillValuesDbEditText<BigDecimal>) findViewById(R.id.et_tip_sum));

        if((Boolean) dbBillPleaseTableBillValuesManager.getColumnValue(valuesRecordId, DbBillPleaseTableBillValuesContract.COLUMN_NAME_IS_TIP_PER_MAIN)) {
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
            et.setDefValue(defValueStr);
            et.pullFromDb();
            et.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
            et.addTextChangedListener(billPleaseTextWatcher);
            et.setOnEditorActionListener(billPleaseOnEditorActionListener);
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
            et.syncValue(value);
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

                        lastDbEt = (BillDbEditText) view;
                    }
                    else {
                        timer.cancel();
                        if(llBillRecords.findViewWithTag(view.getTag()).equals(llBillRecords.getChildAt(llBillRecords.getChildCount() - 1))) {
                            Editable curDbEtEditableText = ((EditText) view).getText();
                            if(curDbEtEditableText != null && curDbEtEditableText.length() > 0) {
                                addBillRecord();
                            }
                        }

                        syncDbEditTextValue((BillDbEditText) view);

                        postSetBackgroundColor(llBillRecords.findViewWithTag(view.getTag()), getResources().getColor(R.color.bill_record_not_picked));

                        llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.GONE);

                        redrawAllSums();
                    }
                    break;
                case R.id.et_tax:
                case R.id.et_tax_sum:
                    if(hasFocus) {
                        if(lastDbEt != null) {
                            switch(lastDbEt.getId()) {
                                case R.id.et_tip:
                                case R.id.et_tip_sum:
                                    redrawTipSum(calcSubtotalSum());
                                    break;
                                default:
                                    break;
                            }
                        }

                        lastDbEt = (BillValuesDbEditText) view;
                    }
                    else {
                        syncDbEditTextValue((BillValuesDbEditText) view);

                        redrawTaxSum(calcSubtotalSum());
                    }
                    break;
                case R.id.et_tip:
                case R.id.et_tip_sum:
                    if(hasFocus) {
                        if(lastDbEt != null) {
                            switch(lastDbEt.getId()) {
                                case R.id.et_tax:
                                case R.id.et_tax_sum:
                                    redrawTaxSum(calcSubtotalSum());
                                    break;
                                default:
                                    break;
                            }
                        }

                        lastDbEt = (BillValuesDbEditText) view;
                    }
                    else {
                        syncDbEditTextValue((BillValuesDbEditText) view);

                        redrawTipSum(calcSubtotalSum());
                    }
                    break;
                case R.id.et_hidden:
                    if(hasFocus) {
                        hideSoftKeyboard(lastDbEt);

                        switch(lastDbEt.getId()) {
                            case R.id.et_tax:
                            case R.id.et_tax_sum:
                                lastDbEt = null;
                                redrawTaxSum(calcSubtotalSum());
                                break;
                            case R.id.et_tip:
                            case R.id.et_tip_sum:
                                lastDbEt = null;
                                redrawTipSum(calcSubtotalSum());
                                break;
                            default:
                                lastDbEt = null;
                                break;
                        }
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

            dlgCreateNewBillLlv = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dlg_create_new_bill_llv, null);

            if(llBillRecords.getChildCount() > 0) {
                dlgCreateNewBillLlv.findViewById(R.id.llv_current_bill_will_be_deleted).setVisibility(View.VISIBLE);
                builder.setNegativeButton(R.string.dlg_cancel, onClickListener);
            }

            BillValuesDbEditText dbEtDefShare = (BillValuesDbEditText) dlgCreateNewBillLlv.findViewById(R.id.et_def_share);
            dbEtDefShare.setRecordId(valuesRecordId);
            dbEtDefShare.pullFromDb();

            builder.setView(dlgCreateNewBillLlv);

            builder.setPositiveButton(R.string.dlg_confirm, onClickListener);

            return builder.create();
        }

        private class CreateNewBillDialogOnClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ((BillValuesDbEditText) dlgCreateNewBillLlv.findViewById(R.id.et_def_share)).pushToDb();

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
    private AbstractDbEditText lastDbEt;
    private View etHidden;
    private int exMotionEvent;
    private BillPleaseOnFocusChangeListener billPleaseOnFocusChangeListener;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;
    private BillPleaseTextWatcher billPleaseTextWatcher;
    private BillPleaseOnEditorActionListener billPleaseOnEditorActionListener;
    private CreateNewBillDialogFragment createNewBillDialogFragment;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillValuesManager;
    private long valuesRecordId;
    private Timer timer;
    private DecimalFormat decimalFormat;
    private int bigValuesScale;

    private static final int NONE_MOTION_EVENT = -1;
    private static final String INTENT_TYPE_SHARE = "text/plain";
    private static final RoundingMode BIG_VALUES_ROUNDING_MODE = RoundingMode.HALF_UP;
}