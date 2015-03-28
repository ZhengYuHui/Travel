package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zheng.travel.utils.MyLog;

public class ShowPoiDetailActivity extends Activity implements
		OnGetPoiSearchResultListener {

	private PoiSearch mPoiSearch = null;// POI检索接口
	private DemoApplication demoApplication;
	private int poiPosition;
	private List<PoiInfo> poiInfo = null;
	private WebView webview;
	private ProgressDialog pd;

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
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);// 设置WebView属性，能够执行Javascript脚本
		webview.requestFocus();// 触摸焦点起作用
		webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {// 网页加载完成
					if (pd.isShowing()) {
						pd.dismiss();
					}
				}
			}
		});
	}

	/***************************************************************
	 * 搜索Poi
	 *************************************************************/
	protected void searchPoi() {
		pd = new ProgressDialog(ShowPoiDetailActivity.this);
		pd.setMessage("努力加载中...请稍等");
		pd.show();
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
			webview.loadUrl(result.getDetailUrl());

			// setContentView(webview);
			// Toast.makeText(ShowPoiDetailActivity.this,
			// result.getName() + ": " + result.getAddress(),
			// Toast.LENGTH_SHORT).show();
			// String text = "POI详情：\n";
			// text = text + "名称-->" + result.getName() + "\n";
			// text = text + "地址-->" + result.getAddress() + "\n";
			// text = text + "签到数量-->" + result.getCheckinNum() + "\n";
			// text = text + "评论数量-->" + result.getCommentNum() + "\n";
			// text = text + "详情 url-->" + result.getDetailUrl() + "\n";
			// text = text + "环境评价-->" + result.getEnvironmentRating() + "\n";
			// text = text + "设施评价-->" + result.getFacilityRating() + "\n";
			// text = text + "喜欢数量-->" + result.getFavoriteNum() + "\n";
			// text = text + "团购数量-->" + result.getGrouponNum() + "\n";
			// text = text + "卫生评价-->" + result.getHygieneRating() + "\n";
			// text = text + "图片数量-->" + result.getImageNum() + "\n";
			// text = text + "综合评价-->" + result.getOverallRating() + "\n";
			// text = text + "价格-->" + result.getPrice() + "\n";
			// text = text + "服务评价-->" + result.getServiceRating() + "\n";
			// text = text + "营业时间-->" + result.getShopHours() + "\n";
			// text = text + "标签-->" + result.getTag() + "\n";
			// text = text + "口味评价-->-->" + result.getTasteRating() + "\n";
			// text = text + "技术评价" + result.getTechnologyRating() + "\n";
			// text = text + "类型-->" + result.getType() + "\n";
			// text = text + "poi 的 uid-->" + result.getUid() + "\n";
			// text = text + "位置-->" + result.getLocation() + "\n";
		}
	}

	@Override
	public void onGetPoiResult(PoiResult result) {

	}

	/*************************************************************************
	 * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回, 如果不做此项处理则整个WebView返回退出
	 ************************************************************************/
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// MyLog.printLi("onKeyDown", "canGoBack()------->" + webview.canGoBack());
	//
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	// // 返回键退回
	// webview.goBack();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

}
