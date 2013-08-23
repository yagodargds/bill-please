package com.yagodar.db;

import android.provider.BaseColumns;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Yagodar on 23.08.13.
 */
public abstract class DbTableBaseContract implements BaseColumns {
    protected DbTableBaseContract(String tableName) {
        this.tableName = tableName;
        this.dbTableColumnInfos = new HashMap<String, DbTableColumnInfo>();
    }

    public String getTableName() {
        return tableName;
    }

    public DbTableColumnInfo getDbTableColumnInfo(String columnName) {
        return dbTableColumnInfos.get(columnName);
    }

    public Collection<DbTableColumnInfo> getAllDbTableColumnInfo() {
        return dbTableColumnInfos.values();
    }

    protected void addDbTableColumnInfo(DbTableColumnInfo dbTableColumnInfo) {
        if(dbTableColumnInfo != null) {
            dbTableColumnInfos.put(dbTableColumnInfo.getColumnName(), dbTableColumnInfo);
        }
    }

    protected void removeDbTableColumnInfo(String columnName) {
        dbTableColumnInfos.remove(columnName);
    }

    private final String tableName;
    private final HashMap<String, DbTableColumnInfo> dbTableColumnInfos;
}
