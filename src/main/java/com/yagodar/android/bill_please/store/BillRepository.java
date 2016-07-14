package com.yagodar.android.bill_please.store;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableBillsContract;
import com.yagodar.android.bill_please.store.db.DbTableOrdersContract;
import com.yagodar.android.custom.model.rep.AbsMultCancelRepository;
import com.yagodar.android.database.sqlite.DbHelper;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.essential.operation.OperationResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillRepository extends AbsMultCancelRepository<Bill> {

    private BillRepository() {
        mManager = DbManager.getInstance();
        mTableManager = mManager.getTableManager(DbTableBillsContract.getInstance());
        mContentValuesHolder = new ContentValues();
    }

    public static BillRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (BillRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BillRepository();
                }
            }
        }

        return INSTANCE;
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Bill> load(long id) {
        return super.load(id);
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Bill>> loadAllMap() {
        return super.loadAllMap();
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Bill> modelById) {
        return super.updateAllMap(modelById);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(List<Bill> modelList) {
        return super.updateAllList(modelList);
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll() {
        return super.deleteAll();
    }

    @Deprecated
    @Override
    public OperationResult<Bill> load(long id, CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill by id not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Bill>> loadAllMap(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load bill map not supported!");
    }

    //endregion

    @Override
    public OperationResult<List<Bill>> loadAllList(CancellationSignal signal) {
        OperationResult<List<Bill>> opResult = new OperationResult<>();
        OperationResult<List<DbTableManager.DbTableRecord>> getAllRecordsResult;
        synchronized (this) {
            getAllRecordsResult = mTableManager.getAllRecords();
        }
        if(!getAllRecordsResult.isSuccessful()) {
            opResult.setFailMessage(getAllRecordsResult.getFailMessage());
            opResult.setFailMessageId(getAllRecordsResult.getFailMessageId());
            opResult.setFailThrowable(getAllRecordsResult.getFailThrowable());
        } else {
            List<Bill> billList = new LinkedList<>();
            long id;
            String name;
            Bill.TaxTipType taxType;
            String dbTaxType;
            String dbTaxVal;
            Bill.TaxTipType tipType;
            String dbTipType;
            String dbTipVal;
            OperationResult<Integer> loadOrderCountResult;
            int orderCount;
            for (DbTableManager.DbTableRecord record : getAllRecordsResult.getData()) {
                id = record.getId();
                name = (String) record.getValue(DbTableBillsContract.COLUMN_NAME_BILL_NAME);
                taxType = null;
                dbTaxType = (String) record.getValue(DbTableBillsContract.COLUMN_NAME_TAX_TYPE);
                if(dbTaxType != null) {
                    taxType = Bill.TaxTipType.valueOf(dbTaxType);
                }
                dbTaxVal = (String) record.getValue(DbTableBillsContract.COLUMN_NAME_TAX_VAL);
                tipType = null;
                dbTipType = (String) record.getValue(DbTableBillsContract.COLUMN_NAME_TIP_TYPE);
                if(dbTipType != null) {
                    tipType = Bill.TaxTipType.valueOf(dbTipType);
                }
                dbTipVal = (String) record.getValue(DbTableBillsContract.COLUMN_NAME_TIP_VAL);
                loadOrderCountResult = OrderRepository.getInstance().loadGroupCount(id);
                if(!loadOrderCountResult.isSuccessful()) {
                    opResult.setFailMessage(loadOrderCountResult.getFailMessage());
                    opResult.setFailMessageId(loadOrderCountResult.getFailMessageId());
                    opResult.setFailThrowable(loadOrderCountResult.getFailThrowable());
                } else {
                    orderCount = loadOrderCountResult.getData();
                    billList.add(new Bill(id, name, taxType, dbTaxVal, tipType, dbTipVal, orderCount));
                }
                if(!opResult.isSuccessful()) {
                    break;
                }
            }
            opResult.setData(billList);
        }
        return opResult;
    }

    //region Deprecated
    @Override
    public OperationResult<Integer> loadCount(CancellationSignal signal) {
        throw new UnsupportedOperationException("Load count not supported!");
    }
    //endregion

    @Override
    public OperationResult<Long> insert(CancellationSignal signal) {
        OperationResult<Long> opResult;
        synchronized (this) {
            opResult = mTableManager.insert();
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Long> insert(Bill model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill must not be null!");
        }
        OperationResult<Long> opResult;
        synchronized (this) {
            opResult = mTableManager.insert(pack(model));
        }
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> update(Bill model, CancellationSignal signal) {
        if(model == null) {
            throw new IllegalArgumentException("Bill must not be null!");
        }
        return update(model.getId(), pack(model), signal);
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Bill> modelById, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update all bills by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(List<Bill> modelList, CancellationSignal signal) {
        throw new UnsupportedOperationException("Update all bills by list not supported!");
    }

    //endregion

    @Override
    public OperationResult<Integer> delete(long id, CancellationSignal signal) {
        OperationResult<Integer> opResult = new OperationResult<>();
        synchronized (this) {
            SQLiteDatabase db = null;
            try {
                db = mManager.getDatabase();
                db.beginTransaction();
                if (signal != null) {
                    signal.throwIfCanceled();
                }
                int rowsAffected;
                rowsAffected = db.delete(DbTableBillsContract.getInstance().getTableName(), BaseColumns._ID + DbHelper.SYMB_OP_EQUALITY + id, null);
                opResult.setData(rowsAffected);
                rowsAffected = db.delete(DbTableOrdersContract.getInstance().getTableName(), DbTableOrdersContract.COLUMN_NAME_BILL_ID + DbHelper.SYMB_OP_EQUALITY + id, null);
                opResult.setData(opResult.getData() + rowsAffected);
                if (signal != null) {
                    signal.throwIfCanceled();
                }
                db.setTransactionSuccessful();
            } catch (OperationCanceledException e) {
                throw e;
            } catch (Exception e) {
                opResult.setFailThrowable(e);
            } finally {
                mManager.endTransaction(db);
                mManager.closeDatabase(db);
            }
        }
        return opResult;
    }

    @Override
    public OperationResult<Integer> delete(Bill model, CancellationSignal signal) {
        return delete(model.getId(), signal);
    }

    //region Deprecated

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll(CancellationSignal signal) {
        throw new UnsupportedOperationException("Delete all bills not supported!");
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

    private ContentValues pack(Bill model) {
        return pack(model.getName(),
                model.getTaxType().toString(),
                model.getFormattedTaxVal(),
                model.getTipType().toString(),
                model.getFormattedTipVal());
    }

    private ContentValues pack(Bundle modelBundle) {
        return pack(modelBundle.getString(DbTableBillsContract.COLUMN_NAME_BILL_NAME),
                modelBundle.getString(DbTableBillsContract.COLUMN_NAME_TAX_TYPE),
                modelBundle.getString(DbTableBillsContract.COLUMN_NAME_TAX_VAL),
                modelBundle.getString(DbTableBillsContract.COLUMN_NAME_TIP_TYPE),
                modelBundle.getString(DbTableBillsContract.COLUMN_NAME_TIP_VAL));
    }

    private ContentValues pack(String name, String taxType, String taxVal, String tipType, String tipVal) {
        mContentValuesHolder.put(DbTableBillsContract.COLUMN_NAME_BILL_NAME, name);
        mContentValuesHolder.put(DbTableBillsContract.COLUMN_NAME_TAX_TYPE, taxType);
        mContentValuesHolder.put(DbTableBillsContract.COLUMN_NAME_TAX_VAL, taxVal);
        mContentValuesHolder.put(DbTableBillsContract.COLUMN_NAME_TIP_TYPE, tipType);
        mContentValuesHolder.put(DbTableBillsContract.COLUMN_NAME_TIP_VAL, tipVal);
        return mContentValuesHolder;
    }

    private final DbManager mManager;
    private final DbTableManager mTableManager;
    private final ContentValues mContentValuesHolder;

    private static volatile BillRepository INSTANCE;
}
