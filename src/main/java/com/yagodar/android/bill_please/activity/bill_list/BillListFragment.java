package com.yagodar.android.bill_please.activity.bill_list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.bill_please.activity.BillPleaseLoaderFactory;
import com.yagodar.android.bill_please.activity.bill.BillActivity;
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

        BillList billList = BillList.getInstance();

        BillListOnClickListener onClickListener = new BillListOnClickListener();

        setListAdapter(new BillListAdapter(getActivity(), onClickListener, billList));

        setEmptyText(getString(R.string.no_data));

        mButtonBillAppend = (Button) getActivity().findViewById(R.id.bill_append_button);
        mButtonBillAppend.setOnClickListener(onClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        setAvailable(true);

        startLoading(BillPleaseLoaderFactory.BillLoaderType.LOAD_BILL_LIST.ordinal(), null);

        if(getLoaderManager().getLoader(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal()) != null) {
            startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal(), null);
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

        super.onLoaderResult(loader, loaderResult);
    }

    @Override
    public void setAvailable(boolean available) {
        super.setAvailable(available);
        mButtonBillAppend.setEnabled(available);
    }

    private class BillListOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bill_append_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.APPEND_BILL.ordinal(), null);
                    break;
                case R.id.bill_edit_button:
                    Intent intent = new Intent(getActivity(), BillActivity.class);
                    intent.putExtras((Bundle) v.getTag());
                    startActivity(intent);
                    break;
                case R.id.bill_remove_button:
                    startLoading(BillPleaseLoaderFactory.BillLoaderType.REMOVE_BILL.ordinal(), (Bundle) v.getTag());
                    break;
                default:
                    break;
            }
        }
    }

    private Button mButtonBillAppend;
}