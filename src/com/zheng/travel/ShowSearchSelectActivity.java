package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class ShowSearchSelectActivity extends Activity {

	private String searchKey;
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
			finish();
			break;
		case R.id.bt_SearchNearby:
			searchKey = et_SearchNearby.getText().toString();
			search();
			break;
		case R.id.iv_indoor_am1:
			searchKey = "公交站";
			search();
			break;
		case R.id.iv_indoor_am2:
			searchKey = "美食";
			search();
			break;
		case R.id.iv_indoor_am3:
			searchKey = "酒店";
			search();
			break;
		case R.id.iv_indoor_am4:
			searchKey = "电影院";
			search();
			break;
		case R.id.iv_indoor_am5:
			searchKey = "医院";
			search();
			break;
		case R.id.iv_indoor_am6:
			searchKey = "购物";
			search();
			break;
		case R.id.iv_indoor_am7:
			searchKey = "附近";
			search();
			break;
		case R.id.iv_indoor_am8:
			// ----------------更多
			startActivity(new Intent(ShowSearchSelectActivity.this,
					ShowMoreSelectActivity.class));
			break;
		case R.id.bt_11:
			searchKey = "钟点房";
			search();
			break;
		case R.id.bt_12:
			searchKey = "宾馆";
			search();
			break;
		case R.id.bt_13:
			searchKey = "面包房";
			search();
			break;
		case R.id.bt_21:
			searchKey = "ATM";
			search();
			break;
		case R.id.bt_22:
			searchKey = "商场";
			search();
			break;
		case R.id.bt_23:
			searchKey = "快递";
			search();
			break;
		case R.id.bt_31:
			searchKey = "酒吧";
			search();
			break;
		case R.id.bt_32:
			searchKey = "洗浴";
			search();
			break;
		case R.id.bt_33:
			searchKey = "运动场";
			search();
			break;
		case R.id.bt_41:
			searchKey = "提车场";
			search();
			break;
		case R.id.bt_42:
			searchKey = "景点";
			search();
			break;
		case R.id.bt_43:
			searchKey = "公交站";
			search();
			break;
		case R.id.bt_51:
			searchKey = "停车场";
			search();
			break;
		case R.id.bt_52:
			searchKey = "宾馆";
			search();
			break;
		case R.id.bt_53:
			searchKey = "地铁站";
			search();
			break;
		}

	}

	private void search() {
		if (!searchKey.trim().isEmpty()) {
			// Toast.makeText(getApplicationContext(), searchKey, 0).show();
			Intent intentNearby = new Intent(ShowSearchSelectActivity.this,
					PoiSearchActivity.class);
			intentNearby.putExtra("searchKey", searchKey);
			startActivity(intentNearby);
		}
	}
}
