package com.aizak.drawnote.activity;

import com.aizak.drawnote.R;

public final class C {

	public static final class DB {
		public static final String DATABASE_NAME = "drawnote.db";
		public static final String TABLE_NAME_NOTES = "notes";
		public static final String TABLE_NAME_PAGES = "pages";

		public static final String CLM_NOTES_ID = "_id";
		public static final String CLM_NOTES_NAME = "name";
		public static final String CLM_NOTES_CREATE_DATE = "create_date";
		public static final String CLM_NOTES_UPDATE_DATE = "update_date";
		public static final String CLM_NOTES_PAGE_COUNT = "page_count";
		public static final String CLM_NOTES_THUMBNAIL = "thumbnail";

		public static final String CLM_PAGES_ID = "_id";
		public static final String CLM_PAGES_NAME = "name";
		public static final String CLM_PAGES_CREATE_DATE = "create_date";
		public static final String CLM_PAGES_UPDATE_DATE = "update_date";
		public static final String CLM_PAGES_INDEX = "page_index";
		public static final String CLM_PAGES_IMAGE = "image";
		public static final String CLM_PAGES_LINE = "line";
	}

	public static final class DefaultPrefarence {
		public static final String test = "";
		public static final int test2 = 0;
	}

	public static final class GCS {
		public static final String[] from = {
				C.DB.CLM_NOTES_ID,
				C.DB.CLM_NOTES_NAME,
				C.DB.CLM_NOTES_CREATE_DATE,
				C.DB.CLM_NOTES_UPDATE_DATE
		};
		public static final int[] to = {
				R.id.row_note_index,
				R.id.row_note_name,
				R.id.row_note_create_date,
				R.id.row_note_update_date,
		};
	}
}