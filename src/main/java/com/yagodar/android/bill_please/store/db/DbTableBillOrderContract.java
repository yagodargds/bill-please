package com.yagodar.android.bill_please.store.db;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;
import com.yagodar.android.database.sqlite.DbTableColumn;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbTableBillOrderContract extends AbstractDbTableContract {
    private DbTableBillOrderContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_ORDER_NAME);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_COST);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_SHARE);
        addDbTableColumn(DbTableColumn.TYPE_LONG, COLUMN_NAME_BILL_ID);
    }

    public static DbTableBillOrderContract getInstance() {
        return INSTANCE;
    }

    public static final String COLUMN_NAME_ORDER_NAME = "order_name";
    public static final String COLUMN_NAME_COST = "cost";
    public static final String COLUMN_NAME_SHARE = "share";
    public static final String COLUMN_NAME_BILL_ID = "bill_id";

    private static final String TABLE_NAME = "bill_order";

    private static final DbTableBillOrderContract INSTANCE = new DbTableBillOrderContract();
}
