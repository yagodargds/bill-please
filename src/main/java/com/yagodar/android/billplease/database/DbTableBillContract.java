package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.AbstractDbTableContract;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbTableBillContract extends AbstractDbTableContract {
    protected DbTableBillContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_TAX_VAL, DEF_VAL_TAX_VAL);
        addDbTableColumn(COLUMN_NAME_TAX_TYPE, DEF_VAL_TAX_TYPE);
        addDbTableColumn(COLUMN_NAME_TIP_VAL, DEF_VAL_TIP_VAL);
        addDbTableColumn(COLUMN_NAME_TIP_TYPE, DEF_VAL_TIP_TYPE);
        addDbTableColumn(COLUMN_NAME_DEF_ITEM_NAME, DEF_VAL_DEF_ITEM_NAME);
        addDbTableColumn(COLUMN_NAME_DEF_COST, DEF_VAL_DEF_COST);
        addDbTableColumn(COLUMN_NAME_DEF_SHARE, DEF_VAL_DEF_SHARE);
    }

    public static DbTableBillContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbTableBillContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_TAX_VAL = "tax_val";
    public static final String COLUMN_NAME_TAX_TYPE = "tax_type";

    public static final String COLUMN_NAME_TIP_VAL = "tip_val";
    public static final String COLUMN_NAME_TIP_TYPE = "tip_type";

    public static final String COLUMN_NAME_DEF_ITEM_NAME = "def_item_name";
    public static final String COLUMN_NAME_DEF_COST = "def_cost";
    public static final String COLUMN_NAME_DEF_SHARE = "def_share";

    private static final String DEF_VAL_TAX_VAL = "-1.0";
    private static final int DEF_VAL_TAX_TYPE = -1;

    private static final String DEF_VAL_TIP_VAL = "-1.0";
    private static final int DEF_VAL_TIP_TYPE = -1;

    private static final String DEF_VAL_DEF_ITEM_NAME = "";
    private static final String DEF_VAL_DEF_COST = "-1.0";
    private static final String DEF_VAL_DEF_SHARE = "-1";

    private static final String TABLE_NAME = "bill";

    private static DbTableBillContract INSTANCE;
}
