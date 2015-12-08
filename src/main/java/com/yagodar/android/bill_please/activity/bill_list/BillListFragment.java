package com.yagodar.android.bill_please.activity.bill_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.LoaderFactory;
import com.yagodar.android.bill_please.activity.bill.BillActivity;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.custom.fragment.progress.recycler_view.AbsLoaderProgressRecyclerViewFragment;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.model.ListModel;

import java.util.HashSet;
import java.util.Set;

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

        mActivity = getActivity();
        View.OnClickListener onClickListener = new OnClickListener();
        mBillList = BillList.getInstance();

        setRecyclerAdapter(new BillListAdapter(mActivity, onClickListener, mBillList));
        //TODO setEmptyText(getString(R.string.no_data));

        mButtonBillAppend = mActivity.findViewById(R.id.bill_append_button);
        mButtonBillAppend.setOnClickListener(onClickListener);

        if (savedInstanceState != null) {
            getRecyclerView().getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(TAG));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setAvailable(true);

        LoaderManager loaderManager = getLoaderManager();

        if(loaderManager.getLoader(LoaderFactory.Type.LOAD_BILL_LIST.getLastId()) != null || !mBillList.isLoaded()) {
            startLastLoading(LoaderFactory.Type.LOAD_BILL_LIST, null);
        }

        startAllProcessLoader(LoaderFactory.Type.REMOVE_BILL, loaderManager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG, getRecyclerView().getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void setAvailable(boolean available) {
        super.setAvailable(available);
        mButtonBillAppend.setEnabled(available); //TODO при ремув - недоступна только строчка с ремувом
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        Loader<LoaderResult> loader = LoaderFactory.createLoader(mActivity, id, args);
        LoaderFactory.onLoaderCreated(id);
        return loader;
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult loaderResult) {
        int id = loader.getId();
        if (loaderResult.isSuccessful() && loaderResult.isNotifyDataSet()) {
            LoaderFactory.Type loaderType = LoaderFactory.Type.get(id);
            switch (loaderType) {
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
        super.onLoaderResult(loader, loaderResult);
        LoaderFactory.onLoaderCompleted(id);
    }

    private void startBillActivity(LoaderFactory.Type type, Bundle args) {
        Intent intent = new Intent(mActivity, BillActivity.class);
        if(args != null) {
            intent.putExtras(args);
        }
        startActivityForResult(intent, type.ordinal());
    }

    private void startAllProcessLoader(LoaderFactory.Type type, LoaderManager loaderManager) {
        Set<Integer> completedLoaderIdSet = new HashSet<>();
        for (Integer id : type.getProcessLoaderIdSet()) {
            if(loaderManager.getLoader(id) != null) {
                startLoading(type, id, null);
            } else {
                completedLoaderIdSet.add(id);
            }
        }
        for (Integer id : completedLoaderIdSet) {
            LoaderFactory.onLoaderCompleted(type, id);
        }
    }

    private void startLastLoading(LoaderFactory.Type type, Bundle args) {
        int id = type.getLastId();
        startLoading(type, id, args);
    }

    private void startNextLoading(LoaderFactory.Type type, Bundle args) {
        int id = type.getNextId();
        startLoading(type, id, args);
    }

    private void startLoading(LoaderFactory.Type type, int id, Bundle args) {
        ProgressShowType progressShowType = type.getProgressShowType();
        startLoading(id, args, progressShowType);
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    startBillActivity(LoaderFactory.Type.APPEND_BILL, (Bundle) v.getTag());
                    break;
                case R.id.bill_edit_button:
                    startBillActivity(LoaderFactory.Type.UPDATE_BILL, (Bundle) v.getTag());
                    break;
                case R.id.bill_remove_button:
                    startNextLoading(LoaderFactory.Type.REMOVE_BILL, (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private Activity mActivity;
    private ListModel<Bill> mBillList;
    private View mButtonBillAppend;

    public static final String TAG = BillListFragment.class.getSimpleName();
}