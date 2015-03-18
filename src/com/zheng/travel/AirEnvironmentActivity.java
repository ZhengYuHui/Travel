package com.zheng.travel;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zheng.travel.domain.Index;
import com.zheng.travel.domain.Results;
import com.zheng.travel.domain.Status;
import com.zheng.travel.domain.WeatherData;
import com.zheng.travel.utils.NetUtils;

public class AirEnvironmentActivity extends Activity {

	private TextView tv_show;
	private EditText et_city;
	private String show_city;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_air_environment);
		tv_show = (TextView) findViewById(R.id.tv_show);
		et_city = (EditText) findViewById(R.id.et_city);

	}

	public void btOnClick(View v) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String strCity = et_city.getText().toString();
				// 使用get方式抓去数据
				final String state = NetUtils.GetWeatherJson(strCity);
				Gson gson = new Gson();
				final Status status = gson.fromJson(state, Status.class);
				// 执行任务在主线程中
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_show.setText(state);
						// ********************* Status ********************/
						System.out.println("error:" + status.getError());
						System.out.println("status:" + status.getStatus());
						System.out.println("date:" + status.getDate());
						// ********************* Results ********************/
						List<Results> results = status.getResults();
						if(results == null){
							return;
						}
						System.out.println("currentCity:" + results.get(0).getCurrentCity());
						System.out.println("pm25:" + results.get(0).getPm25());
						// ********************* Index ********************/
						List<Index> index = results.get(0).getIndex();
						for(int i = 0; i<index.size() ; i++){
							System.out.println("title:" + index.get(i).getTitle());
							System.out.println("zs:" + index.get(i).getZs());
							System.out.println("tipt:" + index.get(i).getTipt());
							System.out.println("des:" + index.get(i).getDes());
						}
						//********************* Results ********************/
						List<WeatherData> weatherData = results.get(0).getWeather_data();
						for(int i = 0; i<weatherData.size() ; i++){
							System.out.println("date:" + weatherData.get(i).getDate());
							System.out.println("dayPictureUrl:" + weatherData.get(i).getDayPictureUrl());
							System.out.println("nightPictureUrl:" + weatherData.get(i).getNightPictureUrl());
							System.out.println("weather:" + weatherData.get(i).getWeather());
							System.out.println("wind:" + weatherData.get(i).getWind());
							System.out.println("temperature:" + weatherData.get(i).getTemperature());
						}
					}
				});
			}
		}).start();
	}

}
