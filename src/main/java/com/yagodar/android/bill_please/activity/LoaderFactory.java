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
public class LoaderFactory {

    public static AbsAsyncTaskLoader createLoader(Context context, int id, Bundle args) {
        try {
            return (AbsAsyncTaskLoader) getType(id).mLoaderClass.getConstructor(Context.class, Bundle.class).newInstance(context, args);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static Type getType(int id) {
        return Type.get(id);
    }

    public static int getNextId(Type type) {
        return type.getNextId();
    }

    public enum Type {
        LOAD_BILL_LIST(LoadBillListLoader.class),
        APPEND_BILL(AppendBillLoader.class),
        UPDATE_BILL(UpdateBillLoader.class),
        REMOVE_BILL(RemoveBillLoader.class),
        LOAD_BILL(LoadBillLoader.class),
        APPEND_BILL_ORDER(AppendBillOrderLoader.class),
        UPDATE_BILL_ORDER(UpdateBillOrderLoader.class),
        REMOVE_BILL_ORDER(RemoveBillOrderLoader.class),
        ;

        Type(Class loaderClass) {
            mLoaderClass = loaderClass;
            mLastId = ordinal();
        }

        public void onStart() {

        }

        public void onFinish() {

        }

        private int getNextId() {
            mLastId = ID_FACTORY.getNextItemId(mLastId);
            return mLastId;
        }

        private static Type get(int id) {
            return VALUES[ID_FACTORY.getGroupId(id)];
        }

        private int mLastId;
        private final Class mLoaderClass;

        private static final Type[] VALUES = values();
        private static final IdGroupIntFactory ID_FACTORY = new IdGroupIntFactory(VALUES.length);
    }

    private static final String TAG = LoaderFactory.class.getSimpleName();
}
