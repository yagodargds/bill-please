package com.yagodar.android.bill_please.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.yagodar.android.bill_please.R;
import com.yagodar.android.database.sqlite.custom.AbstractDbEditText;

import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

public class BillActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timer = new Timer();
        exMotionEvent = NONE_MOTION_EVENT;
    }

    private void hideFocus() {
        etHidden.requestFocus();
    }

    public void onButtonClick(View button) {
        switch(button.getId()) {
            case R.id.btn_share_bill:
                hideFocus();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getShareText());
                sendIntent.setType(INTENT_TYPE_SHARE);
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string.btn_lbl_share_bill)));
                break;
            default:
                break;
        }
    }

    private String getShareText() {
        String shareText = "";

        /*//app name tag
        shareText += getResources().getString(R.string.app_tag);
        shareText += "\n";

        //records
        shareText += "[" + getResources().getString(R.string.lbl_item_name) + "]";
        shareText += "\n";
        if(llBillRecords.getChildCount() > 1) {
            LinearLayout billRecordLl;
            BigDecimal cost;
            BigInteger share;
            for(int i = 0; i < llBillRecords.getChildCount(); i++) {
                try {
                    billRecordLl = (LinearLayout) llBillRecords.getChildAt(i);
                    shareText += ((EditText) billRecordLl.findViewById(R.id.et_item_name)).getText();
                    cost = ((DbEditText<BigDecimal>) billRecordLl.findViewById(R.id.et_cost)).getValue();
                    share = ((DbEditText<BigInteger>) billRecordLl.findViewById(R.id.et_share)).getValue();
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_cost) + ":" + decimalFormat.format(cost);
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_share) + ":" + share;
                    shareText += "\t|\t" + getResources().getString(R.string.lbl_subtotal) + ":" + decimalFormat.format(cost.divide(new BigDecimal(share), bigValuesScale, BIG_VALUES_ROUNDING_MODE));
                    shareText += "\n";
                }
                catch(Exception ignored) {}
            }
        }
        else {
            shareText += getResources().getString(R.string.share_no_items);
            shareText += "\n";
        }

        //subtotal
        shareText += "[" + getResources().getString(R.string.lbl_subtotal) + "]" + ":" + ((TextView) findViewById(R.id.tv_subtotal_sum)).getText();
        shareText += "\n";

        //tax
        shareText += "[" + getResources().getString(R.string.lbl_tax) + "]" + ":" + ((EditText) findViewById(R.id.et_tax)).getText();
        shareText += "\t/\t" + ((TextView) findViewById(R.id.et_tax_sum)).getText();
        shareText += "\n";

        //tip
        shareText += "[" + getResources().getString(R.string.lbl_tip) + "]" + ":" + ((EditText) findViewById(R.id.et_tip)).getText();
        shareText += "\t/\t" + ((TextView) findViewById(R.id.et_tip_sum)).getText();
        shareText += "\n";

        //total
        shareText += "[" + getResources().getString(R.string.lbl_total) + "]" + ":" + ((TextView) findViewById(R.id.tv_total_sum)).getText();*/

        return shareText;
    }


    private class BillTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            timer.cancel();

            if(lastDbEt != null && lastDbEt.isFocused()) {
                timer = new Timer();
                try {
                    //timer.schedule(new PushToDbTimerTask(), getResources().getInteger(R.integer.push_to_db_delay_millisecs));
                }
                catch(Exception ignored) {}
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private class BillOnEditorActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                View nextFocusView = null;

                switch(textView.getId()) {
                    case R.id.et_item_name:
                    case R.id.et_cost:
                    case R.id.et_share:
                    case R.id.et_def_item_name:
                    case R.id.et_def_cost:
                    case R.id.et_def_share:
                    case R.id.et_tip:
                    case R.id.et_tip_sum:
                    case R.id.et_tax:
                    case R.id.et_tax_sum:
                        nextFocusView = ((AbstractDbEditText) textView).getNextFocusView(View.FOCUS_FORWARD);
                        break;
                    default:
                        break;
                }

                if(nextFocusView != null) {
                    nextFocusView.requestFocus();
                    return true;
                }
            }

            return false;
        }
    }

    public static class CreateNewBillDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.btn_lbl_create_new_bill);
            builder.setView(getActivity().getLayoutInflater().inflate(R.layout.dlg_create_new_bill_llv, null));
            builder.setNegativeButton(R.string.dlg_cancel, onClickListener);
            builder.setPositiveButton(R.string.dlg_confirm, onClickListener);

            return builder.create();
        }

        private class CreateNewBillDialogOnClickListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //createNewBill();
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        }

        private DialogInterface.OnClickListener onClickListener = new CreateNewBillDialogOnClickListener();
    }

    private class PushToDbTimerTask extends TimerTask {
        @Override
        public void run() {
            if(lastDbEt != null) {
                switch(lastDbEt.getId()) {
                    case R.id.et_tax:
                    case R.id.et_tax_sum:
                        lastDbEt.pushToDb();
                        //redrawTaxSum(calcSubtotalSum());
                        break;
                    case R.id.et_tip:
                    case R.id.et_tip_sum:
                        lastDbEt.pushToDb();
                        //redrawTipSum(calcSubtotalSum());
                        break;
                    default:
                        lastDbEt.pushToDb();
                        //redrawAllSums();
                        break;
                }
            }
        }
    }

    private AbstractDbEditText lastDbEt;
    private View etHidden;
    private int exMotionEvent;
    private Timer timer;

    private static final int NONE_MOTION_EVENT = -1;
    private static final String INTENT_TYPE_SHARE = "text/plain";
    private static final RoundingMode BIG_VALUES_ROUNDING_MODE = RoundingMode.HALF_UP;
}