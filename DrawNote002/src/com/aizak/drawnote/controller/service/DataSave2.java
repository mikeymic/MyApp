package com.aizak.drawnote.controller.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.IBinder;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.SerializeManager;
import com.aizak.drawnote.view.DrawingView;

public class DataSave2 extends Service {

	private final String TAG = this.getClass().getSimpleName();
	private final ArrayList<Line> saveLines = new ArrayList<Line>();
	private DBControl db;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		db = new DBControl(getApplication());

		boolean save = intent.getBooleanExtra("save", false);

		if (save) {
			savePage(intent);
		} else {
			getPage(intent);
		}

		return START_STICKY;
	}

	public void savePage(Intent intent) {
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
		db.updatePage(cNoteName, cPageIndex, lines, image, thumbnail, pageCount);

	}

	public void getPage(Intent intent) {
		if (Data.editingLine != null) {
			Data.editingLine.clear();
		}
		if (Data.bitmap != null) {
			Data.bitmap = null;
		}

		String name = intent.getStringExtra(C.DB.CLM_PAGES_NAME);
		int index = intent.getIntExtra(C.DB.CLM_PAGES_INDEX, 0);

		byte[][] stream = db.getPageWidthImage(name, index);
		if (stream != null) {
			if (stream[0] != null) {
				Data.savedLine.addAll((ArrayList<Line>) SerializeManager.deserializeData(stream[0]));
			}
			if (stream[1] != null) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(stream[1], 0, stream[1].length);
				Data.bitmap = bitmap.copy(Config.ARGB_8888, true);
				DrawingView.bmpCanvas = new Canvas(Data.bitmap);
//				drawingView.setBmpCanvas(new Canvas(Data.bitmap));
			} else {
				saveLines.clear();
			}
		}
	}
}
