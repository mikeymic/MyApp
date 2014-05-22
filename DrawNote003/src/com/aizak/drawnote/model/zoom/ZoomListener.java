package com.aizak.drawnote.model.zoom;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class ZoomListener implements OnScaleGestureListener {

    public float scaleFactor = 1.0f;
    private float oldFoucusX;
    private float oldFoucusY;
    private float currentFoucusX;
    private float currentFoucusY;
    public boolean isZoom = false;

    private final ZoomState mState = new ZoomState();

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
	oldFoucusX = detector.getFocusX();
	oldFoucusY = detector.getFocusY();
	isZoom = true;

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
    	isZoom = false;
    }


}
