package com.zheng.travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

public class GuideActivity extends Activity {
	private RelativeLayout relativeLayout;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setAnimation();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				enter();// 进入主页面
			}
		}, 3000);
	}

	/*************************************************************
	 * 设置出现时的动画
	 ************************************************************/
	protected void setAnimation() {
		ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(900);
		LayoutAnimationController lac = new LayoutAnimationController(sa);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac.setDelay(0.5f);
		relativeLayout.setLayoutAnimation(lac);
	}

	/*************************************************************
	 * 进入主页面
	 ************************************************************/
	protected void enter() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish();// 关闭掉当前界面。
	}

}
