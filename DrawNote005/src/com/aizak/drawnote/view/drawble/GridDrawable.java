package com.aizak.drawnote.view.drawble;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.drawable.ShapeDrawable;

public class GridDrawable extends ShapeDrawable {

	private Paint paint;

	float w;
	float h;
	int width;
	int height;

	private float hCount;
	private float wCount;


	private float moveX;
	private float moveY;
	private float scaleFactor = 1.0f;

	public GridDrawable() {
		createPaint();
	}

	private void createPaint() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeWidth(3);
		paint.setColor(Color.BLUE);
		paint.setAlpha(30);
	}


	/* (非 Javadoc)
	 * @see android.graphics.drawable.ShapeDrawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		width = getBounds().width();
		height = getBounds().height();

		wCount = (float)width/(50*1.0f);
		hCount = (float)height/(50*1.0f);

		w = width/((float)width/(50*scaleFactor));
		h = height/((float)height/(50*scaleFactor));

		canvas.drawColor(Color.WHITE);

		for (int i = 1; i < wCount; i++) {
			canvas.drawLine(moveX+w*i, 0, moveX+w*i, height, paint);
		}
		for (int i = 1; i < hCount; i++) {
			canvas.drawLine(0, moveY+h*i, width, moveY+h*i, paint);
		}
	}


	/**
	 * @param moveX セットする startX
	 */
	public void setMoveX(float moveX) {
		this.moveX = moveX;
	}

	/**
	 * @param moveY セットする startY
	 */
	public void setMoveY(float moveY) {
		this.moveY = moveY;
	}

	/**
	 * @param scaleFactor セットする scaleFactor
	 */
	public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}


}
