package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.yagodar.android.bill_please.activity.bill.loader.AppendBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.LoadBillLoader;
import com.yagodar.android.bill_please.activity.bill.loader.RemoveBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.UpdateBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.AppendBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.LoadBillListLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.RemoveBillLoader;
import com.yagodar.android.bill_please.activity.bill_order.loader.UpdateBillOrderLoader;
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

        LOAD_BILL,
        APPEND_BILL_ORDER,
        UPDATE_BILL_ORDER,
        REMOVE_BILL_ORDER,

        ;
    }

    private static final Map<Integer, Class> BILL_LOADER_BY_ID = new HashMap<>();
    static {
        BILL_LOADER_BY_ID.put(BillLoaderType.LOAD_BILL_LIST.ordinal(), LoadBillListLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.APPEND_BILL.ordinal(), AppendBillLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.REMOVE_BILL.ordinal(), RemoveBillLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.UPDATE_BILL.ordinal(), UpdateBillLoader.class);

        BILL_LOADER_BY_ID.put(BillLoaderType.LOAD_BILL.ordinal(), LoadBillLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.APPEND_BILL_ORDER.ordinal(), AppendBillOrderLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.REMOVE_BILL_ORDER.ordinal(), RemoveBillOrderLoader.class);
        BILL_LOADER_BY_ID.put(BillLoaderType.UPDATE_BILL_ORDER.ordinal(), UpdateBillOrderLoader.class);
    }

    private static final String LOG_TAG = BillPleaseLoaderFactory.class.getSimpleName();
}
