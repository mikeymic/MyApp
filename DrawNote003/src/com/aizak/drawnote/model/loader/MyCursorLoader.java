package com.aizak.drawnote.model.loader;

import android.content.Context;
import android.database.Cursor;

import com.aizak.drawnote.model.database.DBControl;

public class MyCursorLoader extends SimpleCursorLoader {

	Context context;
	private DBControl db;
	private final String name;

	public MyCursorLoader(Context context, String name) {
		super(context);
		this.context = context;
		this.name = name;
	}

	@Override
	public Cursor loadInBackground() {
		db = new DBControl(context);

		if (name != null) {
			return db.getPagesCursor(name);
		}

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
