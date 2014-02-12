package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbTableBillContract extends AbstractDbTableContract {
    private DbTableBillContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_ITEM_NAME, DEF_VAL_ITEM_NAME);
        addDbTableColumn(COLUMN_NAME_COST, DEF_VAL_COST);
        addDbTableColumn(COLUMN_NAME_SHARE, DEF_VAL_SHARE);
    }

    public static DbTableBillContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbTableBillContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_ITEM_NAME = "item_name";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_SHARE = "share";

    private static final String DEF_VAL_ITEM_NAME = "";
    private static final String DEF_VAL_COST = "-1.0";
    private static final String DEF_VAL_SHARE = "-1";

    private static final String TABLE_NAME = "bill";

    private static DbTableBillContract INSTANCE;
}
