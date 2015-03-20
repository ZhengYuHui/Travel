package com.zheng.travel.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*******************************************************
 * 对整个界面的所有控件都应用自定义字体
 ******************************************************/
public class FontManager {

	public static void changeFonts(ViewGroup root, Activity act) {

		Typeface tf = Typeface.createFromAsset(act.getAssets(),
				"EPSONhangshuti.ttf");

		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof TextView) {
				((TextView) v).setTypeface(tf);
			} else if (v instanceof Button) {
				((Button) v).setTypeface(tf);
			} else if (v instanceof EditText) {
				((EditText) v).setTypeface(tf);
			} else if (v instanceof ViewGroup) {
				changeFonts((ViewGroup) v, act);
			}
		}

	}
}
