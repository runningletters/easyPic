package org.hekangping.easypic;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private static final String TAG = "MyAdapter";

	private LayoutInflater mInflater;
	private List<? extends Map<String, ?>> list;
	private int layoutID;

	private static ViewHolder viewHolder = null;

	private int selectItem = -1;

	public MyAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource) {
		Log.d(TAG, "MyAdapter()");
		this.mInflater = LayoutInflater.from(context);
		this.list = data;
		this.layoutID = resource;

	}

	@Override
	public int getCount() {
		Log.d(TAG, "getCount()");
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		Log.d(TAG, "getItem()");
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		Log.d(TAG, "getItemId()");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView()");
		if (convertView == null) {
			convertView = mInflater.inflate(layoutID, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.idColumn = (TextView) convertView
					.findViewById(R.id.dataIndex);
			viewHolder.textColumn = (TextView) convertView
					.findViewById(R.id.dataContent);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			// Log.d("MyAdapter", "¾ÉµÄconvertView,position=" + position);
		}
		if (list != null && list.size() > 0) {
			Map<String, Object> objDataMap = (Map<String, Object>) list
					.get(position);
			viewHolder.idColumn.setText(objDataMap.get("id").toString());
			viewHolder.textColumn.setText(objDataMap.get("text").toString());
		}
		if (position == selectItem) {
			convertView.setBackgroundColor(Color.parseColor("#33CCFF"));
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}

		return convertView;
	}

	public final class ViewHolder {
		public ImageView img;
		public TextView title;
		public TextView info;
		public TextView idColumn;
		public TextView textColumn;
		public Button viewBtn;
	}

	public void setSelectItem(int selectItem) {
		Log.d(TAG, "setSelectItem()");
		this.selectItem = selectItem;
	}

}
