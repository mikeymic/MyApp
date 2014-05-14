package com.aizak.drawnote.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.util.C;

public class OverlayService extends Service {

	private byte[] data;
	private Bitmap bmp;

	private WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	private View view;

	/*
	 * (非 Javadoc)
	 *
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("TEST", "OverlayService#onStartCommand");

		setNotification();
		createView(intent);
		wm.addView(view, createLayoutParams());

//		return START_REDELIVER_INTENT;
		return START_STICKY;
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.d("TEST", "OverlayService#onStartCommand");
		super.onCreate();
		Toast.makeText(this, "オーバーレイを開始しました", Toast.LENGTH_SHORT).show();
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		wm.removeView(view); // サービスが破棄されるときには重ね合わせしていたViewを削除する
		view = null;
		bmp.recycle();
		Toast.makeText(this, "オーバーレイを終了しました", Toast.LENGTH_SHORT).show();
	}

	/* (非 Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 *
	 */
	private void setNotification() {
		Intent notificationintent = new Intent(Intent.ACTION_VIEW);//ダイアログ読んで終了？
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationintent, 0);

		 //Notification notification = new Notification.Builder(this)
		  Notification notification = new NotificationCompat.Builder(this)
		   .setContentTitle("content title")
		   .setContentText("content text")
		   .setSmallIcon(R.drawable.ic_launcher)
		   .setContentIntent(contentIntent)
		   .build();

		  NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		  manager.notify(2, notification);
		  startForeground(2,notification);
	}

	/**
	 * @param intent
	 */
	private void createView(Intent intent) {
		view = new View(this); //Bitmapを格納するViewの生成

		if (data == null) {
			data = intent.getByteArrayExtra(C.OVERLAY.EXTRA_IMAGE);
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		final int height = intent.getIntExtra(C.OVERLAY.EXTRA_TOP, 0);
//-------------------- << <速度テスト中> >> -------------------
		//BitmapをViewにセット ver 16 から
//		view.setBackground(new BitmapDrawable(getResources(), bmp));
		//Canvasに書き出してからコピー
		view = new View(this) {
			@Override
			protected void onDraw(Canvas canvas) {
				canvas.drawBitmap(bmp, 0, height, null);
			}
		};
	}

	/**
	 * @return WindowManager.LayoutParams
	 */
	private LayoutParams createLayoutParams() {

		int MP = LayoutParams.MATCH_PARENT; //幅を親に合わせる
		int LAYER = LayoutParams.TYPE_SYSTEM_OVERLAY; //システムアラートウィンドウを使う
		int FLG = LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH; //このViewに対してonTouchEventを検知しない
		int FORMAT = PixelFormat.TRANSLUCENT;
		LayoutParams params = new LayoutParams(MP, MP, LAYER, FLG, FORMAT);
		return params;
	}

}