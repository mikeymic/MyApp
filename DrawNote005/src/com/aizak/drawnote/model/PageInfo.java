package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.Observable;

public class PageInfo extends Observable {

	public String noteName = "no-title";
	public String pageTag = "no-tag";

	public int currentPage = 0;
	public int pageCount = 0;

	public ArrayList<Line> saveLines;
	public byte[] bitmap;

	public void updatePageInfo(String noteName, int currentPage, int pageCount) {
		this.noteName = noteName;
		this.currentPage = currentPage;
		this.pageCount = pageCount;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * @param noteName
	 *            セットする noteName
	 */
	public void setNoteName(String noteName) {
		this.noteName = noteName;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * @param currentPage
	 *            セットする currentPage
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		setChanged();
		notifyObservers(this);
	}

	/**
	 * @param pageCount
	 *            セットする pageCount
	 */
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
		setChanged();
		notifyObservers(this);
	}

}
