package com.timo.sengokuixanews;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class MainActivity extends ListActivity implements OnClickListener {
	private static final String RSS_FEED_URL = "http://sengokuixa.jp/rss/news.php";
	private RssListAdapter mAdapter;
	private AdView mAdView;
	AdRequest mRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.button_reload).setOnClickListener(this);

		ListView listView = getListView();
		listView.setScrollingCacheEnabled(false); // スクロール時の黒点滅を防止
		listView.setOnItemClickListener(mListener);
		listView.setBackgroundResource(R.drawable.bg);

		// リストの中身をセット
		ArrayList<Item> items = new ArrayList<Item>();
		mAdapter = new RssListAdapter(this, items);
		setListAdapter(mAdapter);

		// リスト更新
		RssParserTask task = new RssParserTask(this, mAdapter);
		task.execute(RSS_FEED_URL);

		// 広告の設定
		mAdView = (AdView) findViewById(R.id.adview);
		mRequest = new AdRequest();
		mRequest.setGender(AdRequest.Gender.MALE);
		// テスト用。エミュレーターでの表示回数をカウントさせたくない時
		// mRequest.addTestDevice(AdRequest.TEST_EMULATOR);
	}

	@Override
	protected void onResume() {
		// 広告更新
		mAdView.loadAd(mRequest);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mAdView.destroy();
		super.onDestroy();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_reload) {
			// リスト更新
			RssParserTask task = new RssParserTask(this, mAdapter);
			task.execute(RSS_FEED_URL);
			// 広告更新
			mAdView.loadAd(mRequest);
		}
	}

	/** リスト選択時に呼ばれるリスナー */
	private AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// 選択した行のデータ取得
			Item item = (Item) parent.getAdapter().getItem(position);

			// URLを渡してブラウザ起動
			Uri uri = Uri.parse(item.getLink().toString());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		};
	};
}
