package com.yagodar.android.bill_please.store;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Order;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableOrdersContract;
import com.yagodar.android.custom.model.rep.AbsMultGroupCancelRepository;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.essential.operation.OperationResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yagodar on 26.06.2015.
 */
public class OrderRepository extends AbsMultGroupCancelRepository<Order> {

    private OrderRepository() {
        mTableManager = DbManager.getInstance().getTableManager(DbTableOrdersContract.getInstance());
        mContentValuesHolder = new ContentValues();
    }

    public static OrderRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (BillRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OrderRepository();
                }
            }
        }

        return INSTANCE;
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult load(long groupId, long id) {
        return super.load(groupId, id);
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Order>> loadGroupMap(long groupId) {
        return super.loadGroupMap(groupId);
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Map<Long, Order>>> loadAllMap() {
        return super.loadAllMap();
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, List<Order>>> loadAllList() {
        return super.loadAllList();
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, Map<Long, Order> modelById) {
        return super.updateGroup(groupId, modelById);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, List<Order> modelList) {
        return super.updateGroup(groupId, modelList);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Map<Long, Order>> modelByIdByGroup) {
        return super.updateAllMap(modelByIdByGroup);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(Map<Long, List<Order>> modelListByGroup) {
        return super.updateAllList(modelListByGroup);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteGroup(long groupId) {
        return super.deleteGroup(groupId);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll() {
        return super.deleteAll();
    }

    @Deprecated
    @Override
    public OperationResult<Order> load(long groupId, long id, CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order by id not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Order>> loadGroupMap(long groupId, CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order map not supported!");
    }

    //endregion

    @Override
    public OperationResult<List<Order>> loadGroupList(long groupId, CancellationSignal signal) {
        OperationResult<List<Order>> opResult = new OperationResult<>();
        OperationResult<List<DbTableManager.DbTableRecord>> getGroupRecordsResult;
        synchronized (this) {
            getGroupRecordsResult = mTableManager.getGroupRecords(DbTableOrdersContract.COLUMN_NAME_BILL_ID, groupId);
        }
        if(!getGroupRecordsResult.isSuccessful()) {
            opResult.setFailMessage(getGroupRecordsResult.getFailMessage());
            opResult.setFailMessageId(getGroupRecordsResult.getFailMessageId());
            opResult.setFailThrowable(getGroupRecordsResult.getFailThrowable());
        } else {
            List<Order> orderList = new LinkedList<>();
            long id;
            String name;
            String cost;
            String share;
            for (DbTableManager.DbTableRecord record : getGroupRecordsResult.getData()) {
                id = record.getId();
                name = (String) record.getValue(DbTableOrdersContract.COLUMN_NAME_ORDER_NAME);
                cost = (String) record.getValue(DbTableOrdersContract.COLUMN_NAME_COST);
                share = (String) record.getValue(DbTableOrdersContract.COLUMN_NAME_SHARE);
                orderList.add(new Order(id, name, cost, share));
            }
            opResult.setData(orderList);
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> loadGroupCount(long groupId, CancellationSignal signal) {
        OperationResult<Integer> opResult;
        synchronized (this) {
            opResult = mTableManager.getGroupRecordsCount(DbTableOrdersContract.COLUMN_NAME_BILL_ID, groupId);
        }
        return opResult;
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Map<Long, Map<Long, Order>>> loadAllMap(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order all map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, List<Order>>> loadAllList(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order all list not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Integer>> loadAllCount(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order all count not supported!");
    }

    //endregion

    @Override
    public OperationResult<Long> insert(long groupId, CancellationSignal signal) {
        OperationResult<Long> opResult;
        synchronized (this) {
            opResult = mTableManager.insertToGroup(DbTableOrdersContract.COLUMN_NAME_BILL_ID, groupId);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Long> insert(long groupId, Order model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }
        OperationResult<Long> opResult;
        synchronized (this) {
            opResult = mTableManager.insert(pack(groupId, model));
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> update(long groupId, Order model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }
        return update(model.getId(), pack(groupId, model), signal);
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, Map<Long, Order> modelById, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update bill order group by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, List<Order> modelList, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update bill order group by list not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Map<Long, Order>> modelByIdByGroup, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update all bill order groups by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(Map<Long, List<Order>> modelListByGroup, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update all bill order groups by list not supported!");
    }

    //endregion

    @Override
    public OperationResult<Integer> delete(long groupId, long id, CancellationSignal signal) {
        OperationResult<Integer> opResult;
        synchronized (this) {
            opResult = mTableManager.delete(id);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_remove_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> delete(long groupId, Order model, CancellationSignal signal) {
        return delete(groupId, model.getId(), signal);
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Integer> deleteGroup(long groupId, CancellationSignal signal) {
        throw new UnsupportedOperationException("Delete bill order group not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll(CancellationSignal signal) {
        throw new UnsupportedOperationException("Delete all bill order groups not supported!");
    }

    //endregion

    public OperationResult<Integer> update(Bundle modelBundle, CancellationSignal signal) {
        if(modelBundle == null) {
            throw new IllegalArgumentException("Bill model bundle must not be null!");
        }
        return update(modelBundle.getLong(BaseColumns._ID), pack(modelBundle), signal);
    }

    private OperationResult<Integer> update(long id, ContentValues modelContentValues, CancellationSignal signal) {
        OperationResult<Integer> opResult;
        synchronized (this) {
            opResult = mTableManager.update(id, modelContentValues);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_update_failed);
        }
        return opResult;
    }

    private ContentValues pack(long groupId, Order model) {
        return pack(groupId,
                model.getName(),
                model.getFormattedCost(),
                model.getFormattedShare());
    }

    private ContentValues pack(Bundle modelBundle) {
        return pack(modelBundle.getLong(DbTableOrdersContract.COLUMN_NAME_BILL_ID),
                modelBundle.getString(DbTableOrdersContract.COLUMN_NAME_ORDER_NAME),
                modelBundle.getString(DbTableOrdersContract.COLUMN_NAME_COST),
                modelBundle.getString(DbTableOrdersContract.COLUMN_NAME_SHARE));
    }

    private ContentValues pack(long groupId, String name, String cost, String share) {
        mContentValuesHolder.put(DbTableOrdersContract.COLUMN_NAME_BILL_ID, groupId);
        mContentValuesHolder.put(DbTableOrdersContract.COLUMN_NAME_ORDER_NAME, name);
        mContentValuesHolder.put(DbTableOrdersContract.COLUMN_NAME_COST, cost);
        mContentValuesHolder.put(DbTableOrdersContract.COLUMN_NAME_SHARE, share);
        return mContentValuesHolder;
    }

    private final DbTableManager mTableManager;
    private final ContentValues mContentValuesHolder;

    private static volatile OrderRepository INSTANCE;
}
