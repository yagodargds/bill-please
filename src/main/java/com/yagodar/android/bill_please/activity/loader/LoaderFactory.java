package com.yagodar.android.bill_please.activity.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

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
        LOAD_BILL_LIST(LoadBillListLoader.class, IdType.UNIT),
        APPEND_BILL(AppendBillLoader.class, IdType.UNIT),
        UPDATE_BILL(UpdateBillLoader.class, IdType.UNIT),
        REMOVE_BILL(RemoveBillLoader.class, IdType.NEXT),
        LOAD_BILL(LoadBillLoader.class, IdType.UNIT),
        APPEND_BILL_ORDER(AppendOrderLoader.class, IdType.UNIT),
        UPDATE_BILL_ORDER(UpdateOrderLoader.class, IdType.UNIT),
        REMOVE_BILL_ORDER(RemoveOrderLoader.class, IdType.NEXT),
        ;

        Type(Class<?> loaderClass, IdType idType) {
            mLastId = ordinal();
            mLoaderClass = loaderClass;
            mIdType = idType;
            mCreatedIdCollection = new HashSet<>();
        }

        public void continueLoading(ILoaderProgressContext context, LoaderManager loaderManager) {
            switch (mIdType) {
                case UNIT:
                    int unitId = ordinal();
                    if (loaderManager.getLoader(unitId) != null) {
                        context.startLoading(unitId, null);
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
                        context.startLoading(id, null);
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
            context.startLoading(id, args);
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
        private final Collection<Integer> mCreatedIdCollection;

        private static final Type[] VALUES = values();
        private static final IdGroupIntFactory ID_FACTORY = new IdGroupIntFactory(VALUES.length);
    }

    private enum IdType {
        UNIT,
        NEXT,
        ;
    }

    private static final String TAG = LoaderFactory.class.getSimpleName();
}
