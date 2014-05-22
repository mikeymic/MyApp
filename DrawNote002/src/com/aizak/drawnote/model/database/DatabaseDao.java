package com.aizak.drawnote.model.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aizak.drawnote.util.C;

public class DatabaseDao {

	private final SQLiteDatabase db;

	public DatabaseDao(SQLiteDatabase db) {
		this.db = db;
		db.execSQL("PRAGMA foreign_keys = true;");// 外部キーを有効にする（毎回呼ぶこと）
	}

	/*--------------------<<<ノート操作> >> -------------------*/
	//全ノート読み込み
	public Cursor getAllNotes(String[] Columns) {
		Cursor cursor = db.query(C.DB.TABLE_NAME_NOTES, Columns, null,
				null, null, null, null);
		return cursor;
	}

	//新規ノート作成
	public void insertNewNote(ContentValues values) {
		db.insert(C.DB.TABLE_NAME_NOTES, null, values);
	}

	//ノート保存
	public void updateNote(String where, String[] args, ContentValues values) {
		db.beginTransaction();
		try {
			db.update(C.DB.TABLE_NAME_NOTES, values, where, args);
		} catch (Exception e) {
			Log.d("TEST", "faild transaction on updateNote");
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	// ノート削除
	public void deleteNote(String where, String[] args) {
		db.delete(C.DB.TABLE_NAME_NOTES, where, args);
	}

	/*--------------------<<<ページ操作> >> -------------------*/
	// 新規ページ作成
	public long insertNewPage(ContentValues values) {
		return db.insert(C.DB.TABLE_NAME_PAGES, null, values);
	}

	// ページ保存
	public long updatePage(String where, String[] args, ContentValues values) {
		long result = 0;
		db.beginTransaction();
		try {
			result = db.update(C.DB.TABLE_NAME_PAGES, values, where, args);
		} catch (Exception e) {
			Log.d("TEST", "faild transaction on updateNote");
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return result;
	}

	// ページ読み込み
	public Cursor getPage(String where, String[] args, String[] Columns) {
		Cursor cursor = db.query(C.DB.TABLE_NAME_PAGES, Columns, where,
				args, null, null, null);
		return cursor;
	}

	// ページ削除
	public void deletePage(String where, String[] args) {
		db.delete(C.DB.TABLE_NAME_PAGES, where, args);
	}

}
