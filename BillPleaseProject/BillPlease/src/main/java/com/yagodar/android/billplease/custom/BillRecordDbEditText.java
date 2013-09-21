package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.DbTableColumn;
import com.yagodar.android.database.sqlite.custom.DbEditText;

/**
 * Created by Yagodar on 07.09.13.
 */
public class BillRecordDbEditText<T extends Object> extends DbEditText<T> {
    public BillRecordDbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void pushToDb() {
        setChanged(getText().toString().length() > 0 && (isInputRegistered() || isChanged()));
        super.pushToDb();
    }

    @Override
    public void pullFromDb() {
        if(isChanged()) {
            super.pullFromDb();
        }
        else {
            clearText();
        }
    }

    public void initDbManagerChanging(DbTableBaseManager dbTableManagerChanging, String dbTableColumnNameChanging) {
        if(dbTableManagerChanging != null) {
            this.dbTableManagerChanging = dbTableManagerChanging;
            this.dbTableColumnChanging = dbTableManagerChanging.getTableContract().getDbTableColumn(dbTableColumnNameChanging);
        }
    }

    public boolean isChanged() {
        boolean result = false;

        if(dbTableManagerChanging != null && dbTableColumnChanging != null) {
            long dbRecordId = getDbRecordId();
            if(dbRecordId != -1) {
                try {
                    result = (Boolean) dbTableManagerChanging.getColumnValue(dbRecordId * getId(), dbTableColumnChanging.getColumnName());
                }
                catch(Exception ignored) {}
            }
        }

        return result;
    }

    private void setChanged(boolean value) {
        if(dbTableManagerChanging != null && dbTableColumnChanging != null) {
            long dbRecordId = getDbRecordId();
            if(dbRecordId != -1 && isChanged() != value) {
                dbTableManagerChanging.setColumnValue(dbRecordId * getId(), dbTableColumnChanging.getColumnName(), value);
            }
        }
    }

    private DbTableBaseManager dbTableManagerChanging;
    private DbTableColumn dbTableColumnChanging;
}
