package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zheng.travel.utils.MyLog;

public class PoiSearchActivity extends Activity implements
		OnGetPoiSearchResultListener {

	private String TAG = "PoiSearchActivity";
	private DemoApplication demoApplication;
	private PoiSearch mPoiSearch = null;// POI检索接口
	private int load_Index = 0;// POI检索结果分页编号
	private double latitude;// 纬度
	private double longitude;// 经度
	private String searchKey;// 搜索关键字

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_poi_search);
		getResultRoutes();
		initView();
		searchPoi(searchKey, 5000);
	}

	/***************************************************************
	 * 初始化控件
	 *************************************************************/
	protected void initView() {
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

	}

	/**********************************************************************************
	 * 获取MainActivity传递过来的定位经纬度
	 ********************************************************************************/
	protected void getResultRoutes() {
		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();
		Intent intent = getIntent();
		searchKey = intent.getStringExtra("searchKey");
		latitude = demoApplication.getLatitude();
		longitude = demoApplication.getLongitude();
		MyLog.printLi(TAG, "searchKey----->" + searchKey);
		MyLog.printLi(TAG, "latitude----->" + latitude);
		MyLog.printLi(TAG, "longitude----->" + longitude);
	}

	/***************************************************************
	 * 搜索Poi
	 *************************************************************/
	protected void searchPoi(String key, int range) {
		LatLng latLng = new LatLng(latitude, longitude);
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).location(latLng)
				.keyword(key).pageNum(load_Index).radius(range));
	}

	/***************************************************************
	 * poi 详情查询结果回调
	 *************************************************************/
	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {

	}

	/***************************************************************
	 * poi 查询结果回调
	 *************************************************************/
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(PoiSearchActivity.this, "未找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			myLogPrint(result);
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(PoiSearchActivity.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	/***************************************************************
	 * 打印 poi 查询结果
	 *************************************************************/
	protected void myLogPrint(PoiResult result) {
		int CurrentPageCapacity = result.getCurrentPageCapacity();// 获取单页容量,单页容量可以通过检索参数指定
		int CurrentPageNum = result.getCurrentPageNum();// 获取当前分页编号
		int TotalPageNum = result.getTotalPageNum();// 获取总分页数
		int TotalPoiNum = result.getTotalPoiNum();// 获取POI总数
		List<PoiInfo> poiInfo = result.getAllPoi();// 获取Poi检索结果
		List<CityInfo> cityInfo = result.getSuggestCityList();// 返回城市列表页的结果数

		MyLog.printLi(TAG, "CurrentPageCapacity--->" + CurrentPageCapacity);
		MyLog.printLi(TAG, "CurrentPageNum--->" + CurrentPageNum);
		MyLog.printLi(TAG, "TotalPageNum--->" + TotalPageNum);
		MyLog.printLi(TAG, "TotalPoiNum--->" + TotalPoiNum);

		for (PoiInfo poiInfo1 : poiInfo) {
			MyLog.printLi(TAG, "poiInfo1.address--->" + poiInfo1.address);// poi地址信息
			MyLog.printLi(TAG, "poiInfo1.city--->" + poiInfo1.city);// poi所在城市
			MyLog.printLi(TAG, "poiInfo1.hasCaterDetails--->"
					+ poiInfo1.hasCaterDetails);// poi点是否有美食类详情页面
			MyLog.printLi(TAG, "poiInfo1.isPano--->" + poiInfo1.isPano);// poi点附近是否有街景，可使用uid检索全景组件的全景数据
			MyLog.printLi(TAG, "poiInfo1.location--->" + poiInfo1.location);// poi坐标当ePoiType为2或4时pt为空
			MyLog.printLi(TAG, "poiInfo1.name--->" + poiInfo1.name);// poi名称
			MyLog.printLi(TAG, "poiInfo1.phoneNum--->" + poiInfo1.phoneNum);// poi电话信息
			MyLog.printLi(TAG, "poiInfo1.type--->" + poiInfo1.type);// poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路,
			MyLog.printLi(TAG, "poiInfo1.uid--->" + poiInfo1.uid);// isPano为true调用街景PanoramaService类的方法检索街景数据
		}
		// for (CityInfo cityInfo1 : cityInfo) {
		// MyLog.printLi(TAG, "cityInfo1.city--->" + cityInfo1.city);// 城市名称
		// MyLog.printLi(TAG, "cityInfo1.num--->" + cityInfo1.num);// 搜索结果数量
		// }

	}

	/***************************************************************
	 * 生命周期onDestroy中销毁mPoiSearch
	 *************************************************************/
	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		super.onDestroy();
	}

}
