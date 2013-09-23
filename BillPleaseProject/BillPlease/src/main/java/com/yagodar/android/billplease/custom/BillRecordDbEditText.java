package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.yagodar.android.billplease.R;
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
            postSetNormalText(String.valueOf(getDbValue()));
        }
        else {
            postSetHintText(getContentDescription().toString());
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

    public void postSetNormalText(final String normalText) {
        try {
            post(new Runnable() {
                @Override
                public void run() {
                    setTypeface(null, Typeface.NORMAL);
                    setTextColor(getResources().getColor(R.color.bill_record_et_text));
                    setText(normalText);
                }
            });
        }
        catch(Exception ignored) {}
    }

    public void postSetHintText(final String defText) {
        try {
            post(new Runnable() {
                @Override
                public void run() {
                    setTypeface(null, Typeface.ITALIC);
                    setTextColor(getResources().getColor(R.color.bill_record_et_hint_text));
                    setText(defText);
                }
            });
        }
        catch(Exception ignored) {}
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
