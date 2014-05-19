package com.aizak.drawnote.model.listener;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.aizak.drawnote.model.zoom.ZoomState;

public class ZoomListener implements OnScaleGestureListener {

	public float scaleFactor = 1.0f;
	private float oldFoucusX;
	private float oldFoucusY;
	private float currentFoucusX;
	private float currentFoucusY;

	private final ZoomState mState = new ZoomState();

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		oldFoucusX = detector.getFocusX();
		oldFoucusY = detector.getFocusY();
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		scaleFactor *= detector.getScaleFactor();

		mState.notifyObservers();
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

}
