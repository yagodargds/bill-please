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
public class UpdateBillLoader extends AbsAsyncTaskLoader {
    public UpdateBillLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult loadInBackground() {
        LoaderResult loaderResult = new LoaderResult();

        if(BillList.getInstance().isLoaded()) {
            Bundle args = getArgs();
            if (args == null || !args.containsKey(Bill.BILL_ID_KEY)) {
                loaderResult.setFailMessageId(R.string.err_remove_failed);
                loaderResult.setFailThrowable(new IllegalArgumentException("Can`t update bill in list with unset args!"));
                loaderResult.setNotifyDataSet(false);
            } else {
                long billId = args.getLong(Bill.BILL_ID_KEY);
                OperationResult<Integer> opResult = BillRepository.getInstance().update(BillList.getInstance().getBill(billId));
                if(opResult.isSuccessful()) {
                    loaderResult.setNotifyDataSet(true);
                } else {
                    loaderResult.setFailMessage(opResult.getFailMessage());
                    loaderResult.setFailMessageId(opResult.getFailMessageId());
                    loaderResult.setFailThrowable(opResult.getFailThrowable());
                    loaderResult.setNotifyDataSet(false);
                }
            }
        } else {
            loaderResult.setFailMessageId(R.string.err_update_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t update bill in unloaded list!"));
            loaderResult.setNotifyDataSet(false);
        }

        return loaderResult;
    }
}
