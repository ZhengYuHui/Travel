package com.zheng.travel.utils;

import android.util.Log;

public class MyLog {
	private static boolean mode = true;

	public static void printS(String string, Object object) {
		if (mode) {
			System.out.println(string + "----------->" + object);
		}
	}

	/**************************************************************************
	 * 1、witch : Log的类型v\d\i\w\e 2、TAG : Log的TAG 3、string: 打印的内容
	 **************************************************************************/
	public static void printLv(String TAG, String showString) {
		if (mode) {
			Log.v(TAG, showString);
		}
	}

	public static void printLd(String TAG, String showString) {
		if (mode) {
			Log.d(TAG, showString);
		}
	}

	public static void printLi(String TAG, String showString) {
		if (mode) {
			Log.i(TAG, showString);
		}
	}

	public static void printLw(String TAG, String showString) {
		if (mode) {
			Log.w(TAG, showString);
		}
	}

	public static void printLe(String TAG, String showString) {
		if (mode) {
			Log.e(TAG, showString);
		}
	}

}
