package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.zheng.travel.adapter.RLStepListViewAdapter;
import com.zheng.travel.adapter.RouteListAdapter;
import com.zheng.travel.utils.MyLog;

public class ShowRouteStepActivity extends Activity {

	private String TAG = "ShowRouteStepActivity";
	private String str_editSt;
	private String str_editEn;
	private String rSRStep[];
	private String NewrSRStep[];
	private int selectType;

	private TextView tv_meg;
	private ListView lv_route_step;
	private RLStepListViewAdapter rLStepListViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_route_step);
		initView();
		getResultRoutes();
	}

	/**********************************************************************************
	 * 初始化控件
	 ********************************************************************************/
	protected void initView() {
		// findViewById
		tv_meg = (TextView) findViewById(R.id.tv_meg);
		// ListView
		lv_route_step = (ListView) findViewById(R.id.lv_route_step);
		rLStepListViewAdapter = new RLStepListViewAdapter(this, new String[0],
				2);
		lv_route_step.setAdapter(rLStepListViewAdapter);// ListView关联Adapter
		// lv_route_results.setOnItemClickListener(new OnListViewItemClick());
	}

	/**********************************************************************************
	 * 获取RoutePlanActivity传递过来的ResultRoutes，并转换成相应的路线类型
	 ********************************************************************************/
	protected void getResultRoutes() {
		Intent intent = getIntent();
		str_editSt = intent.getStringExtra("str_editSt");
		str_editEn = intent.getStringExtra("str_editEn");
		selectType = intent.getIntExtra("selectType", 2);
		int position = intent.getIntExtra("position", 1);
		switch (selectType) {
		case 1:
			tv_meg.setText("自驾路线" + position);
			break;
		case 2:
			tv_meg.setText("公交路线" + position);
			break;
		case 3:
			tv_meg.setText("步行路线" + position);
			break;
		}

		// tv_start.setText(str_editSt);
		// tv_end.setText(str_editEn);

		rSRStep = intent.getStringArrayExtra("rSRStep");
		NewrSRStep = new String[rSRStep.length + 2];
		NewrSRStep[0] = str_editSt;
		NewrSRStep[NewrSRStep.length - 1] = str_editEn;
		for (int i = 0, y = 1; i < rSRStep.length; i++, y++) {
			NewrSRStep[y] = rSRStep[i];
		}
		rLStepListViewAdapter.updateListView(NewrSRStep, selectType);
	}

	/***********************************************************************************
	 * 顶部按键监听事件
	 **********************************************************************************/
	public void SearchButtonProcess(View v) {
		// 实际使用中请对起点终点城市进行正确的设定
		if (v.getId() == R.id.bt_goMap) {// 显示地图

		} else if (v.getId() == R.id.back) {// 返回
			finish();
		}
	}
}
