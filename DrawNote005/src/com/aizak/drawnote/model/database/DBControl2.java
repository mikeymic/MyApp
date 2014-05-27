package com.aizak.drawnote.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.aizak.drawnote.util.C;

public class DBControl2 {

	//ビルドバージョンで、WhereArgsのStringの扱いが変わる
	//1.6 : " 'string' "
	//1.7 : " string "
	private final Context context;
	private final DatabaseHelper helper;

	private static final String[] COLUMNS_NOTES = { C.DB.CLM_NOTES_ID,
			C.DB.CLM_NOTES_NAME, C.DB.CLM_NOTES_CREATE_DATE,
			C.DB.CLM_NOTES_UPDATE_DATE, C.DB.CLM_NOTES_PAGE_COUNT,
			C.DB.CLM_NOTES_THUMBNAIL };

	private static final String[] COLUMNS_PAGES = {
			C.DB.CLM_PAGES_ID,
			C.DB.CLM_PAGES_NAME,
			C.DB.CLM_PAGES_CREATE_DATE,
			C.DB.CLM_PAGES_UPDATE_DATE,
			C.DB.CLM_PAGES_INDEX,
			C.DB.CLM_PAGES_LINE,
			C.DB.CLM_PAGES_IMAGE };

	private final String whereNote = C.DB.CLM_NOTES_NAME + " = ? ";
	private final String whereNoteIndex = C.DB.CLM_NOTES_ID + " = ? ";
	private final String wherePage = C.DB.CLM_PAGES_NAME + " = ? ";
	private final String wherePageIndex = C.DB.CLM_PAGES_INDEX + " =  ? ";

	//**-------------------- DB操作 --------------------**//

	static int n = 0;

	public DBControl2(Context context) {
		this.context = context;
		helper = new DatabaseHelper(context);
	}

	//新規ノート作成
	public void insertNewNote() {
		n++;
		String name = "test" + String.valueOf(n);

		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_NOTES_NAME, name);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.insertNewNote(values);

