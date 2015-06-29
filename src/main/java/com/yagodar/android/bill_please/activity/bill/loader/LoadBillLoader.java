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

import java.util.List;

/**
 * Created by yagodar on 24.06.2015.
 */
public class LoadBillLoader extends AbsAsyncTaskLoader {
    public LoadBillLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult loadInBackground() {
        LoaderResult loaderResult = new LoaderResult();

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_load_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t load bill order list from unloaded bill list!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID)) {
            loaderResult.setFailMessageId(R.string.err_load_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t load bill order list without set args!"));
            loaderResult.setNotifyDataSet(false);
            return loaderResult;
        }

        long billId = args.getLong(BaseColumns._ID);
        Bill bill = BillList.getInstance().getModel(billId);
        if(!bill.isLoaded()) {
            OperationResult<List<BillOrder>> opResult = BillOrderRepository.getInstance().loadGroupList(billId);
            if (opResult.isSuccessful()) {
                List<BillOrder> billOrderList = opResult.getData();
                bill.setModelList(billOrderList);
                loaderResult.setNotifyDataSet(true);
            } else {
                loaderResult.setFailMessage(opResult.getFailMessage());
                loaderResult.setFailMessageId(opResult.getFailMessageId());
                loaderResult.setFailThrowable(opResult.getFailThrowable());
                loaderResult.setNotifyDataSet(false);
            }
        } else {
            loaderResult.setNotifyDataSet(true);
        }

        return loaderResult;
    }
}
