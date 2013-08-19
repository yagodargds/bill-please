package com.yagodar.billplease.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.yagodar.db.DbHelper;

import java.util.ArrayList;

/**
 * Created by Yagodar on 19.08.13.
 */
public class BillPleaseDb {
    public BillPleaseDb(Context context) {
        dbHelper = new BillPleaseDbHelper(context);
    }

    public ArrayList<PersonalBillRecord> getAllPersonalBillRecords() {
        ArrayList<PersonalBillRecord> records = new ArrayList<PersonalBillRecord>();

        Cursor cs = dbHelper.getReadableDatabase().query(null, null, null, null, null, null, null);
        if(cs != null) {
            while(cs.moveToNext()) {
                records.add(new PersonalBillRecord(cs.getLong(cs.getColumnIndex(BillPleaseDbContract.TablePersonalBill._ID)),
                        cs.getString(cs.getColumnIndex(BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_ITEM_NAME)),
                        cs.getDouble(cs.getColumnIndex(BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_COST)),
                        cs.getInt(cs.getColumnIndex(BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_SHARE)),
                        (byte) cs.getInt(cs.getColumnIndex(BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_CHANGES_MASK))));
            }

            cs.close();
        }

        return records;
    }








    public long addBillRow(String defItemName, double defCost, int defShare) {
        ContentValues values = new ContentValues();
        values.put(Column.ITEM.toString(), defItemName);
        values.put(Column.COST.toString(), defCost);
        values.put(Column.SHARE.toString(), defShare);

        return insert(null, values);
    }

    public int setBillRowItemName(long rowTag, String itemName) {
        ContentValues values = new ContentValues();
        values.put(Column.ITEM.toString(), itemName);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public int setBillRowItemNameChanged(long rowTag, boolean isChanged) {
        ContentValues values = new ContentValues();
        values.put(Column.ITEM_CHANGED.toString(), isChanged ? 1 : 0);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public int setBillRowCost(long rowTag, double cost) {
        ContentValues values = new ContentValues();
        values.put(Column.COST.toString(), cost);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public int setBillRowCostChanged(long rowTag, boolean isChanged) {
        ContentValues values = new ContentValues();
        values.put(Column.COST_CHANGED.toString(), isChanged ? 1 : 0);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public int setBillRowShare(long rowTag, int share) {
        ContentValues values = new ContentValues();
        values.put(Column.SHARE.toString(), share);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public int setBillRowShareChanged(long rowTag, boolean isChanged) {
        ContentValues values = new ContentValues();
        values.put(Column.SHARE_CHANGED.toString(), isChanged ? 1 : 0);

        return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public String getBillRowItemName(long rowTag) {
        String result = "";

        Cursor cs = query(new String[] { Column.ITEM.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getString(0);
            }

            cs.close();
        }

        return result;
    }

    public boolean isBillRowItemNameChanged(long rowTag) {
        boolean result = false;

        Cursor cs = query(new String[] { Column.ITEM_CHANGED.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getInt(0) == 1 ? true : false;
            }

            cs.close();
        }

        return result;
    }

    public double getBillRowCost(long rowTag) {
        int result = 0;

        Cursor cs = query(new String[] { Column.COST.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getInt(0);
            }

            cs.close();
        }

        return result;
    }

    public boolean isBillRowCostChanged(long rowTag) {
        boolean result = false;

        Cursor cs = query(new String[] { Column.COST_CHANGED.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getInt(0) == 1 ? true : false;
            }

            cs.close();
        }

        return result;
    }

    public int getBillRowShare(long rowTag) {
        int result = 0;

        Cursor cs = query(new String[] { Column.SHARE.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getInt(0);
            }

            cs.close();
        }

        return result;
    }

    public boolean isBillRowShareChanged(long rowTag) {
        boolean result = false;

        Cursor cs = query(new String[] { Column.SHARE_CHANGED.toString() }, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null, null, null, null, null);
        if(cs != null) {
            if(cs.moveToNext()) {
                result = cs.getInt(0) == 1 ? true : false;
            }

            cs.close();
        }

        return result;
    }

    public long delBillRow(long rowTag) {
        return delete(Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
    }

    public long delAllBillRows() {
        return delete(null, null);
    }









    public class PersonalBillRecord {
        private PersonalBillRecord(long rowTag, String itemName, double cost, int share, byte changesMask) {
            this.rowTag = rowTag;
            this.itemName = itemName;
            this.cost = cost;
            this.share = share;
            this.changesMask = changesMask;
        }

        public long getRowTag() {
            return rowTag;
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
            //Xzz & 100 == 100
            return (changesMask & ITEM_NAME_CHANGED_MASK) == ITEM_NAME_CHANGED_MASK;
        }

        public boolean isCostChanged() {
            //zXz & 010 == 010
            return (changesMask & COST_CHANGED_MASK) == COST_CHANGED_MASK;
        }

        public boolean isShareChanged() {
            //zzX & 001 == 001
            return (changesMask & SHARE_CHANGED_MASK) == SHARE_CHANGED_MASK;
        }

        private long rowTag;
        private String itemName;
        private double cost;
        private int share;
        private byte changesMask;

        private static final byte ITEM_NAME_CHANGED_MASK = 4; //0b100
        private static final byte COST_CHANGED_MASK = 2; //0b010
        private static final byte SHARE_CHANGED_MASK = 1; //0b001
    }

    private DbHelper dbHelper;
}