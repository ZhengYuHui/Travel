package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zheng.travel.domain.Index;
import com.zheng.travel.domain.Results;
import com.zheng.travel.domain.Status;
import com.zheng.travel.ui.TasksCompletedView;

public class AirMonitoringActivity extends Activity {

	private DemoApplication demoApplication;
	private Status status;
	private View include[] = new View[4];
	private ImageView iv_index[] = new ImageView[4];
	private TextView tv_msg[] = new TextView[4];
	private TextView tv_data[] = new TextView[4];
	private TextView tv_TodayMsgDetails;
	private TextView tv_TodayMsgCity;
	private TasksCompletedView mTasksCompletedView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_monitoring);
		initView();
	}

	/****************************************************
	 * 底部控件点击事件
	 *****************************************************/
	public void ImageViewOnClick(View v) {
		switch (v.getId()) {
		case R.id.include_1:

			break;
		case R.id.include_2:

			break;
		case R.id.include_3:

			break;
		case R.id.include_4:

			break;
		case R.id.back:
			finish();
			break;
		}
	}

	/****************************************************
	 * 初始化控件
	 *****************************************************/
	protected void initView() {

		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();
		status = demoApplication.getStatus();
		List<Results> results = status.getResults();
		List<Index> index = results.get(0).getIndex();

		tv_TodayMsgCity = (TextView) findViewById(R.id.tv_TodayMsgCity);
		tv_TodayMsgDetails = (TextView) findViewById(R.id.tv_TodayMsgDetails);
		tv_TodayMsgCity.setText(results.get(0).getCurrentCity());

		include[0] = findViewById(R.id.include_1);
		iv_index[0] = (ImageView) include[0].findViewById(R.id.iv_index);
		tv_msg[0] = (TextView) include[0].findViewById(R.id.tv_msg);
		tv_data[0] = (TextView) include[0].findViewById(R.id.tv_data);
		tv_data[0].setText(index.get(0).getZs());

		include[1] = findViewById(R.id.include_2);
		iv_index[1] = (ImageView) include[1].findViewById(R.id.iv_index);
		tv_msg[1] = (TextView) include[1].findViewById(R.id.tv_msg);
		tv_data[1] = (TextView) include[1].findViewById(R.id.tv_data);
		iv_index[1].setImageResource(R.drawable.ic_weather_dress);
		tv_msg[1].setText("感冒指数");
		tv_data[1].setText(index.get(3).getZs());

		include[2] = findViewById(R.id.include_3);
		iv_index[2] = (ImageView) include[2].findViewById(R.id.iv_index);
		tv_msg[2] = (TextView) include[2].findViewById(R.id.tv_msg);
		tv_data[2] = (TextView) include[2].findViewById(R.id.tv_data);
		iv_index[2].setImageResource(R.drawable.ic_weather_uv);
		tv_msg[2].setText("防晒指数");
		tv_data[2].setText(index.get(5).getZs());

		include[3] = findViewById(R.id.include_4);
		iv_index[3] = (ImageView) include[3].findViewById(R.id.iv_index);
		tv_msg[3] = (TextView) include[3].findViewById(R.id.tv_msg);
		tv_data[3] = (TextView) include[3].findViewById(R.id.tv_data);
		iv_index[3].setImageResource(R.drawable.ic_weather_movement);
		tv_msg[3].setText("运动指数");
		tv_data[3].setText(index.get(4).getZs());

		mTasksCompletedView = (TasksCompletedView) findViewById(R.id.tasks_view1);
		new Thread(new JKProgressRunable(results.get(0).getPm25())).start();
	}

	/*******************************************************
	 * 设置进度条的进度以及数据
	 ******************************************************/
	private class JKProgressRunable implements Runnable {

		private String data;
		public JKProgressRunable(String data1) {
			data = data1;
		}

		@Override
		public void run() {
			double mProgress = Double.parseDouble(data);
			mProgress = (mProgress / 500) * 100;
			mTasksCompletedView
					.setProgressAndProgressNum((int) mProgress, data);
		}
	}
}
