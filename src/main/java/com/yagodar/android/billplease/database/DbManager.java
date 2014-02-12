package com.yagodar.android.billplease.database;

import android.content.Context;

import com.yagodar.android.database.sqlite.AbstractDbManager;
import com.yagodar.android.database.sqlite.AbstractDbTableContract;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Yagodar on 19.08.13.
 */
public class DbManager extends AbstractDbManager {
    private DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected Collection<AbstractDbTableContract> registerDbTableContracts() {
        ArrayList<AbstractDbTableContract> dbTableContracts = new ArrayList<AbstractDbTableContract>();

        dbTableContracts.add(DbTableBillContract.getInstance());
        dbTableContracts.add(DbTableBillValuesContract.getInstance());

        return dbTableContracts;
    }

    public static DbManager getInstance(Context context) {
        if(INSTANCE == null || !INSTANCE.isContextEquals(context)) {
            INSTANCE = new DbManager(context);
        }

        return INSTANCE;
    }

    private static DbManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 1;
}