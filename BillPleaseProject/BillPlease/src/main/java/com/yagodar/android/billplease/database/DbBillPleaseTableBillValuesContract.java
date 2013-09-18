package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.DbTableBaseContract;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbBillPleaseTableBillValuesContract extends DbTableBaseContract {
    protected DbBillPleaseTableBillValuesContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_TAX, DEF_VAL_TAX);
        addDbTableColumn(COLUMN_NAME_TIP, DEF_VAL_TIP);
        addDbTableColumn(COLUMN_NAME_DEF_SHARE, DEF_VAL_DEF_SHARE);
    }

    public static DbBillPleaseTableBillValuesContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTableBillValuesContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_TAX = "tax";
    public static final String COLUMN_NAME_TIP = "tip";
    public static final String COLUMN_NAME_DEF_SHARE = "def_share";

    private static final double DEF_VAL_TAX = 0.0;
    private static final double DEF_VAL_TIP = 0.0;
    private static final int DEF_VAL_DEF_SHARE = 1;

    private static final String TABLE_NAME = "bill_values";

    private static DbBillPleaseTableBillValuesContract INSTANCE;
}
