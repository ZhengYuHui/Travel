package com.zheng.travel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.zheng.travel.adapter.RSListViewAdapter;
import com.zheng.travel.utils.MyLog;

public class RoutePlanActivity extends Activity {

	private ImageView transit;
	private ImageView drive;
	private ImageView walk;
	private EditText editSt;
	private EditText editEn;

	private String str_editSt;
	private String str_editEn;

	private int selectType = 2;
	private SharedPreferences sp;
	private ListView lv_location_query;
	private RSListViewAdapter myRSListViewAdapter;
	private String TAG = "RoutePlanActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route_plan);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		transit = (ImageView) findViewById(R.id.transit);
		drive = (ImageView) findViewById(R.id.drive);
		walk = (ImageView) findViewById(R.id.walk);

		editSt = (EditText) findViewById(R.id.start);
		editEn = (EditText) findViewById(R.id.end);
		// 起点终点输入点击事件
		editSt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(RoutePlanActivity.this,
						SPointSearchActivity.class), 1);
			}
		});
		editEn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(RoutePlanActivity.this,
						EPointSearchActivity.class), 2);
			}
		});

		lv_location_query = (ListView) findViewById(R.id.lv_location_query);
		// 设置Adapter
		myRSListViewAdapter = new RSListViewAdapter(this, new String[0]);
		// ListView关联Adapter
		lv_location_query.setAdapter(myRSListViewAdapter);
		// 设置ListView条目点击的事件监听器
		lv_location_query.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String rSR = sp.getString("rSR" + position, "始->终");
				String[] strarray = rSR.split("->");
				editSt.setText(strarray[0]);
				editEn.setText(strarray[1]);
			}
		});
		setRouteSearchListView();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String result;
		switch (requestCode) {
		case 1: // 起始地点
			if (data != null) {
				result = data.getExtras().getString("str_startPoint");
				editSt.setText(result);
			}
			break;
		case 2: // 目标地点
			if (data != null) {
				result = data.getExtras().getString("str_endPoint");
				editEn.setText(result);
			}
			break;
		}
	}

	/***********************************************************************************
	 * 设置路线搜索ListView的显示
	 **********************************************************************************/
	protected void setRouteSearchListView() {
		// 获取sp保存的路线搜索记录总数
		int routeSearchRecordNumber = sp.getInt("routeSearchRecordNumber", 0);
		// 存在路线搜索记录数
		if (routeSearchRecordNumber > 0) {
			// 生成长度为routeSearchRecordNumber + 1的数组，给Adapter提供数据
			String rSR[] = new String[routeSearchRecordNumber + 1];
			// 读取sp中的所有rSRx 字符数据
			for (int i = 0; i < routeSearchRecordNumber + 1; i++) {
				rSR[i] = sp.getString("rSR" + i, "始->终");
			}
			myRSListViewAdapter.updateListView(rSR);
		}
	}

	/***********************************************************************************
	 * 顶部按键监听事件
	 **********************************************************************************/
	public void SearchButtonProcess(View v) {
		// 实际使用中请对起点终点城市进行正确的设定
		if (v.getId() == R.id.drive) {// 选择自驾路线
			walk.setBackgroundResource(R.drawable.route_icon_onfoot);
			transit.setBackgroundResource(R.drawable.route_icon_bus);
			drive.setBackgroundResource(R.drawable.route_icon_car_hl);
			selectType = 1;
		} else if (v.getId() == R.id.transit) {// 选择公交路线
			walk.setBackgroundResource(R.drawable.route_icon_onfoot);
			transit.setBackgroundResource(R.drawable.route_icon_bus_hl);
			drive.setBackgroundResource(R.drawable.route_icon_car);
			selectType = 2;
		} else if (v.getId() == R.id.walk) {// 选择步行路线
			walk.setBackgroundResource(R.drawable.route_icon_onfoot_hl);
			transit.setBackgroundResource(R.drawable.route_icon_bus);
			drive.setBackgroundResource(R.drawable.route_icon_car);
			selectType = 3;
		} else if (v.getId() == R.id.back) {// 返回
			finish();
		} else if (v.getId() == R.id.bt_route_exchange) {// 终始点互换
			String str_editSt = editSt.getText().toString();
			String str_editEn = editEn.getText().toString();
			editSt.setText(str_editEn);
			editEn.setText(str_editSt);
		} else if (v.getId() == R.id.search) {// 搜索路线
			// 处理搜索按钮响应
			str_editSt = editSt.getText().toString();
			str_editEn = editEn.getText().toString();
			intentActivity();
		}
	}

	protected void intentActivity() {
		// 跳转到搜索页面
		Intent intent = new Intent(RoutePlanActivity.this,
				RouteResultsActivity.class);
		intent.setFlags(0);// 设置Flag，告诉RouteResultsActivity这个intent的来源
		intent.putExtra("selectType", selectType);
		intent.putExtra("str_editSt", str_editSt);
		intent.putExtra("str_editEn", str_editEn);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		// mSearch.destroy();
		super.onDestroy();
	}

}
