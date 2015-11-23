package com.yagodar.android.bill_please.activity.bill_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.BillPleaseLoaderFactory;
import com.yagodar.android.bill_please.activity.bill.BillActivity;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.custom.fragment.progress.recycler_view.AbsLoaderProgressRecyclerViewFragment;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListFragment extends AbsLoaderProgressRecyclerViewFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBillList = BillList.getInstance();

        BillListOnClickListener onClickListener = new BillListOnClickListener();

        setRecyclerAdapter(new BillListAdapter(getActivity(), onClickListener, mBillList));

        //TODO setEmptyText(getString(R.string.no_data));

        mButtonBillAppend = (Button) getActivity().findViewById(R.id.bill_append_button);

        if (savedInstanceState != null) {
            getRecyclerView().getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT_PARCELABLE_TAG));
        }

        mButtonBillAppend.setOnClickListener(onClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, toString() + " onActivityResult() request=" + requestCode + " result=" + resultCode);

        switch(resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal()) {
                    int appendItemPosition = getRecycleAdapter().getItemCount() - 1;
                    getRecycleAdapter().notifyItemInserted(appendItemPosition);
                    getRecyclerView().smoothScrollToPosition(appendItemPosition);
                }
                else if(requestCode == BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal()) {
                    //TODO
                    getRecycleAdapter().notifyDataSetChanged();
                }
                break;
            case Activity.RESULT_CANCELED:
               break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setAvailable(true);

        LoaderManager loaderManager = getLoaderManager();
        if(loaderManager.getLoader(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal()) != null || !mBillList.isLoaded()) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal(), null);
        }
        if(loaderManager.getLoader(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal(), null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_LAYOUT_PARCELABLE_TAG, getRecyclerView().getLayoutManager().onSaveInstanceState());
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return BillPleaseLoaderFactory.createLoader(getActivity(), id, args);
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult loaderResult) {
        if (loaderResult.isSuccessful() && loaderResult.isNotifyDataSet()) {
            int loaderId = loader.getId();
            if (loaderId == BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal()) {
                getRecycleAdapter().notifyDataSetChanged();
            }
            if (loaderId == BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal()) {
                getRecycleAdapter().notifyDataSetChanged(); //TODO
            }
        }

        super.onLoaderResult(loader, loaderResult);
    }

    @Override
    public void setAvailable(boolean available) {
        super.setAvailable(available);
        mButtonBillAppend.setEnabled(available);
    }

    private void startActivityForResult(Class<?> cls, int requestCode, Bundle args) {
        Intent intent = new Intent(getActivity(), cls);
        if(args != null) {
            intent.putExtras(args);
        }
        startActivityForResult(intent, requestCode);
    }

    private class BillListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    startActivityForResult(BillActivity.class, BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal(), null);
                    break;
                case R.id.bill_edit_button:
                    startActivityForResult(BillActivity.class, BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                case R.id.bill_remove_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private BillList mBillList;

    private Button mButtonBillAppend;

    private static final String RECYCLER_LAYOUT_PARCELABLE_TAG = "recycler_layout_parcelable";

    public static final String TAG = BillListFragment.class.getSimpleName();
}