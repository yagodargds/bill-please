package com.yagodar.android.bill_please.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.factory.IdGroupIntFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by yagodar on 17.06.2015.
 */
public class LoaderFactory {

    @SuppressWarnings("unchecked")
    public static AsyncTaskLoader<LoaderResult> createLoader(Context context, int id, Bundle args) {
        try {
            Type type = Type.get(id);
            AsyncTaskLoader<LoaderResult> loader = (AsyncTaskLoader<LoaderResult>) type.mLoaderClass.getConstructor(Context.class, Bundle.class).newInstance(context, args);
            type.registerCreatedId(id);
            return loader;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public enum Type {
        LOAD_BILL_LIST(LoadBillListLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.NORMAL),
        APPEND_BILL(AppendBillLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        UPDATE_BILL(UpdateBillLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        REMOVE_BILL(RemoveBillLoader.class, IdType.NEXT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        LOAD_BILL(LoadBillLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.NORMAL),
        APPEND_BILL_ORDER(AppendBillOrderLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        UPDATE_BILL_ORDER(UpdateBillOrderLoader.class, IdType.UNIT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        REMOVE_BILL_ORDER(RemoveBillOrderLoader.class, IdType.NEXT, ILoaderProgressContext.ProgressShowType.HIDDEN),
        ;

        Type(Class<?> loaderClass, IdType idType, ILoaderProgressContext.ProgressShowType progressShowType) {
            mLastId = ordinal();
            mLoaderClass = loaderClass;
            mIdType = idType;
            mProgressShowType = progressShowType;
            mCreatedIdCollection = new HashSet<>();
        }

        public void continueLoading(ILoaderProgressContext context, LoaderManager loaderManager) {
            switch (mIdType) {
                case UNIT:
                    int unitId = ordinal();
                    if (loaderManager.getLoader(unitId) != null) {
                        context.startLoading(unitId, null, mProgressShowType);
                    }
                    break;
                case NEXT:
                    Collection<Integer> continueIdCollection = new ArrayList<>();
                    Collection<Integer> removeIdCollection = new ArrayList<>();
                    synchronized (mCreatedIdCollection) {
                        for (int id : mCreatedIdCollection) {
                            if (loaderManager.getLoader(id) != null) {
                                continueIdCollection.add(id);
                            } else {
                                removeIdCollection.add(id);
                            }
                        }
                    }
                    for (int id : continueIdCollection) {
                        context.startLoading(id, null, mProgressShowType);
                    }
                    synchronized (mCreatedIdCollection) {
                        for (int id : removeIdCollection) {
                            mCreatedIdCollection.remove(id);
                        }
                    }
                    continueIdCollection.clear();
                    removeIdCollection.clear();
                    break;
                default:
                    break;
            }
        }

        public void startLoading(ILoaderProgressContext context, Bundle args) {
            int id;
            switch (mIdType) {
                case UNIT:
                    id = ordinal();
                    break;
                case NEXT:
                    id = getNextId();
                    break;
                default:
                    id = ordinal();
                    break;
            }
            context.startLoading(id, args, mProgressShowType);
        }

        public static Type get(int id) {
            return VALUES[ID_FACTORY.getGroupId(id)];
        }

        private int getNextId() {
            mLastId = ID_FACTORY.getNextItemId(mLastId);
            return mLastId;
        }

        private void registerCreatedId(int id) {
            switch (mIdType) {
                case UNIT:
                    break;
                case NEXT:
                    synchronized (mCreatedIdCollection) {
                        mCreatedIdCollection.add(id);
                    }
                    break;
                default:
                    break;
            }
        }

        private int mLastId;
        private final Class<?> mLoaderClass;
        private final IdType mIdType;
        private final ILoaderProgressContext.ProgressShowType mProgressShowType;
        private final Collection<Integer> mCreatedIdCollection;

        private static final Type[] VALUES = values();
        private static final IdGroupIntFactory ID_FACTORY = new IdGroupIntFactory(VALUES.length);
    }

    public enum IdType {
        UNIT,
        NEXT,
        ;
    }

    private static final String TAG = LoaderFactory.class.getSimpleName();
}
