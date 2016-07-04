package com.yagodar.android.bill_please.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.loader.LoaderFactory;
import com.yagodar.android.bill_please.model.Bill;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.bill_please.util.FragmentUtils;
import com.yagodar.android.custom.adapter.AbsRecyclerViewAdapter;
import com.yagodar.android.custom.fragment.progress.recycler_view.AbsLoaderProgressRecyclerFragment;
import com.yagodar.android.custom.loader.LoaderResult;
import com.yagodar.essential.model.ListModel;
import com.yagodar.essential.operation.OperationResult;

import java.util.List;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListFragment extends AbsLoaderProgressRecyclerFragment<BillListAdapter> {
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //TODO bug with append on long load (dont update list)

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

        setEmptyImage(R.drawable.icon_no_data);
        setEmptyText(R.string.no_data);

        Activity activity = getActivity();

        View.OnClickListener onClickListener = new OnClickListener();
        View.OnLongClickListener onLongClickListener = new OnLongClickListener();

        mBillList = BillList.getInstance();

        setRecyclerAdapter(
                new BillListAdapter(
                        activity,
                        onClickListener,
                        onLongClickListener,
                        mBillList
                )
        );

        mActionModeCallback = new ActionModeCallback();

        mButtonBillAppend = activity.findViewById(R.id.bill_append_button);
        mButtonBillAppend.setOnClickListener(onClickListener);

        if (savedInstanceState != null) {
            getContentView().getLayoutManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(
                            ViewSaveState.RECYCLER_LAYOUT.name()
                    )
            );
            getRecycleAdapter().getSelectionManager().onRestoreInstanceState(
                    savedInstanceState.getParcelable(
                            ViewSaveState.RECYCLER_SELECTION.name()
                    )
            );
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        int count = getRecycleAdapter().getSelectionManager().getSelectedItemCount();
        if(count > 0) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
            mActionMode.setTitle(
                    String.valueOf(count)
            );
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
        outState.putParcelable(
                ViewSaveState.RECYCLER_LAYOUT.name(),
                getContentView().getLayoutManager().onSaveInstanceState()
        );
        outState.putParcelable(
                ViewSaveState.RECYCLER_SELECTION.name(),
                getRecycleAdapter().getSelectionManager().onSaveInstanceState()
        );
    }

    @Override
    public void onStartLoading(int id, Bundle args) {
        LoaderFactory.Type type = LoaderFactory.Type.get(id);
        switch (type) {
            case LOAD_BILL_LIST:
                setContentShown(false);
                mButtonBillAppend.setEnabled(false);
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

    private void toggleBillSelection(View billView) {
        AbsRecyclerViewAdapter.SelectionManager selectionManager = getRecycleAdapter().getSelectionManager();
        selectionManager.toggleItemSelection(
                getContentView().getChildAdapterPosition(billView)
        );
        int count = selectionManager.getSelectedItemCount();
        if(count == 0) {
            mActionMode.finish();
        }
        else {
            mActionMode.setTitle(
                    String.valueOf(count)
            );
        }
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    FragmentUtils.startActivityForResult(BillListFragment.this, BillActivity.class, LoaderFactory.Type.APPEND_BILL, null);
                    break;
                case R.id.bill_row_view:
                    if (mActionMode != null) {
                        toggleBillSelection(v);
                        break;
                    }
                    FragmentUtils.startActivityForResult(
                            BillListFragment.this,
                            BillActivity.class,
                            LoaderFactory.Type.UPDATE_BILL,
                            (Bundle) v.getTag()
                    );
                    break;
                default:
                    break;
            }
        }
    }

    private class OnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.bill_row_view:
                    if (mActionMode != null) {
                        return false;
                    }
                    mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                    toggleBillSelection(v);
                    return true;
                default:
                    return false;
            }
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.cab_activity_bill_list, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cab_activity_bill_list_remove:
                    Bundle bundle;
                    for (int position : getRecycleAdapter().getSelectionManager().getSelectedItemPositions()) {
                        bundle = new Bundle();
                        bundle.putLong(BaseColumns._ID, getRecycleAdapter().getItemId(position));
                        LoaderFactory.Type.REMOVE_BILL.startLoading(
                                BillListFragment.this,
                                (Bundle) getRecycleAdapter().getItem(position).getTag()
                        );
                    }
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            getRecycleAdapter().getSelectionManager().clearItemSelections();
        }
    }

    private enum ViewSaveState {
        RECYCLER_LAYOUT,
        RECYCLER_SELECTION,
        ;
    }

    private ListModel<Bill> mBillList;
    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback;
    private View mButtonBillAppend;
}