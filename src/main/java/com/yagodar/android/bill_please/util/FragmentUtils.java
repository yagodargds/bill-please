package com.yagodar.android.bill_please.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.yagodar.android.bill_please.activity.loader.LoaderFactory;

/**
 * Created by yagodar on 08.12.2015.
 */
public class FragmentUtils {
    public static void startActivityForResult(Fragment fragment, Class<?> cls, LoaderFactory.Type type, Bundle args) {
        Intent intent = new Intent(fragment.getContext(), cls);
        if(args != null) {
            intent.putExtras(args);
        }
        fragment.startActivityForResult(intent, type.ordinal());
    }
}
