package com.yagodar.android.bill_please.activity.bill_order.loader;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.BillOrderRepository;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 24.06.2015.
 */
public class UpdateBillOrderLoader extends AbsAsyncTaskLoader {
    public UpdateBillOrderLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult loadInBackground() {
        LoaderResult loaderResult = new LoaderResult();
        loaderResult.setNotifyDataSet(false);

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t update bill order in unloaded bill list!"));
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID) || !args.containsKey(DbTableBillOrderContract.COLUMN_NAME_BILL_ID)) {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t update bill order in bill, in bill list with unset args!"));
            return loaderResult;
        }

        long billId = args.getLong(DbTableBillOrderContract.COLUMN_NAME_BILL_ID);
        Bill bill = BillList.getInstance().getModel(billId);

        if(!bill.isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t update bill order from unloaded bill!"));
            return loaderResult;
        }

        long billOrderId = args.getLong(BaseColumns._ID);
        BillOrder billOrder = bill.getModel(billOrderId);
        OperationResult<Integer> opResult = BillOrderRepository.getInstance().update(billId, billOrder);
        if(!opResult.isSuccessful()) {
            loaderResult.setFailMessage(opResult.getFailMessage());
            loaderResult.setFailMessageId(opResult.getFailMessageId());
            loaderResult.setFailThrowable(opResult.getFailThrowable());
        }

        return loaderResult;
    }
}
