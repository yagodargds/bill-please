package com.yagodar.android.billplease.database;

import android.content.Context;

import com.yagodar.android.database.sqlite.DbBaseManager;

/**
 * Created by Yagodar on 19.08.13.
 */
public class DbBillPleaseManager extends DbBaseManager<DbBillPleaseHelper> {
    private DbBillPleaseManager(Context context) {
        super(new DbBillPleaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION));

        addDbTableManager(new DbBillPleaseTableBillManager());
    }

    public static DbBillPleaseManager getInstance(Context context) {
        if(INSTANCE == null || !INSTANCE.isContextEquals(context)) {
            INSTANCE = new DbBillPleaseManager(context);
        }

        return INSTANCE;
    }

    private static DbBillPleaseManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 3;
}