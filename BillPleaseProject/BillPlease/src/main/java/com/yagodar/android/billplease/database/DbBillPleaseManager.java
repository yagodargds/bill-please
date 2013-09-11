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
        dbTableManagers.add(new DbTableBaseManager<DbBillPleaseManager>(DbBillPleaseTableBillRecordEtChangingContract.getInstance()));
        dbTableManagers.add(new DbTableBaseManager<DbBillPleaseManager>(DbBillPleaseTableBillTaxTipContract.getInstance()));

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

    public long addNewBillRecord() {
        DbTableBaseManager dbBillPleaseTableBillManager = getDbTableManager(DbBillPleaseTableBillContract.getInstance());
        DbTableBaseManager dbBillPleaseTableBillRecordEtChangingManager = getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());

        long dbRecordId = dbBillPleaseTableBillManager.addRecord();

        if(dbRecordId != -1) {
            boolean success = true;

            ContentValues values = new ContentValues();

            values.put(BaseColumns._ID, dbRecordId * R.id.et_item_name);
            if(dbBillPleaseTableBillRecordEtChangingManager.addRecord(null, values) == -1) {
                success = false;
            }
            else {
                values.clear();

                values.put(BaseColumns._ID, dbRecordId * R.id.et_cost);
                if(dbBillPleaseTableBillRecordEtChangingManager.addRecord(null, values) == -1) {
                    success = false;
                    dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_item_name);
                }
                else {
                    values.clear();

                    values.put(BaseColumns._ID, dbRecordId * R.id.et_share);
                    if(dbBillPleaseTableBillRecordEtChangingManager.addRecord(null, values) == -1) {
                        success = false;
                        dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_item_name);
                        dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_cost);
                    }
                }
            }

            if(!success) {
                dbBillPleaseTableBillManager.delRecord(dbRecordId);
                dbRecordId = -1;
            }
        }

        return dbRecordId;
    }

    public void delBillRecord(long dbRecordId) {
        getDbTableManager(DbBillPleaseTableBillContract.getInstance()).delRecord(dbRecordId);

        DbTableBaseManager dbBillPleaseTableBillRecordEtChangingManager = getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance());
        dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_item_name);
        dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_cost);
        dbBillPleaseTableBillRecordEtChangingManager.delRecord(dbRecordId * R.id.et_share);
    }

    public void delAllBillRecords() {
        getDbTableManager(DbBillPleaseTableBillContract.getInstance()).delAllRecords();
        getDbTableManager(DbBillPleaseTableBillRecordEtChangingContract.getInstance()).delAllRecords();
    }

    private static DbBillPleaseManager INSTANCE;

    private static final String DATABASE_NAME = "bill_please.db";
    private static final int DATABASE_VERSION = 20;
}