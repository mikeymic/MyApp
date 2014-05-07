package com.aizak.drawnote.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import android.graphics.Paint;

public class MyPaint extends Paint implements Serializable {

	private static final long serialVersionUID = -8744912664482897467L;

	boolean aa;
	boolean filter;
	Cap cap;
	Join join;
	Style style;
	float width;
	int color;


	public MyPaint() {
		super();
	}

	public MyPaint(int flags) {
		super(flags);
	}

	public MyPaint(MyPaint paint) {
		super(paint);
		this.aa = paint.aa;
		this.filter = paint.filter;
		this.cap = paint.cap;
		this.join = paint.join;
		this.style = paint.style;
		this.width = paint.width;
		this.color = paint.color;
		setPaintStyles();
	}

	public void setPaintStyles() {
		super.setAntiAlias(aa);
		super.setFilterBitmap(filter);
		super.setStrokeWidth(width);
		super.setColor(color);
		super.setStrokeJoin(join);
		super.setStyle(style);
		super.setStrokeCap(cap);
	}

	@Override
	public void setAntiAlias(boolean aa) {
		this.aa = aa;
		super.setAntiAlias(aa);
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		this.filter = filter;
		super.setFilterBitmap(filter);
	}

	@Override
	public void setStrokeCap(Cap cap) {
		this.cap = cap;
		super.setStrokeCap(cap);
	}

	@Override
	public void setStrokeJoin(Join join) {
		this.join = join;
		super.setStrokeJoin(join);
	}

	@Override
	public void setStrokeWidth(float width) {
		this.width = width;
		super.setStrokeWidth(width);
	}

	@Override
	public void setStyle(Style style) {
		this.style = style;
		super.setStyle(style);
	}

	@Override
	public void setColor(int color) {
		this.color = color;
		super.setColor(color);
	}

	private void readObject(ObjectInputStream in) throws IOException,
	ClassNotFoundException {
		in.defaultReadObject();
		setPaintStyles();
	}

}
