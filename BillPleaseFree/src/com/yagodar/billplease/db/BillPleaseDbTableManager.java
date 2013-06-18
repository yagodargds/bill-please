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
				billRows.add(new BillRow(	cs.getLong(Column.ROW_TAG.ordinal()), 
											cs.getString(Column.ITEM.ordinal()), 
											cs.getInt(Column.ITEM_CHANGED.ordinal()) == 1 ? true : false, 
											cs.getDouble(Column.COST.ordinal()), 
											cs.getInt(Column.COST_CHANGED.ordinal()) == 1 ? true : false, 
											cs.getInt(Column.SHARE.ordinal()), 
											cs.getInt(Column.SHARE_CHANGED.ordinal()) == 1 ? true : false));
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
	
	public int setBillRowItemNameChanged(long rowTag) {
		ContentValues values = new ContentValues();
		values.put(Column.ITEM_CHANGED.toString(), 1);
		
		return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public int setBillRowCost(long rowTag, double cost) {
		ContentValues values = new ContentValues();
		values.put(Column.COST.toString(), cost);
		
		return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public int setBillRowCostChanged(long rowTag) {
		ContentValues values = new ContentValues();
		values.put(Column.COST_CHANGED.toString(), 1);
		
		return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public int setBillRowShare(long rowTag, int share) {
		ContentValues values = new ContentValues();
		values.put(Column.SHARE.toString(), share);
		
		return update(values, Column.ROW_TAG.toString() + QUERY_EQUALITY_SYMBOL + rowTag, null);
	}
	
	public int setBillRowShareChanged(long rowTag) {
		ContentValues values = new ContentValues();
		values.put(Column.SHARE_CHANGED.toString(), 1);
		
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
		private BillRow(long rowTag, String itemName, boolean isItemNameChanged, double cost, boolean isCostChanged, int share, boolean isShareChanged) {
			this.rowTag = rowTag;
			this.itemName = itemName;
			this.isItemNameChanged = isItemNameChanged;
			this.cost = cost;
			this.isCostChanged = isCostChanged;
			this.share = share;
			this.isShareChanged = isShareChanged;
		}
		
		public long getRowTag() {
			return rowTag;
		}
		
		public String getItemName() {
			return itemName;
		}
		
		public boolean isItemNameChanged() {
			return isItemNameChanged;
		}
		
		public double getCost() {
			return cost;
		}
		
		public boolean isCostChanged() {
			return isCostChanged;
		}
		
		public int getShare() {
			return share;
		}

		public boolean isShareChanged() {
			return isShareChanged;
		}

		private long rowTag;
		private String itemName;
		private boolean isItemNameChanged;
		private double cost;
		private boolean isCostChanged;
		private int share;
		private boolean isShareChanged;
	}
	
	private enum Column {
		ROW_TAG(" INTEGER PRIMARY KEY, "),
		ITEM(" TEXT NOT NULL, "),
		ITEM_CHANGED(" BOOLEAN DEFAULT 0, "),
		COST(" DOUBLE DEFAULT 0.0, "),
		COST_CHANGED(" BOOLEAN DEFAULT 0, "),
		SHARE(" INTEGER DEFAULT 1, "),
		SHARE_CHANGED(" BOOLEAN DEFAULT 0");

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
