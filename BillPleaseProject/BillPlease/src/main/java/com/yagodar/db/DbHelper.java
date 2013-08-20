package com.yagodar.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yagodar on 13.08.13.
 */
public abstract class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static final String OP_EQUALITY = "=";

    protected static final String BRACKET_OPEN = "(";
    protected static final String BRACKET_CLOSE = ")";

    protected static final String SEP_COMMA = ",";
    protected static final String SEP_DOT_COMMA = ";";

    protected static final String TYPE_INTEGER = " INTEGER";
    protected static final String TYPE_TEXT = " TEXT";
    protected static final String TYPE_REAL = " REAL";

    protected static final String EXPR_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    protected static final String EXPR_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    protected static final String EXPR_PRIMARY_KEY = " PRIMARY KEY";
    protected static final String EXPR_NOT_NULL = " NOT NULL";
    protected static final String EXPR_DEFAULT = " DEFAULT ";
}
