package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.DbTableBaseContract;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbBillPleaseTableBillContract extends DbTableBaseContract {
    private DbBillPleaseTableBillContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_ITEM_NAME, DEF_VAL_ITEM_NAME);
        addDbTableColumn(COLUMN_NAME_COST, DEF_VAL_COST);
        addDbTableColumn(COLUMN_NAME_SHARE, DEF_VAL_SHARE);
    }

    public static DbBillPleaseTableBillContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTableBillContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_ITEM_NAME = "item_name";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_SHARE = "share";

    private static final String DEF_VAL_ITEM_NAME = "-";
    private static final double DEF_VAL_COST = 0.0;
    private static final int DEF_VAL_SHARE = 1;

    private static final String TABLE_NAME = "bill";

    private static DbBillPleaseTableBillContract INSTANCE;
}
