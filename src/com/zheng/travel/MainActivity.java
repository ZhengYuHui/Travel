package com.zheng.travel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.zheng.travel.utils.MyLog;

public class MainActivity extends Activity implements
		OnGetGeoCoderResultListener {
	// 定位相关
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationClient mLocClient;
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private double latitude;// 纬度
	private double longitude;// 经度
	// UI相关
	private Button requestLocButton; // 定位模式按键
	private Button commonButton; // 普通图层按键
	private Button satelliteButton; // 卫星图层按键
	private Button trafficButton; // 交通图层按键
	private Button thermalButton; // 热力图层按键
	private Button searchButton;// 搜索按键
	private Button moreButton; // 更多按键
	private LinearLayout ll_head2;
	private RelativeLayout RL_Route;
	private RelativeLayout RL_Navi;
	private RelativeLayout RL_Nearby;
	private TextView locationSearch;// 地点搜索输入框
	private AlertDialog dialog;
	// private EditText locationSearchCity;// 城市搜索输入框
	// 控制显示相关
	boolean isShowHead2 = false;// 是否显示ll_head2
	boolean isCommonOrSatellite = false;// 显示普通OR卫星图
	boolean isShowTraffic = false;// 是否显示交通图层
	boolean isShowThermal = false;// 是否显示热力图层
	boolean isFirstLoc = true;// 是否首次定位

	float zoomLevel = (float) 15.0;// 设置地图级别
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	private SDKReceiver mReceiver;

	/**************************************************************
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 *************************************************************/
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				// text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				// text.setText("网络出错");
				Toast.makeText(getApplicationContext(), "网络出错", 0).show();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		initOtherView();
		initMap();
		registerMapSDKBroadcast();
	}

	/**************************************************************
	 * 注册 SDK 广播监听者
	 *************************************************************/
	protected void registerMapSDKBroadcast() {
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	/**************************************************************
	 * 初始化Map地图控件
	 *************************************************************/
	protected void initMap() {
		// 定位模式按键
		requestLocButton = (Button) findViewById(R.id.button1);
		mCurrentMode = LocationMode.NORMAL;
		requestLocButton.setOnClickListener(btnClickListener);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 设置地图级别
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
		mBaiduMap.animateMapStatus(u);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		// 开启指南针
		mBaiduMap.getUiSettings().setCompassEnabled(true);

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	/**************************************************************
	 * 初始化其他控件
	 *************************************************************/
	protected void initOtherView() {

		RL_Route = (RelativeLayout) findViewById(R.id.RL_Route);
		RL_Navi = (RelativeLayout) findViewById(R.id.RL_Navi);
		RL_Nearby = (RelativeLayout) findViewById(R.id.RL_Nearby);
		ll_head2 = (LinearLayout) findViewById(R.id.ll_head2);
		locationSearch = (TextView) findViewById(R.id.locationSearch);
		searchButton = (Button) findViewById(R.id.searchButton);
		moreButton = (Button) findViewById(R.id.moreButton);
		commonButton = (Button) findViewById(R.id.commonButton);
		satelliteButton = (Button) findViewById(R.id.satelliteButton);
		trafficButton = (Button) findViewById(R.id.trafficButton);
		thermalButton = (Button) findViewById(R.id.thermalButton);

		RL_Route.setOnClickListener(btnClickListener);
		RL_Navi.setOnClickListener(btnClickListener);
		RL_Nearby.setOnClickListener(btnClickListener);
		locationSearch.setOnClickListener(btnClickListener);
		searchButton.setOnClickListener(btnClickListener);
		moreButton.setOnClickListener(btnClickListener);
		commonButton.setOnClickListener(btnClickListener);
		satelliteButton.setOnClickListener(btnClickListener);
		trafficButton.setOnClickListener(btnClickListener);
		thermalButton.setOnClickListener(btnClickListener);

	}

	/**************************************************************
	 * 监听按键点击事件
	 *************************************************************/
	private OnClickListener btnClickListener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.RL_Route:
				Intent intent = null;
				intent = new Intent(MainActivity.this, RoutePlanActivity.class);
				startActivity(intent);
				break;
			case R.id.RL_Navi:
				startActivity(new Intent(MainActivity.this,
						AirEnvironmentActivity.class));
				break;
			case R.id.RL_Nearby:
				Intent intentNearby = new Intent(MainActivity.this,
						PoiSearchActivity.class);
				intentNearby.putExtra("latitude", latitude);
				intentNearby.putExtra("longitude", longitude);
				startActivity(intentNearby);
				break;
			case R.id.button1:
				requestLocButtonEvent();
				break;
			case R.id.commonButton:// 普通视图
				commonOrSatelliteEvent(true);
				break;
			case R.id.satelliteButton:// 卫星视图
				commonOrSatelliteEvent(false);
				break;
			case R.id.trafficButton:// 交通视图
				trafficButtonEvent();
				break;
			case R.id.thermalButton:// 热力视图
				thermalButtonEvent();
				break;
			case R.id.moreButton:// 更多
				moreButtonEvent();
				break;
			case R.id.locationSearch:// 搜索
				showSearchDialog();
				break;

			}
		}
	};

	/**************************************************************
	 * searchButton按键点击事件
	 *************************************************************/
	private void searchButtonEvent(String searchCity, String address) {
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// Geo搜索
		mSearch.geocode(new GeoCodeOption().city(searchCity).address(address));
	}

	/**************************************************************
	 * thermalButton按键点击事件
	 *************************************************************/
	private void thermalButtonEvent() {

		isShowThermal = !isShowThermal;
		if (isShowThermal) {
			thermalButton.setTextColor(getResources().getColor(
					R.color.blue_color));
		} else {
			thermalButton.setTextColor(getResources().getColor(R.color.black));
		}
		mBaiduMap.setBaiduHeatMapEnabled(isShowThermal);
	}

	/**************************************************************
	 * trafficButton按键点击事件
	 *************************************************************/
	private void trafficButtonEvent() {

		isShowTraffic = !isShowTraffic;
		if (isShowTraffic) {
			trafficButton.setTextColor(getResources().getColor(
					R.color.blue_color));
		} else {
			trafficButton.setTextColor(getResources().getColor(R.color.black));
		}
		mBaiduMap.setTrafficEnabled(isShowTraffic);
	}

	/**************************************************************
	 * commonOrSatellite按键点击事件
	 *************************************************************/
	private void commonOrSatelliteEvent(boolean type) {

		if (type) {
			commonButton.setTextColor(getResources().getColor(
					R.color.blue_color));
			satelliteButton
					.setTextColor(getResources().getColor(R.color.black));
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		} else {
			satelliteButton.setTextColor(getResources().getColor(
					R.color.blue_color));
			commonButton.setTextColor(getResources().getColor(R.color.black));
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		}
	}

	/**************************************************************
	 * moreButton按键点击事件
	 *************************************************************/
	private void moreButtonEvent() {
		isCommonOrSatellite = !isCommonOrSatellite;
		if (isCommonOrSatellite) {
			Animation aa = AnimationUtils.loadAnimation(MainActivity.this,
					R.anim.push_right_out);
			ll_head2.startAnimation(aa);

			ll_head2.setVisibility(View.VISIBLE);
			moreButton.setBackgroundResource(R.drawable.mbox_up_btn_hl);
		} else {
			Animation aa = AnimationUtils.loadAnimation(MainActivity.this,
					R.anim.push_left_out);
			ll_head2.startAnimation(aa);
			new Handler().postDelayed(new Runnable() {
				public void run() {
					ll_head2.setVisibility(View.GONE);
				}
			}, 500);
			moreButton.setBackgroundResource(R.drawable.mbox_down_btn_nor);
		}
	}

	/**************************************************************
	 * requestLocButton按键点击事件
	 *************************************************************/
	private void requestLocButtonEvent() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		switch (mCurrentMode) {
		case NORMAL:// 由普通转跟随
			requestLocButton.setBackgroundResource(R.drawable.navi_idle_gps_3d);
			mCurrentMode = LocationMode.FOLLOWING;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker));
			break;
		case COMPASS:// 由罗盘转普通
			requestLocButton
					.setBackgroundResource(R.drawable.navi_idle_gps_locked);
			mCurrentMode = LocationMode.NORMAL;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker));
			break;
		case FOLLOWING:// 由跟随转罗盘
			requestLocButton
					.setBackgroundResource(R.drawable.navi_idle_gps_unlocked);
			mCurrentMode = LocationMode.COMPASS;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker));
			break;
		}
	}

	/**************************************************************
	 * 地点查询结果result（返回经纬度）
	 *************************************************************/
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.clear();
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.icon_marka)));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));

		// String strInfo = String.format("纬度：%f 经度：%f",
		// result.getLocation().latitude, result.getLocation().longitude);
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {

	}

	/**************************************************************
	 * 定位SDK监听函数
	 *************************************************************/
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	/*******************************************************
	 * 显示地址搜索对话框
	 ******************************************************/
	public void showSearchDialog() {

		AlertDialog.Builder builder = new Builder(MainActivity.this);
		View view = View.inflate(MainActivity.this, R.layout.dialog_search,
				null);
		final EditText searchCity = (EditText) view.findViewById(R.id.code);
		final EditText searchAddress = (EditText) view
				.findViewById(R.id.prompt);

		// 确定按键，范围内发送指令，范围外提示重输
		Button searchButton = (Button) view.findViewById(R.id.bt_ok);
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String City = searchCity.getText().toString().trim();
				String Address = searchAddress.getText().toString().trim();
				if (City.trim().isEmpty()) {
					Toast.makeText(MainActivity.this, "城市输入不能为空", 1000).show();
					Animation shake = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.shake);
					searchCity.startAnimation(shake);
				} else if (Address.isEmpty()) {
					Toast.makeText(MainActivity.this, "具体地址输入不能为空", 1000)
							.show();
					Animation shake = AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.shake);
					searchAddress.startAnimation(shake);
				} else {
					searchButtonEvent(City, Address);
					dialog.dismiss();
				}
			}
		});

		dialog = builder.create();
		// dialog.setCancelable(false);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

	/*******************************************************
	 * 生命周期
	 ******************************************************/
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

}
