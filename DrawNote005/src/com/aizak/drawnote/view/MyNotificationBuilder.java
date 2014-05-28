package com.aizak.drawnote.view;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.aizak.drawnote.R;
import com.aizak.drawnote.controller.DrawNoteActivity;

public class MyNotificationBuilder extends NotificationCompat.Builder {

	public MyNotificationBuilder(Context context) {
		super(context);
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, DrawNoteActivity.class), 0);
		// NotificationBuilderのインスタンスを作成
		setContentIntent(pi).setTicker("手書きメモ")// ステータスバーに表示されるテキスト
				.setSmallIcon(R.drawable.ic_launcher)// アイコン
				.setContentTitle("手書きメモ（仮）")// Notificationが開いたとき
				.setContentText("起動します")// Notificationが開いたとき
				.setWhen(System.currentTimeMillis())// 通知するタイミング
				.setPriority(Integer.MAX_VALUE);
	}

}
