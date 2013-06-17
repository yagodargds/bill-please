package com.yagodar.billplease.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.yagodar.db.DbTableManager;

public class BillPleaseDbTableManager extends DbTableManager {
	public static BillPleaseDbTableManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new BillPleaseDbTableManager();
		}
		return INSTANCE;
	}
	
	public ArrayList<BillRow> getBillRows() {
		ArrayList<BillRow> billRows = new ArrayList<BillRow>();
		
		Cursor cs = query(null, null, null, null, null, null, null);
		if(cs != null) {
			while(cs.moveToNext()) {
				billRows.add(new BillRow(cs.getLong(Column.ROW_TAG.ordinal()), cs.getString(Column.ITEM.ordinal()), cs.getInt(Column.COST.ordinal()), cs.getInt(Column.SHARE.ordinal())));
			}
			
			cs.close();
		}
		
		return billRows;
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
	
	public int setBillRowCost(long rowTag, int cost) {
		ContentValues values = new ContentValues();
		values.put(Column.COST.toString(), cost);
		
		return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public int setBillRowShare(long rowTag, int share) {
		ContentValues values = new ContentValues();
		values.put(Column.SHARE.toString(), share);
		
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
	
	public int getBillRowCost(long rowTag) {
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
	
	public long delBillRow(long rowTag) {
		return delete(Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public long delAllBillRows() {
		return delete(null, null);
	}
	
	@Override
	protected String getCreateTableColumnsSqlStr() {
		String result = "";
		
		for (Column column : Column.values()) {
			result += column.getColumnSqlStr();
		}
		
		return result;
	}
	
	private BillPleaseDbTableManager() {
		super(BillPleaseDbTableManager.class.getSimpleName());
	}
	
	public class BillRow {
		private BillRow(long rowTag, String itemName, int cost, int share) {
			this.rowTag = rowTag;
			this.itemName = itemName;
			this.cost = cost;
			this.share = share;
		}
		
		public long getRowTag() {
			return rowTag;
		}
		public String getItemName() {
			return itemName;
		}
		public int getCost() {
			return cost;
		}
		public int getShare() {
			return share;
		}

		private long rowTag;
		private String itemName;
		private int cost;
		private int share;
	}
	
	private enum Column {
		ROW_TAG(" INTEGER PRIMARY KEY, "),
		ITEM(" TEXT NOT NULL, "),
		COST(" REAL DEFAULT 0.0, "),
		SHARE(" INTEGER DEFAULT 1");

		Column(String columnSqlStrPostfix) {
			this.columnDatatype = columnSqlStrPostfix;
		}

		public String getColumnSqlStr() {
			return toString() + columnDatatype;
		}

		private final String columnDatatype;
	}

	private static BillPleaseDbTableManager INSTANCE; 
}
