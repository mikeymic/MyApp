package com.aizak.drawnote.model.thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;

public class MyRunnable implements Runnable {

	Canvas canvas;

	public MyRunnable(Canvas cannvas) {
		canvas = cannvas;
	}

	@Override
	public void run() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		int sSize = Data.savedLine.size();
		for (int i = 0; i < sSize; i++) {
			Line line = Data.savedLine.get(i);
			line.drawLine(canvas);
		}
		int eSize = Data.editingLine.size();
		for (int i = 0; i < eSize; i++) {
			Line line = Data.editingLine.get(i);
			line.drawLine(canvas);
		}
	}

}
