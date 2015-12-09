package com.yagodar.android.bill_please.activity.bill_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.FragmentUtils;
import com.yagodar.android.bill_please.activity.LoaderFactory;
import com.yagodar.android.bill_please.activity.bill.BillActivity;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.custom.fragment.progress.recycler_view.AbsLoaderProgressRecyclerViewFragment;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.model.ListModel;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListFragment extends AbsLoaderProgressRecyclerViewFragment {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, toString() + " onActivityResult() request=" + requestCode + " result=" + resultCode);

        switch(resultCode) {
            case Activity.RESULT_OK:
                if (requestCode == LoaderFactory.Type.APPEND_BILL.ordinal()) {
                    int appendItemPosition = getRecycleAdapter().getItemCount() - 1;
                    getRecycleAdapter().notifyItemInserted(appendItemPosition);
                    getRecyclerView().smoothScrollToPosition(appendItemPosition);
                }
                else if(requestCode == LoaderFactory.Type.UPDATE_BILL.ordinal()) {
                    //TODO
                    getRecycleAdapter().notifyDataSetChanged();
                }
                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();

        View.OnClickListener onClickListener = new OnClickListener();
        mBillList = BillList.getInstance();

        setRecyclerAdapter(new BillListAdapter(activity, onClickListener, mBillList));
        //TODO setEmptyText(getString(R.string.no_data));

        mButtonBillAppend = activity.findViewById(R.id.bill_append_button);
        mButtonBillAppend.setOnClickListener(onClickListener);

        if (savedInstanceState != null) {
            getRecyclerView().getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(TAG));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LoaderManager loaderManager = getLoaderManager();
        if(!mBillList.isLoaded()) {
            LoaderFactory.Type.LOAD_BILL_LIST.startLoading(this, null);
        } else {
            LoaderFactory.Type.LOAD_BILL_LIST.continueLoading(this, loaderManager);
        }
        LoaderFactory.Type.REMOVE_BILL.continueLoading(this, loaderManager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG, getRecyclerView().getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void setAvailable(boolean available, int id, Bundle args) {
        LoaderFactory.Type type = LoaderFactory.Type.get(id);
        switch (type) {
            case LOAD_BILL_LIST:
                setContentShown(available);
                mButtonBillAppend.setEnabled(available);
                break;
            case REMOVE_BILL:
                if(!available) {
                    RecyclerView.ViewHolder viewHolder = getRecyclerView().findViewHolderForItemId(id);
                    //viewHolder.itemView.setEnabled(false); //TODO npe!
                }
                break;
        }
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return LoaderFactory.createLoader(getActivity(), id, args);
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult result) {
        if (result.isSuccessful() && result.isNotifyDataSet()) {
            int id = loader.getId();
            LoaderFactory.Type type = LoaderFactory.Type.get(id);
            switch (type) {
                case LOAD_BILL_LIST:
                    getRecycleAdapter().notifyDataSetChanged();
                    break;
                case REMOVE_BILL:
                    getRecycleAdapter().notifyDataSetChanged(); //TODO
                    break;
                default:
                    break;
            }
        }
        super.onLoaderResult(loader, result);
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    FragmentUtils.startActivityForResult(BillListFragment.this, BillActivity.class, LoaderFactory.Type.APPEND_BILL, (Bundle) v.getTag());
                    break;
                case R.id.bill_edit_button:
                    FragmentUtils.startActivityForResult(BillListFragment.this, BillActivity.class, LoaderFactory.Type.UPDATE_BILL, (Bundle) v.getTag());
                    break;
                case R.id.bill_remove_button:
                    LoaderFactory.Type.REMOVE_BILL.startLoading(BillListFragment.this, (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private ListModel<Bill> mBillList;
    private View mButtonBillAppend;

    public static final String TAG = BillListFragment.class.getSimpleName();
}