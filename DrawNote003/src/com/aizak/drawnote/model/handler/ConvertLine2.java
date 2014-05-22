package com.aizak.drawnote.model.handler;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;

import com.aizak.drawnote.model.Line;

public class ConvertLine2 implements Runnable {

	Matrix matrix;
	Bitmap b;
	Line tmp;
	Canvas c;
	int w;
	int h;

	public ConvertLine2(Bitmap b, Matrix matrix) {
		this.b = b;
		this.matrix = matrix;
		tmp = new Line();
		c = new Canvas(b);
	}

	@Override
	public void run() {
		c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	}

}
