package com.yagodar.billplease.db;

import android.content.ContentValues;

import com.yagodar.db.DbTableManager;

public class BillPleaseDbTableManager extends DbTableManager {
	public static BillPleaseDbTableManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new BillPleaseDbTableManager();
		}
		return INSTANCE;
	}
	
	public long addBillRow(String itemName, int cost, int share) {
		ContentValues values = new ContentValues();
		values.put(Column.ITEM.toString(), itemName);
		values.put(Column.COST.toString(), cost);
		values.put(Column.SHARE.toString(), share);
		
		return insert(null, values);
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
