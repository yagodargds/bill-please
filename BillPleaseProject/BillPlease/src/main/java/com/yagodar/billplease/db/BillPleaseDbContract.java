package com.yagodar.billplease.db;

import android.provider.BaseColumns;

import com.yagodar.db.DbHelper;

/**
 * Created by Yagodar on 13.08.13.
 */
public abstract class BillPleaseDbContract {
    public abstract class TablePersonalBill implements BaseColumns {
        public static final String TABLE_NAME = "personal_bill";

        public static final String COLUMN_NAME_ITEM_NAME = "item_name";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_SHARE = "share";
        public static final String COLUMN_NAME_CHANGES_MASK = "changes_mask";

        public static final String DEF_VAL_ITEM_NAME = "-";
        public static final double DEF_VAL_COST = 0.0;
        public static final int DEF_VAL_SHARE = 1;
        public static final byte DEF_VAL_CHANGES_MASK = 0;
    }
}
