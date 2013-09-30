package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.DbEditText;

/**
 * Created by Yagodar on 29.09.13.
 */
public class BillDbEditText<T extends Object> extends DbEditText<T> {
    public BillDbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DbTableBaseManager registerTableManager(String s) {
        return null;
    }
}
