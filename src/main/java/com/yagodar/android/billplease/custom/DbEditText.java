package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.yagodar.android.billplease.database.DbManager;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.android.database.sqlite.custom.AbstractDbEditText;

/**
 * Created by Yagodar on 29.09.13.
 */
public class DbEditText<T extends Object> extends AbstractDbEditText<T> {
    public DbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected DbTableManager registerTableManager(String tableName) {
        DbManager dbManager = DbManager.getInstance(getContext());
        if(dbManager != null) {
            return dbManager.getDbTableManager(tableName);
        }

        return null;
    }
}
