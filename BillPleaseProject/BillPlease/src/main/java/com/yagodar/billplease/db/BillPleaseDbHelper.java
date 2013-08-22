package com.yagodar.billplease.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yagodar.db.BaseDbHelper;

/**
 * Created by Yagodar on 19.08.13.
 */
public class BillPleaseDbHelper extends BaseDbHelper<BillPleaseDbManager> {
    protected BillPleaseDbHelper(Context context, String dbName, SQLiteDatabase.CursorFactory csFactory, int dbVersion) {
        super(context, dbName, csFactory, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PERSONAL_BILL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PERSONAL_BILL);
        onCreate(db);
    }

    private static final String SQL_CREATE_PERSONAL_BILL = EXPR_CREATE_TABLE_IF_NOT_EXISTS
            + BillPleaseDbContract.TablePersonalBill.TABLE_NAME
            + BRACKET_OPEN
            + BillPleaseDbContract.TablePersonalBill._ID + TYPE_INTEGER + EXPR_PRIMARY_KEY
            + SEP_COMMA
            + BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_ITEM_NAME + TYPE_TEXT + EXPR_DEFAULT + BillPleaseDbContract.TablePersonalBill.DEF_VAL_ITEM_NAME
            + SEP_COMMA
            + BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_COST + TYPE_REAL + EXPR_DEFAULT + BillPleaseDbContract.TablePersonalBill.DEF_VAL_COST
            + SEP_COMMA
            + BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_SHARE + TYPE_INTEGER + EXPR_DEFAULT + BillPleaseDbContract.TablePersonalBill.DEF_VAL_SHARE
            + SEP_COMMA
            + BillPleaseDbContract.TablePersonalBill.COLUMN_NAME_CHANGES_MASK + TYPE_INTEGER + EXPR_DEFAULT + BillPleaseDbContract.TablePersonalBill.DEF_VAL_CHANGES_MASK
            + BRACKET_CLOSE
            + SEP_DOT_COMMA;

    private static final String SQL_DELETE_PERSONAL_BILL = EXPR_DROP_TABLE_IF_EXISTS
            + BillPleaseDbContract.TablePersonalBill.TABLE_NAME;



}
