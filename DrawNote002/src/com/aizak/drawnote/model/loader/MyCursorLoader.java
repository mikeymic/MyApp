package com.aizak.drawnote.model.loader;

import android.content.Context;
import android.database.Cursor;

import com.aizak.drawnote.model.database.DBControl;

public class MyCursorLoader extends SimpleCursorLoader {

	Context context;
	private DBControl db;

	public MyCursorLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public Cursor loadInBackground() {
		db = new DBControl(context);
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
