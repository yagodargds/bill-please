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
	
	public long addBillRow(String defItemName, int defCost, int defShare) {
		ContentValues values = new ContentValues();
		values.put(Column.ITEM.toString(), defItemName);
		values.put(Column.COST.toString(), defCost);
		values.put(Column.SHARE.toString(), defShare);
		
		return insert(null, values);
	}
	
	public long setBillRowItemName(long rowTag, String itemName) {
		return setBillRow(rowTag, itemName, getBillRowCost(rowTag), getBillRowShare(rowTag));
	}
	
	public long setBillRowCost(long rowTag, int cost) {
		return setBillRow(rowTag, getBillRowItemName(rowTag), cost, getBillRowShare(rowTag));
	}
	
	public long setBillRowShare(long rowTag, int share) {
		return setBillRow(rowTag, getBillRowItemName(rowTag), getBillRowCost(rowTag), share);
	}
	
	public String getBillRowItemName(long rowTag) {
		String result = "";
		
		Cursor cs = query(new String[] { Column.ITEM.toString() }, Column.ROW_TAG.toString() + "=" + rowTag, null, null, null, null, null);
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
		
		Cursor cs = query(new String[] { Column.COST.toString() }, Column.ROW_TAG.toString() + "=" + rowTag, null, null, null, null, null);
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
		
		Cursor cs = query(new String[] { Column.SHARE.toString() }, Column.ROW_TAG.toString() + "=" + rowTag, null, null, null, null, null);
		if(cs != null) {
			if(cs.moveToNext()) {
				result = cs.getInt(0);
			}
			
			cs.close();
		}
		
		return result;
	}
	
	public long delBillRow(long rowTag) {
		return delete(Column.ROW_TAG.toString() + "=" + rowTag, null);
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
	
	private long setBillRow(long rowTag, String itemName, int cost, int share) {
		ContentValues values = new ContentValues();
		values.put(Column.ROW_TAG.toString(), rowTag);
		values.put(Column.ITEM.toString(), itemName);
		values.put(Column.COST.toString(), cost);
		values.put(Column.SHARE.toString(), share);
		
		return replace(null, values);
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
		COST(" INTEGER DEFAULT 0, "),
		SHARE(" INTEGER DEFAULT 0");

		Column(String columnSqlStrPostfix) {
			this.columnSqlStrPostfix = columnSqlStrPostfix;
		}

		public String getColumnSqlStr() {
			return toString() + columnSqlStrPostfix;
		}

		private final String columnSqlStrPostfix;
	}

	private static BillPleaseDbTableManager INSTANCE; 
}