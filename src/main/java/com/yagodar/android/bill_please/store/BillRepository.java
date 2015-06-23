package com.yagodar.android.bill_please.store;

import android.content.ContentValues;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.bill_please.store.db.DbTableBillContract;
import com.yagodar.android.database.sqlite.DbTableManager;
import com.yagodar.essential.model.rep.IMultRepository;
import com.yagodar.essential.operation.OperationResult;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillRepository implements IMultRepository<Bill> {

    private BillRepository() {
        mTableManager = DbManager.getInstance().getTableManager(DbTableBillContract.getInstance());
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

    @Deprecated
    @Override
    public OperationResult<Bill> load(long id) {
        throw new UnsupportedOperationException("Load bill by id not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Map<Long, Bill>> loadAllMap() {
        throw new UnsupportedOperationException("Load bill map not supported!");
    }

    @Override
    public OperationResult<List<Bill>> loadAllList() {
        OperationResult<List<Bill>> opResult = new OperationResult<>();

        OperationResult<List<DbTableManager.DbTableRecord>> getAllRecordsResult = mTableManager.getAllRecords();
        if(!getAllRecordsResult.isSuccessful()) {
            opResult.setFailMessage(getAllRecordsResult.getFailMessage());
            opResult.setFailMessageId(getAllRecordsResult.getFailMessageId());
            opResult.setFailThrowable(getAllRecordsResult.getFailThrowable());
        } else {
            List<Bill> billList = new LinkedList<>();

            long id;
            for (DbTableManager.DbTableRecord record : getAllRecordsResult.getData()) {
                id = record.getId();

                String name = (String) record.getValue(DbTableBillContract.COLUMN_NAME_BILL_NAME);

                Bill.TaxTipType taxType = null;
                String dbTaxType = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TAX_TYPE);
                if(dbTaxType != null) {
                    taxType = Bill.TaxTipType.valueOf(dbTaxType);
                }

                BigDecimal taxVal = null;
                String dbTaxVal = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TAX_VAL);
                if(dbTaxVal != null) {
                    taxVal = new BigDecimal(dbTaxVal);
                }

                Bill.TaxTipType tipType = null;
                String dbTipType = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TIP_TYPE);
                if(dbTipType != null) {
                    tipType = Bill.TaxTipType.valueOf(dbTipType);
                }

                BigDecimal tipVal = null;
                String dbTipVal = (String) record.getValue(DbTableBillContract.COLUMN_NAME_TIP_VAL);
                if(dbTipVal != null) {
                    tipVal = new BigDecimal(dbTipVal);
                }

                Bill bill = new Bill(id, name, taxType, taxVal, tipType, tipVal);

                billList.add(bill);
            }

            opResult.setData(billList);
        }

        return opResult;
    }

    @Override
    public OperationResult<Long> insert() {
        OperationResult<Long> opResult = mTableManager.insert();

        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }

        return opResult;
    }

    @Override
    public OperationResult<Long> insert(Bill model) {
        if(model == null) {
            throw new IllegalArgumentException("Bill must not be null!");
        }

        ContentValues contentValues = getContentValues(model);
        OperationResult<Long> opResult = mTableManager.insert(contentValues);
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_append_failed);
        }

        return opResult;
    }

    @Override
    public OperationResult<Integer> update(Bill model) {
        if(model == null) {
            throw new IllegalArgumentException("Bill must not be null!");
        }

        ContentValues contentValues = getContentValues(model);
        OperationResult<Integer> opResult = mTableManager.update(model.getId(), contentValues);
        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_update_failed);
        }

        return opResult;
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllMap(Map<Long, Bill> modelById) {
        throw new UnsupportedOperationException("Update all bills by map not supported!");
    }

    @Deprecated
    @Override
    public OperationResult<Integer> updateAllList(List<Bill> modelList) {
        throw new UnsupportedOperationException("Update all bills by list not supported!");
    }

    @Override
    public OperationResult<Integer> delete(long id) {
        OperationResult<Integer> opResult = mTableManager.delete(id);

        if(!opResult.isSuccessful()) {
            opResult.setFailMessageId(R.string.err_remove_failed);
        }

        return opResult;
    }

    @Deprecated
    @Override
    public OperationResult<Integer> deleteAll() {
        throw new UnsupportedOperationException("Delete all bills not supported!");
    }

    private ContentValues getContentValues(Bill model) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbTableBillContract.COLUMN_NAME_BILL_NAME, model.getName());
        contentValues.put(DbTableBillContract.COLUMN_NAME_TAX_TYPE, model.getTaxType().toString());
        contentValues.put(DbTableBillContract.COLUMN_NAME_TAX_VAL, model.getFormattedTaxVal());
        contentValues.put(DbTableBillContract.COLUMN_NAME_TIP_TYPE, model.getTipType().toString());
        contentValues.put(DbTableBillContract.COLUMN_NAME_TIP_VAL, model.getFormattedTipVal());
        return contentValues;
    }

    private DbTableManager mTableManager;

    private static volatile BillRepository INSTANCE;
}
