package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

	private TextView tv_start;
	private TextView tv_end;
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
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		// ListView
		lv_route_step = (ListView) findViewById(R.id.lv_route_step);
		rLStepListViewAdapter = new RLStepListViewAdapter(this, new String[0]);
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
		tv_start.setText(str_editSt);
		tv_end.setText(str_editEn);

		rSRStep = intent.getStringArrayExtra("rSRStep");
		for (int i = 0; i < rSRStep.length; i++) {
			MyLog.printLi(TAG, rSRStep[i]);
		}
		rLStepListViewAdapter.updateListView(rSRStep);
	}
}
