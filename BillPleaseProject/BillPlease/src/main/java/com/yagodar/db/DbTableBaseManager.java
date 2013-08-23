package com.yagodar.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Yagodar on 22.08.13.
 */
public class DbTableBaseManager<T extends DbBaseManager> {
    protected DbTableBaseManager(DbTableBaseContract tableContract) {
        this.tableContract = tableContract;
    }

    public String getSQLExprCreateDbTable() {
        return DbBaseHelper.EXPR_CREATE_TABLE_IF_NOT_EXISTS
                + getTableName()
                + DbBaseHelper.SYMB_BRACKET_OPEN
                + getSQLExprCreateDbTableColumns()
                + DbBaseHelper.SYMB_BRACKET_CLOSE
                + DbBaseHelper.SYMB_DOT_COMMA;
    }

    public String getSQLExprDeleteDbTable() {
        return DbBaseHelper.EXPR_DROP_TABLE_IF_EXISTS
                + getTableName();
    }

    protected String getTableContract() {
        return tableContract.getTableName();
    }

    protected String getTableName() {
        return tableContract.getTableName();
    }

    protected void setDbManager(T dbManager) {
        this.dbManager = dbManager;
    }

    protected T getDbManager() {
        return dbManager;
    }

    protected long insert(String nullColumnHack, ContentValues values) {
        return dbManager.insert(getTableName(), nullColumnHack, values);
    }

    protected int update(ContentValues values, String whereClause, String[] whereArgs) {
        return dbManager.update(getTableName(), values, whereClause, whereArgs);
    }

    protected long replace(String nullColumnHack, ContentValues initialValues) {
        return dbManager.replace(getTableName(), nullColumnHack, initialValues);
    }

    protected Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return dbManager.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    protected long delete(String whereClause, String[] whereArgs) {
        return dbManager.delete(getTableName(), whereClause, whereArgs);
    }

    private String getSQLExprCreateDbTableColumns() {
        String sqlExpr = "";

        for (DbTableColumnInfo columnInfo : tableContract.getAllDbTableColumnInfo()) {
            sqlExpr += columnInfo.getSQLExprOfCreation();
            sqlExpr += DbBaseHelper.SYMB_COMMA;
        }

        return sqlExpr.substring(0, sqlExpr.length() - DbBaseHelper.SYMB_COMMA.length());
    }

    private T dbManager;

    private final DbTableBaseContract tableContract;
}
