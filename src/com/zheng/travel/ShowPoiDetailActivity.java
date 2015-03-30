package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zheng.travel.utils.DensityUtil;
import com.zheng.travel.utils.MyLog;

public class ShowPoiDetailActivity extends Activity implements
		OnGetPoiSearchResultListener {

	private String TAG = "ShowPoiDetailActivity";

	private PoiSearch mPoiSearch = null;// POI检索接口
	private DemoApplication demoApplication;
	private int poiPosition;
	private List<PoiInfo> poiInfo = null;
	private WebView webview;
	private ProgressDialog pd;

	private SharedPreferences sp;
	private WindowManager wm;
	private Button bt_close;
	private long[] mHits = new long[2];
	private WindowManager.LayoutParams params;

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
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		bt_close = new Button(ShowPoiDetailActivity.this);
		// bt_close.setWidth(DensityUtil.dip2px(ShowPoiDetailActivity.this,
		// 70));
		// bt_close.setHeight(DensityUtil.dip2px(ShowPoiDetailActivity.this,
		// 50));
		bt_close.setText("双击关闭");
		bt_close.setBackgroundResource(R.drawable.clean_selector);
		bt_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {// 500ms内双击
					finish();
				}
			}
		});

		// 给view对象设置一个触摸的监听器
		bt_close.setOnTouchListener(new bt_closeTouchListener());

		// 通过params设置窗体的参数
		params = new WindowManager.LayoutParams();
		// 设置窗体的宽高为包裹内容
		// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = DensityUtil.dip2px(ShowPoiDetailActivity.this, 50);
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// 与窗体左上角对其
		params.gravity = Gravity.TOP + Gravity.LEFT;
		// 指定窗体距离左边100 上边100个像素
		params.x = sp.getInt("lastx", 0);
		params.y = sp.getInt("lasty", 0);
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		wm.addView(bt_close, params);

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

	/***************************************************************
	 * Poi的搜索结果返回
	 *************************************************************/
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		MyLog.printLi("onKeyDown", "canGoBack()------->" + webview.canGoBack());

		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			// 返回键退回
			webview.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*************************************************************************
	 * 给view对象设置一个触摸的监听器
	 * ************************************************************************/
	protected class bt_closeTouchListener implements OnTouchListener {

		// 定义手指的初始化位置
		int startX;
		int startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:// 手指按下屏幕
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动
				int newX = (int) event.getRawX();
				int newY = (int) event.getRawY();
				int dx = newX - startX;
				int dy = newY - startY;
				params.x += dx;
				params.y += dy;
				// 考虑边界问题
				if (params.x < 0) {
					params.x = 0;
				}
				if (params.y < 0) {
					params.y = 0;
				}
				if (params.x > (wm.getDefaultDisplay().getWidth() - bt_close
						.getWidth())) {
					params.x = (wm.getDefaultDisplay().getWidth() - bt_close
							.getWidth());
				}
				if (params.y > (wm.getDefaultDisplay().getHeight() - bt_close
						.getHeight())) {
					params.y = (wm.getDefaultDisplay().getHeight() - bt_close
							.getHeight());
				}
				wm.updateViewLayout(bt_close, params);
				// 重新初始化手指的开始结束位置。
				startX = (int) event.getRawX();
				startY = (int) event.getRawY();
				break;
			case MotionEvent.ACTION_UP:// 手指离开屏幕一瞬间
				// 记录控件距离屏幕左上角的坐标
				Editor editor = sp.edit();
				editor.putInt("lastx", params.x);
				editor.putInt("lasty", params.y);
				editor.commit();
				break;
			}
			return false;// 事件处理完毕了。不要让父控件 父布局响应触摸事件了。

		}
	}
}
