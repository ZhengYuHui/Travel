package com.zheng.travel.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.zheng.travel.R;

public class TasksCompletedView extends View {

	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画圆环背景的画笔
	private Paint mStrokePaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 画下标文体的画笔
	private Paint mTextFlagPaint;
	// 画数据字体的画笔
	private Paint mDataPaint;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 圆环背景颜色
	private int mStrokeColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;

	// 字的大小
	private float mTxtSize;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 字的颜色
	private int mTxtColor;

	// 数据字体的大小
	private float mDataSize;
	// 数据字体的长度
	private float mDataWidth;
	// 数据字体的高度
	private float mDataHeight;
	// 数据字体的颜色
	private int mDataColor;
	// 数据内容
	private String mDataName;

	// 下标文字的大小
	private float mTxtFlagSize;
	// 下标文字的颜色
	private int mTxtFlagColor;
	// 下标的名字
	private String mTxtFlagName;
	// 下标的长度
	private float mTxtFlagWidth;
	// 下标的高度
	private float mTxtFlagHeight;

	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	// 符号
	private Paint mSymbolPaint;
	// 符号大小
	private float mSymbolSize;
	private boolean mshowSymbol;
	private boolean mshowProgressNum;
	private String mProgressNum;

	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}

	// 获取自定义属性和默认值
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(
				R.styleable.TasksCompletedView_radiusC, 80);
		mStrokeWidth = typeArray.getDimension(
				R.styleable.TasksCompletedView_strokeWidthC, 10);
		mCircleColor = typeArray.getColor(
				R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		mRingColor = typeArray.getColor(
				R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);
		mStrokeColor = typeArray.getColor(
				R.styleable.TasksCompletedView_strokeColorC, 0xFFFFFFFF);
		mTxtColor = typeArray.getColor(
				R.styleable.TasksCompletedView_textColor, 0xFFFFFFFF);
		mTxtSize = typeArray.getDimension(
				R.styleable.TasksCompletedView_textSize, 35);
		mTxtFlagColor = typeArray.getColor(
				R.styleable.TasksCompletedView_textFlagColor, 0xFFFFFFFF);
		mTxtFlagSize = typeArray.getDimension(
				R.styleable.TasksCompletedView_textFlagSize, 20);
		mTxtFlagName = typeArray
				.getString(R.styleable.TasksCompletedView_textFlagName);

		mDataColor = typeArray.getColor(
				R.styleable.TasksCompletedView_dataColor, 0xFFFFFFFF);
		mDataSize = typeArray.getDimension(
				R.styleable.TasksCompletedView_dataSize, 20);
		mshowSymbol = typeArray.getBoolean(
				R.styleable.TasksCompletedView_showSymbol, true);
		mSymbolSize = typeArray.getDimension(
				R.styleable.TasksCompletedView_symbolSize, 20);
		mshowProgressNum = typeArray.getBoolean(
				R.styleable.TasksCompletedView_showProgressNum, true);
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	// 画笔设置
	private void initVariable() {
		// 外层圆环背景
		mStrokePaint = new Paint();
		mStrokePaint.setAntiAlias(true);
		mStrokePaint.setColor(mStrokeColor);
		mStrokePaint.setStyle(Paint.Style.STROKE);
		mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
		mStrokePaint.setStrokeWidth(mStrokeWidth);
		// 内层圆
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		// 外层圆环
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeCap(Paint.Cap.ROUND);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		// 字体
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		// mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setColor(mTxtColor);
		mTextPaint.setTextSize(mTxtSize);
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);

		// 符号
		mSymbolPaint = new Paint();
		mSymbolPaint.setAntiAlias(true);
		mSymbolPaint.setStyle(Paint.Style.FILL);
		mSymbolPaint.setColor(mTxtColor);
		mSymbolPaint.setTextSize(mSymbolSize);

		// 下标
		mTextFlagPaint = new Paint();
		mTextFlagPaint.setAntiAlias(true);
		mTextFlagPaint.setStyle(Paint.Style.FILL);
		mTextFlagPaint.setColor(mTxtFlagColor);
		mTextFlagPaint.setTextSize(mTxtFlagSize);
		FontMetrics fmFlag = mTextFlagPaint.getFontMetrics();
		mTxtFlagHeight = (int) Math.ceil(fmFlag.descent - fmFlag.ascent);

		// 数据
		mDataPaint = new Paint();
		mDataPaint.setAntiAlias(true);
		mDataPaint.setStyle(Paint.Style.FILL);
		mDataPaint.setColor(mDataColor);
		mDataPaint.setTextSize(mDataSize);
		FontMetrics fmData = mDataPaint.getFontMetrics();
		mDataHeight = (int) Math.ceil(fmData.descent - fmData.ascent);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;

		// 画下标
		mTxtFlagWidth = mTextFlagPaint.measureText(mTxtFlagName, 0,
				mTxtFlagName.length());
		canvas.drawText(mTxtFlagName, mXCenter - mTxtFlagWidth / 2, mYCenter
				+ mTxtFlagHeight / 4 + (float) (mRingRadius / 1.3),
				mTextFlagPaint);

		// 画数据
		if (mDataName != null) {
			mDataWidth = mDataPaint.measureText(mDataName, 0,
					mDataName.length());
			canvas.drawText(mDataName, mXCenter - mDataWidth / 2, mYCenter
					+ mDataHeight / 8 + (float) (mRingRadius / 2), mDataPaint);
		}
		// 画内层圆
		// canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);

		if (mProgress > 0) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			// 画外层环背景
			canvas.drawArc(oval, -225, 270, false, mStrokePaint);
			// 画外层环进度
			canvas.drawArc(oval, -225,
					((float) mProgress / mTotalProgress) * 270, false,
					mRingPaint);
			// 画字体
			String txt = "";
			if (mshowProgressNum) {
				txt = mProgress + "";
			} else {
				txt = mProgressNum + "";
			}
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter
					+ mTxtHeight / 4, mTextPaint);
			if (mshowSymbol) {
				// 画符号
				canvas.drawText("%", mXCenter - mTxtWidth / 2 + mTxtWidth,
						mYCenter - (float) (mTxtHeight / 4.3), mSymbolPaint);
			}
		}
	}

	public void setProgressAndData(int progress, String dataName) {
		mProgress = progress;
		mDataName = dataName;
		// 刷新Progress
		postInvalidate();
	}

	public void setProgressAndProgressNum(int progress, String ProgressNum) {
		mProgress = progress;
		mProgressNum = ProgressNum;
		// 刷新Progress
		postInvalidate();
	}

	public void setProgress(int progress) {
		mProgress = progress;
		// 刷新Progress
		postInvalidate();
	}

}
