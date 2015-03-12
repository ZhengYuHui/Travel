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

public class EPointSearchActivity extends Activity {
	private String TAG = "EPointSearchActivity";
	private SharedPreferences sp;
	private EditText ed_endPoint;
	private ListView lv_endPoint;
	private RSListViewAdapter myRSListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_epoint_search);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		ed_endPoint = (EditText) findViewById(R.id.ed_endPoint);
		lv_endPoint = (ListView) findViewById(R.id.lv_endPoint);
		// 设置ListView条目点击的事件监听器
		lv_endPoint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ed_endPoint.setText(sp.getString("epSR" + position, "目标地点"));
			}
		});

		setRouteSearchListView(true);
	}

	/***********************************************************************************
	 * 顶部按键监听事件
	 **********************************************************************************/
	public void SearchPoint(View v) {

		if (v.getId() == R.id.ps_sure) {// 确定
			String str_endPoint = ed_endPoint.getText().toString();
			// 判断输入是否为空
			if (str_endPoint.trim().isEmpty()) {
				Toast.makeText(EPointSearchActivity.this, "输入不能为空", 1).show();
				Animation shake = AnimationUtils.loadAnimation(
						EPointSearchActivity.this, R.anim.shake);
				ed_endPoint.startAnimation(shake);
			} else {
				saveRouteSearch(str_endPoint);
				Intent intent = new Intent(); // 数据是使用Intent返回
				intent.putExtra("str_endPoint", str_endPoint);// 把返回数据存入Intent
				EPointSearchActivity.this.setResult(RESULT_OK, intent); // 设置返回数据
				EPointSearchActivity.this.finish();// 关闭Activity
			}
			// setRouteSearchListView(false);
		} else if (v.getId() == R.id.ps_back) {// 返回
			finish();
		}
	}

	/***********************************************************************************
	 * 设置路线搜索ListView的显示(mode为true：生成Adapter ；mode为false：更新Adapter)
	 **********************************************************************************/
	protected void setRouteSearchListView(boolean mode) {
		// 获取sp保存的起始地点搜索记录总数
		int ePointSearchRecordNumber = sp.getInt("EPointSearchRecordNumber", 0);
		// 存在起始地点搜索记录数
		if (ePointSearchRecordNumber > 0) {
			// 生成长度为routeSearchRecordNumber + 1的数组，给Adapter提供数据
			String epSR[] = new String[ePointSearchRecordNumber + 1];
			// 读取sp中的所有rSRx 字符数据
			for (int i = 0; i < ePointSearchRecordNumber + 1; i++) {
				epSR[i] = sp.getString("epSR" + i, "目标地点");
			}
			if (mode) {
				// 设置Adapter
				myRSListViewAdapter = new RSListViewAdapter(this, epSR);
				// ListView关联Adapter
				lv_endPoint.setAdapter(myRSListViewAdapter);
			} else {
				myRSListViewAdapter.updateListView(epSR);
			}
		}
	}

	/***********************************************************************************
	 * 保存路线搜索到sp
	 **********************************************************************************/
	protected void saveRouteSearch(String str) {
		// 获取sp保存的起始地点搜索记录总数
		int ePointSearchRecordNumber = sp.getInt("EPointSearchRecordNumber", 0);
		boolean have = true;// 决定是否保存数据的标记
		if (ePointSearchRecordNumber >= 0) {
			String epSR;
			// 比较、判断sp中是否存在相同的搜索记录
			for (int i = 0; i <= ePointSearchRecordNumber; i++) {
				epSR = sp.getString("epSR" + i, "起始地点");
				if (epSR.equals(str)) {
					have = false;
				}
			}
		}
		if (have) {// 保存搜索记录
			// 起始地点搜索记录总数加一保存，保存新的搜索记录
			ePointSearchRecordNumber++;
			Editor editor = sp.edit();
			editor.putInt("EPointSearchRecordNumber", ePointSearchRecordNumber);
			editor.putString("epSR" + ePointSearchRecordNumber, str);
			editor.commit();
		}
	}
}
