package com.aizak.drawnote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.MyPaint;
import com.aizak.drawnote.model.listener.ScrollListener;
import com.aizak.drawnote.model.zoom.ZoomListener;
import com.aizak.drawnote.model.zoom.ZoomPanUtil;
import com.aizak.drawnote.model.zoom.ZoomRatio;
import com.aizak.drawnote.model.zoom.ZoomState;
import com.aizak.drawnote.model.zoom.ZoomUtil;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.view.BackgroudAlphaSeekBar.OnAlphaChangedListener;
import com.aizak.drawnote.view.ColorPickerDialog.OnColorChangedListener;

public class DrawingView extends View implements OnAlphaChangedListener, OnColorChangedListener {

	private static final int TOUCH_TOLERANCE = 4;

	private int drawMode;
	private int backgroundColor = Color.argb(100, 255, 255, 255);;
	private int penColor = Color.BLACK;
	private final int penWidth = 3;

	private Line line;
	private final Line scaledLine = line = new Line();
	private CommandInvoker invoker;

	private final Paint scaledPaint = new Paint();

	// リスナー
	// ScrollListener scrollListener;
	private ZoomListener zoomListener;
	private ScaleGestureDetector scaleGestureDetector;
	private ScrollListener scrollListener;

	// Obverbale
	ZoomRatio zoomRatio;
	ZoomState zoomState;

	private final Rect src = new Rect();
	private final Rect dst = new Rect();
	private final ZoomUtil zoomUtil = new ZoomUtil(src, dst);

	// 座標
	private final PointF newP = new PointF();
	private final PointF oldP = new PointF();
	private final PointF distP = new PointF();

	Matrix matrix;

	float moveX = 0f;
	float moveY = 0f;

	private boolean isZoom = false;

	ZoomPanUtil zoomPanUtil;

	PointF cz = new PointF();
	PointF currentPan = new PointF();
	float zoom = 1.0f;
	float oldSF = 1.0f;

	public DrawingView(Context context) {
		super(context);
		init(context);
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		invoker = new CommandInvoker();

		zoomListener = new ZoomListener();
		scrollListener = new ScrollListener();
		scaleGestureDetector = new ScaleGestureDetector(context, zoomListener);

		zoomPanUtil = new ZoomPanUtil(ZoomPanUtil.NONE, this);

		matrix = new Matrix();
	}

	private MyPaint createPaint(int color, int width) {
		MyPaint paint = new MyPaint();
		paint.setStrokeWidth(width);
		paint.setColor(color);
		return paint;
	}

	public void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
		switch (drawMode) {
			case C.DW.MODE_CLEAR:
				Log.d("TEST", "through MODE_CLEAR");
				break;
			case C.DW.MODE_UNDO:
				invoker.undo();
				break;
			case C.DW.MODE_REDO:
				invoker.redo();
				break;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (drawMode) {
			case C.DW.MODE_CLEAR:
				Log.d("TEST", "DrawingView#onDraw#MODE_CLEAR");
				break;
			case C.DW.MODE_DRAW:
				Data.drawLine(line);
				break;
			case C.DW.MODE_UNDO:
			case C.DW.MODE_REDO:
				break;
			case C.DW.MODE_MOVE:
				break;
			case C.DW.MODE_ZOOM:
				break;
		}
		canvas.drawColor(backgroundColor);
		canvas.drawBitmap(Data.bitmap, 0, 0, bmpFilter);
		zoomUtil.calculateZoomRectangles(getWidth(), getHeight(),
				Data.bitmap.getWidth(), Data.bitmap.getHeight());
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		zoomUtil.updateZoomRatio(w, h, Data.bitmap.getWidth(),
				Data.bitmap.getHeight());
		zoomUtil.updateViewRectF(getLeft(), getTop(), getRight(), getBottom());
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int pointer = event.getPointerCount();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
				float cx = (event.getX(0) + event.getX(1)) / 2;
				float cy = (event.getY(0) + event.getY(1)) / 2;
				cz.set(cx, cy);
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		if (pointer > 1) {
			isZoom = true;
			setDrawMode(C.DW.MODE_ZOOM);
			boolean result = scaleGestureDetector.onTouchEvent(event);
			zoom = zoomListener.scaleFactor;

			invalidate();
			return result;
		}

		newP.set(event.getX(), event.getY());

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawMode = C.DW.MODE_DRAW;
				line = new Line();
				// line.moveTo(zoomUtil.scaleCoordinates(newP.x, newP.y));
				line.moveTo(newP);
				line.setPaint(createPaint(penColor, penWidth));
				oldP.set(newP);
				break;
			case MotionEvent.ACTION_MOVE:

				/*-----------タッチポイントが移動しているかの確認----------*/
				// 移動していない時
				distP.set(Math.abs(newP.x - oldP.x), Math.abs(newP.y - oldP.y));
				if ((distP.x < TOUCH_TOLERANCE) && (distP.y < TOUCH_TOLERANCE)) {
					break;
				}

				// 移動している時
				// line.lineTo(zoomUtil.scaleCoordinates(newP.x, newP.y));
				line.lineTo(newP);
				oldP.set(newP);

				break;
			case MotionEvent.ACTION_UP:
				// line.setLastPoint(zoomUtil.scaleCoordinates(newP.x, newP.y));
				line.setLastPoint(newP);
				invoker.invoke(new AddLineCommand(line));
				break;
		}
		invalidate();
		return true;
	}

	public Canvas getBmpCanvas() {
		return bmpCanvas;
	}

	public void setColor(int color) {
		penColor = color;
	}

	public void setBmpCanvas(Canvas bmpCanvas) {
		this.bmpCanvas = bmpCanvas;
		setDrawMode(C.DW.MODE_CLEAR);
	}

	@Override
	public void onBackgroundAlphaChanged(int alpha) {
		backgroundColor = Color.argb(alpha, 255, 255, 255);
		invalidate();
	}

	@Override
	public void colorChanged(int color) {
		penColor = color;

	}
}
