package com.yagodar.billplease.db;

import android.provider.BaseColumns;

import com.yagodar.db.DbConstants;

/**
 * Created by Yagodar on 13.08.13.
 */
public abstract class BillPleaseContract {
    public abstract class TablePersonalBill implements BaseColumns {
        public static final String TABLE_NAME = "personal_bill";

        public static final String COLUMN_NAME_ITEM = "item";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_SHARE = "share";
        public static final String COLUMN_NAME_CHANGES_MASK = "changes_mask";

        public static final String DEF_VAL_ITEM = "-";
        public static final double DEF_VAL_COST = 0.0;
        public static final double DEF_VAL_SHARE = 1;
        public static final byte DEF_VAL_CHANGES_MASK = 0;

        public static final String EXPR_CREATE_TABLE = DbConstants.EXPR_CREATE_TABLE_IF_NOT_EXISTS
                                                        + TABLE_NAME
                                                        + DbConstants.BRACKET_OPEN
                                                        + _ID + DbConstants.TYPE_INTEGER + DbConstants.EXPR_PRIMARY_KEY + DbConstants.SEP_COMMA
                                                        + COLUMN_NAME_ITEM + DbConstants.TYPE_TEXT + DbConstants.EXPR_NOT_NULL + DbConstants.SEP_COMMA
                                                        + COLUMN_NAME_COST + DbConstants.TYPE_DOUBLE + DbConstants.EXPR_DEFAULT + DEF_VAL_COST + DbConstants.SEP_COMMA
                                                        + COLUMN_NAME_SHARE + DbConstants.TYPE_INTEGER + DbConstants.EXPR_DEFAULT + DEF_VAL_SHARE + DbConstants.SEP_COMMA
                                                        + COLUMN_NAME_CHANGES_MASK + DbC;

        public static final String EXPR_DELETE_TABLE = "bill_please.db";
    }



    public static final String DATABASE_NAME = "bill_please.db";
    public static final int DATABASE_VERSION = 1;
}
