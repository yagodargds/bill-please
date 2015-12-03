package com.yagodar.android.bill_please.application;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.yagodar.android.bill_please.activity.bill.BillFragment;
import com.yagodar.android.bill_please.activity.bill_list.loader.RemoveBillLoader;
import com.yagodar.android.bill_please.store.db.DbManager;
import com.yagodar.android.custom.loader.AbsAsyncTaskLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yagodar on 19.06.2015.
 */
public class BillPleaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DbManager.initInstance(getApplicationContext());
        if(DEBUG) {
            try {
                final File path = new File(Environment.getExternalStorageDirectory(), TAG + "_logs");
                if (!path.exists()) {
                    path.mkdir();
                }
                String logFilter = "" +
                        AbsAsyncTaskLoader.TAG + ":V " +
                        RemoveBillLoader.TAG + ":V " +
                        BillFragment.TAG + ":V " +
                        "*:S";
                Runtime.getRuntime().exec("logcat -c");
                Runtime.getRuntime().exec("logcat -v time -f " + path + File.separator + new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss").format(new Date()) + ".log " + logFilter);

            } catch (IOException e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
    }

    private static final boolean DEBUG = true;
    private static final String TAG = BillPleaseApplication.class.getSimpleName();
}
