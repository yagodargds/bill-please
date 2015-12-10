package com.yagodar.android.bill_please.store;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableBillContract;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
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
        mTableManager = mManager.getTableManager(DbTableBillContract.getInstance());
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
            for (DbTableManager.DbTableRecord record : getAllRecordsResult.getData()) {
                id = record.getId();
                name = (String) record.getValue(DbTableBillContract.COLUMN_NAME_BILL_NAME);
                taxType = null;
                dbTaxType = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TAX_TYPE);
                if(dbTaxType != null) {
                    taxType = Bill.TaxTipType.valueOf(dbTaxType);
                }
                dbTaxVal = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TAX_VAL);
                tipType = null;
                dbTipType = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TIP_TYPE);
                if(dbTipType != null) {
                    tipType = Bill.TaxTipType.valueOf(dbTipType);
                }
                dbTipVal = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TIP_VAL);
                billList.add(new Bill(id, name, taxType, dbTaxVal, tipType, dbTipVal));
            }
            opResult.setData(billList);
        }
        return opResult;
    }

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
            setContentValues(model);
            opResult = mTableManager.insert(mContentValuesHolder);
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
        OperationResult<Integer> opResult;
        synchronized (this) {
            setContentValues(model);
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
                rowsAffected = db.delete(DbTableBillContract.getInstance().getTableName(), BaseColumns._ID + DbHelper.SYMB_OP_EQUALITY + id, null);
                opResult.setData(rowsAffected);
                rowsAffected = db.delete(DbTableBillOrderContract.getInstance().getTableName(), DbTableBillOrderContract.COLUMN_NAME_BILL_ID + DbHelper.SYMB_OP_EQUALITY + id, null);
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

    private void setContentValues(Bill model) {
        mContentValuesHolder.put(DbTableBillContract.COLUMN_NAME_BILL_NAME, model.getName());
        mContentValuesHolder.put(DbTableBillContract.COLUMN_NAME_TAX_TYPE, model.getTaxType().toString());
        mContentValuesHolder.put(DbTableBillContract.COLUMN_NAME_TAX_VAL, model.getFormattedTaxVal());
        mContentValuesHolder.put(DbTableBillContract.COLUMN_NAME_TIP_TYPE, model.getTipType().toString());
        mContentValuesHolder.put(DbTableBillContract.COLUMN_NAME_TIP_VAL, model.getFormattedTipVal());
    }

    private final DbManager mManager;
    private final DbTableManager mTableManager;
    private final ContentValues mContentValuesHolder;

    private static volatile BillRepository INSTANCE;
}
