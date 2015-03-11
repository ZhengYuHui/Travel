package com.zheng.travel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zheng.travel.R;
import com.zheng.travel.utils.MyLog;

public class RouteListAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;
	private List<String> listData;
	private List<String> listUnit;
	private List<String> listMax;
	private List<String> listMin;

	/****************************************************
	 * 初始化数据
	 *****************************************************/
	public RouteListAdapter(Context context, List<String> list,
			List<String> listData, List<String> listUnit, List<String> listMax,
			List<String> listMin) {
		this.context = context;
		this.list = list;
		this.listData = listData;
		this.listUnit = listUnit;
		this.listMax = listMax;
		this.listMin = listMin;
	}

	/****************************************************
	 * 定义ListView的数据的长度
	 *****************************************************/
	@Override
	public int getCount() {
		return list.size() + 1;
		// return list.size();
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
		if (position == (list.size())) {
			view = new TextView(context);
			view.setMinimumHeight(60);
		} else {
			// 判断缓存对象是否为null, 不为null时已经缓存了对象
			if (convertView != null && convertView instanceof LinearLayout) {
				MyLog.printS("getView: 复用", position);
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				MyLog.printS("getView: 新建", position);
				// 布局填充器对象, 用于把xml布局转换成view对象
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.route_list_item, null);
				holder = new ViewHolder();
				// holder.li_completed_view = (CompletedView) view
				// .findViewById(R.id.li_completed_view);
				holder.li_LinearLayout = (LinearLayout) view
						.findViewById(R.id.li_LinearLayout);
				holder.li_TextView_name = (TextView) view
						.findViewById(R.id.li_TextView_name);
				holder.li_TextView_shuju = (TextView) view
						.findViewById(R.id.li_TextView_shuju);
				holder.li_TextView_danwei = (TextView) view
						.findViewById(R.id.li_TextView_danwei);
				holder.li_max = (TextView) view.findViewById(R.id.li_max);
				holder.li_min = (TextView) view.findViewById(R.id.li_min);
				// 当孩子生出来的时候找到他们的引用，存放在记事本，放在父亲的口袋
				view.setTag(holder);
			}
			// holder.li_completed_view.setProgress(Integer.parseInt(listData
			// .get(position)));
			holder.li_TextView_name.setText(list.get(position));
			holder.li_TextView_shuju.setText(listData.get(position));
			holder.li_TextView_danwei.setText(listUnit.get(position));
			holder.li_max.setText("最大值：" + listMax.get(position));
			holder.li_min.setText("最小值：" + listMin.get(position));

		}
		return view;
	}

	static class ViewHolder {
		LinearLayout li_LinearLayout;
		// CompletedView li_completed_view;
		TextView li_TextView_name;
		TextView li_TextView_shuju;
		TextView li_TextView_danwei;
		TextView li_max;
		TextView li_min;
	}

	/*****************************************
	 * 重新设置数据列表 listData 的Item数据
	 ****************************************/
	public void setListDataItem(int location, String object) {
		listData.set(location, object);
		this.notifyDataSetChanged();
	}

	/*****************************************
	 * 重新设置数据列表 listData 的所有数据
	 ****************************************/
	public void setListData(List<String> newListData) {
		listData = newListData;
		this.notifyDataSetChanged();
	}

}
