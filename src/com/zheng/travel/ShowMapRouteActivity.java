package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class ShowMapRouteActivity extends Activity implements
		BaiduMap.OnMapClickListener {

	private DemoApplication demoApplication;
	private int selectType;
	private int position;
	private RouteLine route = null;
	private int myRouteLines;
	OverlayManager routeOverlay = null;
	private int nodeIndex = -1;// 节点索引,供浏览节点时使用
	private TextView tv_node_meg;
	private TextView tv_meg;
	private TextView popupText = null;// 泡泡view
	// 地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
	// 如果不处理touch事件，则无需继承，直接使用MapView即可
	MapView mMapView = null; // 地图View
	BaiduMap mBaidumap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_map_route);
		initView();
		getResultRoutes();
	}

	/**********************************************************************************
	 * 初始化控件
	 ********************************************************************************/
	protected void initView() {
		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();
		// 初始化地图
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
		// 地图点击事件处理
		mBaidumap.setOnMapClickListener(this);
		tv_node_meg = (TextView) findViewById(R.id.tv_node_meg);
		tv_meg = (TextView) findViewById(R.id.tv_meg);
	}

	/**********************************************************************************
	 * 获取RoutePlanActivity传递过来的ResultRoutes，并转换成相应的路线类型
	 ********************************************************************************/
	protected void getResultRoutes() {
		Intent intent = getIntent();
		selectType = intent.getIntExtra("selectType", 2);
		position = (intent.getIntExtra("position", 1) - 1);
		mBaidumap.clear();
		initMap();

	}

	/**********************************************************************************
	 * 地图显示初始化
	 ********************************************************************************/
	protected void initMap() {
		tv_node_meg.setText("起点");
		switch (selectType) {
		case 1:
			tv_meg.setText("自驾路线" + (position + 1));
			nodeIndex = -1;
			DrivingRouteResult Dresult = demoApplication.getDresult();
			route = Dresult.getRouteLines().get(position);
			myRouteLines = Dresult.getRouteLines().size();
			// 绘制路线
			DrivingRouteOverlay Doverlay = new DrivingRouteOverlay(mBaidumap);
			routeOverlay = Doverlay;
			mBaidumap.setOnMarkerClickListener(Doverlay);
			Doverlay.setData(Dresult.getRouteLines().get(position));
			Doverlay.addToMap();
			Doverlay.zoomToSpan();
			break;
		case 2:
			tv_meg.setText("公交路线" + (position + 1));
			nodeIndex = -1;
			TransitRouteResult Tresult = demoApplication.getTresult();
			myRouteLines = Tresult.getRouteLines().size();
			route = Tresult.getRouteLines().get(position);
			// 绘制路线
			TransitRouteOverlay Toverlay = new TransitRouteOverlay(mBaidumap);
			mBaidumap.setOnMarkerClickListener(Toverlay);
			routeOverlay = Toverlay;
			Toverlay.setData(Tresult.getRouteLines().get(position));
			Toverlay.addToMap();
			Toverlay.zoomToSpan();
			break;
		case 3:
			tv_meg.setText("步行路线" + (position + 1));
			nodeIndex = -1;
			WalkingRouteResult Wresult = demoApplication.getWresult();
			route = Wresult.getRouteLines().get(position);
			myRouteLines = Wresult.getRouteLines().size();
			// 绘制路线
			WalkingRouteOverlay Woverlay = new WalkingRouteOverlay(mBaidumap);
			mBaidumap.setOnMarkerClickListener(Woverlay);
			routeOverlay = Woverlay;
			Woverlay.setData(Wresult.getRouteLines().get(position));
			Woverlay.addToMap();
			Woverlay.zoomToSpan();
			break;
		}

	}

	/***********************************************************************************
	 * 顶部按键监听事件
	 **********************************************************************************/
	public void SearchButtonProcess(View v) {
		if (v.getId() == R.id.bt_pre) {// 上一条路线
			if (position > 0) {
				route = null;// 重置浏览节点的路线数据
				mBaidumap.clear();
				position--;
				initMap();
			}
		} else if (v.getId() == R.id.bt_next) {// 下一条路线
			if (position < myRouteLines) {
				route = null;// 重置浏览节点的路线数据
				mBaidumap.clear();
				position++;
				initMap();
			}
		} else if (v.getId() == R.id.bt_list) {// 列表
			finish();
		} else if (v.getId() == R.id.back) {// 返回
			finish();
		}
	}

	/**********************************************************************************
	 * 节点浏览
	 ********************************************************************************/
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.bt_node_pre) {
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.bt_node_next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				Toast.makeText(ShowMapRouteActivity.this, "已无下一步", 0).show();
				return;
			}
		} else if (v.getId() == R.id.bt_node_pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				Toast.makeText(ShowMapRouteActivity.this, "已无上一步", 0).show();
				return;
			}
		}
		showNodeInformation();
	}

	/**********************************************************************************
	 * 显示节点信息
	 ********************************************************************************/
	protected void showNodeInformation() {

		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		tv_node_meg.setText(nodeTitle);
		popupText = new TextView(ShowMapRouteActivity.this);
		popupText.setBackgroundResource(R.drawable.b_poi_real);
		mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onMapClick(LatLng point) {
		mBaidumap.hideInfoWindow();
	}

	@Override
	public boolean onMapPoiClick(MapPoi poi) {
		return false;
	}

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
		mMapView.onDestroy();
		super.onDestroy();
	}

}
