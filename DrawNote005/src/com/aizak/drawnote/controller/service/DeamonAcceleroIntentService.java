package com.aizak.drawnote.controller.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.aizak.drawnote.model.listener.AcceleroListener;
import com.aizak.drawnote.model.listener.AcceleroListener.OnAcceleroListener;


public class DeamonAcceleroIntentService extends Service {

	private AcceleroListener acceleroListener;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	/*--------------------<<<センサーのリスナーの設定> >> -------------------*/
	acceleroListener = new AcceleroListener(this);
	acceleroListener.setOnAccelaroListener(OnDetectAccelero);
	acceleroListener.onRegisterAcceleroListener();

	return  START_STICKY;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		Toast.makeText(this, "サービスを開始しました", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
//		Toast.makeText(this, "サービスを停止しました", Toast.LENGTH_SHORT).show();
		acceleroListener.onUnRegisterAcceleroListener();
		super.onDestroy();
	}

	/*--------------------<<<加速度検知時の処理> >> -------------------*/
	private OnAcceleroListener OnDetectAccelero = new OnAcceleroListener() {
		@Override
		public void onAccelero() {
			Intent i = new Intent();
			i.setAction("com.aizak.drawnote.android.IntentAction.VIEW");
			sendBroadcast(i);
		}
	};




}
