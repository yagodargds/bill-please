package com.yagodar.android.billplease.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yagodar.android.billplease.R;
import com.yagodar.android.database.sqlite.DbTableManager;

/**
 * Created by АППДКт78М on 10.11.2014.
 */
public class BillActivityMVC extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bill_please_llv_mvc);

        /**
         * TODO
         * загружаем список всех билов с пометкой id каждого
         * передаём конкретное id била в данное активити и загружаем соответственно бил.
         * а пока что вместо этого заглушка
         */
        //long billId =

    }

    public void onButtonClick(View button) {

    }

    /*private long gagInitBillId() {
        DbTableManager dbTableBillValuesManager;
        if(dbTableBillValuesManager.getAllRecords().size() == 0) {
            return dbTableBillValuesManager.addRecord();
        }
        else {
            return dbTableBillValuesManager.getAllRecords().iterator().next().getId();
        }
    }*/
}
