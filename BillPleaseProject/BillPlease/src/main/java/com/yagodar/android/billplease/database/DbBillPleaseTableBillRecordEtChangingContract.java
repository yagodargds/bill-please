package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.DbTableBaseContract;

/**
 * Created by Yagodar on 30.08.13.
 */
public class DbBillPleaseTableBillRecordEtChangingContract extends DbTableBaseContract {
    protected DbBillPleaseTableBillRecordEtChangingContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_IS_CHANGED, DEF_VAL_IS_CHANGED);
    }

    public static DbBillPleaseTableBillRecordEtChangingContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTableBillRecordEtChangingContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_IS_CHANGED = "is_changed";

    private static final boolean DEF_VAL_IS_CHANGED = false;

    private static final String TABLE_NAME = "bill_record_et_changing";

    private static DbBillPleaseTableBillRecordEtChangingContract INSTANCE;
}
