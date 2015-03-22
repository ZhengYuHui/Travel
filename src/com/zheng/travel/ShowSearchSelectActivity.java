package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class ShowSearchSelectActivity extends Activity {

	private String searchKey;
	private Boolean isSearch = true;
	private EditText et_SearchNearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_search_select);
		et_SearchNearby = (EditText) findViewById(R.id.et_SearchNearby);
	}

	public void SearchNearby(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			isSearch = false;
			finish();
			break;
		case R.id.bt_SearchNearby:
			searchKey = et_SearchNearby.getText().toString();
			break;
		case R.id.iv_indoor_am1:
			searchKey = "公交站";
			break;
		case R.id.iv_indoor_am2:
			searchKey = "美食";
			break;
		case R.id.iv_indoor_am3:
			searchKey = "酒店";
			break;
		case R.id.iv_indoor_am4:
			searchKey = "电影院";
			break;
		case R.id.iv_indoor_am5:
			searchKey = "医院";
			break;
		case R.id.iv_indoor_am6:
			searchKey = "购物";
			break;
		case R.id.iv_indoor_am7:
			searchKey = "附近";
			break;
		case R.id.iv_indoor_am8:
			// ----------------更多
			break;
		case R.id.bt_11:
			searchKey = "钟点房";
			break;
		case R.id.bt_12:
			searchKey = "宾馆";
			break;
		case R.id.bt_13:
			searchKey = "面包房";
			break;
		case R.id.bt_21:
			searchKey = "ATM";
			break;
		case R.id.bt_22:
			searchKey = "商场";
			break;
		case R.id.bt_23:
			searchKey = "快递";
			break;
		case R.id.bt_31:
			searchKey = "酒吧";
			break;
		case R.id.bt_32:
			searchKey = "洗浴";
			break;
		case R.id.bt_33:
			searchKey = "运动场";
			break;
		case R.id.bt_41:
			searchKey = "提车场";
			break;
		case R.id.bt_42:
			searchKey = "景点";
			break;
		case R.id.bt_43:
			searchKey = "公交站";
			break;
		case R.id.bt_51:
			searchKey = "停车场";
			break;
		case R.id.bt_52:
			searchKey = "宾馆";
			break;
		case R.id.bt_53:
			searchKey = "地铁站";
			break;
		}
		if (isSearch && (!searchKey.trim().isEmpty())) {
			Toast.makeText(getApplicationContext(), searchKey, 0).show();
			Intent intentNearby = new Intent(ShowSearchSelectActivity.this,
					PoiSearchActivity.class);
			intentNearby.putExtra("searchKey", searchKey);
			startActivity(intentNearby);
		}
	}
}
