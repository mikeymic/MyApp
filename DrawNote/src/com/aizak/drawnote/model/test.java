package com.aizak.drawnote.model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

public class test {

	public static Bitmap mBitmap;
	public static Canvas mCanvas;

	public static void createNewBitmap(Activity activity) {
		Rect rect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		mBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	public static void createAsNew(Bitmap bitmap) {
		Bitmap.createBitmap(bitmap);
		mCanvas = new Canvas(mBitmap);
	}

	public static void setCanvas() {
		mCanvas = new Canvas(mBitmap);
	}

}
