package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.Observable;

import android.graphics.Bitmap;

public class SaveData {

	public class NoteData{

	}

	public class PageData extends Observable{
		private String name;
		private int index;
		private Bitmap bitmap;
		private Bitmap thumbnail;
		private ArrayList<Line> lines;

		public PageData(String name, int index, Bitmap bitmap, Bitmap thumbmail, ArrayList<Line> lines) {
			this.name = name;
			this.index = index;
			this.bitmap = bitmap;
			this.thumbnail = thumbnail;
			this.lines = lines;
		}

		/**
		 * @return lines
		 */
		public ArrayList<Line> getLines() {
			return lines;
		}


		/**
		 * @return name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return index
		 */
		public int getIndex() {
			return index;
		}
		/**
		 * @return bitmap
		 */
		public Bitmap getBitmap() {
			return bitmap;
		}
		/**
		 * @return thumbnail
		 */
		public Bitmap getThumbnail() {
			return thumbnail;
		}

		/**
		 * @param lines セットする lines
		 */
		public void setLines(ArrayList<Line> lines) {
			this.lines = lines;
		}
		/**
		 * @param name セットする name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @param index セットする index
		 */
		public void setIndex(int index) {
			this.index = index;
		}

		/**
		 * @param bitmap セットする bitmap
		 */
		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

		/**
		 * @param thumbnail セットする thumbnail
		 */
		public void setThumbnail(Bitmap thumbnail) {
			this.thumbnail = thumbnail;
		}

		public void setPageDatas(String name, int index, Bitmap bitmap, Bitmap thumbmail) {
			this.name = name;
			this.index = index;
			this.bitmap = bitmap;
			this.thumbnail = thumbnail;
		}




	}


}
