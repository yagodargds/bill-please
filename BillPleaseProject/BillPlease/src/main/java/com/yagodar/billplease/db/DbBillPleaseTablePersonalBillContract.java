package com.yagodar.billplease.db;

import com.yagodar.db.DbBaseHelper;
import com.yagodar.db.DbTableBaseContract;
import com.yagodar.db.DbTableColumnInfo;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbBillPleaseTablePersonalBillContract extends DbTableBaseContract {
    private DbBillPleaseTablePersonalBillContract() {
        super(TABLE_NAME);

        addDbTableColumnInfo(new DbTableColumnInfo(COLUMN_NAME_TAG, true));
        addDbTableColumnInfo(new DbTableColumnInfo(COLUMN_NAME_ITEM_NAME, DEF_VAL_ITEM_NAME));
        addDbTableColumnInfo(new DbTableColumnInfo(COLUMN_NAME_COST, DEF_VAL_COST));
        addDbTableColumnInfo(new DbTableColumnInfo(COLUMN_NAME_SHARE, DEF_VAL_SHARE));
        addDbTableColumnInfo(new DbTableColumnInfo(COLUMN_NAME_CHANGES_MASK, DEF_VAL_CHANGES_MASK));
    }

    public static DbBillPleaseTablePersonalBillContract getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DbBillPleaseTablePersonalBillContract();
        }

        return INSTANCE;
    }

    protected static final String COLUMN_NAME_TAG = _ID;
    protected static final String COLUMN_NAME_ITEM_NAME = "item_name";
    protected static final String COLUMN_NAME_COST = "cost";
    protected static final String COLUMN_NAME_SHARE = "share";
    protected static final String COLUMN_NAME_CHANGES_MASK = "changes_mask";

    private static final String TABLE_NAME = "personal_bill";

    private static final String DEF_VAL_ITEM_NAME = "-";
    private static final double DEF_VAL_COST = 0.0;
    private static final int DEF_VAL_SHARE = 1;
    private static final byte DEF_VAL_CHANGES_MASK = 0;

    private static DbBillPleaseTablePersonalBillContract INSTANCE;
}
