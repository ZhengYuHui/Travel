package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.zheng.travel.adapter.RSListViewAdapter;
import com.zheng.travel.utils.MyLog;

public class EPointSearchActivity extends Activity implements
		OnGetSuggestionResultListener {
	private String TAG = "EPointSearchActivity";
	private SharedPreferences sp;
	private EditText ed_endPoint;
	private ListView lv_endPoint;
	private ProgressBar pb_search;
	private RSListViewAdapter myRSListViewAdapter;

	private SuggestionSearch mSuggestionSearch = null;
	private String epSRAllSuggestions[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_epoint_search);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);

		pb_search = (ProgressBar) findViewById(R.id.pb_search);
		ed_endPoint = (EditText) findViewById(R.id.ed_endPoint);
		// EditText输入监听事件
		ed_endPoint.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// EditText输入为空，ListView显示sp保存的搜索记录
				if (ed_endPoint.getText().toString().trim().isEmpty()) {
					setRouteSearchListView();
				} else {// EditText输入不为空，ListView显示建议列表
					// 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
					mSuggestionSearch
							.requestSuggestion((new SuggestionSearchOption())
									.keyword(ed_endPoint.getText().toString())
									.city(""));
					// 显示ProgressBar提示搜索中
					pb_search.setVisibility(View.VISIBLE);
				}
			}
		});

		lv_endPoint = (ListView) findViewById(R.id.lv_endPoint);
		myRSListViewAdapter = new RSListViewAdapter(this, new String[0]);// 设置Adapter
		lv_endPoint.setAdapter(myRSListViewAdapter); // ListView关联Adapter
		// 设置ListView条目点击的事件监听器
		lv_endPoint.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ed_endPoint.getText().toString().trim().isEmpty()) {
					ed_endPoint.setText(sp.getString("epSR" + position, "目标地点"));
				} else {
					ed_endPoint.setText(epSRAllSuggestions[position]);
				}
			}
		});

		setRouteSearchListView();
	}

	/***********************************************************************************
	 * 返回建议搜索服务的结果
	 **********************************************************************************/
	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		// 建议结果为空，传入空数组，listview不显示
		if (res == null || res.getAllSuggestions() == null) {
			epSRAllSuggestions = new String[0];
			myRSListViewAdapter.updateListView(epSRAllSuggestions);
			pb_search.setVisibility(View.GONE);
			return;
		}
		// 建议结果不为空，传入结果size大小的数组，listview显示建议结果
		epSRAllSuggestions = new String[res.getAllSuggestions().size()];
		int i = 0;
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null) {
				MyLog.printLi(TAG, "info.key----->" + info.key);
				epSRAllSuggestions[i] = info.key;
				i++;
			}
		}
		myRSListViewAdapter.updateListView(epSRAllSuggestions);
		pb_search.setVisibility(View.GONE);
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
	 * 设置路线搜索ListView的显示
	 **********************************************************************************/
	protected void setRouteSearchListView() {
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
			myRSListViewAdapter.updateListView(epSR);
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
				epSR = sp.getString("epSR" + i, "目标地点");
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
