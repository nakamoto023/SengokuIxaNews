package com.timo.sengokuixanews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RssListAdapter extends ArrayAdapter<Item> {
	private LayoutInflater mInflater;
	private TextView mTitle;
	private TextView mDate;

	public RssListAdapter(Context context, List<Item> objects) {
		super(context, 0, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_row, null);
		} else {
			view = convertView;
		}

		Item item = this.getItem(position);
		if (item != null) {
			// タイトルを設定
			String title = item.getTitle().toString();
			mTitle = (TextView) view.findViewById(R.id.title);
			mTitle.setText(title);

			// 日付をフォーマットして設定
			String pattern[] = { DateUtils.PATTERN_RFC1123 };
			String pubdate = item.getDate().toString();
			String formattedDate = "";
			try {
				Date date = DateUtils.parseDate(pubdate, pattern);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy年MM月dd日 HH時mm分");
				formattedDate = sdf.format(date);
			} catch (DateParseException e) {
				e.printStackTrace();
			}
			mDate = (TextView) view.findViewById(R.id.date);
			mDate.setText(formattedDate);
		}
		return view;
	}
}
