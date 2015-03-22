package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class ShowMoreSelectActivity extends Activity {

	private String searchKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_more_select);
	}

	private void search() {
		if (!searchKey.trim().isEmpty()) {
			Intent intentNearby = new Intent(ShowMoreSelectActivity.this,
					PoiSearchActivity.class);
			intentNearby.putExtra("searchKey", searchKey);
			startActivity(intentNearby);
		}
	}

	public void SearchNearby(View v) {
		switch (v.getId()) {
		case 1:

			break;

		}
	}

}
