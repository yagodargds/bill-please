package com.yagodar.android.bill_please.activity.bill.loader;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.BillRepository;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 19.06.2015.
 */
public class UpdateBillLoader extends AbsAsyncTaskLoader {
    public UpdateBillLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult load(CancellationSignal signal) {
        LoaderResult loaderResult = new LoaderResult(getArgs());
        loaderResult.setNotifyDataSet(false);

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t update bill in unloaded list!"));
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID)) {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t update bill in list with unset args!"));
            return loaderResult;
        }

        long billId = args.getLong(BaseColumns._ID);
        OperationResult<Integer> opResult = BillRepository.getInstance().update(BillList.getInstance().getModel(billId));
        if(!opResult.isSuccessful()) {
            loaderResult.setFailMessage(opResult.getFailMessage());
            loaderResult.setFailMessageId(opResult.getFailMessageId());
            loaderResult.setFailThrowable(opResult.getFailThrowable());
        }

        return loaderResult;
    }
}
