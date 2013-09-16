package com.yagodar.android.billplease.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
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
        billPleaseOnEditorActionListener = new BillPleaseOnEditorActionListener();

        dbBillPleaseManager = DbBillPleaseManager.getInstance(this);
        dbBillPleaseTableBillManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillContract.getInstance());
        dbBillPleaseTableBillRecordEtChangingManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());
        dbBillPleaseTableBillTaxTipManager = dbBillPleaseManager.getDbTableManager(DbBillPleaseTableBillTaxTipContract.getInstance());

        loadBill();

        hideFocus();

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

            if(exDbEt != null && exDbEt instanceof BillRecordEditText) {
                llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.VISIBLE);
            }
			break;
		case R.id.btn_del_bill_record:
            if(exDbEt != null && exDbEt instanceof BillRecordEditText) {
                delBillRecord(exDbEt.getDbRecordId());
            }
			break;
		case R.id.btn_new_bill:
            hideFocus();
            delAllBillRecords();
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
        shareText += "#" + getResources().getString(R.string.app_name).replaceAll("\\s", "");
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
                    cost = ((BillRecordEditText<Double>) billRecordLl.findViewById(R.id.et_cost)).getDbValue();
                    share = ((BillRecordEditText<Integer>) billRecordLl.findViewById(R.id.et_share)).getDbValue();
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_cost) + ":" + String.valueOf(decimalFormat.format(cost));
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_share) + ":" + String.valueOf(share);
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + String.valueOf(decimalFormat.format(cost / (double) share));
                    shareText += "\n";
                }
                catch(Exception ignored) {}
            }
        }
        else {
            shareText += getResources().getString(R.string.lbl_no_items);
            shareText += "\n";
        }

        //subtotal
        shareText += "[" + getResources().getString(R.string.lbl_subtotal) + "]" + ":" + ((TextView) findViewById(R.id.tv_subtotal_sum)).getText();
        shareText += "\n";

        //tax
        shareText += "[" + getResources().getString(R.string.lbl_tax) + "]" + ":" + ((EditText) findViewById(R.id.et_tax)).getText();
        shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + ((TextView) findViewById(R.id.tv_tax_sum)).getText();
        shareText += "\n";

        //tip
        shareText += "[" + getResources().getString(R.string.lbl_tip) + "]" + ":" + ((EditText) findViewById(R.id.et_tip)).getText();
        shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + ((TextView) findViewById(R.id.tv_tip_sum)).getText();
        shareText += "\n";

        //total
        shareText += "[" + getResources().getString(R.string.lbl_total) + "]" + ":" + ((TextView) findViewById(R.id.tv_total_sum)).getText();

        return shareText;
    }

    private void addBillRecord() {
        long dbRecordId = dbBillPleaseManager.addNewBillRecord();

        if(dbRecordId != -1) {
            drawBillRecord(dbRecordId);
        }
    }

	private void loadBill() {
        llActionBar = ((LinearLayout) findViewById(R.id.ll_action_bar_icons));

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

        llBillRecords = ((LinearLayout) findViewById(R.id.ll_bill_records));

		llBillRecords.removeAllViews();

		for (DbTableBaseManager.DbTableRecord dbRecord : dbBillPleaseTableBillManager.getAllRecords()) {
			drawBillRecord(dbRecord.getId());
		}

        if(llBillRecords.getChildCount() == 0) {
            addBillRecord();
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
            dbEtTip.setDbValue(Double.parseDouble(getResources().getString(R.string.def_tip_double)));
        }
        dbEtTip.pullFromDb();
        dbEtTip.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        dbEtTip.addTextChangedListener(billPleaseTextWatcher);
	}

    private void delBillRecord(long dbRecordId) {
        dbBillPleaseManager.delBillRecord(dbRecordId);

        View billRecordLlToDel = llBillRecords.findViewWithTag(dbRecordId);

        BillRecordEditText share = (BillRecordEditText) billRecordLlToDel.findViewById(R.id.et_share);
        BillRecordEditText itemName = (BillRecordEditText) billRecordLlToDel.findViewById(R.id.et_item_name);

        BillRecordEditText exEtShare = (BillRecordEditText) itemName.getNextFocusView(View.FOCUS_BACKWARD);
        final BillRecordEditText postEtItemName = (BillRecordEditText) share.getNextFocusView(View.FOCUS_FORWARD);

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

    private void delAllBillRecords() {
        dbBillPleaseManager.delAllBillRecords();
        llBillRecords.removeAllViews();
        addBillRecord();
        redrawAllSums();
    }

	private void drawBillRecord(long recordId) {
		LinearLayout billRecordLl = (LinearLayout) getLayoutInflater().inflate(R.layout.bill_record_llv, null);

		billRecordLl.setTag(recordId);

        BillRecordEditText<String> etItemName = (BillRecordEditText) billRecordLl.findViewById(R.id.et_item_name);
        etItemName.setDbRecordId(recordId);
        etItemName.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME);
        etItemName.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etItemName.pullFromDb();
        etItemName.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etItemName.addTextChangedListener(billPleaseTextWatcher);
        etItemName.setOnEditorActionListener(billPleaseOnEditorActionListener);

        BillRecordEditText<Double> etCost = (BillRecordEditText) billRecordLl.findViewById(R.id.et_cost);
        etCost.setDbRecordId(recordId);
        etCost.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_COST);
        etCost.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etCost.pullFromDb();
        etCost.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etCost.addTextChangedListener(billPleaseTextWatcher);
        etCost.setOnEditorActionListener(billPleaseOnEditorActionListener);

        BillRecordEditText<Integer> etShare = (BillRecordEditText) billRecordLl.findViewById(R.id.et_share);
        etShare.setDbRecordId(recordId);
        etShare.initDbManagerBase(dbBillPleaseTableBillManager, DbBillPleaseTableBillContract.COLUMN_NAME_SHARE);
        etShare.initDbManagerChanging(dbBillPleaseTableBillRecordEtChangingManager, DbBillPleaseTableBillRecordEtChangingContract.COLUMN_NAME_IS_CHANGED);
        etShare.pullFromDb();
        etShare.setOnFocusChangeListener(billPleaseOnFocusChangeListener);
        etShare.addTextChangedListener(billPleaseTextWatcher);
        etShare.setOnEditorActionListener(billPleaseOnEditorActionListener);

        llBillRecords.addView(billRecordLl);

        if(llBillRecords.getChildCount() > 1) {
            BillRecordEditText<Integer> exEtShare = (BillRecordEditText) llBillRecords.getChildAt(llBillRecords.getChildCount() - 2).findViewById(R.id.et_share);

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
        postSetText(((TextView) findViewById(R.id.tv_subtotal_sum)), String.valueOf(decimalFormat.format(subtotalSum)));

        redrawTaxTipSums(subtotalSum);
    }

    private void redrawTaxTipSums(double subtotalSum) {
        double taxSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tax)).getDbValue() / 100.0 );
        postSetText(((TextView) findViewById(R.id.tv_tax_sum)), String.valueOf(decimalFormat.format(taxSum)));

        double tipSum = subtotalSum * ( ((DbEditText<Double>) findViewById(R.id.et_tip)).getDbValue() / 100.0 );
        postSetText(((TextView) findViewById(R.id.tv_tip_sum)), String.valueOf(decimalFormat.format(tipSum)));

        postSetText(((TextView) findViewById(R.id.tv_total_sum)), String.valueOf(decimalFormat.format(subtotalSum + taxSum + tipSum)));
    }

    private double calcSubtotalSum() {
        double value = 0.0;

        LinearLayout billRecordLl;
        for(int i = 0; i < llBillRecords.getChildCount(); i++) {
            try {
                billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                value += ((BillRecordEditText<Double>) billRecordLl.findViewById(R.id.et_cost)).getDbValue() / (double) ((BillRecordEditText<Integer>) billRecordLl.findViewById(R.id.et_share)).getDbValue();
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
                            postSetText((TextView) view, "");
                        }

                        if(llBillRecords.indexOfChild(llBillRecords.findViewWithTag(view.getTag())) != llBillRecords.getChildCount() - 1) {
                            llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.VISIBLE);
                        }
                        else {
                            llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.GONE);
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

                        if(llBillRecords.findViewWithTag(view.getTag()).equals(llBillRecords.getChildAt(llBillRecords.getChildCount() - 1)) && ((BillRecordEditText) view).isChanged()) {
                            addBillRecord();
                        }

                        llActionBar.findViewById(R.id.btn_del_bill_record).setVisibility(View.GONE);

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

                        redrawTaxTipSums(calcSubtotalSum());
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

    private class BillPleaseOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                View nextFocusView = null;

                switch(textView.getId()) {
                    case R.id.et_item_name:
                    case R.id.et_cost:
                    case R.id.et_share:
                        nextFocusView = ((BillRecordEditText) textView).getNextFocusView(View.FOCUS_FORWARD);
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

    private class PushToDbTimerTask extends TimerTask {
        @Override
        public void run() {
            if(exDbEt != null) {
                exDbEt.pushToDb();
                redrawAllSums();
            }
        }
    }

    private LinearLayout llActionBar;
	private LinearLayout llBillRecords;
    private DbEditText exDbEt;
    private View etHidden;
    private int exMotionEvent;
    private BillPleaseOnFocusChangeListener billPleaseOnFocusChangeListener;
    private BillPleaseOnTouchListener billPleaseOnTouchListener;
    private BillPleaseTextWatcher billPleaseTextWatcher;
    private BillPleaseOnEditorActionListener billPleaseOnEditorActionListener;
    private DbBillPleaseManager dbBillPleaseManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillRecordEtChangingManager;
    private DbTableBaseManager<DbBillPleaseManager> dbBillPleaseTableBillTaxTipManager;
    private Timer timer;
    private DecimalFormat decimalFormat;

    private static final int NONE_MOTION_EVENT = -1;
    private static final String INTENT_TYPE_SHARE = "text/plain";
}