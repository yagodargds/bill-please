package com.yagodar.android.bill_please.activity.bill.loader;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.BillOrderRepository;
import com.yagodar.android.bill_please.store.db.DbTableBillOrderContract;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 24.06.2015.
 */
public class RemoveBillOrderLoader extends AbsAsyncTaskLoader {
    public RemoveBillOrderLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult load(CancellationSignal signal) {
        LoaderResult loaderResult = new LoaderResult();

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_remove_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t remove bill order from unloaded bill list!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID) || !args.containsKey(DbTableBillOrderContract.COLUMN_NAME_BILL_ID)) {
            loaderResult.setFailMessageId(R.string.err_remove_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t remove bill order from bill without set args!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        long billId = args.getLong(DbTableBillOrderContract.COLUMN_NAME_BILL_ID);
        Bill bill = BillList.getInstance().getModel(billId);

        if(!bill.isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_remove_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t remove bill order from unloaded bill!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        long billOrderId = args.getLong(BaseColumns._ID);
        OperationResult<Integer> opResult = BillOrderRepository.getInstance().delete(billId, billOrderId);
        if(opResult.isSuccessful()) {
            bill.removeModel(billOrderId);
            loaderResult.setNotifyDataSet(true);
        } else {
            loaderResult.setFailMessage(opResult.getFailMessage());
            loaderResult.setFailMessageId(opResult.getFailMessageId());
            loaderResult.setFailThrowable(opResult.getFailThrowable());
            loaderResult.setNotifyDataSet(false);
        }

        return loaderResult;
    }
}
