package com.yagodar.android.bill_please.activity.bill_list.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.store.BillRepository;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

import java.util.List;

/**
 * Created by yagodar on 17.06.2015.
 */
public class LoadBillListLoader extends AbsAsyncTaskLoader {

    public LoadBillListLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult load(CancellationSignal signal) {
        LoaderResult loaderResult = new LoaderResult(getArgs());

        if(!BillList.getInstance().isLoaded()) {
            OperationResult<List<Bill>> opResult = BillRepository.getInstance().loadAllList();
            loaderResult.setData(opResult);
            if(opResult.isSuccessful()) {
                loaderResult.setNotifyDataSet(true);
            } else {
                loaderResult.setFailMessage(opResult.getFailMessage());
                loaderResult.setFailMessageId(opResult.getFailMessageId());
                loaderResult.setFailThrowable(opResult.getFailThrowable());
                loaderResult.setNotifyDataSet(false);
            }
        } else {
            loaderResult.setNotifyDataSet(false);
        }

        return loaderResult;
    }
}
