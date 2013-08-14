package com.yagodar.billplease.db;

import android.provider.BaseColumns;

import com.yagodar.db.BaseContract;

/**
 * Created by Yagodar on 13.08.13.
 */
public abstract class BillPleaseContract implements BaseContract {
    public abstract class PersonalBillColumns implements BaseColumns {
        public static final String TABLE_NAME = "personal_bill";
        public static final String COLUMN_NAME_ITEM = "item";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_SHARE = "share";
        public static final String COLUMN_NAME_CHANGES_MASK = "changes_mask";
    }


}
