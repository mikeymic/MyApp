package com.aizak.drawnote.controller.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.aizak.drawnote.controller.DrawNoteActivity;
import com.aizak.drawnote.model.listener.AcceleroListener;
import com.aizak.drawnote.model.listener.AcceleroListener.OnAcceleroListener;

public class MyIntentService extends IntentService {

	private static boolean flg;

	public static void setFlg(boolean flg) {
		MyIntentService.flg = flg;
	}

	private AcceleroListener acceleroListener;

	public MyIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
	}

	/* (非 Javadoc)
	 * @see android.app.IntentService#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return super.onBind(intent);
	}

	/* (非 Javadoc)
	 * @see android.app.IntentService#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/* (非 Javadoc)
	 * @see android.app.IntentService#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/* (非 Javadoc)
	 * @see android.app.IntentService#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*--------------------<<<センサーのリスナーの設定> >> -------------------*/
		acceleroListener = new AcceleroListener(getApplicationContext());
		acceleroListener.setOnAccelaroListener(OnDetectAccelero);

		Toast.makeText(this, "サービスを開始しました", Toast.LENGTH_SHORT).show();

		return START_STICKY;
	}

	/* (非 Javadoc)
	 * @see android.app.IntentService#setIntentRedelivery(boolean)
	 */
	@Override
	public void setIntentRedelivery(boolean enabled) {
		super.setIntentRedelivery(enabled);
	}

	/*--------------------<<<加速度検知時の処理> >> -------------------*/
	private final OnAcceleroListener OnDetectAccelero = new OnAcceleroListener() {
		@Override
		public void onAccelero() {
			Intent intent = new Intent(MyIntentService.this, DrawNoteActivity.class);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setAction((Intent.ACTION_MAIN));
			startActivity(intent);

		}
	};
}
