package com.yagodar.billplease;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class BillPleaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.bill_please_layout);
	}
	
	public void onButtonClick(View view) {
		if(view != null) {
			switch(view.getId()) {
			case R.id.btn_add_row:
				addBillRow();
				break;
			case R.id.btn_del_row:
				break;
			case R.id.btn_new_bill:
				break;
			default:
				break;
			}
		}
	}
	
	private void addBillRow() {
		View llBillRows = findViewById(R.id.ll_bill_rows);
		if(llBillRows != null && llBillRows instanceof LinearLayout) {
			LinearLayout newBillRow = new LinearLayout(this, null, R.style.app_row_llv);
			
			((LinearLayout) llBillRows).addView(newBillRow);
		}
	}
}
