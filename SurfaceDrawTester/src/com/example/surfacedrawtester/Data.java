package com.example.surfacedrawtester;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Data {
	public static Bitmap bitmap;
	public static ArrayList<Line> savedLine = new ArrayList<Line>();
	public static ArrayList<Line> editingLine = new ArrayList<Line>();
	public static Canvas canvas;

	public static float sf = 1.0f;
}
