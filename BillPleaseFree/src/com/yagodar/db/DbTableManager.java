package com.yagodar.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

abstract public class DbTableManager {
	protected DbTableManager(String tableName) {
		this.tableName = tableName;
	}
	
	protected void onDbCreate(SQLiteDatabase db) {
		db.execSQL(getDropTableSqlStr());
		db.execSQL(getCreateTableSqlStr());
	}
	
	protected long delete(String whereClause, String[] whereArgs) {
		int rowsAffected = 0;
		
		try {
			rowsAffected = dbProvider.getWritableDatabase().delete(tableName, whereClause, whereArgs);
		}
		catch(SQLiteException sqle) {
			Log.e(tableName, "SQLiteException while delete on table " + tableName + "!", sqle);
		}
		catch(Exception e) {
			Log.e(tableName, "Exception while delete on table " + tableName + "!", e);
		}
		
		return rowsAffected;
	}
	
	protected long replace(String nullColumnHack, ContentValues initialValues) {
		long rowId = 0L;
		
		try {
			rowId = dbProvider.getWritableDatabase().replace(tableName, nullColumnHack, initialValues);
		}
		catch(SQLiteException sqle) {
			Log.e(tableName, "SQLiteException while replace on table " + tableName + "!", sqle);
		}
		catch(Exception e) {
			Log.e(tableName, "Exception while replace on table " + tableName + "!", e);
		}
		
		return rowId;
	}
	
	protected long insert(String nullColumnHack, ContentValues values) {
		long rowId = 0L;
		
		try {
			rowId = dbProvider.getWritableDatabase().insert(tableName, nullColumnHack, values);
		}
		catch(SQLiteException sqle) {
			Log.e(tableName, "SQLiteException while insert on table " + tableName + "!", sqle);
		}
		catch(Exception e) {
			Log.e(tableName, "Exception while insert on table " + tableName + "!", e);
		}
		
		return rowId;
	}
	
	protected Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		Cursor cs = null;
		
		try {
			cs = dbProvider.getReadableDatabase().query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		}
		catch(SQLiteException sqle) {
			Log.e(tableName, "SQLiteException while query '" + selection + "' on table " + tableName + "!", sqle);
		}
		catch(Exception e) {
			Log.e(tableName, "Exception while query '" + selection + "' on table " + tableName + "!", e);
		}
		
		return cs;
	}
	
	protected void setDbProvider(DbProvider dbProvider) {
		this.dbProvider = dbProvider;
	}
	
	abstract protected String getCreateTableColumnsSqlStr();

	private String getCreateTableSqlStr() {
		return CREATE_TABLE_QUERY_PREFIX + tableName + CREATE_TABLE_QUERY_OPEN_BRACKET + getCreateTableColumnsSqlStr() + CREATE_TABLE_QUERY_CLOSE_BRACKET;
	}
	
	private String getDropTableSqlStr() {
		return DROP_TABLE_QUERY_PREFIX + tableName;
	}
	
	private DbProvider dbProvider;
	
	private final String tableName;
	
	protected static final String DROP_TABLE_QUERY_PREFIX = "DROP TABLE IF EXISTS ";
	protected static final String CREATE_TABLE_QUERY_PREFIX = "CREATE TABLE ";
	protected static final String CREATE_TABLE_QUERY_OPEN_BRACKET = " (";
	protected static final String CREATE_TABLE_QUERY_CLOSE_BRACKET = ");";
}