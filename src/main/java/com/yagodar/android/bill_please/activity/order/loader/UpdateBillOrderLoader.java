package com.yagodar.android.bill_please.activity.order.loader;

import android.content.Context;
import android.os.Bundle;

import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 24.06.2015.
 */
public class UpdateBillOrderLoader extends AbsAsyncTaskLoader {
    public UpdateBillOrderLoader(Context context, Bundle args) {
        super(context, args);
    }

    @Override
    public LoaderResult loadInBackground() {
        return null;
    }
}
