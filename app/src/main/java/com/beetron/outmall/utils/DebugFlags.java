package com.beetron.outmall.utils;

import android.util.Log;

/**
 * 打印日志类，
 * TAG 标识打印出自何处
 * MSG 打印内容
 * @author apple
 *
 */
public class DebugFlags {

	private static final boolean flag = true;

//	private static final String TAG = "Eteng";

	public static void logD(String TAG, String msg){
		if(flag)
			Log.d(TAG, msg);
	}

	public static void logE(String TAG, String msg){
		if(flag)
			Log.e(TAG, msg);
	}

	public static void logV(String TAG, String msg){
		if(flag)
			Log.v(TAG, msg);
	}

	public static void logW(String TAG, String msg){
		if(flag)
			Log.w(TAG, msg);
	}

	public static void logI(String TAG, String msg){
		if(flag)
			Log.i(TAG, msg);
	}
}
