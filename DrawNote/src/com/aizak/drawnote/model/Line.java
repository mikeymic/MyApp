package com.aizak.drawnote.model;

import java.io.Serializable;

import android.graphics.Canvas;

public class Line implements Serializable {

	private static final long serialVersionUID = 6255752248513019027L;

	public CustomPath path;
	public MyPaint paint;

	public Line() {
		path = new CustomPath();
		paint = new MyPaint();
	}

	public Line(CustomPath path, MyPaint paint) {
		this.path = new CustomPath(path);
		this.paint = new MyPaint(paint);
	}

	public void setPaint(MyPaint paint) {
		this.paint = new MyPaint(paint);
	}

	public void moveTo(float x, float y) {
		path.moveTo(x, y);
	}

	public void lineTo(float x, float y) {
		path.lineTo(x, y);
	}

	public void setLastPoint(float dx, float dy) {
		path.setLastPoint(dx, dy);
	}

	public void drawLine(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
