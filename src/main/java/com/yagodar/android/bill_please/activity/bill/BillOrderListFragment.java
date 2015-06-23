package com.yagodar.android.bill_please.activity.bill;

import android.content.Loader;
import android.os.Bundle;

import com.yagodar.android.custom.fragment.progress.AbsLoaderProgressListFragment;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 23.06.2015.
 */
public class BillOrderListFragment extends AbsLoaderProgressListFragment {

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult loaderResult) {

    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {

    }
}
