package com.yagodar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yagodar on 13.08.13.
 */
public abstract class DbBaseHelper<T extends DbBaseManager> extends SQLiteOpenHelper {
    protected DbBaseHelper(Context context, String dbName, SQLiteDatabase.CursorFactory csFactory, int dbVersion) {
        super(context, dbName, csFactory, dbVersion);
    }

    protected void setDbManager(T dbManager) {
        this.dbManager = dbManager;
    }

    protected T getDbManager() {
        return dbManager;
    }

    private T dbManager;

    public static final String SYMB_OP_EQUALITY = "=";

    public static final String SYMB_BRACKET_OPEN = "(";
    public static final String SYMB_BRACKET_CLOSE = ")";

    public static final String SYMB_COMMA = ",";
    public static final String SYMB_DOT_COMMA = ";";

    public static final String SYMB_APOSTROPHE = "'";

    public static final String TYPE_TEXT = " TEXT";
    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_REAL = " REAL";

    public static final String EXPR_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    public static final String EXPR_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String EXPR_PRIMARY_KEY = " PRIMARY KEY";
    public static final String EXPR_NOT_NULL = " NOT NULL";
    public static final String EXPR_DEFAULT = " DEFAULT ";
}
