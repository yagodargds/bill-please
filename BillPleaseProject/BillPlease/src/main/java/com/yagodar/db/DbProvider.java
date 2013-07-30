package com.yagodar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Класс наследуется от <code>SQLiteOpenHelper</code>. Обеспечивает работу с базой данных приложения.
 * <br /><br />
 * 
 * @author yagodar
 * @version 1.1.0, 13.06.2013
 * @see SQLiteOpenHelper
 */
public class DbProvider extends SQLiteOpenHelper {	
	/**
	 * Вызывается при создании либо изменении версии базы данных. 
	 * <br /><br />
	 * Создаёт пустые таблицы базы данных, удаляя старые (если они были уже соданы).
	 */
	@Override	
	public void onCreate(SQLiteDatabase db) {
		for (DbTableManager dbTableManager : DbTableManagersHolder.getInstance().getAllDbTableManagers(getDbName(db))) {
			dbTableManager.onDbCreate(db);
		}
	}

	/**
	 * Вызывается при повышении версии базы данных.
	 * <br /><br />
	 * Вызывает <code>onCreate(SQLiteDatabase)</code>.
	 * 
	 * @see #onCreate(SQLiteDatabase)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}	

	/**
	 * Вызывается при понижении версии базы данных.
	 * <br /><br />
	 * Вызывает <code>onUpgrade(SQLiteDatabase)</code>.
	 * 
	 * @see #onUpgrade(SQLiteDatabase, int, int)
	 */
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public static DbProvider newInstance(Context context, String dbName, int dbVersion) {
		if(INSTANCE != null) {
			INSTANCE.close();
		}
		
		INSTANCE = new DbProvider(context, dbName, dbVersion);
		
		return INSTANCE;
	}

	public static DbProvider getInstance() {
		return INSTANCE;
	}

	/**
	 * @param context Context приложения. Необходим для подключении/создании базы данных именно этого приложения. Не может быть null.
	 * @see SQLiteOpenHelper#SQLiteOpenHelper(Context, String, android.database.sqlite.SQLiteDatabase.CursorFactory, int)
	 */
	private DbProvider(Context context, String dbName, int dbVersion) {
		super(context, dbName, null, dbVersion);
		
		for (DbTableManager dbTableManager : DbTableManagersHolder.getInstance().getAllDbTableManagers(dbName)) {
			dbTableManager.setDbProvider(this);
		}
	}

	private String getDbName(SQLiteDatabase db) {
		return db.getPath().substring(db.getPath().lastIndexOf("/") + 1);
	}
	
	private static DbProvider INSTANCE;
}
