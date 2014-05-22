package com.aizak.drawnote.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aizak.drawnote.util.C;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final String password = "test-password";

	private static final int DATABASE_VERSION = 3;

	private static final String CREATE_DATABASE_TABLE_NOTES = "CREATE TABLE "
			+ C.DB.TABLE_NAME_NOTES
			+ " ("
			+ C.DB.CLM_NOTES_ID
			+ " INTEGER PRIMARY KEY ASC ON CONFLICT REPLACE AUTOINCREMENT, "
			+ C.DB.CLM_NOTES_NAME
			+ " TEXT NOT NULL CONSTRAINT 'unique_note_name' UNIQUE ON CONFLICT ROLLBACK, "
			+ C.DB.CLM_NOTES_CREATE_DATE
			+ " DATETIME NOT NULL CONSTRAINT 'page_create' UNIQUE ON CONFLICT ROLLBACK DEFAULT (datetime('now', 'localtime')), "
			+ C.DB.CLM_NOTES_UPDATE_DATE
			+ " DATETIME DEFAULT (datetime('now', 'localtime')), "
			+ C.DB.CLM_NOTES_PAGE_COUNT + " INTEGER DEFAULT (0) , "
			+ C.DB.CLM_NOTES_THUMBNAIL + " BLOB " + " );";

	private static final String CREATE_DATABASE_TABLE_PAGES = "CREATE TABLE "
			+ C.DB.TABLE_NAME_PAGES
			+ " ("
			+ C.DB.CLM_PAGES_ID
			+ " INTEGER  PRIMARY KEY ASC ON CONFLICT REPLACE AUTOINCREMENT, "
			+ C.DB.CLM_PAGES_NAME
			+ " TEXT REFERENCES notes ("
			+ C.DB.CLM_NOTES_NAME
			+ ") ON DELETE CASCADE ON UPDATE CASCADE, "
			+ C.DB.CLM_PAGES_CREATE_DATE
			+ " DATETIME NOT NULL DEFAULT ( datetime( 'now', 'localtime' )  ), "
			+ C.DB.CLM_PAGES_UPDATE_DATE
			+ " DATETIME CONSTRAINT 'page_update' UNIQUE DEFAULT ( datetime( 'now', 'localtime' )  ), "
			+ C.DB.CLM_PAGES_INDEX + " INTEGER NOT NULL DEFAULT ( 1 ), "
			+ C.DB.CLM_PAGES_LINE + " BLOB, " + C.DB.CLM_PAGES_IMAGE
			+ " BLOB, " + " CONSTRAINT 'uniqe_index' UNIQUE ( "
			+ C.DB.CLM_PAGES_NAME + " COLLATE 'NOCASE' ASC, "
			+ C.DB.CLM_PAGES_INDEX
			+ " COLLATE 'BINARY' ASC )  ON CONFLICT FAIL " + ");";

	private static final String DROP_TABLE_NOTES = "DROP TABLE IF EXIST"
			+ C.DB.TABLE_NAME_NOTES + ";";

	private static final String DROP_TABLE_PAGES = "DROP TABLE IF EXIST"
			+ C.DB.TABLE_NAME_PAGES + ";";

	private static final String INDEX_NOTES_NAME = "CREATE INDEX idx_notes ON "
			+ C.DB.TABLE_NAME_NOTES + " ( " + C.DB.CLM_NOTES_NAME
			+ " COLLATE BINARY ASC " + " );";

	private static final String INDEX_PAGES_NAME = "CREATE UNIQUE INDEX idx_pages ON "
			+ C.DB.TABLE_NAME_PAGES
			+ " ( "
			+ C.DB.CLM_PAGES_NAME
			+ " COLLATE NOCASE ASC, "
			+ C.DB.CLM_PAGES_INDEX
			+ " COLLATE BINARY ASC " + " );";

	private static final String TRIGER_CREATE_NEW_PAGE = "CREATE TRIGGER create_new_page AFTER INSERT ON "
			+ C.DB.TABLE_NAME_NOTES
			+ " BEGIN "
			+ " INSERT INTO "
			+ C.DB.TABLE_NAME_PAGES
			+ " ( "
			+ C.DB.CLM_PAGES_NAME
			+ ", "
			+ C.DB.CLM_PAGES_CREATE_DATE
			+ " ) "
			+ " VALUES ( "
			+ " ( "
			+ " SELECT "
			+ C.DB.CLM_NOTES_NAME
			+ " FROM "
			+ C.DB.TABLE_NAME_NOTES
			+ " WHERE ROWID = last_insert_rowid() "
			+ " ), " + " datetime( 'now', 'localtime' ) " + " ); " + " END;";

	private static final String TRIGER_UPDATE_NOTE_UPDATE_DATE = "CREATE TRIGGER update_note_update_date AFTER UPDATE ON "
			+ C.DB.TABLE_NAME_PAGES
			+ " BEGIN "
			+ " UPDATE "
			+ C.DB.TABLE_NAME_NOTES
			+ " SET "
			+ C.DB.CLM_PAGES_UPDATE_DATE
			+ " = ( "
			+ " SELECT max ( "
			+ C.DB.CLM_PAGES_UPDATE_DATE
			+ " ) "
			+ " FROM " + C.DB.TABLE_NAME_PAGES + " ); " + " END;";

	public DatabaseHelper(Context context) {
		super(context, C.DB.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL("PRAGMA foreign_keys = true;");//外部キーを有効にする（毎回呼ぶこと）
		db.execSQL(CREATE_DATABASE_TABLE_NOTES);
//		db.execSQL(TRIGER_CREATE_NEW_PAGE);
		db.execSQL(CREATE_DATABASE_TABLE_PAGES);
//		db.execSQL(TRIGER_UPDATE_NOTE_UPDATE_DATE);
		db.execSQL(INDEX_NOTES_NAME);
		db.execSQL(INDEX_PAGES_NAME);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE_NOTES);
		db.execSQL(DROP_TABLE_PAGES);
	}

//	public synchronized SQLiteDatabase getWritableDatabase() {
//		return super.getWritableDatabase(password);
//	}
//
//	public synchronized SQLiteDatabase getReadableDatabase() {
//		return super.getReadableDatabase(password);
//	}

}
