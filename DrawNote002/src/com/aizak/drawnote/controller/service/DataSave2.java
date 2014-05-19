package com.aizak.drawnote.controller.service;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.IBinder;
import android.widget.Toast;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.SerializeManager;

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
		Toast.makeText(getApplicationContext(),
				cNoteName + "--" + String.valueOf(cPageIndex) + "--" + String.valueOf(pageCount),
				Toast.LENGTH_SHORT).show();
		db.updatePage(cNoteName, cPageIndex, lines, image, thumbnail, pageCount);

		return START_STICKY;
	}

}
