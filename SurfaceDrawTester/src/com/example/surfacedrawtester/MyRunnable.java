package com.example.surfacedrawtester;

import android.graphics.Matrix;
import android.util.Log;

public class MyRunnable implements Runnable {

	private final Matrix matrix;

	public MyRunnable() {
		matrix = new Matrix();
	}

	@Override
	public void run() {
		Log.d("TEST", "Runnable start");

		matrix.reset();
		matrix.postScale(Data.sf, Data.sf);
		for (int i = 0; i < Data.editingLine.size(); i++) {
			Data.editingLine.get(i).path.transform(matrix);
			Data.editingLine.get(i).drawLine(Data.canvas);
		}
		Log.d("TEST", "Runnable start");
	}
}
