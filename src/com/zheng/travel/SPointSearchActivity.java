package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zheng.travel.adapter.RSListViewAdapter;

public class SPointSearchActivity extends Activity {

	private String TAG = "SPointSearchActivity";
	private SharedPreferences sp;
	private EditText ed_startPoint;
	private ListView lv_startPoint;
	private RSListViewAdapter myRSListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_point_search);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		ed_startPoint = (EditText) findViewById(R.id.ed_startPoint);
		lv_startPoint = (ListView) findViewById(R.id.lv_startPoint);
		// 设置ListView条目点击的事件监听器
		lv_startPoint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ed_startPoint.setText(sp.getString("spSR" + position, "起始地点"));
			}
		});

		setRouteSearchListView(true);
	}

	/***********************************************************************************
	 * 顶部按键监听事件
	 **********************************************************************************/
	public void SearchPoint(View v) {

		if (v.getId() == R.id.ps_sure) {// 确定
			String str_startPoint = ed_startPoint.getText().toString();
			// 判断输入是否为空
			if (str_startPoint.trim().isEmpty()) {
				Toast.makeText(SPointSearchActivity.this, "输入不能为空", 1).show();
				Animation shake = AnimationUtils.loadAnimation(
						SPointSearchActivity.this, R.anim.shake);
				ed_startPoint.startAnimation(shake);
			} else {
				saveRouteSearch(str_startPoint);
                Intent intent = new Intent(); //数据是使用Intent返回
                intent.putExtra("str_startPoint", str_startPoint);//把返回数据存入Intent
                SPointSearchActivity.this.setResult(RESULT_OK, intent); //设置返回数据
                SPointSearchActivity.this.finish();//关闭Activity
			}
			//setRouteSearchListView(false);
		} else if (v.getId() == R.id.ps_back) {// 返回
			finish();
		}
	}

	/***********************************************************************************
	 * 设置路线搜索ListView的显示(mode为true：生成Adapter ；mode为false：更新Adapter)
	 **********************************************************************************/
	protected void setRouteSearchListView(boolean mode) {
		// 获取sp保存的起始地点搜索记录总数
		int sPointSearchRecordNumber = sp.getInt("SPointSearchRecordNumber", 0);
		// 存在起始地点搜索记录数
		if (sPointSearchRecordNumber > 0) {
			// 生成长度为routeSearchRecordNumber + 1的数组，给Adapter提供数据
			String spSR[] = new String[sPointSearchRecordNumber + 1];
			// 读取sp中的所有rSRx 字符数据
			for (int i = 0; i < sPointSearchRecordNumber + 1; i++) {
				spSR[i] = sp.getString("spSR" + i, "起始地点");
			}
			if (mode) {
				// 设置Adapter
				myRSListViewAdapter = new RSListViewAdapter(this, spSR);
				// ListView关联Adapter
				lv_startPoint.setAdapter(myRSListViewAdapter);
			} else {
				myRSListViewAdapter.updateListView(spSR);
			}
		}
	}

	/***********************************************************************************
	 * 保存路线搜索到sp
	 **********************************************************************************/
	protected void saveRouteSearch(String str_editSt) {
		// 获取sp保存的起始地点搜索记录总数
		int sPointSearchRecordNumber = sp.getInt("SPointSearchRecordNumber", 0);
		boolean have = true;// 决定是否保存数据的标记
		if (sPointSearchRecordNumber >= 0) {
			String spSR;
			// 比较、判断sp中是否存在相同的搜索记录
			for (int i = 0; i <= sPointSearchRecordNumber; i++) {
				spSR = sp.getString("spSR" + i, "起始地点");
				if (spSR.equals(str_editSt)) {
					have = false;
				}
			}
		}
		if (have) {// 保存搜索记录
			// 起始地点搜索记录总数加一保存，保存新的搜索记录
			sPointSearchRecordNumber++;
			Editor editor = sp.edit();
			editor.putInt("SPointSearchRecordNumber", sPointSearchRecordNumber);
			editor.putString("spSR" + sPointSearchRecordNumber, str_editSt);
			editor.commit();
		}
	}

}
