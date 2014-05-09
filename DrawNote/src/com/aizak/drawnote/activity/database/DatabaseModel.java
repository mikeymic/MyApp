package com.aizak.drawnote.activity.database;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.aizak.drawnote.activity.C;

public class DatabaseModel {

	private final Context context;

	public DatabaseModel(Context context) {
		this.context = context;
	}

	//**-------------------- DB操作 --------------------**//

	int n = 0;

	//ノート//
	public void createNote() {
		n++;
		Toast.makeText(context, String.valueOf(n), Toast.LENGTH_SHORT).show();
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.insertNewNote("test" + String.valueOf(n));
		helper.close();
	}

	public void updateNote() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void deleteNote() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public Cursor readNote() {
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.findNotes();
//		helper.close();
		return cursor;
	}

	public void readNotes() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void getNoteCount() {
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	//ページ//
	public void createPage() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void updatePage() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void deletePage(String name, int index) {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public byte[] readPage(String name, int index) {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.readPage(null, index);

		byte[] stream = null;
		while (cursor.moveToNext() != false) {
			stream = cursor.getBlob(cursor.getColumnIndex(C.DB.CLM_PAGES_LINE));
		}
		helper.close();

		return stream;
	}

	public void readPages() {

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public int getPageCount(String name) {
		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		int count = dao.getPageCount(name).getCount();
		helper.close();
		return count;
	}
}
