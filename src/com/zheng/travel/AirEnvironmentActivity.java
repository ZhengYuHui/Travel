package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zheng.travel.domain.Index;
import com.zheng.travel.domain.Results;
import com.zheng.travel.domain.Status;
import com.zheng.travel.domain.WeatherData;
import com.zheng.travel.utils.NetUtils;

public class AirEnvironmentActivity extends Activity {

	private int[] weatherColor = { R.color.weather_sun, R.color.weather_cloudy,
			R.color.weather_rain };

	private DemoApplication demoApplication;
	private RelativeLayout RL_TodayMsg;
	private TextView tv_TodayMsgCity;
	private TextView tv_TodayMsgTemp;
	private TextView tv_TodayMsgDetails;

	private String show_city;

	private View include_1;
	private ImageView iv_weather_1;
	private TextView tv_weekDay_1;
	private TextView tv_temperature_1;
	private View include_2;
	private ImageView iv_weather_2;
	private TextView tv_weekDay_2;
	private TextView tv_temperature_2;
	private View include_3;
	private ImageView iv_weather_3;
	private TextView tv_weekDay_3;
	private TextView tv_temperature_3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_environment);
		initView();
		initData("广州");
	}

	/****************************************************
	 * 底部控件点击事件
	 *****************************************************/
	public void ImageViewOnClick(View v) {
		switch (v.getId()) {
		case R.id.monitor:
			startActivity(new Intent(AirEnvironmentActivity.this,
					AirMonitoringActivity.class));
			break;
		case R.id.back:
			finish();
			break;
		case R.id.add:

			break;
		}
	}

	/****************************************************
	 * 初始化控件
	 *****************************************************/
	protected void initView() {
		// 获取全局变量类
		demoApplication = (DemoApplication) getApplication();

		RL_TodayMsg = (RelativeLayout) findViewById(R.id.RL_TodayMsg);
		tv_TodayMsgCity = (TextView) findViewById(R.id.tv_TodayMsgCity);
		tv_TodayMsgTemp = (TextView) findViewById(R.id.tv_TodayMsgTemp);
		tv_TodayMsgDetails = (TextView) findViewById(R.id.tv_TodayMsgDetails);

		include_1 = findViewById(R.id.include_1);
		iv_weather_1 = (ImageView) include_1.findViewById(R.id.iv_weather);
		tv_weekDay_1 = (TextView) include_1.findViewById(R.id.tv_weekDay);
		tv_temperature_1 = (TextView) include_1
				.findViewById(R.id.tv_temperature);

		include_2 = findViewById(R.id.include_2);
		iv_weather_2 = (ImageView) include_2.findViewById(R.id.iv_weather);
		tv_weekDay_2 = (TextView) include_2.findViewById(R.id.tv_weekDay);
		tv_temperature_2 = (TextView) include_2
				.findViewById(R.id.tv_temperature);

		include_3 = findViewById(R.id.include_3);
		iv_weather_3 = (ImageView) include_3.findViewById(R.id.iv_weather);
		tv_weekDay_3 = (TextView) include_3.findViewById(R.id.tv_weekDay);
		tv_temperature_3 = (TextView) include_3
				.findViewById(R.id.tv_temperature);
	}

	/****************************************************
	 * 获取数据
	 *****************************************************/
	protected void initData(final String strCity) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				// String strCity = "广州";
				// 使用get方式抓去数据
				final String state = NetUtils.GetWeatherJson(strCity);
				Gson gson = new Gson();
				final Status status = gson.fromJson(state, Status.class);
				// 执行任务在主线程中
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						List<Results> results = status.getResults();
						if (results == null) {
							return;
						}
						demoApplication.setStatus(status);

						List<Index> index = results.get(0).getIndex();
						List<WeatherData> weatherData = results.get(0)
								.getWeather_data();
						updateView(results, weatherData);
						systemOut(status);
					}
				});
			}
		}).start();
	}

	/****************************************************
	 * 更新view显示
	 *****************************************************/
	protected void updateView(List<Results> results,
			List<WeatherData> weatherData) {
		// 今天
		tv_TodayMsgCity.setText(results.get(0).getCurrentCity());
		String strTemp1 = weatherData.get(0).getTemperature().substring(0, 2);
		String strTemp2 = weatherData.get(0).getTemperature().substring(5, 7);
		int intTemp1 = Integer.parseInt(strTemp1);
		int intTemp2 = Integer.parseInt(strTemp2);
		int intTemp = (intTemp1 + intTemp2) / 2;
		tv_TodayMsgTemp.setText(intTemp + "℃");
		tv_TodayMsgDetails.setText(weatherData.get(0).getWeather() + " "
				+ weatherData.get(0).getWind());
		selectRLTodayMsgBackground(weatherData.get(0).getWeather());
		// 明天
		tv_weekDay_1.setText(weatherData.get(1).getDate());
		tv_temperature_1.setText(weatherData.get(1).getTemperature());
		selectWeatherPicture(iv_weather_1, weatherData.get(1).getWeather());
		// 后天
		tv_weekDay_2.setText(weatherData.get(2).getDate());
		tv_temperature_2.setText(weatherData.get(2).getTemperature());
		selectWeatherPicture(iv_weather_2, weatherData.get(2).getWeather());
		// 大后天
		tv_weekDay_3.setText(weatherData.get(3).getDate());
		tv_temperature_3.setText(weatherData.get(3).getTemperature());
		selectWeatherPicture(iv_weather_3, weatherData.get(3).getWeather());
	}

	/****************************************************
	 * 设置今天天气背景
	 *****************************************************/
	protected void selectRLTodayMsgBackground(String msg) {
		if (msg.equals("晴")) {
			RL_TodayMsg.setBackgroundColor(weatherColor[0]);
		} else if (msg.equals("多云") | msg.equals("多云转阴") | msg.equals("阴转多云")) {
			RL_TodayMsg.setBackgroundColor(weatherColor[1]);
		} else if (msg.equals("阴")) {
			RL_TodayMsg.setBackgroundColor(weatherColor[2]);
		} else if (msg.equals("阵雨") | msg.equals("小雨") | msg.equals("中雨")
				| msg.equals("大雨") | msg.equals("暴雨") | msg.equals("大暴雨")
				| msg.equals("特大暴雨") | msg.equals("小雨转中雨")
				| msg.equals("中雨转大雨") | msg.equals("大雨转暴雨")
				| msg.equals("暴雨转大暴雨") | msg.equals("大暴雨转特大暴雨")) {
			RL_TodayMsg.setBackgroundColor(weatherColor[2]);
		} else if (msg.equals("雷阵雨")) {
			RL_TodayMsg.setBackgroundColor(weatherColor[2]);
		}
	}

	/****************************************************
	 * 设置天气图片
	 *****************************************************/
	protected void selectWeatherPicture(ImageView iv_weather, String msg) {
		if (msg.equals("晴")) {
			iv_weather.setImageResource(R.drawable.weather_icon_sun);
		} else if (msg.equals("多云") | msg.equals("多云转阴") | msg.equals("阴转多云")) {
			iv_weather.setImageResource(R.drawable.weather_icon_cloud);
		} else if (msg.equals("阴")) {
			iv_weather.setImageResource(R.drawable.weather_icon_clouded);
		} else if (msg.equals("阵雨") | msg.equals("小雨") | msg.equals("中雨")
				| msg.equals("大雨") | msg.equals("暴雨") | msg.equals("大暴雨")
				| msg.equals("特大暴雨") | msg.equals("小雨转中雨")
				| msg.equals("中雨转大雨") | msg.equals("大雨转暴雨")
				| msg.equals("暴雨转大暴雨") | msg.equals("大暴雨转特大暴雨")) {
			iv_weather.setImageResource(R.drawable.weather_icon_rain);
		} else if (msg.equals("雷阵雨")) {
			iv_weather
					.setImageResource(R.drawable.weather_icon_rain_thunder_shower);
		}
	}

	/****************************************************
	 * 打印数据到控制台
	 *****************************************************/
	protected void systemOut(Status status) {
		// ********************* Status ********************/
		System.out.println("error:" + status.getError());
		System.out.println("status:" + status.getStatus());
		System.out.println("date:" + status.getDate());
		// ********************* Results ********************/
		List<Results> results = status.getResults();
		if (results == null) {
			return;
		}
		System.out.println("currentCity:" + results.get(0).getCurrentCity());
		System.out.println("pm25:" + results.get(0).getPm25());
		// ********************* Index ********************/
		List<Index> index = results.get(0).getIndex();
		for (int i = 0; i < index.size(); i++) {
			System.out.println("title:" + index.get(i).getTitle());
			System.out.println("zs:" + index.get(i).getZs());
			System.out.println("tipt:" + index.get(i).getTipt());
			System.out.println("des:" + index.get(i).getDes());
		}
		// ********************* Results ********************/
		List<WeatherData> weatherData = results.get(0).getWeather_data();
		for (int i = 0; i < weatherData.size(); i++) {
			System.out.println("date:" + weatherData.get(i).getDate());
			System.out.println("dayPictureUrl:"
					+ weatherData.get(i).getDayPictureUrl());
			System.out.println("nightPictureUrl:"
					+ weatherData.get(i).getNightPictureUrl());
			System.out.println("weather:" + weatherData.get(i).getWeather());
			System.out.println("wind:" + weatherData.get(i).getWind());
			System.out.println("temperature:"
					+ weatherData.get(i).getTemperature());
		}

	}

}
