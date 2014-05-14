package com.aizak.drawnote.activity.list;

import android.content.Context;
import android.database.Cursor;

import com.aizak.drawnote.controller.DBController;

public class MyCursorLoader extends SimpleCursorLoader {

	Context context;
	private DBController db;

	public MyCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		db = new DBController(context);
		return db.getNotes();
	}

	/* (非 Javadoc)
	 * @see com.aizak.drawnote.activity.list.SimpleCursorLoader#onReset()
	 */
	@Override
	protected void onReset() {
		super.onReset();
		if (db != null) {
			db.close();
		}
	}




}
