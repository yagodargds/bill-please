package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.yagodar.android.bill_please.activity.bill_list.loader.AppendBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.LoadBillListLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.RemoveBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.UpdateBillLoader;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillPleaseLoaderFactory {

    public static AbsAsyncTaskLoader createLoader(Context context, int id, Bundle args) {
        try {
            return (AbsAsyncTaskLoader) BILL_LOADER_BY_ID.get(id).getConstructor(Context.class, Bundle.class).newInstance(context, args);
        } catch (InstantiationException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    public enum BillLoaderType {
        LOAD_BILL_LIST,
        APPEND_BILL,
        UPDATE_BILL,
        REMOVE_BILL,
        ;
    }

    private static final Map<Integer, Class> BILL_LOADER_BY_ID = new HashMap<>();
    static {
        BILL_LOADER_BY_ID.put(BillLoaderType.LOAD_BILL_LIST.ordinal(), LoadBillListLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.APPEND_BILL.ordinal(), AppendBillLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.REMOVE_BILL.ordinal(), RemoveBillLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.UPDATE_BILL.ordinal(), UpdateBillLoader.class);
    }

    private static final String LOG_TAG = BillPleaseLoaderFactory.class.getSimpleName();
}
