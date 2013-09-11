package com.yagodar.android.billplease.database;

import com.yagodar.android.database.sqlite.DbTableBaseContract;

/**
 * Created by Yagodar on 11.09.13.
 */
public class DbBillPleaseTableBillTaxTipContract extends DbTableBaseContract {
    protected DbBillPleaseTableBillTaxTipContract() {
        super(TABLE_NAME);

        addDbTableColumn(true, _ID);
        addDbTableColumn(COLUMN_NAME_TAX, DEF_VAL_TAX);
        addDbTableColumn(COLUMN_NAME_TIP, DEF_VAL_TIP);
    }

    public static DbBillPleaseTableBillTaxTipContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTableBillTaxTipContract();
        }

        return INSTANCE;
    }

    public static final String COLUMN_NAME_TAX = "tax";
    public static final String COLUMN_NAME_TIP = "tip";

    private static final double DEF_VAL_TAX = 0.0;
    private static final double DEF_VAL_TIP = 0.0;

    private static final String TABLE_NAME = "bill_tax_tip";

    private static DbBillPleaseTableBillTaxTipContract INSTANCE;
}
