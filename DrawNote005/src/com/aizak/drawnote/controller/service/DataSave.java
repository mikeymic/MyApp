package com.aizak.drawnote.controller.service;

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.widget.Toast;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.SerializeManager;

public class DataSave extends IntentService {

	private final String TAG = this.getClass().getSimpleName();
	private final ArrayList<Line> saveLines = new ArrayList<Line>();
	private DBControl db;

	public DataSave() {
		super("DataSave");
		init();
	}

	public DataSave(String name) {
		super(name);
		init();
	}

	private void init() {
		db = new DBControl(getApplication());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		saveLines.clear();
		saveLines.addAll(Data.savedLine);
		saveLines.addAll(Data.editingLine);
		Bitmap bitmap = Data.bitmap.copy(Config.ARGB_8888, true);
		Bitmap thumb = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, false);
		byte[] lines = SerializeManager.serializeData(saveLines);
		byte[] image = SerializeManager.serializeData(bitmap);
		byte[] thumbnail = SerializeManager.serializeData(thumb);

		String cNoteName = intent.getStringExtra(C.DB.CLM_NOTES_NAME);
		int cPageIndex = intent.getIntExtra(C.DB.CLM_PAGES_INDEX, 0);
		int pageCount = intent.getIntExtra(C.DB.CLM_NOTES_PAGE_COUNT, 0);
		Toast.makeText(getApplication(), cNoteName + String.valueOf(cPageIndex) + String.valueOf(pageCount),
				Toast.LENGTH_SHORT).show();
		db.updatePage(cNoteName, cPageIndex, lines, image, thumbnail, pageCount);
	}

}
