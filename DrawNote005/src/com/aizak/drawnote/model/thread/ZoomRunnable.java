package com.aizak.drawnote.model.thread;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;

public class ZoomRunnable implements Runnable {

	float zoom;
	int width;
	int height;
	private final PointF cz;
	private final Matrix matrix;
	private final Canvas canvas;

	public ZoomRunnable(Canvas canvas, float zoom, int width, int height, PointF cz, Matrix matrix) {
		this.zoom = zoom;
		this.width = width;
		this.height = height;
		this.cz = cz;
		this.matrix = matrix;
		this.canvas = canvas;
	}

	@Override
	public void run() {
		//zoom値の総量でMAXとMINを判別
		zoom = Math.min(6f, Math.max(0.8f, zoom));

		float cx = width * 0.5f;
		float cy = height * 0.5f;

		float px = (cx - cz.x) * (zoom - 1.0f);
		float py = (cy - cz.y) * (zoom - 1.0f);

		matrix.reset();
		matrix.setTranslate(0, 0);
//		matrix.preTranslate(-cx, -cy);
		matrix.postScale(zoom, zoom, cx, cy);
		matrix.postTranslate(px, py);

		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

		Line tmp = new Line();
		int sssSize = Data.savedLine.size();
		for (int i = 0; i < sssSize; i++) {
			tmp.setPaint(Data.savedLine.get(i).paint);
			Data.savedLine.get(i).path.transform(matrix);
			Data.savedLine.get(i).path.transform(matrix, tmp.path);
			canvas.drawPath(tmp.path, tmp.paint);
		}
		int eeeSize = Data.editingLine.size();
		for (int j = 0; j < eeeSize; j++) {
			tmp.setPaint(Data.editingLine.get(j).paint);
			Data.editingLine.get(j).path.transform(matrix);
			Data.editingLine.get(j).path.transform(matrix, tmp.path);
			canvas.drawPath(tmp.path, tmp.paint);
		}

	}
}
