package com.zheng.travel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.zheng.travel.adapter.RouteListAdapter;
import com.zheng.travel.utils.MyLog;
import com.zheng.travel.utils.TransformationTime;

public class RouteResultsActivity extends Activity implements
		OnGetRoutePlanResultListener, OnGetGeoCoderResultListener {

	private String TAG = "RouteResultsActivity";
	private ImageView transit;
	private ImageView drive;
	private ImageView walk;
	private TextView tv_noFound;
	private TextView tv_refresh;
	private TextView tv_callTaxi;
	private int selectType;
	private String str_editSt;
	private String str_editEn;
	private ListView lv_route_results;
	private RouteListAdapter routeListAdapter;
	private ProgressDialog pd;
	private SharedPreferences sp;
	private GeoCoder GeoSearch = null; // 名称与经纬度互转
	private boolean GeoSearchType = false;// 起点终点转换为经纬度：false->起点；true->终点
	private RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private boolean searchMode = false;// 搜索方式：false->名称；true->经纬度
	private double dou_stLatitude;// 起点经度
	private double dou_stLongitude;// 起点纬度
	private double dou_edLatitude;// 终点经度
	private double dou_edLongitude;// 终点纬度

	private RouteLine routeLine[];
	private DemoApplication demoApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_route_results);

		initView();
		getResultRoutes();
		initImageView();
	}

	/**********************************************************************************
	 * 初始化控件
	 ********************************************************************************/
	protected void initView() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();
		// 初始化搜索模块，注册事件监听
		GeoSearch = GeoCoder.newInstance();
		GeoSearch.setOnGetGeoCodeResultListener(this);

		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		// findViewById
		tv_callTaxi = (TextView) findViewById(R.id.tv_callTaxi);
		tv_noFound = (TextView) findViewById(R.id.tv_noFound);
		tv_refresh = (TextView) findViewById(R.id.tv_refresh);
		transit = (ImageView) findViewById(R.id.transit);
		drive = (ImageView) findViewById(R.id.drive);
		walk = (ImageView) findViewById(R.id.walk);
		// ListView
		lv_route_results = (ListView) findViewById(R.id.lv_route_results);
		routeListAdapter = new RouteListAdapter(this, new String[0],
				new String[0]);
		lv_route_results.setAdapter(routeListAdapter);// ListView关联Adapter
		lv_route_results.setOnItemClickListener(new OnListViewItemClick());
	}

	/**********************************************************************************
	 * 初始化顶部的路线类型图标
	 ********************************************************************************/
	protected void initImageView() {
		walk.setBackgroundResource(R.drawable.route_icon_onfoot);
		transit.setBackgroundResource(R.drawable.route_icon_bus);
		drive.setBackgroundResource(R.drawable.route_icon_car);
		switch (selectType) {
		case 1:
			drive.setBackgroundResource(R.drawable.route_icon_car_hl);
			break;
		case 2:
			transit.setBackgroundResource(R.drawable.route_icon_bus_hl);
			break;
		case 3:
			walk.setBackgroundResource(R.drawable.route_icon_onfoot_hl);
			break;
		}
	}

	/**********************************************************************************
	 * 路线方案ListView条目点击
	 ********************************************************************************/
	class OnListViewItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 得到路线方案具体步骤数
			int y = routeLine[position].getAllStep().size();
			String rSRStep[] = new String[y];
			for (int i = 0; i < y; i++) {
				Object step = routeLine[position].getAllStep().get(i);
				switch (selectType) {
				case 1:
					rSRStep[i] = ((DrivingRouteLine.DrivingStep) step)
							.getInstructions();
					break;
				case 2:
					rSRStep[i] = ((TransitRouteLine.TransitStep) step)
							.getInstructions();
					break;
				case 3:
					rSRStep[i] = ((WalkingRouteLine.WalkingStep) step)
							.getInstructions();
					break;
				}
			}
			// 跳转到搜索页面
			Intent intent = new Intent(RouteResultsActivity.this,
					ShowRouteStepActivity.class);
			intent.putExtra("str_editSt", str_editSt);
			intent.putExtra("str_editEn", str_editEn);
			intent.putExtra("position", (position + 1));
			intent.putExtra("selectType", selectType);
			intent.putExtra("rSRStep", rSRStep);
			startActivity(intent);
		}
	}

	/**********************************************************************************
	 * 搜索路线方案
	 ********************************************************************************/
	protected void startSearch() {
		tv_noFound.setVisibility(View.GONE);
		tv_refresh.setVisibility(View.GONE);
		lv_route_results.setVisibility(View.GONE);
		tv_callTaxi.setVisibility(View.GONE);
		pd = new ProgressDialog(RouteResultsActivity.this);
		pd.setMessage("努力查询中...请稍等");
		pd.show();

		PlanNode stNode = null;
		PlanNode enNode = null;
		if (searchMode) {
			LatLng stNodeLocation = new LatLng(dou_stLatitude, dou_stLongitude);
			LatLng enNodeLocation = new LatLng(dou_edLatitude, dou_edLongitude);
			stNode = PlanNode.withLocation(stNodeLocation);
			enNode = PlanNode.withLocation(enNodeLocation);
		} else {
			// 设置起终点信息，对于公交 search 来说，城市名无意义
			stNode = PlanNode.withCityNameAndPlaceName("广州", str_editSt);
			enNode = PlanNode.withCityNameAndPlaceName("广州", str_editEn);
		}

		switch (selectType) {
		case 1:
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
			break;
		case 2:
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
					.city("广州").to(enNode));
			break;
		case 3:
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
					.to(enNode));
			break;
		default:
			break;
		}
	}

	/**********************************************************************************
	 * 获取RoutePlanActivity传递过来的ResultRoutes，并转换成相应的路线类型
	 ********************************************************************************/
	protected void getResultRoutes() {
		Intent intent = getIntent();
		selectType = intent.getIntExtra("selectType", 2);
		switch (intent.getFlags()) {
		case 0:
			str_editSt = intent.getStringExtra("str_editSt");
			str_editEn = intent.getStringExtra("str_editEn");
			if (str_editSt.equals("我的位置")) {
				str_editSt = demoApplication.getPositioning();
			} else if (str_editEn.equals("我的位置")) {
				str_editEn = demoApplication.getPositioning();
			}
			startSearch();
			break;
		case 1:
			searchMode = true;
			dou_stLatitude = demoApplication.getLatitude();
			dou_stLongitude = demoApplication.getLongitude();
			dou_edLatitude = intent.getDoubleExtra("POIlatitude", 0);
			dou_edLongitude = intent.getDoubleExtra("POIlongitude", 0);
			str_editEn = intent.getStringExtra("POIname");
			str_editSt = "我的位置";
			startSearch();
			break;
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
			tv_noFound.setVisibility(View.VISIBLE);
			tv_refresh.setVisibility(View.VISIBLE);
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			demoApplication.setTresult(result);
			showTRouteLine(result);
			saveRouteSearch(str_editSt, str_editEn);// 保存搜索记录
		}
	}

	// 显示公交路线
	protected void showTRouteLine(TransitRouteResult Tresult) {

		MyLog.printLi(TAG, "路线方案总数----" + Tresult.getRouteLines().size());

		String rSRLine[] = new String[Tresult.getRouteLines().size()];
		String rSRTime[] = new String[Tresult.getRouteLines().size()];
		routeLine = new RouteLine[Tresult.getRouteLines().size()];
		for (int ii = 0; ii < Tresult.getRouteLines().size(); ii++) {
			// 显示所有路线
			RouteLine myroute = Tresult.getRouteLines().get(ii);
			routeLine[ii] = myroute;
			rSRLine[ii] = "路线" + (ii + 1);
			rSRTime[ii] = "耗时："
					+ TransformationTime.secToTime(myroute.getDuration());
		}
		routeListAdapter.updateListView(rSRLine, rSRTime);
		lv_route_results.setVisibility(View.VISIBLE);
		tv_callTaxi.setText("打的：约"
				+ Tresult.getTaxiInfo().getTotalPrice()
				+ "元; "
				+ " 耗时"
				+ TransformationTime.secToTime(Tresult.getTaxiInfo()
						.getDuration()));
		tv_callTaxi.setVisibility(View.VISIBLE);
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
			tv_noFound.setVisibility(View.VISIBLE);
			tv_refresh.setVisibility(View.VISIBLE);
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			demoApplication.setDresult(result);
			showDRouteLine(result);
			saveRouteSearch(str_editSt, str_editEn);// 保存搜索记录
		}
	}

	// 显示自驾路线
	protected void showDRouteLine(DrivingRouteResult result) {
		MyLog.printLi(TAG, "路线方案总数----" + result.getRouteLines().size());

		String rSRLine[] = new String[result.getRouteLines().size()];
		String rSRTime[] = new String[result.getRouteLines().size()];
		routeLine = new RouteLine[result.getRouteLines().size()];
		for (int ii = 0; ii < result.getRouteLines().size(); ii++) {
			// 显示所有路线
			RouteLine myroute = result.getRouteLines().get(ii);
			routeLine[ii] = myroute;
			rSRLine[ii] = "路线" + (ii + 1);
			rSRTime[ii] = "耗时："
					+ TransformationTime.secToTime(myroute.getDuration());
		}
		routeListAdapter.updateListView(rSRLine, rSRTime);
		lv_route_results.setVisibility(View.VISIBLE);

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
			tv_noFound.setVisibility(View.VISIBLE);
			tv_refresh.setVisibility(View.VISIBLE);
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			demoApplication.setWresult(result);
			showWRouteLine(result);
			saveRouteSearch(str_editSt, str_editEn);// 保存搜索记录
		}
	}

	// 显示步行路线
	protected void showWRouteLine(WalkingRouteResult result) {
		MyLog.printLi(TAG, "路线方案总数----" + result.getRouteLines().size());

		String rSRLine[] = new String[result.getRouteLines().size()];
		String rSRTime[] = new String[result.getRouteLines().size()];
		routeLine = new RouteLine[result.getRouteLines().size()];
		for (int ii = 0; ii < result.getRouteLines().size(); ii++) {
			// 显示所有路线
			RouteLine myroute = result.getRouteLines().get(ii);
			routeLine[ii] = myroute;
			rSRLine[ii] = "路线" + (ii + 1);
			rSRTime[ii] = "耗时："
					+ TransformationTime.secToTime(myroute.getDuration());
		}
		routeListAdapter.updateListView(rSRLine, rSRTime);
		lv_route_results.setVisibility(View.VISIBLE);
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
			startSearch();
		} else if (v.getId() == R.id.transit) {// 选择公交路线
			walk.setBackgroundResource(R.drawable.route_icon_onfoot);
			transit.setBackgroundResource(R.drawable.route_icon_bus_hl);
			drive.setBackgroundResource(R.drawable.route_icon_car);
			selectType = 2;
			startSearch();
		} else if (v.getId() == R.id.walk) {// 选择步行路线
			walk.setBackgroundResource(R.drawable.route_icon_onfoot_hl);
			transit.setBackgroundResource(R.drawable.route_icon_bus);
			drive.setBackgroundResource(R.drawable.route_icon_car);
			selectType = 3;
			startSearch();
		} else if (v.getId() == R.id.back) {// 返回
			finish();
		} else if (v.getId() == R.id.tv_refresh) {// 刷新
			startSearch();
		} else if (v.getId() == R.id.tv_callTaxi) {// 打的

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

	/**************************************************************
	 * 地点查询结果result（返回经纬度）
	 *************************************************************/
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(RouteResultsActivity.this, "抱歉，未能找到结果",
					Toast.LENGTH_LONG).show();
			return;
		}
		// if (GeoSearchType) {
		// dou_edLatitude = result.getLocation().latitude;
		// dou_edLongitude = result.getLocation().longitude;
		// } else {
		// dou_stLatitude = result.getLocation().latitude;
		// dou_stLongitude = result.getLocation().longitude;
		// }
		// startSearch();
	}

	/**************************************************************
	 * 根据经纬度返回地点信息
	 *************************************************************/
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

	}

	@Override
	protected void onDestroy() {
		mSearch.destroy();
		GeoSearch.destroy();
		super.onDestroy();
	}

}
