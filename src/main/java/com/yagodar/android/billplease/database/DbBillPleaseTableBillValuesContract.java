package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.DbTableBaseContract;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbBillPleaseTableBillValuesContract extends DbTableBaseContract {
    protected DbBillPleaseTableBillValuesContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_TAX_PER, DEF_VAL_TAX_PER);
        addDbTableColumn(COLUMN_NAME_TAX_SUM, DEF_VAL_TAX_SUM);
        addDbTableColumn(COLUMN_NAME_IS_TAX_PER_MAIN, DEF_VAL_TAX_PER_MAIN);
        addDbTableColumn(COLUMN_NAME_TIP_PER, DEF_VAL_TIP_PER);
        addDbTableColumn(COLUMN_NAME_TIP_SUM, DEF_VAL_TIP_SUM);
        addDbTableColumn(COLUMN_NAME_IS_TIP_PER_MAIN, DEF_VAL_TIP_PER_MAIN);
        addDbTableColumn(COLUMN_NAME_DEF_SHARE, DEF_VAL_DEF_SHARE);
    }

    public static DbBillPleaseTableBillValuesContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTableBillValuesContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_TAX_PER = "tax_per";
    public static final String COLUMN_NAME_TAX_SUM = "tax_sum";
    public static final String COLUMN_NAME_IS_TAX_PER_MAIN = "is_tax_per_main";
    public static final String COLUMN_NAME_TIP_PER = "tip_per";
    public static final String COLUMN_NAME_TIP_SUM = "tip_sum";
    public static final String COLUMN_NAME_IS_TIP_PER_MAIN = "is_tip_per_main";
    public static final String COLUMN_NAME_DEF_SHARE = "def_share";

    private static final double DEF_VAL_TAX_PER = 0.0;
    private static final double DEF_VAL_TAX_SUM = 0.0;
    private static final boolean DEF_VAL_TAX_PER_MAIN = true;
    private static final double DEF_VAL_TIP_PER = 0.0;
    private static final double DEF_VAL_TIP_SUM = 0.0;
    private static final boolean DEF_VAL_TIP_PER_MAIN = true;
    private static final int DEF_VAL_DEF_SHARE = 1;

    private static final String TABLE_NAME = "bill_values";

    private static DbBillPleaseTableBillValuesContract INSTANCE;
}
