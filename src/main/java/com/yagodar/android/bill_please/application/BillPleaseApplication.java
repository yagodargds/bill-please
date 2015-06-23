package com.yagodar.android.bill_please.application;

import android.app.Application;

import com.yagodar.android.bill_please.store.db.DbManager;

/**
 * Created by yagodar on 19.06.2015.
 */
public class BillPleaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DbManager.initInstance(getApplicationContext());
    }

}
