package com.yagodar.android.bill_please.store.db;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;
import com.yagodar.android.database.sqlite.DbTableColumn;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbTableOrdersContract extends AbstractDbTableContract {
    private DbTableOrdersContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, true, _ID);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_ORDER_NAME);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_COST);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_SHARE);
        addDbTableColumn(DbTableColumn.TYPE_LONG, COLUMN_NAME_BILL_ID);
    }

    public static DbTableOrdersContract getInstance() {
        return INSTANCE;
    }

    public static final String COLUMN_NAME_ORDER_NAME = "name";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_SHARE = "share";
    public static final String COLUMN_NAME_BILL_ID = "bill_id";

    public static final String TABLE_NAME = "orders";

    private static final DbTableOrdersContract INSTANCE = new DbTableOrdersContract();
}
