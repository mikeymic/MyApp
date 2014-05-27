package com.example.surfacedrawtester;

import android.os.Handler;
import android.os.Looper;

public class Redraw extends Thread {

	public Handler mHandler;
	public Runnable runnable;

	@Override
	public void run() {
		super.run();

		Looper.prepare();
		mHandler = new Handler();
		Looper.loop();
	}

}
