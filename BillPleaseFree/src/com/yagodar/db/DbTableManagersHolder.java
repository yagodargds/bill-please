package com.yagodar.db;

import java.util.ArrayList;
import java.util.HashMap;

public class DbTableManagersHolder {
	public void addDbTableManager(String dbName, DbTableManager dbTableManager) {
		if(dbName != null && !dbName.equals("") && dbTableManager != null) {
			if(!dbTableManagers.containsKey(dbName)) {
				dbTableManagers.put(dbName, new ArrayList<DbTableManager>());
			}
			
			if(!dbTableManagers.get(dbName).contains(dbTableManager)) {
				dbTableManagers.get(dbName).add(dbTableManager);
			}
		}
	}
	
	public static DbTableManagersHolder getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DbTableManagersHolder();
		}
		
		return INSTANCE;
	}
	
	protected ArrayList<DbTableManager> getAllDbTableManagers(String dbName) {
		return dbTableManagers.get(dbName);
	}
	
	private DbTableManagersHolder() {
		dbTableManagers = new HashMap<String, ArrayList<DbTableManager>>();
	}
	
	private HashMap<String, ArrayList<DbTableManager>> dbTableManagers;
	
	private static DbTableManagersHolder INSTANCE;
}
