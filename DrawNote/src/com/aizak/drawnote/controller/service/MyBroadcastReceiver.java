package com.aizak.drawnote.controller.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.util.C;

public class MyBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String name = intent.getStringExtra(C.RV.INTENT_NAME);

		switch (name) {
			case C.OVERLAY.EXTRA_NAME:
				context.stopService(new Intent(context, OverlayService.class));
				String msg = context.getString(R.string.msg_overlay_service_stop);
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}
	}
}
