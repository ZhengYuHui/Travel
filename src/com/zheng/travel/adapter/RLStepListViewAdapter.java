package com.zheng.travel.adapter;

import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zheng.travel.R;
import com.zheng.travel.utils.MyLog;

public class RLStepListViewAdapter extends BaseAdapter {

	private static final String TAG = "RLStepListViewAdapter";
	private Context context;
	private String rSR[];
	private int selectType;

	public RLStepListViewAdapter(Context context, String rSR[], int selectType) {
		this.context = context;
		this.rSR = rSR;
		this.selectType = selectType;
	}

	/****************************************************
	 * 定义ListView的数据的长度
	 *****************************************************/
	@Override
	public int getCount() {
		return rSR.length;
	}

	/****************************************************
	* 
	*****************************************************/
	@Override
	public Object getItem(int position) {
		return null;
	}

	/****************************************************
	 * 根据ListView的位置得到数据源集合中的ID
	 *****************************************************/
	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**************************************************
	 * 此方法返回的是ListView的列表中某一行的View对象 position 当前返回的view的索引位置 convertView 缓存对象
	 * parent 就是ListView对象
	 ***************************************************/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		// ViewHolder holder;
		// // 判断缓存对象是否为null, 不为null时已经缓存了对象
		// if (convertView == null) {
		// MyLog.printLi(TAG, "getView: 新建" + position);
		// // 布局填充器对象, 用于把xml布局转换成view对象
		// LayoutInflater inflater = LayoutInflater.from(context);
		// view = inflater.inflate(R.layout.route_step_item, null);
		// holder = new ViewHolder();
		// holder.iv_1 = (ImageView) view.findViewById(R.id.iv_1);
		// holder.tv_show = (TextView) view.findViewById(R.id.tv_line);
		// view.setTag(holder);
		// } else { // 不为null时 复用缓存对象
		// MyLog.printLi(TAG, "getView: 复用缓存" + position);
		// view = convertView;
		// holder = (ViewHolder) view.getTag();
		// }

		// 布局填充器对象, 用于把xml布局转换成view对象
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.route_step_item, null);
		ImageView iv_1 = (ImageView) view.findViewById(R.id.iv_1);
		TextView tv_show = (TextView) view.findViewById(R.id.tv_line);
		tv_show.setText(rSR[position]);
		if (position == 0) {
			iv_1.setBackgroundResource(R.drawable.direction_bus_list_bus_start_line);
		} else if (position == (rSR.length - 1)) {
			iv_1.setBackgroundResource(R.drawable.direction_bus_list_bus_end_line);
		} else {
			switch (selectType) {
			case 1:
				iv_1.setBackgroundResource(R.drawable.default_path_pathinfo_taxi_normal);
				break;
			case 2:
				String strType = rSR[position].substring(0, 2);
				if (strType.equals("乘坐")) {
					iv_1.setBackgroundResource(R.drawable.default_path_pathinfo_taxi_normal);
				}
				break;
			case 3:
				iv_1.setBackgroundResource(R.drawable.default_path_pathinfo_foot_normal);
				break;
			}
			// holder.iv_1.setMinimumHeight(holder.tv_show.getHeight()+20);
		}
		return view;
	}

	static class ViewHolder {
		ImageView iv_1;
		TextView tv_show;
	}

	public void updateListView(String rSR[], int selectType) {
		this.rSR = rSR;
		this.selectType = selectType;
		this.notifyDataSetChanged();
	}
}
