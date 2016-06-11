package com.yagodar.android.bill_please.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.loader.LoaderFactory;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.util.FragmentUtils;
import com.yagodar.android.custom.fragment.progress.recycler_view.AbsLoaderProgressRecyclerFragment;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.model.ListModel;
import com.yagodar.essential.operation.OperationResult;

import java.util.List;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListFragment extends AbsLoaderProgressRecyclerFragment {
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                long billId = data.getLongExtra(BaseColumns._ID, -1L);
                int billPos = mBillList.getPos(billId);
                LoaderFactory.Type type = LoaderFactory.Type.get(requestCode);
                switch (type) {
                    case APPEND_BILL:
                        getRecycleAdapter().notifyItemInserted(billPos);
                        break;
                    case UPDATE_BILL:
                        getRecycleAdapter().notifyItemChanged(billPos);
                        break;
                }
                getContentView().smoothScrollToPosition(billPos);
                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyImage(R.drawable.no_data);
        setEmptyText(R.string.no_data);

        Activity activity = getActivity();

        View.OnClickListener onClickListener = new OnClickListener();
        mBillList = BillList.getInstance();
        setRecyclerAdapter(new BillListAdapter(activity, onClickListener, mBillList));

        mButtonBillAppend = activity.findViewById(R.id.bill_append_button);
        mButtonBillAppend.setOnClickListener(onClickListener);

        if (savedInstanceState != null) {
            getContentView().getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(TAG));
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
        outState.putParcelable(TAG, getContentView().getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onStartLoading(int id, Bundle args) {
        LoaderFactory.Type type = LoaderFactory.Type.get(id);
        switch (type) {
            case LOAD_BILL_LIST:
                setContentShown(false);
                mButtonBillAppend.setEnabled(false);
                break;
            case REMOVE_BILL:
                long recordId = args.getLong(BaseColumns._ID);
                BillListAdapter.ViewHolder viewHolder = (BillListAdapter.ViewHolder) getContentView().findViewHolderForItemId(recordId);
                viewHolder.setEnabled(false);
                break;
        }
    }

    @Override
    public void onFinishLoading(int id, LoaderResult result) {
        LoaderFactory.Type type = LoaderFactory.Type.get(id);
        switch (type) {
            case LOAD_BILL_LIST:
                setContentShown(true);
                mButtonBillAppend.setEnabled(true);
                break;
            case REMOVE_BILL:
                if(result == null || result.isSuccessful()) {
                    break;
                }
                long recordId = result.getArgs().getLong(BaseColumns._ID);
                BillListAdapter.ViewHolder viewHolder = (BillListAdapter.ViewHolder) getContentView().findViewHolderForItemId(recordId);
                viewHolder.setEnabled(true);
                break;
        }
    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return LoaderFactory.createLoader(getActivity(), id, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult result) {
        OperationResult opResult = result.getData();
        if (opResult.isSuccessful()) {
            int id = loader.getId();
            LoaderFactory.Type type = LoaderFactory.Type.get(id);
            switch (type) {
                case LOAD_BILL_LIST:
                    List<Bill> billList = (List<Bill>) opResult.getData();
                    mBillList.setModelList(billList);
                    getRecycleAdapter().notifyDataSetChanged();
                    break;
                case REMOVE_BILL:
                    long billId = result.getArgs().getLong(BaseColumns._ID);
                    int billPos = mBillList.getPos(billId);
                    mBillList.removeModel(billId);
                    getRecycleAdapter().notifyItemRemoved(billPos);
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
                    FragmentUtils.startActivityForResult(BillListFragment.this, BillActivity.class, LoaderFactory.Type.APPEND_BILL, null);
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