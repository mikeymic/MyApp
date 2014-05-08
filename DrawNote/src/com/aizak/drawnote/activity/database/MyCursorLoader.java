package com.aizak.drawnote.activity.database;

import android.content.Context;
import android.database.Cursor;

public class MyCursorLoader extends SimpleCursorLoader {

	public MyCursorLoader(Context context) {
		super(context);
	}

	@Override
	public Cursor loadInBackground() {
		DatabaseHelper helper = new DatabaseHelper(getContext());
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.findNotes();
		return cursor;
	}
}
