package com.aizak.drawnote.view.drawble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;


public class TextLineDrawable extends ShapeDrawable {

	private Path[] path = new Path[24];
	private int defaultSpace;
	private int width;
	private int height;
	private int margin = 20;

	private Matrix matrix;
	private Paint paint;
	private int alpha = 200;
	private int backgroudColor = Color.argb(alpha, 255, 255, 255);
	private int lineColor = Color.argb(alpha, 130, 130, 200);

	public boolean iszoom = false;

	public TextLineDrawable(int width, int height, Matrix matrix) {
		this.width = width;
		this.height = height;
		this.matrix = matrix;

		createPaint();

		defaultSpace = height/24;

		for (int i = 0; i < path.length; i++) {
			for (int j = 0; j < 2; j++) {
				if (j ==  0) {
					path[i] = new Path();
					path[i].moveTo(0+margin, defaultSpace*i);
				}
				path[i].lineTo(width-margin, defaultSpace*i);
			}
		}

	}

	private void createPaint() {
		paint = new Paint();
		paint.setAntiAlias(false);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeWidth(3);
		paint.setColor(lineColor);
	}

	@Override
	public void draw(Canvas canvas) {
		Path p = new Path();
		canvas.drawColor(backgroudColor);
		for (int i = 0; i < path.length; i++) {
			if (iszoom) {
				path[i].transform(matrix, p);
			} else {
				p = path[i];
			}
			canvas.drawPath(p, paint);
		}


	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
		updatePaintColor();
		paint.setColor(lineColor);
	}

	private void updatePaintColor() {
		backgroudColor = Color.argb(alpha, 255, 255, 255);
		lineColor = Color.argb(alpha/4, 130, 130, 200);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

}
