package com.aizak.drawnote.model.zoom;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.view.DrawingView;

public class ZoomPanUtil {

	public static class Anchor {

		public static final int CENTER = 0;
		public static final int TOPLEFT = 1;
	}

	public static final int NONE = 0;
	public static final int DRAG = 1;
	public static final int ZOOM = 2;
	public int mode = NONE;

	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	public float oldDist = 1f;

	// / The current pan position
	PointF currentPan;
	// / The current zoom position
	float currentZoom;
	// / The windows dimensions that we are zooming/panning in
	View window;
	View child;
	Matrix matrix;
	// Pan jitter is a workaround to get the video view to update its layout
	// properly when zoom is changed
	public int panJitter = 0;
	public int anchor;

	public ZoomPanUtil(int anchor, DrawingView window) {
		// Initialize class fields
		currentPan = new PointF(0, 0);
		currentZoom = 1f;

		this.window = window;
		matrix = new Matrix();
		// matrix.setTranslate(0, 0);
		// matrix.setScale(1.0f, 1.0f);
		this.anchor = anchor;
		onPanZoomChanged();
	}

	public float spacing(MotionEvent event) {

		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// Calculate the mid point of the first two fingers
	public void midPoint(MotionEvent event) {
		// ...
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		mid.set(x / 2, y / 2);
	}

	public void doZoom(float scale) {

		float oldZoom = currentZoom;

		// multiply in the zoom change
		// currentZoom *= scale;
		currentZoom = scale;

		// this limits the zoom
		currentZoom = Math.max(getMinimumZoom(), currentZoom);
		currentZoom = Math.min(8f, currentZoom);

		// Adjust the pan accordingly
		// Need to make it such that the point under the zoomCenter remains
		// under the zoom center after the zoom

		// calculate in fractions of the image so:

		float width = window.getWidth();
		float height = window.getHeight();
		float oldScaledWidth = width * oldZoom;
		float oldScaledHeight = height * oldZoom;
		float newScaledWidth = width * currentZoom;
		float newScaledHeight = height * currentZoom;

		if (anchor == Anchor.CENTER) {

			float reqXPos = ((oldScaledWidth - width) * 0.5f + mid.x - currentPan.x)
					/ oldScaledWidth;
			float reqYPos = ((oldScaledHeight - height) * 0.5f + mid.y - currentPan.y)
					/ oldScaledHeight;
			float actualXPos = ((newScaledWidth - width) * 0.5f + mid.x - currentPan.x)
					/ newScaledWidth;
			float actualYPos = ((newScaledHeight - height) * 0.5f + mid.y - currentPan.y)
					/ newScaledHeight;

			currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
			currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
		} else {
			// assuming top left
			float reqXPos = (mid.x - currentPan.x) / oldScaledWidth;
			float reqYPos = (mid.y - currentPan.y) / oldScaledHeight;
			float actualXPos = (mid.x - currentPan.x) / newScaledWidth;
			float actualYPos = (mid.y - currentPan.y) / newScaledHeight;
			currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
			currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
		}

		onPanZoomChanged();
	}

	public void doPan(float panX, float panY) {
		currentPan.x += panX;
		currentPan.y += panY;
		onPanZoomChanged();
	}

	private float getMinimumZoom() {
		return 1f;
	}

	// / Call this to reset the Pan/Zoom state machine
	public void reset() {
		// Reset zoom and pan
		currentZoom = getMinimumZoom();
		currentPan = new PointF(0f, 0f);
		onPanZoomChanged();
	}

	public Matrix onPanZoomChanged() {

		// Things to try: use a scroll view and set the pan from the scrollview
		// when panning, and set the pan of the scroll view when zooming

		float winWidth = window.getWidth();
		float winHeight = window.getHeight();

		if (anchor == Anchor.CENTER) {

			float maxPanX = (currentZoom - 1f) * window.getWidth() * 0.5f;
			float maxPanY = (currentZoom - 1f) * window.getHeight() * 0.5f;
			currentPan.x = Math.max(-maxPanX, Math.min(maxPanX, currentPan.x));
			currentPan.y = Math.max(-maxPanY, Math.min(maxPanY, currentPan.y));
		} else {
			// assume top left
			float maxPanX = (currentZoom - 1f) * window.getWidth();
			float maxPanY = (currentZoom - 1f) * window.getHeight();
			currentPan.x = Math.max(-maxPanX, Math.min(0, currentPan.x));
			currentPan.y = Math.max(-maxPanY, Math.min(0, currentPan.y));
		}

		float bmWidth = Data.bitmap.getWidth();
		float bmHeight = Data.bitmap.getHeight();

		float fitToWindow = Math.min(winWidth / bmWidth, winHeight / bmHeight);
		float xOffset = (winWidth - bmWidth * fitToWindow) * 0.5f * currentZoom;
		float yOffset = (winHeight - bmHeight * fitToWindow) * 0.5f
				* currentZoom;

		// matrix.reset();
		matrix.postScale(currentZoom * fitToWindow, currentZoom * fitToWindow);
		matrix.postTranslate(currentPan.x + xOffset, currentPan.y + yOffset);
		return matrix;
	}

}
