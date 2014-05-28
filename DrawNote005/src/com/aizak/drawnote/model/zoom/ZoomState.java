package com.aizak.drawnote.model.zoom;

import java.util.Observable;


public class ZoomState extends Observable {

	private float zoomScale = 1.0f;
	private float panX;
	private float panY;
	private float span;
	private float focusX;
	private float focusY;

	public float getZoomX(float ratio) {
		return Math.min(zoomScale, zoomScale * ratio);
	}

	public float getZoomY(float ratio) {
		return Math.min(zoomScale, zoomScale / ratio);
	}

	// ------------------------ Getters & Setters ------------------------ //

	/**
	 * @return zoomScale
	 */
	public float getZoomScale() {
		return zoomScale;
	}

	/**
	 * @return span
	 */
	public float getSpan() {
		return span;
	}

	/**
	 * @return focusX
	 */
	public float getFocusX() {
		return focusX;
	}

	/**
	 * @return focusY
	 */
	public float getFocusY() {
		return focusY;
	}

	public float getZoom() {
		return zoomScale;
	}

	public float getPanX() {
		return panX;
	}

	public float getPanY() {
		return panY;
	}





	/**
	 * @param zoomScale セットする zoomScale
	 */
	public void setZoomScale(float zoomScale) {
		this.zoomScale = zoomScale;
	}

	/**
	 * @param span セットする span
	 */
	public void setSpan(float span) {
		this.span = span;
		setChanged();
	}

	/**
	 * @param focusX セットする focusX
	 */
	public void setFocusX(float focusX) {
		this.focusX = focusX;
		setChanged();
	}

	/**
	 * @param focusY セットする focusY
	 */
	public void setFocusY(float focusY) {
		this.focusY = focusY;
		setChanged();
	}

	public void setZoom(float zoomScale) {
		if (this.zoomScale != zoomScale) {
			this.zoomScale = zoomScale;
			setChanged();
		}
	}

	public void setPanX(float panX) {
		if (this.panX != panX) {
			this.panX = panX;
			setChanged();
		}
	}

	public void setPanY(float panY) {
		if (this.panY != panY) {
			this.panY = panY;
			setChanged();
		}
	}
}
