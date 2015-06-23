package com.yagodar.android.bill_please.activity.bill_list.loader;

import android.content.Context;
import android.os.Bundle;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.BillRepository;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 19.06.2015.
 */
public class AppendBillLoader extends AbsAsyncTaskLoader {
    public AppendBillLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult loadInBackground() {
        LoaderResult loaderResult = new LoaderResult();

        if(BillList.getInstance().isLoaded()) {
            OperationResult<Long> opResult = BillRepository.getInstance().insert();
            if(opResult.isSuccessful()) {
                long newBillId = opResult.getData();
                BillList.getInstance().putBill(new Bill(newBillId));
                loaderResult.setNotifyDataSet(true);
            } else {
                loaderResult.setFailMessage(opResult.getFailMessage());
                loaderResult.setFailMessageId(opResult.getFailMessageId());
                loaderResult.setFailThrowable(opResult.getFailThrowable());
                loaderResult.setNotifyDataSet(false);
            }
        } else {
            loaderResult.setFailMessageId(R.string.err_append_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t insert to unloaded bill list!"));
            loaderResult.setNotifyDataSet(false);
        }

        return loaderResult;
    }
}
