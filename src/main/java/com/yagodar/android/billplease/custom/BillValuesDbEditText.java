package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.yagodar.android.billplease.database.DbBillPleaseManager;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.custom.AbstractDbEditText;

/**
 * Created by Yagodar on 07.10.13.
 */
public class BillValuesDbEditText<T extends Object> extends AbstractDbEditText<T> {
    public BillValuesDbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DbTableBaseManager registerTableManager(String tableName) {
        DbBillPleaseManager dbBillPleaseManager = DbBillPleaseManager.getInstance(getContext());
        if(dbBillPleaseManager != null) {
            return dbBillPleaseManager.getDbTableManager(tableName);
        }

        return null;
    }
}
