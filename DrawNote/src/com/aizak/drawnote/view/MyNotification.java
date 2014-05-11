package com.aizak.drawnote.view;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.aizak.drawnote.R;
import com.aizak.drawnote.fragment.NoteFragment;

public class MyNotification extends Notification {

	public MyNotification(Context context) {
		PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, NoteFragment.class), 0);
		// NotificationBuilderのインスタンスを作成
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentIntent(pi).setTicker("テキスト")// ステータスバーに表示されるテキスト
					.setSmallIcon(R.drawable.ic_launcher)// アイコン
					.setContentTitle("タイトル")// Notificationが開いたとき
					.setContentText("メッセージ")// Notificationが開いたとき
					.setWhen(System.currentTimeMillis())// 通知するタイミング
					.setPriority(Integer.MAX_VALUE);
		flags =Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		builder.build();
	}



}
