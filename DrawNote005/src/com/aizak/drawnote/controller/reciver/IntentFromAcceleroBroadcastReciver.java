package com.aizak.drawnote.controller.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aizak.drawnote.controller.DrawNoteActivity;


public class IntentFromAcceleroBroadcastReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//AlarmView.classは実行したいActivityに変更してください。
		Intent ViewActivity = new Intent(context, DrawNoteActivity.class);
		//Activityを新たに起動する時はこのフラグを立てないとActivityが起動しない。
		ViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(ViewActivity);
	}

}
