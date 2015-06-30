package com.yagodar.android.bill_please.activity.bill_order;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.AbsBillPleaseTextWatcher;
import com.yagodar.android.bill_please.activity.BillPleaseLoaderFactory;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.fragment.IOnActivityBackPressedListener;
import com.yagodar.android.custom.fragment.progress.AbsLoaderProgressFragment;
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

        View.OnClickListener onClickListener = new BillOrderOnClickListener();

        mButtonBillOrderUpdate = (Button) getActivity().findViewById(R.id.bill_order_update_button);
        mButtonBillOrderUpdate.setOnClickListener(onClickListener);

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

        setAvailable(true);

        if (getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL_ORDER.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL_ORDER.ordinal(), null, true);
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
        return BillPleaseLoaderFactory.createLoader(getActivity(), id, args);
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

    private void updateModelName() {
        mBillOrder.setName(mEditTextName.getText().toString());
    }

    private void onNameLoaded() {
        mEditTextName.setText(mBillOrder.getName());
    }

    private void updateModelCost() {
        mBillOrder.setCost(mEditTextCost.getText().toString());
    }

    private void onCostLoaded() {
        mEditTextCost.setText(mBillOrder.getFormattedCost());
    }

    private void updateModelShare() {
        mBillOrder.setShare(mEditTextShare.getText().toString());
    }

    private void onShareLoaded() {
        mEditTextShare.setText(mBillOrder.getFormattedShare());
    }

    private void onSubtotalChanged() {
        mTextViewSubtotal.setText(mBillOrder.getFormattedSubtotal());
    }

    private class BillOrderOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_order_update_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL_ORDER.ordinal(), getArguments(), true);
                    break;
                default:
                    break;
            }
        }
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
            updateModelName();
        }
    }

    private class BillOrderCostTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateModelCost();
            notifyOrderChanged();
        }
    }

    private class BillOrderShareTextWatcher extends AbsBillPleaseTextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateModelShare();
            notifyOrderChanged();
        }
    }

    private Bill mBill;
    private BillOrder mBillOrder;

    private Button mButtonBillOrderUpdate;

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
}
