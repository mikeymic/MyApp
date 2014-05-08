package com.aizak.drawnote.activity.listener;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class ZoomListener implements OnScaleGestureListener {

	public float scaleFactor = 1.0f;
	public float oldSf = 1.0f;
	public float newSf = 1.0f;
	private float oldFoucusX;
	private float oldFoucusY;
	public float currentFoucusX;
	public float currentFoucusY;
	public float span;

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		oldSf = detector.getScaleFactor();
		span = detector.getCurrentSpan();
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scaleFactor *= detector.getScaleFactor();

		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

}
