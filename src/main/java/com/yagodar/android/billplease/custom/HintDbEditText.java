package com.yagodar.android.billplease.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.util.AttributeSet;

import com.yagodar.android.billplease.R;
import com.yagodar.android.database.sqlite.DbTableBaseManager;
import com.yagodar.android.database.sqlite.DbTableColumn;
import com.yagodar.android.database.sqlite.custom.DbEditText;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Created by Yagodar on 25.09.13.
 */
public class HintDbEditText<T extends Object> extends DbEditText<T> {
    public HintDbEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        initHint = getHint().toString();

        /*TypedArray styledAttrs = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.HintDbEditText,
                0,
                0);

        try {
            hintTypeface = styledAttrs.getInteger(R.styleable.HintDbEditText_hintTypeface, 0);
            hintShowDefValue = styledAttrs.getBoolean(R.styleable.HintDbEditText_hintShowDefValue, true);
            setDefValueStr(styledAttrs.getString(R.styleable.HintDbEditText_defValueStr));
            minValueStr = styledAttrs.getString(R.styleable.HintDbEditText_minValueStr);
        }
        finally {
            styledAttrs.recycle();
        }*/

        postUpdateHint();
    }

    @Override
    public void pullFromDb() {
        Object pullValue = super.getDbValue();

        Object defValueByDb = null;
        DbTableColumn dbTableColumnBase = getDbTableColumnBase();
        if(dbTableColumnBase != null) {
            defValueByDb = dbTableColumnBase.getDefValue();
        }

        Object defValueByStr = getValStr(defValueStr);

        if(pullValue == null || pullValue.equals(defValueByDb) || pullValue.equals(defValueByStr)) {
            clearText();
        }
        else {
            postSetText(String.valueOf(pullValue));
        }
    }

    @Override
    public T getDbValue() {
       T pullValue = super.getDbValue();

        T defValueByDb = null;
        DbTableColumn dbTableColumnBase = getDbTableColumnBase();
        if(dbTableColumnBase != null) {
            defValueByDb = (T) dbTableColumnBase.getDefValue();
        }

        T defValueByStr = getValStr(defValueStr);

        if(pullValue == null) {
            if(defValueByStr != null) {
                pullValue = defValueByStr;
            }
            else {
                pullValue = defValueByDb;
            }
        }
        else if(pullValue.equals(defValueByDb)) {
            if(defValueByStr != null) {
                pullValue = defValueByStr;
            }
        }

        return pullValue;
    }

    @Override
    protected void registerDatabase(String dbTableName, String dbColumnName) {
        super.registerDatabase(dbTableName, dbColumnName);
        postUpdateHint();
    }

    @Override
    protected DbTableBaseManager registerTableManager(String dbTableName) {
        return null;
    }

    private T getValStr(String from) {
        Object value = null;

        String text = from;
        Object bigValue;

        try {
            switch(getDbTableColumnBase().getType()) {
                case DbTableColumn.TYPE_DOUBLE:
                    bigValue = new BigDecimal(text);
                    value = ((BigDecimal) bigValue).doubleValue();

                    if(((BigDecimal) bigValue).compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
                        value = Double.MAX_VALUE;
                    }
                    else if(((BigDecimal) bigValue).compareTo(BigDecimal.valueOf(-Double.MAX_VALUE)) < 0) {
                        value = -Double.MAX_VALUE;
                    }

                    break;
                case DbTableColumn.TYPE_FLOAT:
                    bigValue = new BigDecimal(text);
                    value = ((BigDecimal) bigValue).floatValue();

                    if(((BigDecimal) bigValue).compareTo(BigDecimal.valueOf(Float.MAX_VALUE)) > 0) {
                        value = Float.MAX_VALUE;
                    }
                    else if(((BigDecimal) bigValue).compareTo(BigDecimal.valueOf(-Float.MAX_VALUE)) < 0) {
                        value = -Float.MAX_VALUE;
                    }

                    break;
                case DbTableColumn.TYPE_INTEGER:
                    bigValue = new BigInteger(text);
                    value = ((BigInteger) bigValue).intValue();

                    if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                        value = Integer.MAX_VALUE;
                    }
                    else if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(-Integer.MAX_VALUE)) < 0) {
                        value = -Integer.MAX_VALUE;
                    }

                    break;
                case DbTableColumn.TYPE_BOOLEAN:
                    value = Boolean.parseBoolean(text);
                    break;
                case DbTableColumn.TYPE_LONG:
                    bigValue = new BigInteger(text);
                    value = ((BigInteger) bigValue).longValue();

                    if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                        value = Long.MAX_VALUE;
                    }
                    else if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(-Long.MAX_VALUE)) < 0) {
                        value = -Long.MAX_VALUE;
                    }

                    break;
                case DbTableColumn.TYPE_SHORT:
                    bigValue = new BigInteger(text);
                    value = ((BigInteger) bigValue).shortValue();

                    if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0) {
                        value = Short.MAX_VALUE;
                    }
                    else if(((BigInteger) bigValue).compareTo(BigInteger.valueOf(-Short.MAX_VALUE)) < 0) {
                        value = -Short.MAX_VALUE;
                    }

                    break;
                default:
                    value = text;
                    break;
            }
        }
        catch(Exception ignored) {}

        return (T) value;
    }

    public void setDefValueStr(String defValueStr) {
        this.defValueStr = defValueStr;
    }

    private void updateHint() {
        String updatedHint = EMPTY_TEXT;

        if(hintShowDefValue) {
            Object defValue = null;
            if(this.defValueStr != null) {
                defValue = this.defValueStr;
            }
            else {
                DbTableColumn dbTableColumnBase = getDbTableColumnBase();
                if(dbTableColumnBase != null) {
                    defValue = dbTableColumnBase.getDefValue();
                }
            }

            updatedHint = HINT_DB_VALUE_SEP + String.valueOf(defValue);
        }

        if(initHint != null && initHint.length() > 0) {
            updatedHint = initHint + updatedHint;
        }
        else if(hintShowDefValue) {
            updatedHint.replaceAll(HINT_DB_VALUE_SEP, EMPTY_TEXT);
        }

        setHint(getTypefacedHtmlText(hintTypeface, updatedHint));
    }

    private void postUpdateHint() {
        try {
            post(new Runnable() {
                @Override
                public void run() {
                    updateHint();
                }
            });
        }
        catch(Exception ignored) {}
    }

    private static CharSequence getTypefacedHtmlText(int typeface, String initText) {
        if(initText == null || typeface < 0 || typeface >= TYPEFACE_TAG.length) {
            return null;
        }

        return Html.fromHtml(TYPEFACE_TAG[typeface] + initText + TYPEFACE_TAG[typeface].replaceAll(TAG_OPENING, TAG_OPENING + TAG_END_OPENING));
    }

    private int hintTypeface;
    private boolean hintShowDefValue;
    private String defValueStr;
    private String minValueStr;

    private String initHint;

    private static final String HINT_DB_VALUE_SEP = ":";

    private static final String TAG_OPENING = "<";
    private static final String TAG_END_OPENING = "/";
    private static final String TAG_CLOSING = ">";

    private static final String TAG_TYPEFACE_BOLD = "b";
    private static final String TAG_TYPEFACE_ITALIC = "i";
    private static final String TAG_TYPEFACE_BOLD_ITALIC = TAG_TYPEFACE_BOLD + TAG_CLOSING + TAG_OPENING + TAG_TYPEFACE_ITALIC;

    private static final String TYPEFACE_TAG[] = new String[] {
            EMPTY_TEXT,                                             //Typeface.NORMAL
            TAG_OPENING + TAG_TYPEFACE_BOLD + TAG_CLOSING,          //Typeface.BOLD
            TAG_OPENING + TAG_TYPEFACE_ITALIC + TAG_CLOSING,        //Typeface.ITALIC
            TAG_OPENING + TAG_TYPEFACE_BOLD_ITALIC + TAG_CLOSING,   //Typeface.BOLD_ITALIC
    };
}
