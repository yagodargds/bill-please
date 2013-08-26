package com.yagodar.android.billplease.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.yagodar.android.database.sqlite.DbBaseHelper;
import com.yagodar.android.database.sqlite.DbTableBaseManager;

import java.util.ArrayList;

/**
 * Created by Yagodar on 22.08.13.
 */
public class DbBillPleaseTablePersonalBillManager extends DbTableBaseManager<DbBillPleaseManager> {
    protected DbBillPleaseTablePersonalBillManager() {
        super(DbBillPleaseTablePersonalBillContract.getInstance());
    }

    public long addPersonalBillRecord() {
        return insert(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_ITEM_NAME, null);
    }

    public long addPersonalBillRecord(PersonalBillRecord personalBillRecord) {
        long tag = -1;

        if(personalBillRecord != null) {
            tag = addPersonalBillRecord(personalBillRecord.getItemName(), personalBillRecord.getCost(), personalBillRecord.getShare(), personalBillRecord.getChangesMask());
        }

        return tag;
    }

    public long addPersonalBillRecord(String itemName, double cost, int share, byte changesMask) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_ITEM_NAME, itemName);
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_COST, cost);
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_SHARE, share);
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_CHANGES_MASK, changesMask);

        return addPersonalBillRecord(values);
    }

    public ArrayList<Long> addAllPersonalBillRecords(ArrayList<PersonalBillRecord> personalBillRecords) {
        ArrayList<Long> tags = new ArrayList<Long>();

        if(personalBillRecords != null && personalBillRecords.size() > 0) {
            for (PersonalBillRecord personalBillRecord : personalBillRecords) {
                tags.add(addPersonalBillRecord(personalBillRecord));
            }
        }

        return tags;
    }

    public int setPersonalBillRecordItemName(long tag, String itemName) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_ITEM_NAME, itemName);

        return setPersonalBillRecordValues(tag, values);
    }

    public int setPersonalBillRecordCost(long tag, double cost) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_COST, cost);

        return setPersonalBillRecordValues(tag, values);
    }

    public int setPersonalBillRecordShare(long tag, int share) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_SHARE, share);

        return setPersonalBillRecordValues(tag, values);
    }

    public int setPersonalBillRecordItemNameChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //1zz = Xzz | 100
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) | PersonalBillRecord.ITEM_NAME_CHANGED_MASK);
        }
        else {
            //0zz = Xzz & ~(100)
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) & (~PersonalBillRecord.ITEM_NAME_CHANGED_MASK));
        }

        return setPersonalBillRecordChangesMask(tag, newChangesMask);
    }

    public int setPersonalBillRecordCostChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //z1z = zXz | 010
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) | PersonalBillRecord.COST_CHANGED_MASK);
        }
        else {
            //z0z = zXz & ~(010)
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) & (~PersonalBillRecord.COST_CHANGED_MASK));
        }

        return setPersonalBillRecordChangesMask(tag, newChangesMask);
    }

    public int setPersonalBillRecordShareChanged(long tag, boolean isChanged) {
        byte newChangesMask;

        if(isChanged) {
            //zz1 = zzX | 001
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) | PersonalBillRecord.SHARE_CHANGED_MASK);
        }
        else {
            //zz0 = zzX & ~(001)
            newChangesMask = (byte) (getPersonalBillRecordChangesMask(tag) & (~PersonalBillRecord.SHARE_CHANGED_MASK));
        }

        return setPersonalBillRecordChangesMask(tag, newChangesMask);
    }

    public boolean isPersonalBillRecordItemNameChanged(long tag) {
        return isItemNameChanged(getPersonalBillRecordChangesMask(tag));
    }

    public boolean isPersonalBillRecordCostChanged(long tag) {
        return isCostChanged(getPersonalBillRecordChangesMask(tag));
    }

    public boolean isPersonalBillRecordShareChanged(long tag) {
        return isShareChanged(getPersonalBillRecordChangesMask(tag));
    }

    public ArrayList<PersonalBillRecord> getAllPersonalBillRecords() {
        ArrayList<PersonalBillRecord> records = new ArrayList<PersonalBillRecord>();

        Cursor cs = query(null, null, null, null, null, null, null);
        if(cs != null) {
            while(cs.moveToNext()) {
                records.add(new PersonalBillRecord(cs.getLong(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_TAG)),
                        cs.getString(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_ITEM_NAME)),
                        cs.getDouble(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_COST)),
                        cs.getInt(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_SHARE)),
                        (byte) cs.getInt(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_CHANGES_MASK))));
            }

            cs.close();
        }

        return records;
    }

    public long delPersonalBillRecord(long tag) {
        return delete(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null);
    }

    public long delAllPersonalBillRecords() {
        return delete(null, null);
    }


    private long addPersonalBillRecord(ContentValues values) {
        return insert(null, values);
    }

    private int setPersonalBillRecordChangesMask(long tag, byte changesMask) {
        ContentValues values = new ContentValues();
        values.put(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_CHANGES_MASK, changesMask);

        return setPersonalBillRecordValues(tag, values);
    }

    private byte getPersonalBillRecordChangesMask(long tag) {
        byte changesMask = 0;

        Cursor cs = query(new String[] { DbBillPleaseTablePersonalBillContract.COLUMN_NAME_CHANGES_MASK }, DbBillPleaseTablePersonalBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null, null, null, null, null);
        if(cs != null) {
            while(cs.moveToNext()) {
                changesMask = (byte) cs.getInt(cs.getColumnIndex(DbBillPleaseTablePersonalBillContract.COLUMN_NAME_CHANGES_MASK));
            }

            cs.close();
        }

        return changesMask;
    }

    private int setPersonalBillRecordValues(long tag, ContentValues values) {
        return update(values, DbBillPleaseTablePersonalBillContract.COLUMN_NAME_TAG + DbBaseHelper.SYMB_OP_EQUALITY + tag, null);
    }

    private static boolean isItemNameChanged(byte changesMask) {
        //Xzz & 100 == 100
        return (changesMask & PersonalBillRecord.ITEM_NAME_CHANGED_MASK) == PersonalBillRecord.ITEM_NAME_CHANGED_MASK;
    }

    private static boolean isCostChanged(byte changesMask) {
        //zXz & 010 == 010
        return (changesMask & PersonalBillRecord.COST_CHANGED_MASK) == PersonalBillRecord.COST_CHANGED_MASK;
    }

    private static boolean isShareChanged(byte changesMask) {
        //zzX & 001 == 001
        return (changesMask & PersonalBillRecord.SHARE_CHANGED_MASK) == PersonalBillRecord.SHARE_CHANGED_MASK;
    }

    public class PersonalBillRecord {
        private PersonalBillRecord(long tag, String itemName, double cost, int share, byte changesMask) {
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
            return DbBillPleaseTablePersonalBillManager.isItemNameChanged(changesMask);
        }

        public boolean isCostChanged() {
            return DbBillPleaseTablePersonalBillManager.isCostChanged(changesMask);
        }

        public boolean isShareChanged() {
            return DbBillPleaseTablePersonalBillManager.isShareChanged(changesMask);
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
