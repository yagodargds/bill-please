package com.yagodar.db;

/**
 * Created by Yagodar on 23.08.13.
 */
public class DbTableColumnInfo {
    public DbTableColumnInfo(String columnName) {
        this(columnName, false, null);
    }

    public DbTableColumnInfo(String columnName, Object defValue) {
        this(columnName, false, defValue);
    }

    public DbTableColumnInfo(String columnName, boolean isPrimaryKey) {
        this(columnName, isPrimaryKey, null);
    }

    private DbTableColumnInfo(String columnName, boolean isPrimaryKey, Object defValue) {
        this.columnName = columnName;
        this.isPrimaryKey = isPrimaryKey;
        this.defValue = defValue;

        if(this.isPrimaryKey) {
            this.type = DbBaseHelper.TYPE_INTEGER;
        }
        else if(defValue != null) {
            if(defValue instanceof String) {
                this.type = DbBaseHelper.TYPE_TEXT;
            }
            else if(defValue instanceof Integer || defValue instanceof Long || defValue instanceof Byte) {
                this.type = DbBaseHelper.TYPE_INTEGER;
            }
            else if(defValue instanceof Float || defValue instanceof Double) {
                this.type = DbBaseHelper.TYPE_REAL;
            }
            else {
                this.type = null;
            }
        }
        else {
            this.type = null;
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isNotNull() {
        return defValue == null || this.type == null;
    }

    public Object getDefValue() {
        return defValue;
    }

    public String getSQLExprOfCreation() {
        String sQLExprOfCreation = columnName + type;

        if(isPrimaryKey) {
            sQLExprOfCreation += DbBaseHelper.EXPR_PRIMARY_KEY;
        }
        else if(!isNotNull()) {
            sQLExprOfCreation += DbBaseHelper.EXPR_DEFAULT + DbBaseHelper.SYMB_APOSTROPHE + defValue + DbBaseHelper.SYMB_APOSTROPHE;
        }
        else {
            sQLExprOfCreation += DbBaseHelper.EXPR_NOT_NULL;
        }

        return sQLExprOfCreation;
    }

    private final String columnName;
    private final String type;
    private final boolean isPrimaryKey;
    private final Object defValue;
}
