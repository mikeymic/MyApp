package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class Line {

	public List<Point> points;
	public Path line;

	public Paint paint;
	public int strokeWidth;
	public int color;

	public Line(Path line, Paint paint, int strokeWidth, int color) {
		this.paint = paint;
		paint.setStrokeWidth(strokeWidth);
		paint.setColor(color);
		points = new ArrayList<Point>();
		this.line = line;
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	public void drawLine(Canvas canvas) {
		canvas.drawPath(line, paint);
	}

}
