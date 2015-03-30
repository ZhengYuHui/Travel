package com.zheng.travel;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.zheng.travel.adapter.SearchNearbyListAdapter;
import com.zheng.travel.utils.MyLog;

public class PoiSearchActivity extends FragmentActivity implements
		OnGetPoiSearchResultListener {

	private Context context;
	private String TAG = "PoiSearchActivity";
	private DemoApplication demoApplication;
	private PoiSearch mPoiSearch = null;// POI检索接口
	private int load_Index = 0;// POI检索结果分页编号
	private int TotalPageNum;// 获取总分页数
	private int Range = 5000;
	private double latitude;// 纬度
	private double longitude;// 经度
	private String searchKey;// 搜索关键字
	private ProgressDialog pd;
	private ListView lv_showSearch;
	private SearchNearbyListAdapter searchNearbyListAdapter;
	private List<PoiInfo> poiInfo = null;
	private TextView tv_showSearchMeg;
	private TextView tv_error;
	private TextView tv_range;
	// 地图显示相关
	private BaiduMap mBaiduMap = null;
	// true->list;false->map
	private boolean showMode = true;
	private LinearLayout LL_map;
	private RelativeLayout RL_list;
	private TextView tv_address_in;
	private TextView tv_name_in;
	private TextView tv_itemNum_in;
	private AlertDialog myDialog;
	private int myIndex;// 当前地图标注编号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_poi_search);
		context = this;
		getResultRoutes();
		initView();
		searchPoi(searchKey, Range);
	}

	/***************************************************************
	 * 初始化控件
	 *************************************************************/
	protected void initView() {
		// 初始地图模式相关控件
		mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.LL_fragment_map))).getBaiduMap();
		LL_map = (LinearLayout) findViewById(R.id.LL_map);
		RL_list = (RelativeLayout) findViewById(R.id.RL_list);
		tv_address_in = (TextView) findViewById(R.id.tv_address_in);
		tv_name_in = (TextView) findViewById(R.id.tv_name_in);
		tv_itemNum_in = (TextView) findViewById(R.id.tv_itemNum_in);
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		tv_range = (TextView) findViewById(R.id.tv_range);
		tv_error = (TextView) findViewById(R.id.tv_error);
		tv_showSearchMeg = (TextView) findViewById(R.id.tv_showSearchMeg);
		tv_showSearchMeg.setText("附近" + searchKey);
		lv_showSearch = (ListView) findViewById(R.id.lv_showSearch);
		searchNearbyListAdapter = new SearchNearbyListAdapter(
				PoiSearchActivity.this, poiInfo);
		lv_showSearch.setAdapter(searchNearbyListAdapter);
		lv_showSearch.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intentNearby = new Intent(PoiSearchActivity.this,
						ShowPoiDetailActivity.class);
				intentNearby.putExtra("poiPosition", position);
				startActivity(intentNearby);
			}
		});
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
	 * 顶部点击事件
	 *************************************************************/
	public void topOnClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:// 返回
			finish();
			break;
		case R.id.tv_map:// 显示模式转换
			changeShowMode();
			break;
		case R.id.iv_map:// 显示模式转换
			changeShowMode();
			break;
		case R.id.tv_1_in:// 地图显示模式底部点击事件（路线）
			Intent intentNearby2 = new Intent(context,
					RouteResultsActivity.class);
			intentNearby2.setFlags(1);
			intentNearby2.putExtra("POIname", poiInfo.get(myIndex).name);
			intentNearby2.putExtra("POIlatitude",
					poiInfo.get(myIndex).location.latitude);
			intentNearby2.putExtra("POIlongitude",
					poiInfo.get(myIndex).location.longitude);
			context.startActivity(intentNearby2);
			break;
		case R.id.tv_2_in:// 地图显示模式底部点击事件（电话）
			if (poiInfo.get(myIndex).phoneNum.trim().equals("")) {
				Toast.makeText(context, "无联系电话", Toast.LENGTH_SHORT).show();
			} else {
				showIndexDialog(myIndex);
			}
			break;
		case R.id.tv_3_in:// 地图显示模式底部点击事件（详情）
			Intent intentNearby = new Intent(context,
					ShowPoiDetailActivity.class);
			intentNearby.putExtra("poiPosition", myIndex);
			startActivity(intentNearby);
			break;
		case R.id.tv_range:// 选择范围
			showRange(Range);
			break;
		case R.id.tv_previous_page:// 上一页
			if (load_Index > 0) {
				load_Index--;
				searchPoi(searchKey, Range);
			} else {
				Toast.makeText(this, "已无上一页", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_nest_page:// 下一页
			if (load_Index < TotalPageNum) {
				load_Index++;
				searchPoi(searchKey, Range);
			} else {
				Toast.makeText(this, "已无下一页", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	/***************************************************************
	 * 改变显示模式（列表/地图）
	 *************************************************************/
	protected void changeShowMode() {
		showMode = !showMode;
		if (showMode) {
			RL_list.setVisibility(View.VISIBLE);
			LL_map.setVisibility(View.GONE);
		} else {
			LL_map.setVisibility(View.VISIBLE);
			RL_list.setVisibility(View.GONE);
		}
	}

	/***************************************************************
	 * 单选对话框
	 *************************************************************/
	public void showRange(int myRange) {
		// 对话框的创建器
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("请选择搜索范围");
		final String[] items = { "5000米", "1公里", "2公里", "5公里" };
		int checkedItem = 0;
		switch (myRange) {
		case 5000:
			checkedItem = 0;
			break;
		case 10000:
			checkedItem = 1;
			break;
		case 20000:
			checkedItem = 2;
			break;
		case 50000:
			checkedItem = 3;
			break;
		}
		final int[] itemRange = { 5000, 10000, 20000, 50000 };
		builder.setSingleChoiceItems(items, checkedItem, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				tv_range.setText(items[which]);
				Range = itemRange[which];
				searchPoi(searchKey, Range);
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	/***************************************************************
	 * 搜索Poi
	 *************************************************************/
	protected void searchPoi(String key, int range) {
		pd = new ProgressDialog(PoiSearchActivity.this);
		pd.setMessage("努力查询中...请稍等");
		pd.show();

		LatLng latLng = new LatLng(latitude, longitude);
		mPoiSearch.searchNearby((new PoiNearbySearchOption()).location(latLng)
				.keyword(key).pageNum(load_Index).radius(range));
	}

	/***************************************************************
	 * poi 详情查询结果回调
	 *************************************************************/
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	/***************************************************************
	 * poi 查询结果回调
	 *************************************************************/
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (pd.isShowing()) {
			pd.dismiss();
		}
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			tv_error.setVisibility(View.VISIBLE);
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			myLogPrint(result);
			TotalPageNum = result.getTotalPageNum();
			poiInfo = result.getAllPoi();// 获取Poi检索结果
			// 更新列表
			searchNearbyListAdapter.updateListView(poiInfo);
			demoApplication.setPoiInfo(poiInfo);
			// 在地图上标注各个poi
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			// 地图下方的POI详情设置
			myIndex = 0;
			tv_itemNum_in.setText(String.valueOf(myIndex + 1));
			tv_name_in.setText(poiInfo.get(myIndex).name);
			tv_address_in.setText(poiInfo.get(myIndex).address);
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
	 * 地图中标注的各个Poi点击事件
	 *************************************************************/
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			myIndex = index;
			tv_itemNum_in.setText(String.valueOf(index + 1));
			tv_name_in.setText(poiInfo.get(index).name);
			tv_address_in.setText(poiInfo.get(index).address);
			return true;
		}
	}

	/*******************************************************
	 * 显示拨打电话提示对话框
	 ******************************************************/
	public void showIndexDialog(final int myPosition) {
		myDialog = null;
		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.call_phone_dialog, null);

		TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		tv_msg.setText("拨打：" + poiInfo.get(myPosition).phoneNum + " ?");
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_data.setText(poiInfo.get(myPosition).name);

		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
		Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
		bt_sure.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 指定其动作为拨打电话
				intent.setAction(Intent.ACTION_CALL);
				// 指定将要拨出的号码
				intent.setData(Uri.parse("tel:"
						+ poiInfo.get(myPosition).phoneNum));
				context.startActivity(intent);
				myDialog.dismiss();
			}
		});
		myDialog = builder.create();
		myDialog.setView(view, 0, 0, 0, 0);
		myDialog.show();
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
		// List<CityInfo> cityInfo = result.getSuggestCityList();// 返回城市列表页的结果数

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
