package com.yagodar.android.bill_please.store.db;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;
import com.yagodar.android.database.sqlite.DbTableColumn;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbTableBillsContract extends AbstractDbTableContract {
    protected DbTableBillsContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, true,  _ID);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_BILL_NAME);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TAX_VAL);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TAX_TYPE);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TIP_VAL);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TIP_TYPE);
    }

    public static DbTableBillsContract getInstance() {
        return INSTANCE;
    }

    public static final String COLUMN_NAME_BILL_NAME = "name";
    public static final String COLUMN_NAME_TAX_VAL = "tax_val";
    public static final String COLUMN_NAME_TAX_TYPE = "tax_type";
    public static final String COLUMN_NAME_TIP_VAL = "tip_val";
    public static final String COLUMN_NAME_TIP_TYPE = "tip_type";

    public static final String TABLE_NAME = "bills";

    private static final DbTableBillsContract INSTANCE = new DbTableBillsContract();
}
