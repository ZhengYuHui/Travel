package com.zheng.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zheng.travel.R;
import com.zheng.travel.utils.MyLog;

public class RLStepListViewAdapter extends BaseAdapter {

	private static final String TAG = "RLStepListViewAdapter";
	private Context context;
	private String rSR[];

	public RLStepListViewAdapter(Context context, String rSR[]) {
		this.context = context;
		this.rSR = rSR;
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
		ViewHolder holder;
		// 判断缓存对象是否为null, 不为null时已经缓存了对象
		if (convertView == null) {
			MyLog.printLi(TAG, "getView: 新建" + position);
			// 布局填充器对象, 用于把xml布局转换成view对象
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.route_step_item, null);
			holder = new ViewHolder();
			holder.tv_show = (TextView) view.findViewById(R.id.tv_line);
			view.setTag(holder);
		} else { // 不为null时 复用缓存对象
			MyLog.printLi(TAG, "getView: 复用缓存" + position);
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.tv_show.setText(rSR[position]);
		return view;
	}

	static class ViewHolder {
		TextView tv_show;
	}

	public void updateListView(String rSR[]) {
		this.rSR = rSR;
		this.notifyDataSetChanged();
	}
}
