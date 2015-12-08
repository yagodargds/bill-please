package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.yagodar.android.bill_please.activity.bill.loader.AppendBillLoader;
import com.yagodar.android.bill_please.activity.bill.loader.AppendBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.LoadBillLoader;
import com.yagodar.android.bill_please.activity.bill.loader.RemoveBillOrderLoader;
import com.yagodar.android.bill_please.activity.bill.loader.UpdateBillLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.LoadBillListLoader;
import com.yagodar.android.bill_please.activity.bill_list.loader.RemoveBillLoader;
import com.yagodar.android.bill_please.activity.bill_order.loader.UpdateBillOrderLoader;
import com.yagodar.android.custom.fragment.progress.ILoaderProgressContext;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.essential.factory.IdGroupIntFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yagodar on 17.06.2015.
 */
public class LoaderFactory {

    public static AbsAsyncTaskLoader createLoader(Context context, int id, Bundle args) {
        try {
            return (AbsAsyncTaskLoader) Type.get(id).mLoaderClass.getConstructor(Context.class, Bundle.class).newInstance(context, args);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public static void startAllProcessLoader(ILoaderProgressContext context, LoaderFactory.Type type, LoaderManager loaderManager) {
        Set<Integer> processLoaderIdSet = new HashSet<>();
        Set<Integer> completedLoaderIdSet = new HashSet<>();
        synchronized (type.mProcessLoaderIdSet) {
            for (Integer id : type.getProcessLoaderIdSet()) {
                if (loaderManager.getLoader(id) != null) {
                    processLoaderIdSet.add(id);
                } else {
                    completedLoaderIdSet.add(id);
                }
            }
        }
        for (Integer id : processLoaderIdSet) {
            context.startLoading(id, null, type.mProgressShowType);
        }
        synchronized (type.mProcessLoaderIdSet) {
            for (Integer id : completedLoaderIdSet) {
                type.mProcessLoaderIdSet.remove(id);
            }
        }
    }

    public static void onLoaderCreated(int id) {
        onLoaderCreated(Type.get(id), id);
    }

    public static void onLoaderCreated(Type type, int id) {
        synchronized (type.mProcessLoaderIdSet) {
            type.mProcessLoaderIdSet.add(id);
        }
    }

    public static void onLoaderCompleted(int id) {
        onLoaderCompleted(Type.get(id), id);
    }

    public static void onLoaderCompleted(Type type, int id) {
        synchronized (type.mProcessLoaderIdSet) {
            type.mProcessLoaderIdSet.remove(id);
        }

    }

    public enum Type {
        LOAD_BILL_LIST(LoadBillListLoader.class, ILoaderProgressContext.ProgressShowType.NORMAL),
        APPEND_BILL(AppendBillLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        UPDATE_BILL(UpdateBillLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        REMOVE_BILL(RemoveBillLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        LOAD_BILL(LoadBillLoader.class, ILoaderProgressContext.ProgressShowType.NORMAL),
        APPEND_BILL_ORDER(AppendBillOrderLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        UPDATE_BILL_ORDER(UpdateBillOrderLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        REMOVE_BILL_ORDER(RemoveBillOrderLoader.class, ILoaderProgressContext.ProgressShowType.HIDDEN),
        ;

        Type(Class<?> loaderClass, ILoaderProgressContext.ProgressShowType progressShowType) {
            mLastId = ordinal();
            mLoaderClass = loaderClass;
            mProgressShowType = progressShowType;
            mProcessLoaderIdSet = new HashSet<>();
        }

        public int getLastId() {
            return mLastId;
        }

        public int getNextId() {
            mLastId = ID_FACTORY.getNextItemId(mLastId);
            return mLastId;
        }

        public ILoaderProgressContext.ProgressShowType getProgressShowType() {
            return mProgressShowType;
        }

        public Set<Integer> getProcessLoaderIdSet() {
            return mProcessLoaderIdSet;
        }

        public static Type get(int id) {
            return VALUES[ID_FACTORY.getGroupId(id)];
        }

        private int mLastId;
        private final Class<?> mLoaderClass;
        private final ILoaderProgressContext.ProgressShowType mProgressShowType;
        private final Set<Integer> mProcessLoaderIdSet;

        private static final Type[] VALUES = values();
        private static final IdGroupIntFactory ID_FACTORY = new IdGroupIntFactory(VALUES.length);
    }

    private static final String TAG = LoaderFactory.class.getSimpleName();
}
