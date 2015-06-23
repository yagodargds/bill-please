package com.yagodar.android.bill_please.activity.bill_list;

import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.BillPleaseLoaderFactory;
import com.yagodar.android.bill_please.model.BillList;
import com.yagodar.android.custom.fragment.progress.AbsLoaderProgressListFragment;
import com.yagodar.android.custom.loader.LoaderResult;

/**
 * Created by yagodar on 17.06.2015.
 */
public class BillListFragment extends AbsLoaderProgressListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BillListOnClickListener onClickListener = new BillListOnClickListener();

        setEmptyText(getString(R.string.no_data));

        setListAdapter(new BillListAdapter(getActivity(), onClickListener));

        buttonAppend = (Button) getActivity().findViewById(R.id.bill_append_button);
        buttonAppend.setOnClickListener(onClickListener);

        setAvailable(true);

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal()) == null) {
            if (!BillList.getInstance().isLoaded()) {
                startLoading(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal(), null);
            }
        } else {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal(), null);
        }

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal(), null);
        }

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal(), null);
        }

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal(), null);
        }

    }

    @Override
    public Loader<LoaderResult> onCreateLoader(int id, Bundle args) {
        return BillPleaseLoaderFactory.createLoader(getActivity(), id, args);
    }

    @Override
    public void onLoaderResult(Loader<LoaderResult> loader, LoaderResult loaderResult) {
        if(loaderResult.isSuccessful() && loaderResult.isNotifyDataSet()) {
            ((BillListAdapter) getListAdapter()).notifyDataSetChanged();
        }

        finishLoading(loader.getId());
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult> loader) {}

    @Override
    public void setAvailable(boolean available) {
        super.setAvailable(available);
        buttonAppend.setEnabled(available);
    }

    private class BillListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                case R.id.bill_edit_button:
                    //nothing yet //TODO
                    break;
                case R.id.bill_update_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.UPDATE_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                case R.id.bill_remove_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private Button buttonAppend;
}