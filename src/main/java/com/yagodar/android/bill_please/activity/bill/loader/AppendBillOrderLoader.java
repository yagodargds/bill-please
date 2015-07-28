package com.yagodar.android.bill_please.activity.bill.loader;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.model.BillOrder;
import com.yagodar.android.bill_please.store.BillOrderRepository;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 24.06.2015.
 */
public class AppendBillOrderLoader extends AbsAsyncTaskLoader {
    public AppendBillOrderLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult load() {
        LoaderResult loaderResult = new LoaderResult();

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_append_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t insert bill order to unloaded bill list!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID)) {
            loaderResult.setFailMessageId(R.string.err_append_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t insert bill order without set args!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        long billId = args.getLong(BaseColumns._ID);
        Bill bill = BillList.getInstance().getModel(billId);

        if(!bill.isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_append_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t insert bill order to unloaded bill!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        OperationResult<Long> opResult = BillOrderRepository.getInstance().insert(billId);
        if(opResult.isSuccessful()) {
            long newBillOrderId = opResult.getData();
            bill.putModel(new BillOrder(newBillOrderId));
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
