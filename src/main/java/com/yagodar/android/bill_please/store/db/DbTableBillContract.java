package com.yagodar.android.bill_please.store.db;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;
import com.yagodar.android.database.sqlite.DbTableColumn;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbTableBillContract extends AbstractDbTableContract {
    protected DbTableBillContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_BILL_NAME);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TAX_VAL);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TAX_TYPE);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TIP_VAL);
        addDbTableColumn(DbTableColumn.TYPE_STRING, COLUMN_NAME_TIP_TYPE);
    }

    public static DbTableBillContract getInstance() {
        return INSTANCE;
    }

    public static final String COLUMN_NAME_BILL_NAME = "bill_name";
    public static final String COLUMN_NAME_TAX_VAL = "tax_val";
    public static final String COLUMN_NAME_TAX_TYPE = "tax_type";
    public static final String COLUMN_NAME_TIP_VAL = "tip_val";
    public static final String COLUMN_NAME_TIP_TYPE = "tip_type";

    private static final String TABLE_NAME = "bill";

    private static final DbTableBillContract INSTANCE = new DbTableBillContract();
}
