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
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.zheng.travel.adapter.RSListViewAdapter;
import com.zheng.travel.utils.MyLog;

public class RoutePlanActivity extends Activity implements
		OnGetRoutePlanResultListener {

	// 搜索相关
	private ImageView transit;
	private ImageView drive;
	private ImageView walk;
	private EditText editSt;
	private EditText editEn;
	private RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private int selectType = 2;
	private ProgressDialog pd;
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
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

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
	 * 保存路线搜索到sp
	 **********************************************************************************/
	protected void saveRouteSearch(String str_editSt, String str_editEn) {
		// 获取sp保存的路线搜索记录总数
		int routeSearchRecordNumber = sp.getInt("routeSearchRecordNumber", 0);
		boolean have = true;// 决定是否保存数据的标记
		if (routeSearchRecordNumber >= 0) {
			String rSR;
			// 比较、判断sp中是否存在相同的搜索记录
			for (int i = 0; i <= routeSearchRecordNumber; i++) {
				rSR = sp.getString("rSR" + i, "始->终");
				if (rSR.equals(str_editSt + "->" + str_editEn)) {
					have = false;
				}
			}
		}
		if (have) {// 保存搜索记录
			// 搜索记录总数加一保存，保存新的搜索记录
			routeSearchRecordNumber++;
			Editor editor = sp.edit();
			editor.putInt("routeSearchRecordNumber", routeSearchRecordNumber);
			editor.putString("rSR" + routeSearchRecordNumber, str_editSt + "->"
					+ str_editEn);
			editor.commit();
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

			pd = new ProgressDialog(RoutePlanActivity.this);
			pd.setMessage("努力查询中...请稍等");
			pd.show();

			// 处理搜索按钮响应
			String str_editSt = editSt.getText().toString();
			String str_editEn = editEn.getText().toString();
			// 设置起终点信息，对于tranist search 来说，城市名无意义
			PlanNode stNode = PlanNode.withCityNameAndPlaceName("广州",
					str_editSt);
			PlanNode enNode = PlanNode.withCityNameAndPlaceName("广州",
					str_editEn);

			switch (selectType) {
			case 1:
				mSearch.drivingSearch((new DrivingRoutePlanOption()).from(
						stNode).to(enNode));
				break;
			case 2:
				mSearch.transitSearch((new TransitRoutePlanOption())
						.from(stNode).city("广州").to(enNode));
				break;
			case 3:
				mSearch.walkingSearch((new WalkingRoutePlanOption()).from(
						stNode).to(enNode));
				break;
			default:
				break;
			}

			saveRouteSearch(str_editSt, str_editEn);

		}
	}

	/**********************************************************************************
	 * 获取公交路线，处理请求的结果路线result
	 ********************************************************************************/
	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

			Intent intent = new Intent();
			intent.putExtra("selectType", selectType);
			intent.setClass(RoutePlanActivity.this, RouteResultsActivity.class);
			startActivity(intent);
			
			System.out.println("RouteLinesSize----"
					+ result.getRouteLines().size());

			for (int ii = 0; ii < result.getRouteLines().size(); ii++) {

				// 显示所有路线
				RouteLine myroute = result.getRouteLines().get(ii);

				int y = myroute.getAllStep().size();
				for (int i = 0; i < y; i++) {
					Object step = myroute.getAllStep().get(i);
					System.out.println("路段经纬度----"
							+ ((TransitRouteLine.TransitStep) step)
									.getEntrace().getLocation());
					System.out.println("路段信息----"
							+ ((TransitRouteLine.TransitStep) step)
									.getInstructions());
				}
			}

			System.out.println("getTaxiInfo----"
					+ result.getTaxiInfo().getDesc());// 路线打车描述信息
			System.out.println("getTaxiInfo----"
					+ result.getTaxiInfo().getDistance() + "m");// 总路程 ， 单位： m
			System.out.println("getTaxiInfo----"
					+ result.getTaxiInfo().getDuration() + "秒");// 总耗时，单位： 秒
			System.out.println("getTaxiInfo----"
					+ result.getTaxiInfo().getTotalPrice() + "元");// 总价 , 单位： 元

		}
	}

	/**********************************************************************************
	 * 获取自驾路线，处理请求的结果路线result
	 ********************************************************************************/
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

		}
	}

	/**********************************************************************************
	 * 获取步行路线，处理请求的结果路线result
	 ********************************************************************************/
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RoutePlanActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {

		}
	}

}
