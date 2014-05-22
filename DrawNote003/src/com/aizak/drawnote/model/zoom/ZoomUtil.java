package com.aizak.drawnote.model.zoom;

import android.graphics.PointF;
import android.graphics.Rect;

public class ZoomUtil {

	private int left;
	private int top;
	private int right;
	private int bottom;

	private Rect src;
	private Rect dst;

    private float zoomRatioF;
    private float scaleFactor = 1.1f;

    private float scaledBrushRatio;


	public ZoomUtil(Rect src, Rect dst) {
		this.src = src;
		this.dst = dst;
	}

    public PointF scaleCoordinates(float x, float y) {
    	PointF p = new PointF((x - dst.left) / (dst.width()) * (src.width()) + src.left,
    			(y - dst.top) / (dst.height()) * (src.height()) + src.top);
	return p;
			}

    public void calculateZoomRectangles(int vWidth, int vHeight, int bWidth, int bHeight) {
	final int viewWidth = vWidth;
	final int viewHeight = vHeight;
	final int bmpWidth = bWidth;
	final int bmpHeight = bHeight;

	// 画面回転した時に比率が変わるので、それを考慮に入れる
	final float zoomX = Math.min(scaleFactor, scaleFactor * zoomRatioF)
		* viewWidth / bmpWidth;
	final float zoomY = Math.min(scaleFactor, scaleFactor * zoomRatioF)
		* viewHeight / bmpHeight;

	final float panY = 0.5f;
	final float panX = 0.5f;

	src.left = (int) ((panX * bmpWidth) - (viewWidth / (zoomX * 2)));
	src.top = (int) ((panY * bmpHeight) - (viewHeight / (zoomY * 2)));
	src.right = (int) (src.left + (viewWidth / zoomX));
	src.bottom = (int) (src.top + (viewHeight / zoomY));

	dst.left = left;
	dst.top = top;
	dst.right = right;
	dst.bottom = bottom;

	// 画面をはみでないように設定
	if (src.left < 0) {
	    dst.left += -src.left * zoomX;
	    src.left = 0;
	}
	if (src.right > bmpWidth) {
	    dst.right -= (src.right - bmpWidth) * zoomX;
	    src.right = bmpWidth;
	}
	if (src.top < 0) {
	    dst.top += -src.top * zoomY;
	    src.top = 0;
	}
	if (src.bottom > bmpHeight) {
	    dst.bottom -= (src.bottom - bmpHeight) * zoomY;
	    src.bottom = bmpHeight;
	}

	// ズ―ム倍率に合わせてペンの太さを変える
	scaledBrushRatio = ((float) dst.width() / (float) src.width());
    }

    public void updateZoomRatio(float viewWidth, float viewHeight,
    	    float contentWidth, float contentHeight) {
    	zoomRatioF = (contentWidth / contentHeight);
        }

    public void updateViewRectF(int left, int top, int right, int bottom) {
    	this.left = left;
    	this.top = top;
    	this.right = right;
    	this.bottom = bottom;
	}

    public void setScaleFactor(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

    public float getScaledBrushRatio() {
		return scaledBrushRatio;
	}

    public float getScaleFactor() {
		return scaleFactor;
	}
}
