package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.yagodar.android.bill_please.activity.bill.loader.AppendBillLoader;
import com.yagodar.android.bill_please.activity.bill.loader.AppendBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.LoadBillLoader;
import com.yagodar.android.bill_please.activity.bill.loader.RemoveBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.UpdateBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.LoadBillListLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.RemoveBillLoader;
import com.yagodar.android.bill_please.activity.bill_order.loader.UpdateBillOrderLoader;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.essential.factory.IdGroupIntFactory;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillPleaseLoaderFactory {

    public static AbsAsyncTaskLoader createLoader(Context context, int id, Bundle args) {
        try {

            //TODO выдача id, несколько лоадеров одновременно. или последовательно.

            //TODO первые несколько бит int - под ordinal группы. остальное - собственно id
            IdGroupIntFactory test = new IdGroupIntFactory(10);
            return (AbsAsyncTaskLoader) BillLoaderType.VALUES[id].mLoaderClass.getConstructor(Context.class, Bundle.class).newInstance(context, args);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    public enum BillLoaderType {
        LOAD_BILL_LIST(LoadBillListLoader.class),
        APPEND_BILL(AppendBillLoader.class),
        UPDATE_BILL(UpdateBillLoader.class),
        REMOVE_BILL(RemoveBillLoader.class),
        LOAD_BILL(LoadBillLoader.class),
        APPEND_BILL_ORDER(AppendBillOrderLoader.class),
        UPDATE_BILL_ORDER(UpdateBillOrderLoader.class),
        REMOVE_BILL_ORDER(RemoveBillOrderLoader.class),
        ;

        BillLoaderType(Class<? extends AbsAsyncTaskLoader> loaderClass) {
            mLoaderClass = loaderClass;
        }

        private final Class mLoaderClass;

        private static final BillLoaderType[] VALUES = values();
    }

    private static final String TAG = BillPleaseLoaderFactory.class.getSimpleName();
}
