package com.aizak.drawnote.activity.list;

import android.content.Context;
import android.database.Cursor;

import com.aizak.drawnote.activity.database.DBModel;

public class MyCursorLoader extends SimpleCursorLoader {

	Context context;

	public MyCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		DBModel db = new DBModel(context);
		return db.getNotes();
	}
}
