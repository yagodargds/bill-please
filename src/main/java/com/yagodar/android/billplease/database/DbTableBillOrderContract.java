package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;
import com.yagodar.android.database.sqlite.DbTableColumn;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbTableBillOrderContract extends AbstractDbTableContract {
    private DbTableBillOrderContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_ITEM_NAME, DEF_VAL_ITEM_NAME);
        addDbTableColumn(COLUMN_NAME_COST, DEF_VAL_COST);
        addDbTableColumn(COLUMN_NAME_SHARE, DEF_VAL_SHARE);
        addDbTableColumn(DbTableColumn.TYPE_LONG, COLUMN_NAME_BILL_ID);
    }

    public static DbTableBillOrderContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbTableBillOrderContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_ITEM_NAME = "item_name";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_SHARE = "share";

    public static final String COLUMN_NAME_BILL_ID = "bill_id";

    private static final String DEF_VAL_ITEM_NAME = "";
    private static final String DEF_VAL_COST = "-1.0";
    private static final String DEF_VAL_SHARE = "-1";

    private static final String TABLE_NAME = "bill_order";

    private static DbTableBillOrderContract INSTANCE;
}
