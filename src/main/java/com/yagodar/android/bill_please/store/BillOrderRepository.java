package com.yagodar.android.bill_please.store;

import android.content.ContentValues;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.model.rep.AbsMultGroupCancelRepository;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.essential.operation.OperationResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yagodar on 26.06.2015.
 */
public class BillOrderRepository extends AbsMultGroupCancelRepository<BillOrder> {

    private BillOrderRepository() {
        mTableManager = DbManager.getInstance().getTableManager(DbTableBillOrderContract.getInstance());
        mContentValuesHolder = new ContentValues();
    }

    public static BillOrderRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (BillRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BillOrderRepository();
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
    public OperationResult<Map<Long, BillOrder>> loadGroupMap(long groupId) {
        return super.loadGroupMap(groupId);
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Map<Long, BillOrder>>> loadAllMap() {
        return super.loadAllMap();
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, List<BillOrder>>> loadAllList() {
        return super.loadAllList();
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, Map<Long, BillOrder> modelById) {
        return super.updateGroup(groupId, modelById);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, List<BillOrder> modelList) {
        return super.updateGroup(groupId, modelList);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Map<Long, BillOrder>> modelByIdByGroup) {
        return super.updateAllMap(modelByIdByGroup);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(Map<Long, List<BillOrder>> modelListByGroup) {
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
    public OperationResult<BillOrder> load(long groupId, long id, CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order by id not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, BillOrder>> loadGroupMap(long groupId, CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order map not supported!");
    }

    //endregion

    @Override
    public OperationResult<List<BillOrder>> loadGroupList(long groupId, CancellationSignal signal) {
        OperationResult<List<BillOrder>> opResult = new OperationResult<>();
        OperationResult<List<DbTableManager.DbTableRecord>> getGroupRecordsResult;
        synchronized (this) {
            getGroupRecordsResult = mTableManager.getGroupRecords(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);
        }
        if(!getGroupRecordsResult.isSuccessful()) {
            opResult.setFailMessage(getGroupRecordsResult.getFailMessage());
            opResult.setFailMessageId(getGroupRecordsResult.getFailMessageId());
            opResult.setFailThrowable(getGroupRecordsResult.getFailThrowable());
        } else {
            List<BillOrder> billOrderList = new LinkedList<>();
            long id;
            String name;
            String cost;
            String share;
            for (DbTableManager.DbTableRecord record : getGroupRecordsResult.getData()) {
                id = record.getId();
                name = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME);
                cost = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_COST);
                share = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_SHARE);
                billOrderList.add(new BillOrder(id, name, cost, share));
            }
            opResult.setData(billOrderList);
        }
        return opResult;
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Map<Long, Map<Long, BillOrder>>> loadAllMap(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order all map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, List<BillOrder>>> loadAllList(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill order all list not supported!");
    }

    //endregion

    @Override
    public OperationResult<Long> insert(long groupId, CancellationSignal signal) {
        OperationResult<Long> opResult;
        synchronized (this) {
            opResult = mTableManager.insertToGroup(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Long> insert(long groupId, BillOrder model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }
        OperationResult<Long> opResult;
        synchronized (this) {
            setContentValues(groupId, model);
            opResult = mTableManager.insert(mContentValuesHolder);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> update(long groupId, BillOrder model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }
        OperationResult<Integer> opResult;
        synchronized (this) {
            setContentValues(groupId, model);
            opResult = mTableManager.update(model.getId(), mContentValuesHolder);
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_update_failed);
        }
        return opResult;
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, Map<Long, BillOrder> modelById, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update bill order group by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, List<BillOrder> modelList, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update bill order group by list not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Map<Long, BillOrder>> modelByIdByGroup, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update all bill order groups by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(Map<Long, List<BillOrder>> modelListByGroup, CancellationSignal signal) {
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
    public OperationResult<Integer> delete(long groupId, BillOrder model, CancellationSignal signal) {
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

    private void setContentValues(long groupId, BillOrder model) {
        mContentValuesHolder.put(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);
        mContentValuesHolder.put(DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME, model.getName());
        mContentValuesHolder.put(DbTableBillOrderContract.COLUMN_NAME_COST, model.getFormattedCost());
        mContentValuesHolder.put(DbTableBillOrderContract.COLUMN_NAME_SHARE, model.getFormattedShare());
    }

    private final DbTableManager mTableManager;
    private final ContentValues mContentValuesHolder;

    private static volatile BillOrderRepository INSTANCE;
}
