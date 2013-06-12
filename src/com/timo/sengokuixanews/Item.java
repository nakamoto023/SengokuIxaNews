package com.timo.sengokuixanews;

public class Item {
	private String mTitle;
	private String mLink;
	private String mDate;

	public Item() {
		mTitle = "";
		mLink = "";
		mDate = "";
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		mDate = date;
	}

}
