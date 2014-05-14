package com.aizak.drawnote.activity.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aizak.drawnote.util.C;

public class DatabaseDao {

	private SQLiteDatabase db;

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
		db.update(C.DB.TABLE_NAME_NOTES, values, where, args);
	}

	// ノート削除
	public void deleteNote(String where, String[] args) {
		db.delete(C.DB.TABLE_NAME_PAGES, where, args);
	}

	/*--------------------<<<ページ操作> >> -------------------*/
	// 新規ページ作成
	public long insertNewPage(ContentValues values) {
		return db.insert(C.DB.TABLE_NAME_PAGES, null, values);
	}

	// ページ保存
	public long updatePage(String where, String[] args, ContentValues values) {
		return db.update(C.DB.TABLE_NAME_PAGES, values, where, args);
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
