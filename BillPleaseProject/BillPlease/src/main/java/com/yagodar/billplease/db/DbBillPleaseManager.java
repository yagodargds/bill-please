package com.yagodar.billplease.db;

import android.content.Context;

import com.yagodar.db.DbBaseManager;

/**
 * Created by Yagodar on 19.08.13.
 */
public class DbBillPleaseManager extends DbBaseManager<DbBillPleaseHelper> {
    private DbBillPleaseManager(Context context) {
        super(new DbBillPleaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION));
        this.context = context;

        addDbTableManager(new DbBillPleaseTablePersonalBillManager());
    }

    public static DbBillPleaseManager getInstance(Context context) {
        if(INSTANCE == null || INSTANCE.context == null || !INSTANCE.context.equals(context)) {
            INSTANCE = new DbBillPleaseManager(context);
        }

        return INSTANCE;
    }

    private final Context context;

    private static DbBillPleaseManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 2;
}