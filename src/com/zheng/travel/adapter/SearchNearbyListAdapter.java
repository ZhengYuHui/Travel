package com.zheng.travel.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.zheng.travel.R;
import com.zheng.travel.RouteResultsActivity;
import com.zheng.travel.ShowPoiDetailActivity;

public class SearchNearbyListAdapter extends BaseAdapter {

	private int resid[] = { R.color.springgreen, R.color.blue_color,
			R.color.azure, R.color.turquoise, R.color.thistle,
			R.color.slateblue, R.color.cornsilk, R.color.sandybrown,
			R.color.royalblue, R.color.red };
	private AlertDialog dialog;
	private Context context;
	private List<PoiInfo> poiInfo;
	private int myPosition;

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
			holder.tv_1 = (TextView) view.findViewById(R.id.tv_1);
			holder.tv_2 = (TextView) view.findViewById(R.id.tv_2);
			holder.tv_3 = (TextView) view.findViewById(R.id.tv_3);

			view.setTag(holder);
		} else { // 不为null时 复用缓存对象
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		holder.tv_itemNum.setBackgroundResource(resid[position]);
		holder.tv_itemNum.setText(String.valueOf(position + 1));
		holder.tv_name.setText(poiInfo.get(position).name);
		holder.tv_address.setText(poiInfo.get(position).address);
		holder.tv_1.setTag(position);
		holder.tv_1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myPosition = Integer.parseInt(v.getTag().toString());
				Intent intentNearby2 = new Intent(context,
						RouteResultsActivity.class);
				intentNearby2.setFlags(1);
				intentNearby2.putExtra("POIname", poiInfo.get(myPosition).name);
				intentNearby2.putExtra("POIlatitude",
						poiInfo.get(myPosition).location.latitude);
				intentNearby2.putExtra("POIlongitude",
						poiInfo.get(myPosition).location.longitude);
				context.startActivity(intentNearby2);
			}
		});
		holder.tv_2.setTag(position);
		holder.tv_2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myPosition = Integer.parseInt(v.getTag().toString());
				if (poiInfo.get(myPosition).phoneNum.trim().equals("")) {
					Toast.makeText(context, "无联系电话", Toast.LENGTH_SHORT).show();
				} else {
					showIndexDialog(myPosition);
				}
			}
		});
		holder.tv_3.setTag(position);
		holder.tv_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myPosition = Integer.parseInt(v.getTag().toString());
				Intent intentNearby = new Intent(context,
						ShowPoiDetailActivity.class);
				intentNearby.putExtra("poiPosition", myPosition);
				context.startActivity(intentNearby);
			}
		});
		return view;
	}

	static class ViewHolder {
		TextView tv_itemNum;
		TextView tv_name;
		TextView tv_address;
		TextView tv_1;
		TextView tv_2;
		TextView tv_3;
	}

	public void updateListView(List<PoiInfo> poiInfo) {
		this.poiInfo = poiInfo;
		this.notifyDataSetChanged();
	}

	/*******************************************************
	 * 显示拨打电话提示对话框
	 ******************************************************/
	public void showIndexDialog(final int myPosition) {
		dialog = null;
		AlertDialog.Builder builder = new Builder(context);
		View view = View.inflate(context, R.layout.call_phone_dialog, null);

		TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		tv_msg.setText("拨打：" + poiInfo.get(myPosition).phoneNum + " ?");
		TextView tv_data = (TextView) view.findViewById(R.id.tv_data);
		tv_data.setText(poiInfo.get(myPosition).name);

		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button bt_sure = (Button) view.findViewById(R.id.bt_sure);
		bt_sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 指定其动作为拨打电话
				intent.setAction(Intent.ACTION_CALL);
				// 指定将要拨出的号码
				intent.setData(Uri.parse("tel:"
						+ poiInfo.get(myPosition).phoneNum));
				context.startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}

}
