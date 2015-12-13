package com.yagodar.android.bill_please.store.db;

import android.content.Context;

import com.yagodar.android.database.sqlite.AbstractDbManager;
import com.yagodar.android.database.sqlite.AbstractDbTableContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yagodar on 19.08.13.
 */
public class DbManager extends AbstractDbManager {
    private DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    protected List<AbstractDbTableContract> regTableContracts() {
        List<AbstractDbTableContract> dbTableContracts = new ArrayList<>();

        dbTableContracts.add(DbTableBillsContract.getInstance());
        dbTableContracts.add(DbTableOrdersContract.getInstance());

        return dbTableContracts;
    }

    public static DbManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("DbManager instance not set!");
        }

        return INSTANCE;
    }

    public static void initInstance(Context appContext) {
        INSTANCE = new DbManager(appContext);
    }

    private static DbManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 1;
}