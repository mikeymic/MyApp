package com.example.surfacedrawtester;

import java.io.Serializable;

import android.graphics.Canvas;
import android.graphics.PointF;

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

	public void moveTo(PointF p) {
		path.moveTo(p.x, p.y);
	}

	public void moveTo(float x, float y) {
		path.moveTo(x, y);
	}

	public void lineTo(PointF p) {
		path.lineTo(p.x, p.y);
	}

	public void lineTo(float x, float y) {
		path.lineTo(x, y);
	}

	public void setLastPoint(PointF p) {
		path.setLastPoint(p.x, p.y);
	}

	public void setLastPoint(float dx, float dy) {
		path.setLastPoint(dx, dy);
	}

	public void drawLine(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
