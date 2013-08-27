package com.yagodar.android.billplease.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.yagodar.android.database.sqlite.DbBaseHelper;
import com.yagodar.android.database.sqlite.DbTableBaseManager;

import java.util.ArrayList;

/**
 * Created by Yagodar on 22.08.13.
 */
public class DbBillPleaseTableBillManager extends DbTableBaseManager<DbBillPleaseManager> {
    protected DbBillPleaseTableBillManager() {
        super(DbBillPleaseTableBillContract.getInstance());
    }

    public long addRecord() {
        return insert(DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME, null);
    }

    public long addRecord(BillRecord record) {
        long tag = -1;

        if(record != null) {
            tag = addRecord(record.getItemName(), record.getCost(), record.getShare(), record.getChangesMask());
        }

        return tag;
    }

    public long addRecord(String itemName, double cost, int share, byte changesMask) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME, itemName);
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_COST, cost);
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_SHARE, share);
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_CHANGES_MASK, changesMask);

        return addRecord(values);
    }

    public ArrayList<Long> addAllRecords(ArrayList<BillRecord> records) {
        ArrayList<Long> tags = new ArrayList<Long>();

        if(records != null && records.size() > 0) {
            for (BillRecord billRecord : records) {
                tags.add(addRecord(billRecord));
            }
        }

        return tags;
    }

    public int setItemName(long tag, String itemName) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME, itemName);

        return setValues(tag, values);
    }

    public int setCost(long tag, double cost) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_COST, cost);

        return setValues(tag, values);
    }

    public int setShare(long tag, int share) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_SHARE, share);

        return setValues(tag, values);
    }

    public int setItemNameChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //1zz = Xzz | 100
            newChangesMask = (byte) (getChangesMask(tag) | BillRecord.ITEM_NAME_CHANGED_MASK);
        }
        else {
            //0zz = Xzz & ~(100)
            newChangesMask = (byte) (getChangesMask(tag) & (~BillRecord.ITEM_NAME_CHANGED_MASK));
        }

        return setChangesMask(tag, newChangesMask);
    }

    public int setCostChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //z1z = zXz | 010
            newChangesMask = (byte) (getChangesMask(tag) | BillRecord.COST_CHANGED_MASK);
        }
        else {
            //z0z = zXz & ~(010)
            newChangesMask = (byte) (getChangesMask(tag) & (~BillRecord.COST_CHANGED_MASK));
        }

        return setChangesMask(tag, newChangesMask);
    }

    public int setShareChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //zz1 = zzX | 001
            newChangesMask = (byte) (getChangesMask(tag) | BillRecord.SHARE_CHANGED_MASK);
        }
        else {
            //zz0 = zzX & ~(001)
            newChangesMask = (byte) (getChangesMask(tag) & (~BillRecord.SHARE_CHANGED_MASK));
        }

        return setChangesMask(tag, newChangesMask);
    }

    public boolean isItemNameChanged(long tag) {
        return isItemNameChanged(getChangesMask(tag));
    }

    public boolean isCostChanged(long tag) {
        return isCostChanged(getChangesMask(tag));
    }

    public boolean isShareChanged(long tag) {
        return isShareChanged(getChangesMask(tag));
    }

    public ArrayList<BillRecord> getAllRecords() {
        ArrayList<BillRecord> records = new ArrayList<BillRecord>();

        Cursor cs = query(null, null, null, null, null, null, null);
        if(cs != null) {
            while(cs.moveToNext()) {
                records.add(new BillRecord(cs.getLong(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_TAG)),
                        cs.getString(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_ITEM_NAME)),
                        cs.getDouble(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_COST)),
                        cs.getInt(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_SHARE)),
                        (byte) cs.getInt(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_CHANGES_MASK))));
            }

            cs.close();
        }

        return records;
    }

    public long delRecord(long tag) {
        return delete(DbBillPleaseTableBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null);
    }

    public long delAllRecords() {
        return delete(null, null);
    }


    private long addRecord(ContentValues values) {
        return insert(null, values);
    }

    private int setChangesMask(long tag, byte changesMask) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTableBillContract.COLUMN_NAME_CHANGES_MASK, changesMask);

        return setValues(tag, values);
    }

    private byte getChangesMask(long tag) {
        byte changesMask = 0;

        Cursor cs = query(new String[] { DbBillPleaseTableBillContract.COLUMN_NAME_CHANGES_MASK }, DbBillPleaseTableBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null, null, null, null, null);
        if(cs != null) {
            while(cs.moveToNext()) {
                changesMask = (byte) cs.getInt(cs.getColumnIndex(DbBillPleaseTableBillContract.COLUMN_NAME_CHANGES_MASK));
            }

            cs.close();
        }

        return changesMask;
    }

    private int setValues(long tag, ContentValues values) {
        return update(values, DbBillPleaseTableBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null);
    }

    private static boolean isItemNameChanged(byte changesMask) {
        //Xzz & 100 == 100
        return (changesMask & BillRecord.ITEM_NAME_CHANGED_MASK) == BillRecord.ITEM_NAME_CHANGED_MASK;
    }

    private static boolean isCostChanged(byte changesMask) {
        //zXz & 010 == 010
        return (changesMask & BillRecord.COST_CHANGED_MASK) == BillRecord.COST_CHANGED_MASK;
    }

    private static boolean isShareChanged(byte changesMask) {
        //zzX & 001 == 001
        return (changesMask & BillRecord.SHARE_CHANGED_MASK) == BillRecord.SHARE_CHANGED_MASK;
    }

    public class BillRecord {
        private BillRecord(long tag, String itemName, double cost, int share, byte changesMask) {
            this.tag = tag;
            this.itemName = itemName;
            this.cost = cost;
            this.share = share;
            this.changesMask = changesMask;
        }

        public long getTag() {
            return tag;
        }

        public String getItemName() {
            return itemName;
        }

        public double getCost() {
            return cost;
        }

        public int getShare() {
            return share;
        }

        public boolean isItemNameChanged() {
            return DbBillPleaseTableBillManager.isItemNameChanged(changesMask);
        }

        public boolean isCostChanged() {
            return DbBillPleaseTableBillManager.isCostChanged(changesMask);
        }

        public boolean isShareChanged() {
            return DbBillPleaseTableBillManager.isShareChanged(changesMask);
        }

        public byte getChangesMask() {
            return changesMask;
        }

        private long tag;
        private String itemName;
        private double cost;
        private int share;
        private byte changesMask;

        private static final byte ITEM_NAME_CHANGED_MASK = 4; //0b100
        private static final byte COST_CHANGED_MASK = 2; //0b010
        private static final byte SHARE_CHANGED_MASK = 1; //0b001
    }
}
