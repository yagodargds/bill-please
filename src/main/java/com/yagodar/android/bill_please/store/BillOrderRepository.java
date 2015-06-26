package com.yagodar.android.bill_please.store;

import android.content.ContentValues;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.essential.model.rep.IMultGroupRepository;
import com.yagodar.essential.operation.OperationResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yagodar on 26.06.2015.
 */
public class BillOrderRepository implements IMultGroupRepository<BillOrder> {

    private BillOrderRepository() {
        mTableManager = DbManager.getInstance().getTableManager(DbTableBillOrderContract.getInstance());
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

    @Deprecated
    @Override
    public OperationResult<BillOrder> load(long groupId, long id) {
        throw new UnsupportedOperationException("Load bill order by id not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, BillOrder>> loadGroupMap(long groupId) {
        throw new UnsupportedOperationException("Load bill order map not supported!");
    }

    @Override
    public OperationResult<List<BillOrder>> loadGroupList(long groupId) {
        OperationResult<List<BillOrder>> opResult = new OperationResult<>();

        OperationResult<List<DbTableManager.DbTableRecord>> getGroupRecordsResult = mTableManager.getGroupRecords(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);
        if(!getGroupRecordsResult.isSuccessful()) {
            opResult.setFailMessage(getGroupRecordsResult.getFailMessage());
            opResult.setFailMessageId(getGroupRecordsResult.getFailMessageId());
            opResult.setFailThrowable(getGroupRecordsResult.getFailThrowable());
        } else {
            List<BillOrder> billOrderList = new LinkedList<>();

            long id;
            for (DbTableManager.DbTableRecord record : getGroupRecordsResult.getData()) {
                id = record.getId();

                String name = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME);
                String cost = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_COST);
                String share = (String) record.getValue(DbTableBillOrderContract.COLUMN_NAME_SHARE);

                BillOrder billOrder = new BillOrder(id, name, cost, share);

                billOrderList.add(billOrder);
            }

            opResult.setData(billOrderList);
        }

        return opResult;
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Map<Long, BillOrder>>> loadAllMap() {
        throw new UnsupportedOperationException("Load bill order all map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, List<BillOrder>>> loadAllList() {
        throw new UnsupportedOperationException("Load bill order all list not supported!");
    }

    @Override
    public OperationResult<Long> insert(long groupId) {
        OperationResult<Long> opResult = mTableManager.insertToGroup(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);

        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }

        return opResult;
    }

    @Override
    public OperationResult<Long> insert(long groupId, BillOrder model) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }

        ContentValues contentValues = getContentValues(groupId, model);
        OperationResult<Long> opResult = mTableManager.insert(contentValues);
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }

        return opResult;
    }

    @Override
    public OperationResult<Integer> update(long groupId, BillOrder model) {
        if(model == null) {
            throw new IllegalArgumentException("Bill Order must not be null!");
        }

        ContentValues contentValues = getContentValues(groupId, model);
        OperationResult<Integer> opResult = mTableManager.update(model.getId(), contentValues);
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_update_failed);
        }

        return opResult;
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, Map<Long, BillOrder> modelById) {
        throw new UnsupportedOperationException("Update bill order group by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateGroup(long groupId, List<BillOrder> modelList) {
        throw new UnsupportedOperationException("Update bill order group by list not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Map<Long, BillOrder>> modelByIdByGroup) {
        throw new UnsupportedOperationException("Update all bill order groups by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(Map<Long, List<BillOrder>> modelListByGroup) {
        throw new UnsupportedOperationException("Update all bill order groups by list not supported!");
    }

    @Override
    public OperationResult<Integer> delete(long groupId, long id) {
        OperationResult<Integer> opResult = mTableManager.delete(id);

        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_remove_failed);
        }

        return opResult;
    }

    @Override
    public OperationResult<Integer> delete(long groupId, BillOrder model) {
        return delete(groupId, model.getId());
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteGroup(long groupId) {
        throw new UnsupportedOperationException("Delete bill order group not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll() {
        throw new UnsupportedOperationException("Delete all bill order groups not supported!");
    }

    private ContentValues getContentValues(long groupId, BillOrder model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbTableBillOrderContract.COLUMN_NAME_BILL_ID, groupId);
        contentValues.put(DbTableBillOrderContract.COLUMN_NAME_ORDER_NAME, model.getName());
        contentValues.put(DbTableBillOrderContract.COLUMN_NAME_COST, model.getFormattedCost());
        contentValues.put(DbTableBillOrderContract.COLUMN_NAME_SHARE, model.getFormattedShare());
        return contentValues;
    }

    private DbTableManager mTableManager;

    private static volatile BillOrderRepository INSTANCE;
}
