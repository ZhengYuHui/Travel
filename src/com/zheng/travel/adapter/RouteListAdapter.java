package com.zheng.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zheng.travel.R;

public class RouteListAdapter extends BaseAdapter {

	private static final String TAG = "RouteListAdapter";
	private Context context;
	private String rSRLine[];
	private String rSRTime[];

	public RouteListAdapter(Context context, String rSRLine[], String rSRTime[]) {
		this.context = context;
		this.rSRLine = rSRLine;
		this.rSRTime = rSRTime;
	}

	/****************************************************
	 * 定义ListView的数据的长度
	 *****************************************************/
	@Override
	public int getCount() {
		return rSRLine.length;
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
			// 布局填充器对象, 用于把xml布局转换成view对象
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.route_list_item, null);
			holder = new ViewHolder();
			holder.tv_line = (TextView) view.findViewById(R.id.tv_line);
			holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
			view.setTag(holder);
		} else { // 不为null时 复用缓存对象
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.tv_line.setText(rSRLine[position]);
		holder.tv_time.setText("(" + rSRTime[position] + ")");
		return view;
	}

	static class ViewHolder {
		TextView tv_line;
		TextView tv_time;
	}

	public void updateListView(String rSRLine[], String rSRTime[]) {
		this.rSRLine = rSRLine;
		this.rSRTime = rSRTime;
		this.notifyDataSetChanged();
	}

}
