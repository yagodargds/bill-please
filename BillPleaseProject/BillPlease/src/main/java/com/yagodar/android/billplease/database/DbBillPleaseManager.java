package com.yagodar.android.billplease.database;

import android.content.ContentValues;
import android.content.Context;
import android.provider.BaseColumns;

import com.yagodar.android.billplease.R;
import com.yagodar.android.database.sqlite.DbBaseManager;
import com.yagodar.android.database.sqlite.DbTableBaseManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Yagodar on 19.08.13.
 */
public class DbBillPleaseManager extends DbBaseManager<DbBillPleaseHelper> {
    private DbBillPleaseManager(Context context) {
        super(context);
    }

    @Override
    protected Collection<DbTableBaseManager> registerDbTableManagers() {
        ArrayList<DbTableBaseManager> dbTableManagers = new ArrayList<DbTableBaseManager>();

        dbTableManagers.add(new DbTableBaseManager<DbBillPleaseManager>(DbBillPleaseTableBillContract.getInstance()));
        dbTableManagers.add(new DbTableBaseManager<DbBillPleaseManager>(DbBillPleaseTableBillValuesContract.getInstance()));

        return dbTableManagers;
    }

    @Override
    protected DbBillPleaseHelper registerDbHelper(Context context) {
        return new DbBillPleaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DbBillPleaseManager getInstance(Context context) {
        if(INSTANCE == null || !INSTANCE.isContextEquals(context)) {
            INSTANCE = new DbBillPleaseManager(context);
        }

        return INSTANCE;
    }

    private static DbBillPleaseManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 45;
}