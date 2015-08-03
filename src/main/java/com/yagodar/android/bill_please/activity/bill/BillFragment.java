package com.yagodar.android.bill_please.activity.bill;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.AbsBillPleaseTextWatcher;
import com.yagodar.android.bill_please.activity.BillPleaseLoaderFactory;
import com.yagodar.android.bill_please.activity.bill_order.BillOrderActivity;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.db.DbTableBillContract;
import com.yagodar.android.custom.fragment.IOnActivityBackPressedListener;
import com.yagodar.android.custom.fragment.progress.AbsLoaderProgressListFragment;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;

import java.math.BigDecimal;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillFragment extends AbsLoaderProgressListFragment implements IOnActivityBackPressedListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUpdateBillBundle = new Bundle(getArguments());
        mUpdateBillBundle.putLong(AbsAsyncTaskLoader.DELAY_START_MILLISECONDS_TAG, UPDATE_BILL_TIMER_TASK_DELAY_MILLIS);

        long billId = getArguments().getLong(BaseColumns._ID);
        mBill = BillList.getInstance().getModel(billId);

        View.OnClickListener onClickListener = new BillOnClickListener();

        setListAdapter(new BillOrderListAdapter(getActivity(), onClickListener, mBill));

        setEmptyText(getString(R.string.no_data));

        mButtonBillOrderAppend = (Button) getActivity().findViewById(R.id.bill_order_append_button);
        mButtonBillOrderAppend.setOnClickListener(onClickListener);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new BillOnCheckedChangeListener();

        mEditTextName = (EditText) getActivity().findViewById(R.id.bill_et_name);
        mTextViewSubtotal = (TextView) getActivity().findViewById(R.id.bill_subtotal);
        mToggleTax = (ToggleButton) getActivity().findViewById(R.id.bill_toggle_tax);
        mEditTextTaxPer = (EditText) getActivity().findViewById(R.id.bill_et_tax_per_val);
        mEditTextTaxAbs = (EditText) getActivity().findViewById(R.id.bill_et_tax_abs_val);
        mToggleTip = (ToggleButton) getActivity().findViewById(R.id.bill_toggle_tip);
        mEditTextTipPer = (EditText) getActivity().findViewById(R.id.bill_et_tip_per_val);
        mEditTextTipAbs = (EditText) getActivity().findViewById(R.id.bill_et_tip_abs_val);
        mTextViewTotal = (TextView) getActivity().findViewById(R.id.bill_total);

        if (savedInstanceState != null) {
            mEditTextName.setText(savedInstanceState.getString(NAME_TAG));
            mTextViewSubtotal.setText(savedInstanceState.getString(SUBTOTAL_TAG));
            mToggleTax.setChecked(savedInstanceState.getBoolean(TAX_TYPE_TAG));
            mEditTextTaxAbs.setText(savedInstanceState.getString(TAX_ABS_VAL_TAG));
            mEditTextTaxAbs.setEnabled(!mToggleTax.isChecked());
            mEditTextTaxPer.setText(savedInstanceState.getString(TAX_PER_VAL_TAG));
            mEditTextTaxPer.setEnabled(mToggleTax.isChecked());
            mToggleTip.setChecked(savedInstanceState.getBoolean(TIP_TYPE_TAG));
            mEditTextTipAbs.setText(savedInstanceState.getString(TIP_ABS_VAL_TAG));
            mEditTextTipAbs.setEnabled(mToggleTip.isChecked());
            mEditTextTipPer.setText(savedInstanceState.getString(TIP_PER_VAL_TAG));
            mEditTextTipPer.setEnabled(!mToggleTip.isChecked());
            mTextViewTotal.setText(savedInstanceState.getString(TOTAL_TAG));
        } else {
            notifyBillLoaded();
        }

        mToggleTax.setOnCheckedChangeListener(onCheckedChangeListener);
        mToggleTip.setOnCheckedChangeListener(onCheckedChangeListener);

        mEditTextName.addTextChangedListener(new BillNameTextWatcher());
        mEditTextTaxAbs.addTextChangedListener(new BillTaxAbsTextWatcher());
        mEditTextTaxPer.addTextChangedListener(new BillTaxPerTextWatcher());
        mEditTextTipAbs.addTextChangedListener(new BillTipAbsTextWatcher());
        mEditTextTipPer.addTextChangedListener(new BillTipPerTextWatcher());

        View.OnFocusChangeListener onFocusChangeListener = new BillOnFocusChangeListener();

        mEditTextName.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextTaxAbs.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextTaxPer.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextTipAbs.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextTipPer.setOnFocusChangeListener(onFocusChangeListener);

        mEditTextHidden = (EditText) getActivity().findViewById(R.id.bill_et_hidden);
        mEditTextHidden.setOnFocusChangeListener(onFocusChangeListener);

        TextView.OnEditorActionListener onEditorActionListener = new BillOnEditorActionListener();
        mEditTextName.setOnEditorActionListener(onEditorActionListener);
        mEditTextTaxAbs.setOnEditorActionListener(onEditorActionListener);
        mEditTextTaxPer.setOnEditorActionListener(onEditorActionListener);
        mEditTextTipAbs.setOnEditorActionListener(onEditorActionListener);
        mEditTextTipPer.setOnEditorActionListener(onEditorActionListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        setAvailable(true);

        startLoading(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL.ordinal(), getArguments());

        if (getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal(), null, true);
        }

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL_ORDER.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL_ORDER.ordinal(), null);
        }

        if (getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL_ORDER.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL_ORDER.ordinal(), null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_TAG, mEditTextName.getText().toString());
        outState.putString(SUBTOTAL_TAG, mTextViewSubtotal.getText().toString());
        outState.putBoolean(TAX_TYPE_TAG, mToggleTax.isChecked());
        outState.putString(TAX_ABS_VAL_TAG, mEditTextTaxAbs.getText().toString());
        outState.putString(TAX_PER_VAL_TAG, mEditTextTaxPer.getText().toString());
        outState.putBoolean(TIP_TYPE_TAG, mToggleTip.isChecked());
        outState.putString(TIP_ABS_VAL_TAG, mEditTextTipAbs.getText().toString());
        outState.putString(TIP_PER_VAL_TAG, mEditTextTipPer.getText().toString());
        outState.putString(TOTAL_TAG, mTextViewTotal.getText().toString());
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return BillPleaseLoaderFactory.createLoader(getActivity(), id, args);
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult loaderResult) {
        if(loaderResult.isSuccessful()) {
            if(loader.getId() == BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL.ordinal()) {
                if(loaderResult.isNotifyDataSet()) {
                    ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                    notifyBillLoaded();
                }
            } else if(loaderResult.isNotifyDataSet()) {
                ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                notifyOrderListChanged();
            }
        }

        super.onLoaderResult(loader, loaderResult);
    }

    @Override
    public void setAvailable(boolean available) {
        super.setAvailable(available);
        mButtonBillOrderAppend.setEnabled(available);
        setTaxRowEnabled(available);
        setTipRowEnabled(available);
    }

    @Override
    public boolean onActivityBackPressed() {
        if(mLastEditText != null) {
            hideFocus();
            return true;
        } else {
            return false;
        }
    }

    private void hideFocus() {
        hideSoftKeyboard(mLastEditText);
        mEditTextHidden.requestFocus();
    }

    private void hideSoftKeyboard(View view) {
        if(view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void notifyBillLoaded() {
        onNameLoaded();
        onSubtotalChanged();
        onTaxLoaded();
        onTaxChanged();
        onTipLoaded();
        onTipChanged();
        onTotalChanged();
    }

    private void notifyOrderListChanged() {
        onSubtotalChanged();
        onTaxChanged();
        onTipChanged();
        onTotalChanged();
    }

    private void notifyTaxChanged() {
        onTaxChanged();
        onTotalChanged();
    }

    private void notifyTipChanged() {
        onTipChanged();
        onTotalChanged();
    }

    private boolean updateModelName() {
        String oldValue = mBill.getName();
        mBill.setName(mEditTextName.getText().toString());
        String newValue = mBill.getName();
        return !newValue.equals(oldValue);
    }

    private void onNameLoaded() {
        mLoading = true;
        mEditTextName.setText(mBill.getName());
        mLoading = false;
    }

    private void onSubtotalChanged() {
        mTextViewSubtotal.setText(mBill.getFormattedSubtotal());
    }

    private void onTaxLoaded() {
        mLoading = true;
        if(mBill.getTaxType() == Bill.TaxTipType.ABSOLUTE) {
            mToggleTax.setChecked(true);
            mEditTextTaxAbs.setText(mBill.getFormattedTaxVal());
        } else if(mBill.getTaxType() == Bill.TaxTipType.PERCENT) {
            mToggleTax.setChecked(false);
            mEditTextTaxPer.setText(mBill.getFormattedTaxVal());
        }
        mLoading = false;
    }

    private boolean updateModelTax() {
        Bill.TaxTipType oldTypeValue = mBill.getTaxType();
        BigDecimal oldValue = mBill.getTaxVal();

        if(mToggleTax.isChecked()) {
            mBill.setTaxVal(Bill.TaxTipType.ABSOLUTE, mEditTextTaxAbs.getText().toString());
        } else {
            mBill.setTaxVal(Bill.TaxTipType.PERCENT, mEditTextTaxPer.getText().toString());
        }

        Bill.TaxTipType newTypeValue = mBill.getTaxType();
        BigDecimal newValue = mBill.getTaxVal();

        return !newTypeValue.equals(oldTypeValue) || newValue.compareTo(oldValue) != 0;
    }

    private void onTaxChanged() {
        mLoading = true;
        if(mBill.getTaxType() == Bill.TaxTipType.ABSOLUTE) {
            mEditTextTaxPer.setText(mBill.getFormattedTaxVal(Bill.TaxTipType.PERCENT));
            mEditTextTaxAbs.setEnabled(true);
            mEditTextTaxPer.setEnabled(false);
            if(mBill.getTipType() == Bill.TaxTipType.ABSOLUTE) {
                mEditTextTaxAbs.setNextFocusDownId(R.id.bill_et_tip_abs_val);
            }
        } else if(mBill.getTaxType() == Bill.TaxTipType.PERCENT) {
            mEditTextTaxAbs.setText(mBill.getFormattedTaxVal(Bill.TaxTipType.ABSOLUTE));
            mEditTextTaxPer.setEnabled(true);
            mEditTextTaxAbs.setEnabled(false);
            if(mBill.getTipType() == Bill.TaxTipType.ABSOLUTE) {
                mEditTextTaxPer.setNextFocusDownId(R.id.bill_et_tip_abs_val);
            }
        }
        mLoading = false;
    }

    private void setTaxRowEnabled(boolean enabled) {
        mToggleTax.setEnabled(enabled);
        if(mBill.getTaxType() == Bill.TaxTipType.ABSOLUTE) {
            mEditTextTaxAbs.setEnabled(enabled);
        } else if(mBill.getTaxType() == Bill.TaxTipType.PERCENT) {
            mEditTextTaxPer.setEnabled(enabled);
        }
    }

    private void onTipLoaded() {
        mLoading = true;
        if(mBill.getTipType() == Bill.TaxTipType.ABSOLUTE) {
            mToggleTip.setChecked(true);
            mEditTextTipAbs.setText(mBill.getFormattedTipVal());
        } else if(mBill.getTipType() == Bill.TaxTipType.PERCENT) {
            mToggleTip.setChecked(false);
            mEditTextTipPer.setText(mBill.getFormattedTipVal());
        }
        mLoading = false;
    }

    private boolean updateModelTip() {
        Bill.TaxTipType oldTypeValue = mBill.getTipType();
        BigDecimal oldValue = mBill.getTipVal();

        if(mToggleTip.isChecked()) {
            mBill.setTipVal(Bill.TaxTipType.ABSOLUTE, mEditTextTipAbs.getText().toString());
        } else {
            mBill.setTipVal(Bill.TaxTipType.PERCENT, mEditTextTipPer.getText().toString());
        }

        Bill.TaxTipType newTypeValue = mBill.getTipType();
        BigDecimal newValue = mBill.getTipVal();

        return !newTypeValue.equals(oldTypeValue) || newValue.compareTo(oldValue) != 0;
    }

    private void onTipChanged() {
        mLoading = true;
        if(mBill.getTipType() == Bill.TaxTipType.ABSOLUTE) {
            mEditTextTipPer.setText(mBill.getFormattedTipVal(Bill.TaxTipType.PERCENT));
            mEditTextTipAbs.setEnabled(true);
            mEditTextTipPer.setEnabled(false);
        } else if(mBill.getTipType() == Bill.TaxTipType.PERCENT) {
            mEditTextTipAbs.setText(mBill.getFormattedTipVal(Bill.TaxTipType.ABSOLUTE));
            mEditTextTipPer.setEnabled(true);
            mEditTextTipAbs.setEnabled(false);
        }
        mLoading = false;
    }

    private void setTipRowEnabled(boolean enabled) {
        mToggleTip.setEnabled(enabled);
        if(mBill.getTipType() == Bill.TaxTipType.ABSOLUTE) {
            mEditTextTipAbs.setEnabled(enabled);
        } else if(mBill.getTipType() == Bill.TaxTipType.PERCENT) {
            mEditTextTipPer.setEnabled(enabled);
        }
    }

    private void onTotalChanged() {
        mTextViewTotal.setText(mBill.getFormattedTotal());
    }

    private void startUpdateBillLoader() {
        Loader updateBillLoader = getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal());
        if (updateBillLoader != null && updateBillLoader.isStarted()) {
            finishLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal());
        }
        startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal(), mUpdateBillBundle, true);
    }

    private class BillOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_order_append_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL_ORDER.ordinal(), getArguments());
                    break;
                case R.id.bill_order_edit_button:
                    Intent intent = new Intent(getActivity(), BillOrderActivity.class);
                    intent.putExtras((Bundle) v.getTag());
                    startActivity(intent);
                    break;
                case R.id.bill_order_remove_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL_ORDER.ordinal(), (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private class BillOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch(v.getId()) {
                case R.id.bill_et_name:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onNameLoaded();
                    }
                    break;
                case R.id.bill_et_tax_abs_val:
                case R.id.bill_et_tax_per_val:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onTaxLoaded();
                    }
                    break;
                case R.id.bill_et_tip_abs_val:
                case R.id.bill_et_tip_per_val:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onTipLoaded();
                    }
                    break;
                case R.id.bill_et_hidden:
                    if(hasFocus) {
                        mLastEditText = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class BillOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(mLoading) {
                return;
            }

            switch (buttonView.getId()) {
                case R.id.bill_toggle_tax:
                    if(updateModelTax()) {
                        startUpdateBillLoader();
                        notifyTaxChanged();
                    }
                    break;
                case R.id.bill_toggle_tip:
                    if(updateModelTip()) {
                        startUpdateBillLoader();
                        notifyTipChanged();
                    }
                    break;
                default:
                    break;
            }

            hideFocus();
        }
    }

    private class BillNameTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(updateModelName()) {
                startUpdateBillLoader();
            }
        }
    }

    private class BillTaxAbsTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(mToggleTax.isChecked()) {
                if(updateModelTax()) {
                    startUpdateBillLoader();
                    notifyTaxChanged();
                }
            }
        }
    }

    private class BillTaxPerTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(!mToggleTax.isChecked()) {
                if(updateModelTax()) {
                    startUpdateBillLoader();
                    notifyTaxChanged();
                }
            }
        }
    }

    private class BillTipAbsTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(mToggleTip.isChecked()) {
                if(updateModelTip()) {
                    startUpdateBillLoader();
                    notifyTipChanged();
                }
            }
        }
    }

    private class BillTipPerTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(!mToggleTip.isChecked()) {
                if(updateModelTip()) {
                    startUpdateBillLoader();
                    notifyTipChanged();
                }
            }
        }
    }

    private class BillOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_NEXT) {
                switch(textView.getId()) {
                    case R.id.bill_et_name:
                        if(mToggleTax.isChecked()) {
                            mEditTextTaxAbs.requestFocus();
                        } else {
                            mEditTextTaxPer.requestFocus();
                        }
                        return true;
                    case R.id.bill_et_tax_abs_val:
                    case R.id.bill_et_tax_per_val:
                        if(mToggleTip.isChecked()) {
                            mEditTextTipAbs.requestFocus();
                        } else {
                            mEditTextTipPer.requestFocus();
                        }
                        return true;
                    case R.id.bill_et_tip_abs_val:
                    case R.id.bill_et_tip_per_val:
                        hideFocus();
                        return true;
                    default:
                        break;
                }
            }

            return false;
        }
    }

    private boolean mLoading;

    private Bundle mUpdateBillBundle;

    private Bill mBill;

    private Button mButtonBillOrderAppend;

    private EditText mEditTextName;
    private TextView mTextViewSubtotal;
    private ToggleButton mToggleTax;
    private EditText mEditTextTaxPer;
    private EditText mEditTextTaxAbs;
    private ToggleButton mToggleTip;
    private EditText mEditTextTipPer;
    private EditText mEditTextTipAbs;
    private TextView mTextViewTotal;

    private EditText mEditTextHidden;

    private EditText mLastEditText;

    private final static String NAME_TAG = DbTableBillContract.COLUMN_NAME_BILL_NAME;
    private final static String SUBTOTAL_TAG = DbTableBillContract.COLUMN_NAME_BILL_NAME + "_subtotal";
    private final static String TAX_TYPE_TAG = DbTableBillContract.COLUMN_NAME_TAX_TYPE;
    private final static String TAX_ABS_VAL_TAG = DbTableBillContract.COLUMN_NAME_TAX_VAL + "_abs";
    private final static String TAX_PER_VAL_TAG = DbTableBillContract.COLUMN_NAME_TAX_VAL + "_per";
    private final static String TIP_TYPE_TAG = DbTableBillContract.COLUMN_NAME_TIP_TYPE;
    private final static String TIP_ABS_VAL_TAG = DbTableBillContract.COLUMN_NAME_TIP_VAL + "_abs";
    private final static String TIP_PER_VAL_TAG = DbTableBillContract.COLUMN_NAME_TIP_VAL + "_per";
    private final static String TOTAL_TAG = DbTableBillContract.COLUMN_NAME_BILL_NAME + "_total";

    private final static long UPDATE_BILL_TIMER_TASK_DELAY_MILLIS = 1500L;
}
