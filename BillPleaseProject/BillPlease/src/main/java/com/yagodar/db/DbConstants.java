package com.yagodar.db;

/**
 * Created by Yagodar on 13.08.13.
 */
public interface DbConstants {
    public static final String BRACKET_OPEN = "(";
    public static final String BRACKET_CLOSE = ")";

    public static final String SEP_COMMA = ",";
    public static final String SEP_DOT_COMMA = ";";

    public static final String OP_EQUALITY = "=";

    public static final String TYPE_INTEGER = " INTEGER";
    public static final String TYPE_DOUBLE = " DOUBLE";

    public static final String TYPE_BOOLEAN = " BOOLEAN";

    public static final String TYPE_TEXT = " TEXT";

    public static final String EXPR_CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
    public static final String EXPR_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String EXPR_PRIMARY_KEY = " PRIMARY KEY";
    public static final String EXPR_NOT_NULL = " NOT NULL";
    public static final String EXPR_DEFAULT = " DEFAULT ";
}
