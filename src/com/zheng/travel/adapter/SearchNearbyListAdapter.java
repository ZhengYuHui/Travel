package com.zheng.travel.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.zheng.travel.R;

public class SearchNearbyListAdapter extends BaseAdapter {

	private Context context;
	private List<PoiInfo> poiInfo;

	public SearchNearbyListAdapter(Context context, List<PoiInfo> poiInfo) {
		this.context = context;
		this.poiInfo = poiInfo;
	}

	/****************************************************
	 * 定义ListView的数据的长度
	 *****************************************************/
	@Override
	public int getCount() {
		if (poiInfo == null) {
			return 0;
		} else {
			return poiInfo.size();
		}
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
			view = inflater.inflate(R.layout.search_nearby_list_item, null);
			holder = new ViewHolder();
			holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
			holder.tv_itemNum = (TextView) view.findViewById(R.id.tv_itemNum);
			holder.tv_address = (TextView) view.findViewById(R.id.tv_address);
			view.setTag(holder);
		} else { // 不为null时 复用缓存对象
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.tv_itemNum.setText(String.valueOf(position + 1));
		holder.tv_name.setText(poiInfo.get(position).name);
		holder.tv_address.setText(poiInfo.get(position).address);
		return view;
	}

	static class ViewHolder {
		TextView tv_itemNum;
		TextView tv_name;
		TextView tv_address;
	}

	public void updateListView(List<PoiInfo> poiInfo) {
		this.poiInfo = poiInfo;
		this.notifyDataSetChanged();
	}

}