		helper.close();
	}

	//ノート保存
	public void updateNote(String name, int index) {
		String where = whereNote + " and " + whereNoteIndex;
		String[] args = { name, String.valueOf(index) };

		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_NOTES_NAME, name);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.updateNote(where, args, values);

		helper.close();
	}

	// ノート削除
	public void deleteNote(String name) {
		String where = whereNote;
		String[] args = { name};

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.deleteNote(where, args);

		helper.close();
	}
	public void deleteNote(int index) {
		String where = whereNoteIndex + "= ?";
		String[] args = { String.valueOf(index) };

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.deleteNote(where, args);

		helper.close();
	}

	//ノート読み込み
	public Cursor getNote() {
		String[] Columns = COLUMNS_NOTES;

		DatabaseHelper helper = new DatabaseHelper(context);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());

		Cursor cursor = dao.getAllNotes(Columns);

		return cursor;
	}

	//全ノート読み込み
	public Cursor getNotes() {
		String[] Columns = COLUMNS_NOTES;
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.getAllNotes(Columns);
		return cursor;
	}

	// ノート数取得
	public int getNoteCount() {
		String[] Columns = new String[] { C.DB.CLM_PAGES_NAME };

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());

		int count = dao.getAllNotes(Columns).getCount();

		helper.close();
		return count;
	}

	/*--------------------<<<ページ操作> >> -------------------*/
	// 新規ページ作成
	public void insertNewPage(String name, int index) {
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_PAGES_NAME, name);
		values.put(C.DB.CLM_PAGES_INDEX, index);

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		dao.insertNewPage(values);

		helper.close();
	}

	// ページ保存
	public void updatePage(String name, int index, byte[] stream) {
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_PAGES_LINE, stream);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.updatePage(where, args, values);

		helper.close();
	}

	public void updatePage(String name, int index, byte[] line, byte[] image) {
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_PAGES_LINE, line);
		values.put(C.DB.CLM_PAGES_IMAGE, image);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.updatePage(where, args, values);

		helper.close();
	}

	public void updatePage(String name, int index, byte[] line, byte[] image, byte[] thumbnail) {
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_PAGES_LINE, line);
		values.put(C.DB.CLM_PAGES_IMAGE, image);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.updatePage(where, args, values);
		helper.close();

		updateThunmNail(name, thumbnail);
	}
	public void updatePage(String name, int index, byte[] line, byte[] image, byte[] thumbnail, int pageCount) {
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_PAGES_LINE, line);
		values.put(C.DB.CLM_PAGES_IMAGE, image);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.updatePage(where, args, values);
		helper.close();

		updateNotesPageCount(name, pageCount);
		updateThunmNail(name, thumbnail);
	}

	public void updateNotesPageCount(String name, int count) {
		String where = whereNote;
		String[] args = { name };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_NOTES_PAGE_COUNT, count);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.updateNote(where, args, values);
		helper.close();
	}
	public void updateThunmNail(String name, byte []thumbnail) {
		String where = whereNote;
		String[] args = { name };
		ContentValues values = new ContentValues();
		values.put(C.DB.CLM_NOTES_THUMBNAIL, thumbnail);

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());

		dao.updateNote(where, args, values);
		helper.close();
	}

	// ページ読み込み
	public byte[] getPage(String name, int index) {
		Log.d("TEST", "Name = " + name + "Index = " + String.valueOf(index));
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		String[] Columns = COLUMNS_PAGES;

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.getPage(where, args, Columns);

		byte[] stream = null;
		while (cursor.moveToNext() != false) {
			stream = cursor.getBlob(cursor.getColumnIndex(C.DB.CLM_PAGES_LINE));
		}

		cursor.close(); // cursorを使っている場合はhelper.close();はよばない事
		return stream;
	}

	// ページ読み込み
	public byte[][] getPageWidthImage(String name, int index) {
		Log.d("TEST", "Name = " + name + "Index = " + String.valueOf(index));
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };
		String[] Columns = COLUMNS_PAGES;

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.getPage(where, args, Columns);

		byte[] lines = null;
		byte[] image = null;

		while (cursor.moveToNext() != false) {
			lines = cursor.getBlob(cursor.getColumnIndex(C.DB.CLM_PAGES_LINE));
			image = cursor.getBlob(cursor.getColumnIndex(C.DB.CLM_PAGES_IMAGE));
		}

		cursor.close();
		return new byte[][] { lines, image };
	}

	// 全ページ読み込み
	public byte[] getPages(String name) {
		String where = wherePage;
		String[] args = { name };
		String[] Columns = COLUMNS_PAGES;

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		Cursor cursor = dao.getPage(where, args, Columns);

		byte[] stream = null;
		while (cursor.moveToNext() != false) {
			stream = cursor.getBlob(cursor.getColumnIndex(C.DB.CLM_PAGES_LINE));
		}

		cursor.close(); // cursorを使っている場合はhelper.close();はよばない事
		return stream;
	}

	// ページ削除
	public void deletePage(String name, int index) {
		String where = wherePage + " and " + wherePageIndex;
		String[] args = { name, String.valueOf(index) };

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.deletePage(where, args);

		helper.close();
	}

	// 全ページ削除
	public void deletePages(String name) {
		String where = C.DB.CLM_PAGES_NAME + " = ? ";
		String[] args = { name };

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		dao.deletePage(where, args);

		helper.close();
	}

	// ページ数取得
	public int getPageCount(String name) {
		String where = C.DB.CLM_PAGES_NAME + " = ? ";
		String[] args = { name };
		String[] Columns = new String[] { C.DB.CLM_PAGES_NAME };

		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase());
		int count = dao.getPage(where, args, Columns).getCount();

		helper.close();
		return count;
	}

	//ページ番号更新
	public void updatePageIndexWhenInsert(String name, int index, int pageCount) {

		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		ContentValues values = new ContentValues();

		for (int i = pageCount; i >= index; i--) {

			String where = wherePage + " and " + wherePageIndex;
			String[] args = { name, String.valueOf(i) };

			values.clear();
			values.put(C.DB.CLM_PAGES_INDEX, i + 1);

			dao.updatePage(where, args, values);
		}
		helper.close();

	}

	//ページ番号更新
	public void updatePageIndexWhenDelete(String name, int index, int pageCount) {
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase());
		ContentValues values = new ContentValues();

		for (int i = index; i <= pageCount; i++) {
			String where = wherePage + " and " + wherePageIndex;
			String[] args = { name, String.valueOf(i + 1) };

			values.clear();
			values.put(C.DB.CLM_PAGES_INDEX, i);

			dao.updatePage(where, args, values);
		}

		helper.close();
	}

	public void close() {
		helper.close();
	}
}
