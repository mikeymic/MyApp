package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.Observable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;

public class Data extends Observable {
	private final Bitmap bitmap;
	private final ArrayList<Line> savedLine;
	private final ArrayList<Line> editingLine;
	private static Paint bmpFilter;
	private static Canvas canvas;

	public Data(Context context) {
		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		canvas = new Canvas(bitmap);

		savedLine = new ArrayList<Line>();
		editingLine = new ArrayList<Line>();

		createBmpPaint();
	}

	private void createBmpPaint() {
		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
		bmpFilter.setAntiAlias(true);
		bmpFilter.setDither(true);
		bmpFilter.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));
	}

	public static void drawLine(Line line) {
		canvas.drawPath(line.path, bmpFilter);
	}

	public static void clear() {
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
	}
}
