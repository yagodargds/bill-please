package com.yagodar.android.bill_please.activity.bill_order;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.AbsBillPleaseTextWatcher;
import com.yagodar.android.bill_please.activity.LoaderFactory;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.fragment.IOnActivityBackPressedListener;
import com.yagodar.android.custom.fragment.progress.common_view.AbsLoaderProgressFragment;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 30.06.2015.
 */
public class BillOrderFragment extends AbsLoaderProgressFragment implements IOnActivityBackPressedListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long billId = getArguments().getLong(DbTableBillOrderContract.COLUMN_NAME_BILL_ID);
        long billOrderId = getArguments().getLong(BaseColumns._ID);
        mBill = BillList.getInstance().getModel(billId);
        mBillOrder = mBill.getModel(billOrderId);

        setContentView(R.layout.bill_order_view);

        mEditTextName = (EditText) getActivity().findViewById(R.id.bill_order_et_name);
        mEditTextCost = (EditText) getActivity().findViewById(R.id.bill_order_et_cost);
        mEditTextShare = (EditText) getActivity().findViewById(R.id.bill_order_et_share);
        mTextViewSubtotal = (TextView) getActivity().findViewById(R.id.bill_order_subtotal);

        if (savedInstanceState != null) {
            mEditTextName.setText(savedInstanceState.getString(NAME_TAG));
            mEditTextCost.setText(savedInstanceState.getString(COST_TAG));
            mEditTextShare.setText(savedInstanceState.getString(SHARE_TAG));
            mTextViewSubtotal.setText(savedInstanceState.getString(SUBTOTAL_TAG));
        } else {
            notifyBillOrderLoaded();
        }

        mEditTextName.addTextChangedListener(new BillOrderNameTextWatcher());
        mEditTextCost.addTextChangedListener(new BillOrderCostTextWatcher());
        mEditTextShare.addTextChangedListener(new BillOrderShareTextWatcher());

        View.OnFocusChangeListener onFocusChangeListener = new BillOrderOnFocusChangeListener();

        mEditTextName.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextCost.setOnFocusChangeListener(onFocusChangeListener);
        mEditTextShare.setOnFocusChangeListener(onFocusChangeListener);

        mEditTextHidden = (EditText) getActivity().findViewById(R.id.bill_order_et_hidden);
        mEditTextHidden.setOnFocusChangeListener(onFocusChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        //setAvailable(true);

        if (getLoaderManager().getLoader(LoaderFactory.Type.UPDATE_BILL_ORDER.ordinal()) != null) {
            startUpdateBillOrderLoader();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(NAME_TAG, mEditTextName.getText().toString());
        outState.putString(COST_TAG, mEditTextCost.getText().toString());
        outState.putString(SHARE_TAG, mEditTextShare.getText().toString());
        outState.putString(SUBTOTAL_TAG, mTextViewSubtotal.getText().toString());
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader = LoaderFactory.createLoader(getActivity(), id, args);
        if (id == LoaderFactory.Type.UPDATE_BILL_ORDER.ordinal()) {
            loader.setUpdateThrottle(UPDATE_BILL_ORDER_TIMER_TASK_DELAY_MILLIS);
        }
        return loader;
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

    private void notifyBillOrderLoaded() {
        onNameLoaded();
        onCostLoaded();
        onShareLoaded();
        onSubtotalChanged();
    }

    private void notifyOrderChanged() {
        onSubtotalChanged();
    }

    private boolean updateModelName() {
        return mBillOrder.setName(mEditTextName.getText().toString());
    }

    private void onNameLoaded() {
        mLoading = true;
        mEditTextName.setText(mBillOrder.getName());
        mLoading = false;
    }

    private boolean updateModelCost() {
        return mBillOrder.setCost(mEditTextCost.getText().toString());
    }

    private void onCostLoaded() {
        mLoading = true;
        mEditTextCost.setText(mBillOrder.getFormattedCost());
        mLoading = false;
    }

    private boolean updateModelShare() {
        return mBillOrder.setShare(mEditTextShare.getText().toString());
    }

    private void onShareLoaded() {
        mLoading = true;
        mEditTextShare.setText(mBillOrder.getFormattedShare());
        mLoading = false;
    }

    private void onSubtotalChanged() {
        mTextViewSubtotal.setText(mBillOrder.getFormattedSubtotal());
    }

    private void startUpdateBillOrderLoader() {
        startLoading(LoaderFactory.Type.UPDATE_BILL_ORDER.ordinal(), getArguments());
    }

    private class BillOrderOnFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch(v.getId()) {
                case R.id.bill_order_et_name:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onNameLoaded();
                    }
                    break;
                case R.id.bill_order_et_cost:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onCostLoaded();
                    }
                    break;
                case R.id.bill_order_et_share:
                    if(hasFocus) {
                        mLastEditText = (EditText) v;
                    } else {
                        onShareLoaded();
                    }
                    break;
                case R.id.bill_order_et_hidden:
                    if(hasFocus) {
                        mLastEditText = null;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class BillOrderNameTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(updateModelName()) {
                startUpdateBillOrderLoader();
            }
        }
    }

    private class BillOrderCostTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(updateModelCost()) {
                startUpdateBillOrderLoader();
                notifyOrderChanged();
            }
        }
    }

    private class BillOrderShareTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(mLoading) {
                return;
            }
            if(updateModelShare()) {
                startUpdateBillOrderLoader();
                notifyOrderChanged();
            }
        }
    }

    private boolean mLoading;

    private Bill mBill;
    private BillOrder mBillOrder;

    private EditText mEditTextName;
    private EditText mEditTextCost;
    private EditText mEditTextShare;
    private TextView mTextViewSubtotal;

    private EditText mEditTextHidden;

    private EditText mLastEditText;

    private final static String NAME_TAG = DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME;
    private final static String COST_TAG = DbTableBillOrderContract.COLUMN_NAME_COST;
    private final static String SHARE_TAG = DbTableBillOrderContract.COLUMN_NAME_SHARE;
    private final static String SUBTOTAL_TAG = DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME + "_subtotal";

    private final static long UPDATE_BILL_ORDER_TIMER_TASK_DELAY_MILLIS = 1500L;
}
