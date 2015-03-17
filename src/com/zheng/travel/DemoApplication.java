package com.zheng.travel;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class DemoApplication extends Application {

	/********************************************
	 * 公交路线全局变量
	 *********************************************/
	public TransitRouteResult Tresult;

	public TransitRouteResult getTresult() {
		return Tresult;
	}

	public void setTresult(TransitRouteResult tresult) {
		Tresult = tresult;
	}

	/********************************************
	 * 自驾路线全局变量
	 *********************************************/
	public DrivingRouteResult Dresult;

	public DrivingRouteResult getDresult() {
		return Dresult;
	}

	public void setDresult(DrivingRouteResult dresult) {
		Dresult = dresult;
	}

	/********************************************
	 * 步行路线全局变量
	 *********************************************/
	public WalkingRouteResult Wresult;

	public WalkingRouteResult getWresult() {
		return Wresult;
	}

	public void setWresult(WalkingRouteResult wresult) {
		Wresult = wresult;
	}

	/********************************************
	 * onCreate
	 *********************************************/
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(this);
	}

}