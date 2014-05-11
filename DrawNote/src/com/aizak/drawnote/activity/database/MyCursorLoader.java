package com.aizak.drawnote.activity.database;

import android.content.Context;
import android.database.Cursor;

public class MyCursorLoader extends SimpleCursorLoader {

	Context context;

	public MyCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		DBModel db = new DBModel(context);
		return db.getNote();
	}
}
