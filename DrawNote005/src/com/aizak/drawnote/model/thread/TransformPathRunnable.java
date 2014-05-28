package com.aizak.drawnote.model.thread;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.Log;

import com.aizak.drawnote.model.Line;

public class TransformPathRunnable implements Runnable {

	private CountDownLatch countDown;

	Bitmap bitmap;
	Canvas canvas;

	ArrayList<Line> lines;
	Matrix matrix;
	int start;
	int count;

	public TransformPathRunnable(ArrayList<Line> lines, int start, int count,
			Bitmap bitmap, Matrix matrix, CountDownLatch countDown) {
		this.lines = lines;
		this.start = start;
		this.count = count;
		this.matrix = matrix;
		this.bitmap = bitmap;
		this.countDown = countDown;
		canvas = new Canvas(this.bitmap);
	}

	@Override
	public void run() {
		Path path = new Path();
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (int i = start; i < count; i++) {
//			Log.d("TEST", String.valueOf("run = " + String.valueOf(i)));
			lines.get(i).path.transform(matrix, path);
			canvas.drawPath(path, lines.get(i).paint);
		}
		Log.d("TEST", String.valueOf("run finished"));
		countDown.countDown();

	}

}
