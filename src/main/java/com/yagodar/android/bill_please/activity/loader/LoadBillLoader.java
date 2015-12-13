package com.yagodar.android.bill_please.activity.loader;

import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.model.Order;
import com.yagodar.android.bill_please.store.OrderRepository;
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
    public LoaderResult load(CancellationSignal signal) {
        LoaderResult loaderResult = new LoaderResult();

        if(!BillList.getInstance().isLoaded()) {
            loaderResult.setFailMessageId(R.string.err_load_failed);
            loaderResult.setFailThrowable(new IllegalStateException("Can`t load bill order list from unloaded bill list!"));
            return loaderResult;
        }

        Bundle args = getArgs();
        if (args == null || !args.containsKey(BaseColumns._ID)) {
            loaderResult.setFailMessageId(R.string.err_load_failed);
            loaderResult.setFailThrowable(new IllegalArgumentException("Can`t load bill order list without set args!"));
            return loaderResult;
        }

        long billId = args.getLong(BaseColumns._ID);
        Bill bill = BillList.getInstance().getModel(billId);
        if(!bill.isLoaded()) {
            OperationResult<List<Order>> opResult = OrderRepository.getInstance().loadGroupList(billId);
            if (opResult.isSuccessful()) {
                List<Order> orderList = opResult.getData();
                bill.setModelList(orderList);
            } else {
                loaderResult.setFailMessage(opResult.getFailMessage());
                loaderResult.setFailMessageId(opResult.getFailMessageId());
                loaderResult.setFailThrowable(opResult.getFailThrowable());
            }
        } else {
        }

        return loaderResult;
    }
}
