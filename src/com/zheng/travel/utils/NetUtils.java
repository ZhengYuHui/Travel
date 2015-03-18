package com.zheng.travel.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.util.Log;

public class NetUtils {

	private static final String TAG = "NetUtils";

	/****************************************************************
	 * 获取XML方式的天气数据
	 * 
	 * @param cityName
	 * @return String state
	 ****************************************************************/
	public static String GetWeatherXML(String cityName) {
		HttpURLConnection conn = null;
		try {

			String data = "http://api.map.baidu.com/telematics/v3/weather?location="
					+ URLEncoder.encode(cityName, "utf-8")
					+ "&output=xml&ak=F2kPH63tdXmbIMaarGd5phKf"
					+ "&mcode=F6:6F:2C:89:AA:99:7C:D9:02:88:B5:82:CA:7C:1F:14:B7:58:9B:47;com.zheng.travel";

			URL url = new URL(data);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET"); // get或者post必须得全大写
			conn.setConnectTimeout(10000); // 连接的超时时间
			conn.setReadTimeout(5000); // 读数据的超时时间

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				String state = getStringFromInputStream(is);
				return state;
			} else {
				Log.i(TAG, "访问失败: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect(); // 关闭连接
			}
		}
		return null;
	}

	/****************************************************************
	 * 获取json方式的天气数据
	 * 
	 * @param cityName
	 * @return String state
	 ****************************************************************/
	public static String GetWeatherJson(String cityName) {
		HttpURLConnection conn = null;
		try {

			String data = "http://api.map.baidu.com/telematics/v3/weather?location="
					+ URLEncoder.encode(cityName, "utf-8")
					+ "&output=json&ak=F2kPH63tdXmbIMaarGd5phKf"
					+ "&mcode=F6:6F:2C:89:AA:99:7C:D9:02:88:B5:82:CA:7C:1F:14:B7:58:9B:47;com.zheng.travel";

			URL url = new URL(data);

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET"); // get或者post必须得全大写
			conn.setConnectTimeout(10000); // 连接的超时时间
			conn.setReadTimeout(5000); // 读数据的超时时间

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				String state = getStringFromInputStream(is);
				return state;
			} else {
				Log.i(TAG, "访问失败: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect(); // 关闭连接
			}
		}
		return null;
	}

	/***************************************************************
	 * 根据流返回一个字符串信息
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 **************************************************************/
	private static String getStringFromInputStream(InputStream is)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;

		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		is.close();
		String html = baos.toString(); // 把流中的数据转换成字符串, 采用的编码是: utf-8
		// String html = new String(baos.toByteArray(), "GBK");
		baos.close();
		return html;
	}
}
