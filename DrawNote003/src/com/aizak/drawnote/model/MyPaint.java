package com.aizak.drawnote.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;

public class MyPaint extends Paint implements Serializable {

	private static final long serialVersionUID = -8744912664482897467L;

	boolean aa = true;
	boolean filter = true;;
	Cap cap = Cap.ROUND;
	Join join = Join.ROUND;
	Style style = Style.STROKE;
	float width = 3;
	int color = Color.BLACK;
	float blurRadius = 0.5f;
	Blur blurStyle = Blur.NORMAL;

	public MyPaint() {
		super();
	}

	public MyPaint(int flags) {
		super(flags);
	}

	public MyPaint(MyPaint paint) {
		super(paint);
		aa = paint.aa;
		filter = paint.filter;
		cap = paint.cap;
		join = paint.join;
		style = paint.style;
		width = paint.width;
		color = paint.color;
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
		super.setMaskFilter(new BlurMaskFilter(blurRadius, blurStyle));
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

	public MaskFilter setMaskFilter(float blurRadius, Blur blurStyle) {
		this.blurRadius = blurRadius;
		this.blurStyle = blurStyle;
		return super.setMaskFilter(new BlurMaskFilter(blurRadius, blurStyle));
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		setPaintStyles();
	}

}
