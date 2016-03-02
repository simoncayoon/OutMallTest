package com.beetron.outmall.customview;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;

public class IphoneDialogOnItemClick implements OnClickListener{
	private IphoneDialogItem mItem;
	private Dialog mDialog;
	public IphoneDialogOnItemClick(IphoneDialogItem item, Dialog dialog){
		mItem = item;
		mDialog = dialog;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(mItem.getOnClickListener() != null) 
			mItem.getOnClickListener().onClick(null);
		
		if(mDialog != null){
			mDialog.dismiss();
			mDialog = null;
		}
	}
}
