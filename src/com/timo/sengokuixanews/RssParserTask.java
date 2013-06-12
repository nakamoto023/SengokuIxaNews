package com.timo.sengokuixanews;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Xml;

public class RssParserTask extends AsyncTask<String, Integer, List<Item>> {
	private MainActivity mActivity;
	private RssListAdapter mAdapter;
	private ProgressDialog mProgressDialog;

	public RssParserTask(MainActivity activity, RssListAdapter adapter) {
		mActivity = activity;
		mAdapter = adapter;
	}

	/** 最初に呼ばれる処理 */
	@Override
	protected void onPreExecute() {
		// プログレスバーを表示する
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setMessage("更新中...");
		mProgressDialog.show();
		super.onPreExecute();
	}

	/** バックグラウンド処理。タスク実行時に渡された値が引数に入ってくる */
	@Override
	protected List<Item> doInBackground(String... params) {
		List<Item> items = new ArrayList<Item>();
		try {
			// HTTP経由でアクセスし、InputStreamを取得する
			URL url = new URL(params[0]);
			InputStream is = url.openConnection().getInputStream();
			items = parseXml(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ここで返した値は、onPostExecuteメソッドの引数として渡される
		return items;
	}

	/** 最後に呼ばれる処理。呼び出し元スレッド上で実行される */
	@Override
	protected void onPostExecute(List<Item> items) {
		// RSSの取得に失敗した場合は、アプリ終了
		if (items.isEmpty()) {
			showFinishDialog();
		}
		// 更新ボタンのため、一度クリアしてから入れていく
		mAdapter.clear();
		for (Item item : items) {
			mAdapter.add(item);
		}
		mProgressDialog.dismiss();
		mActivity.setListAdapter(mAdapter);
		super.onPostExecute(items);
	}

	/** XMLをパース */
	private List<Item> parseXml(InputStream is) throws IOException,
			XmlPullParserException {
		List<Item> items = new ArrayList<Item>();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			Item currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
				case XmlPullParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("item")) {
						currentItem = new Item();
					} else if (currentItem != null) {
						if (tag.equals("title")) {
							currentItem.setTitle(parser.nextText());
						} else if (tag.equals("link")) {
							currentItem.setLink(parser.nextText());
						} else if (tag.equals("pubDate")) {
							currentItem.setDate(parser.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if (tag.equals("item")) {
						items.add(currentItem);
						currentItem = null;
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

	/** 終了ダイアログを表示 */
	private void showFinishDialog() {
		new AlertDialog.Builder(mActivity).setTitle("更新エラー")
				.setMessage("新着情報が取得できませんでした。ネットへ接続できるかご確認ください。アプリを終了します。")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mActivity.finish();
					}
				}).show();
	}
}
