package com.beetron.outmall.customview;

import android.view.View.OnClickListener;

public class IphoneDialogItem {
	private  boolean mIsSpecial;
	private String mText;
	private int mViewId;
	private OnClickListener mClickListener=null;
	
	public  IphoneDialogItem(String textId, int viewId,OnClickListener clickListener){
		mText = textId;
		mViewId = viewId;
		mClickListener=clickListener;
	}
	public void onClick(){
	}

	public boolean isSpecial() {
		return mIsSpecial;
	}

	public void setSpecial(boolean mIsSpecial) {
		this.mIsSpecial = mIsSpecial;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
	}

	public int getViewId() {
		return mViewId;
	}

	public void setViewId(int mViewId) {
		this.mViewId = mViewId;
	}
	
	public void setOnClickListener(OnClickListener clickListener)
	{
		mClickListener=clickListener;
	}
	
	public OnClickListener getOnClickListener()
	{
		return mClickListener;
	}
}

