package com.aizak.drawnote.model.zoom;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

public class ZoomListener implements OnScaleGestureListener {

	public float zoom = 1.0f;
	public float span;
	public float focusX;
	public float focusY;
    public boolean isZoom = false;

    private final ZoomState mState = new ZoomState();

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
	isZoom = true;

	return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
    	zoom *= detector.getScaleFactor();
    	span = detector.getCurrentSpan();
    	focusX = detector.getFocusX();
    	focusY = detector.getFocusY();
//    	mState.setZoom(mState.getZoom()* detector.getScaleFactor());
//    	mState.setSpan(detector.getCurrentSpan());
//    	mState.setFocusX(detector.getFocusX());
//    	mState.setFocusY(detector.getFocusX());

	mState.notifyObservers(mState);
	return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    	isZoom = false;
    }


}
