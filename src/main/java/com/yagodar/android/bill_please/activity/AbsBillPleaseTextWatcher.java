package com.yagodar.android.bill_please.activity;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by yagodar on 30.06.2015.
 */
public abstract class AbsBillPleaseTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public abstract void onTextChanged(CharSequence s, int start, int before, int count);

    @Override
    public void afterTextChanged(Editable s) {}
}
