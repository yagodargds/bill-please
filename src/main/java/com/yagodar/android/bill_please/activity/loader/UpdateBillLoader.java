package com.yagodar.android.bill_please.activity.loader;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.os.CancellationSignal;

import com.yagodar.android.bill_please.store.BillRepository;
import com.yagodar.android.bill_please.store.db.DbTableBillsContract;
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
        Bundle args = getArgs();
        long billId = args.getLong(BaseColumns._ID);
        ContentValues billContentValues = args.getParcelable(DbTableBillsContract.TABLE_NAME);
        OperationResult opResult = BillRepository.getInstance().update(billId, billContentValues, signal);
        return new LoaderResult(opResult);
    }
}
