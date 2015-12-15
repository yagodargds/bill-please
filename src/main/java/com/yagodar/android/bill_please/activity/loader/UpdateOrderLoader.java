package com.yagodar.android.bill_please.activity.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.store.OrderRepository;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.operation.OperationResult;

/**
 * Created by yagodar on 24.06.2015.
 */
public class UpdateOrderLoader extends AbsAsyncTaskLoader {
    public UpdateOrderLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult load(CancellationSignal signal) {
        OperationResult<Integer> opResult = OrderRepository.getInstance().update(getArgs(), signal);
        return new LoaderResult(opResult);
    }
}
