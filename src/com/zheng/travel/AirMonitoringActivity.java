package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zheng.travel.domain.Index;
import com.zheng.travel.domain.Results;
import com.zheng.travel.domain.Status;
import com.zheng.travel.ui.TasksCompletedView;

public class AirMonitoringActivity extends Activity {

	private int ImageResource[] = { R.drawable.ic_weather_temperature,
			R.drawable.ic_weather_temperature,
			R.drawable.ic_weather_temperature, R.drawable.ic_weather_dress,
			R.drawable.ic_weather_movement, R.drawable.ic_weather_uv };
	private String strResource[] = { "体感指数", "洗车指数", "旅游指数", "感冒指数", "运动指数",
			"防晒指数" };
	private int ImageViewResource[] = { R.color.index_view_1,
			R.color.index_view_1, R.color.index_view_1, R.color.index_view_2,
			R.color.index_view_4, R.color.index_view_3, };

	private DemoApplication demoApplication;
	private Status status;
	private View include[] = new View[4];
	private ImageView iv_index[] = new ImageView[4];
	private TextView tv_msg[] = new TextView[4];
	private TextView tv_data[] = new TextView[4];
	private TextView tv_TodayMsgDetails;
	private TextView tv_TodayMsgCity;
	private TasksCompletedView mTasksCompletedView;
	private AlertDialog dialog;

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
		case R.id.back:
			finish();
			break;
		}
	}

	/*******************************************************
	 * 显示设置参数对话框
	 ******************************************************/
	public void showIndexDialog(int i) {
		dialog = null;
		AlertDialog.Builder builder = new Builder(AirMonitoringActivity.this);
		View view = View.inflate(AirMonitoringActivity.this,
				R.layout.index_dialog, null);

		ImageView iv_index = (ImageView) view.findViewById(R.id.iv_index);
		View iv_view = (View) view.findViewById(R.id.iv_view);
		TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);

		Typeface typeFace = Typeface.createFromAsset(getAssets(),
				"EPSONhangshuti.ttf");
		// 应用字体
		tv_data.setTypeface(typeFace);

		List<Results> results = status.getResults();
		List<Index> index = results.get(0).getIndex();

		iv_index.setImageResource(ImageResource[i]);
		iv_view.setBackgroundResource(ImageViewResource[i]);
		tv_msg.setText(strResource[i] + " : " + index.get(i).getZs());
		tv_data.setText(index.get(i).getDes());

		dialog = builder.create();
		// dialog.setCancelable(false);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
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
		tv_TodayMsgDetails.setText(status.getDate());

		include[0] = findViewById(R.id.include_1);
		include[0].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showIndexDialog(0);
			}
		});
		iv_index[0] = (ImageView) include[0].findViewById(R.id.iv_index);
		tv_msg[0] = (TextView) include[0].findViewById(R.id.tv_msg);
		tv_data[0] = (TextView) include[0].findViewById(R.id.tv_data);
		tv_data[0].setText(index.get(0).getZs());

		include[1] = findViewById(R.id.include_2);
		include[1].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showIndexDialog(3);
			}
		});
		iv_index[1] = (ImageView) include[1].findViewById(R.id.iv_index);
		tv_msg[1] = (TextView) include[1].findViewById(R.id.tv_msg);
		tv_data[1] = (TextView) include[1].findViewById(R.id.tv_data);
		iv_index[1].setImageResource(R.drawable.ic_weather_dress);
		tv_msg[1].setText("感冒指数");
		tv_data[1].setText(index.get(3).getZs());

		include[2] = findViewById(R.id.include_3);
		include[2].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showIndexDialog(5);
			}
		});
		iv_index[2] = (ImageView) include[2].findViewById(R.id.iv_index);
		tv_msg[2] = (TextView) include[2].findViewById(R.id.tv_msg);
		tv_data[2] = (TextView) include[2].findViewById(R.id.tv_data);
		iv_index[2].setImageResource(R.drawable.ic_weather_uv);
		tv_msg[2].setText("防晒指数");
		tv_data[2].setText(index.get(5).getZs());

		include[3] = findViewById(R.id.include_4);
		include[3].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showIndexDialog(4);
			}
		});
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
			double mProgressForI;
			for (double i = 0; i <= mProgress; i++) {
				mProgressForI = (i / 500) * 100;
				mTasksCompletedView.setProgressNew((int) mProgressForI, data);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
