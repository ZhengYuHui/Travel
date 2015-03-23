package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class ShowPoiDetailActivity extends Activity implements
		OnGetPoiSearchResultListener {

	private PoiSearch mPoiSearch = null;// POI检索接口
	private DemoApplication demoApplication;
	private int poiPosition;
	private List<PoiInfo> poiInfo = null;
	private TextView tv_showSearchMeg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_poi_detail);
		getResultRoutes();
		initView();
		searchPoi();
	}

	/**********************************************************************************
	 * 获取MainActivity传递过来的定位经纬度
	 ********************************************************************************/
	protected void getResultRoutes() {
		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();
		poiInfo = demoApplication.getPoiInfo();
		Intent intent = getIntent();
		poiPosition = intent.getIntExtra("poiPosition", 0);
	}

	/***************************************************************
	 * 初始化控件
	 *************************************************************/
	protected void initView() {
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		tv_showSearchMeg = (TextView) findViewById(R.id.tv_showSearchMeg);
	}

	/***************************************************************
	 * 搜索Poi
	 *************************************************************/
	protected void searchPoi() {
		PoiInfo poi = poiInfo.get(poiPosition);
		mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
				.poiUid(poi.uid));
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(ShowPoiDetailActivity.this, "抱歉，未找到结果",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ShowPoiDetailActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
			String text = "POI详情：\n";
			text = text + "名称-->" + result.getName() + "\n";
			text = text + "地址-->" + result.getAddress() + "\n";
			text = text + "签到数量-->" + result.getCheckinNum() + "\n";
			text = text + "评论数量-->" + result.getCommentNum() + "\n";
			text = text + "详情 url-->" + result.getDetailUrl() + "\n";
			text = text + "环境评价-->" + result.getEnvironmentRating() + "\n";
			text = text + "设施评价-->" + result.getFacilityRating() + "\n";
			text = text + "喜欢数量-->" + result.getFavoriteNum() + "\n";
			text = text + "团购数量-->" + result.getGrouponNum() + "\n";
			text = text + "卫生评价-->" + result.getHygieneRating() + "\n";
			text = text + "图片数量-->" + result.getImageNum() + "\n";
			text = text + "综合评价-->" + result.getOverallRating() + "\n";
			text = text + "价格-->" + result.getPrice() + "\n";
			text = text + "服务评价-->" + result.getServiceRating() + "\n";
			text = text + "营业时间-->" + result.getShopHours() + "\n";
			text = text + "标签-->" + result.getTag() + "\n";
			text = text + "口味评价-->-->" + result.getTasteRating() + "\n";
			text = text + "技术评价" + result.getTechnologyRating() + "\n";
			text = text + "类型-->" + result.getType() + "\n";
			text = text + "poi 的 uid-->" + result.getUid() + "\n";
			text = text + "位置-->" + result.getLocation() + "\n";

			tv_showSearchMeg.setText(text);

		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {

	}

}
