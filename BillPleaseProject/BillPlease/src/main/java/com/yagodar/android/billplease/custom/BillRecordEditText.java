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
public class BillRecordEditText<T extends Object> extends DbEditText<T> {
    public BillRecordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        nextFocusViews = new SparseArray<View>();
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

    @Override
    public View focusSearch(int direction) {
        View searchedView = nextFocusViews.get(direction);

        if(searchedView == null) {
            searchedView = super.focusSearch(direction);
        }

        return searchedView;
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

    public void setNextFocusView(int direction, View view) {
        nextFocusViews.put(direction, view);
    }

    public View getNextFocusView(int direction) {
        return nextFocusViews.get(direction);
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
    private SparseArray<View> nextFocusViews;
}
