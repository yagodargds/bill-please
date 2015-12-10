package com.yagodar.android.bill_please.activity.bill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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
import com.yagodar.android.bill_please.activity.LoaderFactory;
import com.yagodar.android.bill_please.activity.bill_order.BillOrderActivity;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.db.DbTableBillContract;
import com.yagodar.android.custom.fragment.IOnActivityBackPressedListener;
import com.yagodar.android.custom.fragment.progress.list_view.AbsLoaderProgressListViewFragment;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillFragment extends AbsLoaderProgressListViewFragment implements IOnActivityBackPressedListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mOnClickListener = new BillOnClickListener();

        initBill(getArguments());

        setEmptyText(getString(R.string.no_data));

        mButtonBillOrderAppend = (Button) getActivity().findViewById(R.id.bill_order_append_button);
        mButtonBillOrderAppend.setOnClickListener(mOnClickListener);

        mEditTextName = (EditText) getActivity().findViewById(R.id.bill_et_name);
        mTextViewSubtotal = (TextView) getActivity().findViewById(R.id.bill_subtotal);
        mToggleTax = (ToggleButton) getActivity().findViewById(R.id.bill_toggle_tax);
        mEditTextTaxPer = (EditText) getActivity().findViewById(R.id.bill_et_tax_per_val);
        mEditTextTaxAbs = (EditText) getActivity().findViewById(R.id.bill_et_tax_abs_val);
        mToggleTip = (ToggleButton) getActivity().findViewById(R.id.bill_toggle_tip);
        mEditTextTipPer = (EditText) getActivity().findViewById(R.id.bill_et_tip_per_val);
        mEditTextTipAbs = (EditText) getActivity().findViewById(R.id.bill_et_tip_abs_val);
        mTextViewTotal = (TextView) getActivity().findViewById(R.id.bill_total);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

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

            savedInstanceState.clear();
        } else {
            notifyBillLoaded();
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new BillOnCheckedChangeListener();

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
    public void onStart() {
        super.onStart();

        Log.d(TAG, toString() + " onLoaderCreated()");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, toString() + " onStop()");
    }

    @Override
    public void onResume() {
        super.onResume();

        //setAvailable(true);

        //LoaderManager loaderManager = getActivity().getSupportLoaderManager(); - NOT EQUAL!
        LoaderManager loaderManager = getLoaderManager();
        Log.d(TAG, toString() + " onResume() LoaderManager=" + loaderManager.toString());

        Bundle args = getArguments();
        Loader appendBillLoader = loaderManager.getLoader(LoaderFactory.Type.APPEND_BILL.ordinal());

        String logMsg = null;
        if(appendBillLoader != null) {
            logMsg = appendBillLoader.toString();
        }
        Log.d(TAG, toString() + " onResume() APPEND_BILL=" + logMsg);

        if (appendBillLoader != null
                || args == null && mBill == null) {
            startLoading(LoaderFactory.Type.APPEND_BILL.ordinal(), null);
        } else {
            if (loaderManager.getLoader(LoaderFactory.Type.LOAD_BILL.ordinal()) != null
                    || mBill != null && !mBill.isLoaded()) {
                startLoading(LoaderFactory.Type.LOAD_BILL.ordinal(), args);
            }

            if (loaderManager.getLoader(LoaderFactory.Type.UPDATE_BILL.ordinal()) != null) {
                startUpdateBillLoader();
            }

            if (loaderManager.getLoader(LoaderFactory.Type.APPEND_BILL_ORDER.ordinal()) != null) {
                startLoading(LoaderFactory.Type.APPEND_BILL_ORDER.ordinal(), null);
            }

            if (loaderManager.getLoader(LoaderFactory.Type.REMOVE_BILL_ORDER.ordinal()) != null) {
                startLoading(LoaderFactory.Type.REMOVE_BILL_ORDER.ordinal(), null);
            }
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
        AsyncTaskLoader loader = LoaderFactory.createLoader(getActivity(), id, args);
        if (id == LoaderFactory.Type.UPDATE_BILL.ordinal()) {
            loader.setUpdateThrottle(UPDATE_BILL_TIMER_TASK_DELAY_MILLIS);
        }
        return loader;
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult result) {
        int loaderId = loader.getId();
        boolean successful = result.isSuccessful();
        boolean notifyDataSet = result.isNotifyDataSet();

        if (loaderId == LoaderFactory.Type.APPEND_BILL.ordinal()) {

            Log.d(TAG, toString() + " FINISH APPEND_BILL");

            if(successful) {
                if(notifyDataSet) {
                    initBill((Bundle)result.getData());
                    notifyBillLoaded();
                }

                setActivityResult(Activity.RESULT_OK);
            }
            else {
                setActivityResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        }
        else if (loaderId == LoaderFactory.Type.LOAD_BILL.ordinal()) {
            if(successful) {
                if(notifyDataSet) {
                    ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                    notifyBillLoaded();
                }
            }
            else {
                getActivity().finish();
            }

            setActivityResult(Activity.RESULT_CANCELED);

        } else if(successful && notifyDataSet) {

            if (loaderId == LoaderFactory.Type.UPDATE_BILL.ordinal()) {
                ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                notifyOrderListChanged();
            } else if (loaderId == LoaderFactory.Type.APPEND_BILL_ORDER.ordinal()) {
                ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                notifyOrderListChanged();
            } else if (loaderId == LoaderFactory.Type.REMOVE_BILL_ORDER.ordinal()) {
                ((BillOrderListAdapter) getListAdapter()).notifyDataSetChanged();
                notifyOrderListChanged();
            }

            setActivityResult(Activity.RESULT_OK);
        }

        super.onLoaderResult(loader, result);
    }

    @Override
    public void onStartLoading(int id, Bundle args) {
        super.onStartLoading(id, args);
        mEditTextName.setEnabled(false);
        mButtonBillOrderAppend.setEnabled(false);
        setTaxRowEnabled(false);
        setTipRowEnabled(false);
    }

    @Override
    public void onFinishLoading(int id, LoaderResult result) {
        super.onFinishLoading(id, result);
        mEditTextName.setEnabled(true);
        mButtonBillOrderAppend.setEnabled(true);
        setTaxRowEnabled(true);
        setTipRowEnabled(true);
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

    private void initBill(Bundle args) {
        if(args == null) {
            return;
        }

        long billId = args.getLong(BaseColumns._ID);
        mBill = BillList.getInstance().getModel(billId);

        mAppendBillOrderBundle = args;

        setListAdapter(new BillOrderListAdapter(getActivity(), mOnClickListener, mBill));
    }

    private void setActivityResult(int resultCode) {
        if(!mResultSetted) {
            getActivity().setResult(resultCode);
            mResultSetted = true;
        }
    }

    private void notifyBillLoaded() {
        if(mBill == null) {
            return;
        }

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
        return mBill.setName(mEditTextName.getText().toString());
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
        if(mToggleTax.isChecked()) {
            return mBill.setTaxVal(Bill.TaxTipType.ABSOLUTE, mEditTextTaxAbs.getText().toString());
        } else {
            return mBill.setTaxVal(Bill.TaxTipType.PERCENT, mEditTextTaxPer.getText().toString());
        }
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

        Bill.TaxTipType taxType = mBill == null ? null : mBill.getTaxType();
        if(taxType != null) {
            if(taxType == Bill.TaxTipType.ABSOLUTE) {
                mEditTextTaxAbs.setEnabled(enabled);
            }
            else if(taxType == Bill.TaxTipType.PERCENT) {
                mEditTextTaxPer.setEnabled(enabled);
            }
        }
        else {
            mEditTextTaxAbs.setEnabled(enabled);
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
        if(mToggleTip.isChecked()) {
            return mBill.setTipVal(Bill.TaxTipType.ABSOLUTE, mEditTextTipAbs.getText().toString());
        } else {
            return mBill.setTipVal(Bill.TaxTipType.PERCENT, mEditTextTipPer.getText().toString());
        }
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

        Bill.TaxTipType tipType = mBill == null ? null : mBill.getTipType();
        if(tipType != null) {
            if(tipType == Bill.TaxTipType.ABSOLUTE) {
                mEditTextTipAbs.setEnabled(enabled);
            }
            else if(tipType == Bill.TaxTipType.PERCENT) {
                mEditTextTipPer.setEnabled(enabled);
            }
        }
        else {
            mEditTextTipAbs.setEnabled(enabled);
            mEditTextTipPer.setEnabled(enabled);
        }
    }

    private void onTotalChanged() {
        mTextViewTotal.setText(mBill.getFormattedTotal());
    }

    private void startUpdateBillLoader() {
        startLoading(LoaderFactory.Type.UPDATE_BILL.ordinal(), getArguments());
    }

    private class BillOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_order_append_button:
                    startLoading(LoaderFactory.Type.APPEND_BILL_ORDER.ordinal(), mAppendBillOrderBundle);
                    break;
                case R.id.bill_order_edit_button:
                    Intent intent = new Intent(getActivity(), BillOrderActivity.class);
                    intent.putExtras((Bundle) v.getTag());
                    startActivity(intent);
                    break;
                case R.id.bill_order_remove_button:
                    startLoading(LoaderFactory.Type.REMOVE_BILL_ORDER.ordinal(), (Bundle) v.getTag());
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

            if(start == 0 && count == 0 && before == 0 && s.length() == 0) {
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

            if(start == 0 && count == 0 && before == 0 && s.length() == 0) {
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

            if(start == 0 && count == 0 && before == 0 && s.length() == 0) {
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

            if(start == 0 && count == 0 && before == 0 && s.length() == 0) {
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

            if(start == 0 && count == 0 && before == 0 && s.length() == 0) {
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
    private boolean mResultSetted;

    private View.OnClickListener mOnClickListener;

    private Bundle mAppendBillOrderBundle;

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

    public static final String TAG = BillFragment.class.getSimpleName();
}
