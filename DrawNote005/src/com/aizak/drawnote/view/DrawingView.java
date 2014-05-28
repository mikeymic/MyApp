package com.aizak.drawnote.view;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.aizak.drawnote.controller.Note.OnBackgroundAlphaListener;
import com.aizak.drawnote.controller.Note.OnPenWidthListener;
import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.MyPaint;
import com.aizak.drawnote.model.thread.MyRunnable;
import com.aizak.drawnote.model.thread.MyThread;
import com.aizak.drawnote.model.thread.TransformPathRunnable;
import com.aizak.drawnote.model.zoom.ZoomListener;
import com.aizak.drawnote.model.zoom.ZoomPanUtil;
import com.aizak.drawnote.model.zoom.ZoomState;
import com.aizak.drawnote.model.zoom.ZoomUtil;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.view.ColorPickerDialog.OnColorChangedListener;
import com.aizak.drawnote.view.drawble.TextLineDrawable;

/**
 * @author takashi
 *
 */
public class DrawingView extends View implements OnBackgroundAlphaListener,
		OnColorChangedListener, OnPenWidthListener , Observer {

	private static final int TOUCH_TOLERANCE = 4;

	private int drawMode;
	// private int backgroundColor = Color.argb(100, 255, 255, 255);;
	private int penColor = Color.BLACK;
	private int penWidth = 3;

	private Line line;
	// private final Line scaledLine = line = new Line();
	private CommandInvoker invoker;

	private final Paint scaledPaint = new Paint();
	private Paint bmpFilter;

	// リスナー
	// ScrollListener scrollListener;
	private ZoomListener zoomListener;
	private ScaleGestureDetector scaleGestureDetector;

	// Obverbale
	ZoomState zoomState;

	public float zoom = 1.0f;
	public float span;
	public float focusX;
	public float focusY;
	public int centerX = getWidth() / 2;
	public int centerY = getHeight() / 2;

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

	MyThread thread = new MyThread();
	final int THREAD_POOL_COUNT = 3;
	volatile Bitmap[] tmpBitmap = new Bitmap[THREAD_POOL_COUNT];

	private boolean isZoom = false;

	ZoomPanUtil zoomPanUtil;
	PointF cz = new PointF();
	PointF currentPan = new PointF();

	ShapeDrawable drawable;

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

		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		Data.bitmap = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);
		Data.canvas = new Canvas(Data.bitmap);
		createBmpPaint();

		for (int i = 0; i < THREAD_POOL_COUNT; i++) {
			tmpBitmap[i] = Bitmap.createBitmap(rect.width(), rect.height(),
					Config.ARGB_8888);
		}

		matrix = new Matrix();
		drawable = new TextLineDrawable(rect.width(), rect.height(), matrix);
		setBackgroundDrawable(drawable);

		zoomListener = new ZoomListener();
		// scrollListener = new ScrollListener();
		scaleGestureDetector = new ScaleGestureDetector(context, zoomListener);

		zoomPanUtil = new ZoomPanUtil(ZoomPanUtil.NONE, this);

		thread.start();
	}

	private MyPaint createPaint(int color, int width) {
		MyPaint paint = new MyPaint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(width);
		paint.setColor(color);
		paint.setMaskFilter(30.0f, Blur.NORMAL);
		return paint;
	}

	private void createBmpPaint() {
		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
		bmpFilter.setAntiAlias(true);
		bmpFilter.setDither(true);
		bmpFilter.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));
	}

	public void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
		switch (drawMode) {
		case C.DW.MODE_CLEAR:
			Log.d("TEST", "through MODE_CLEAR");
			break;
		case C.DW.MODE_UNDO:
			invoker.undo();
			thread.handler.post(new MyRunnable(Data.canvas));
			break;
		case C.DW.MODE_REDO:
			invoker.redo();
			thread.handler.post(new MyRunnable(Data.canvas));
			break;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (drawMode) {
		case C.DW.MODE_CLEAR:
			Data.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			Log.d("TEST", "DrawingView#onDraw#MODE_CLEAR");
			break;
		case C.DW.MODE_DRAW:
			Data.canvas.drawPath(line.path, line.paint);
			break;
		case C.DW.MODE_UNDO:
		case C.DW.MODE_REDO:
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			break;
		case C.DW.MODE_MOVE:
			break;
		case C.DW.MODE_ZOOM:

			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			break;
		}
		// canvas.drawColor(backgroundColor);
		canvas.drawBitmap(Data.bitmap, 0, 0, bmpFilter);
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

		// switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// case MotionEvent.ACTION_POINTER_DOWN:
		// // float cx = (event.getX(0) + event.getX(1)) / 2;
		// // float cy = (event.getY(0) + event.getY(1)) / 2;
		// // cz.set(cx, cy);
		// break;
		// case MotionEvent.ACTION_MOVE:
		// break;
		// case MotionEvent.ACTION_UP:
		// break;
		// }
		if (pointer > 1) {
			setDrawMode(C.DW.MODE_ZOOM);
			boolean result = scaleGestureDetector.onTouchEvent(event);
			zoom = zoomListener.zoom;
			focusX = zoomListener.focusX;
			focusY = zoomListener.focusY;
			isZoom = zoomListener.isZoom;
			((TextLineDrawable) drawable).iszoom = isZoom;
			transformPaths();
			return result;
		} else {
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
			invalidateDrawable(drawable);

		}

		return true;
	}

	@Override
	public void onBackgroundAlphaChanged(int alpha) {
		// backgroundColor = Color.argb(alpha, 255, 255, 255);
		drawable.setAlpha(alpha);
		invalidate();
		invalidateDrawable(drawable);
	}

	@Override
	public void colorChanged(int color) {
		penColor = color;

	}

	@Override
	public void onPenWidthChanged(int penWidth) {
		this.penWidth = penWidth;
		Log.d("TEST", "pen width = " + String.valueOf(this.penWidth));

	}

	@Override
	public void update(Observable observable, Object data) {
		zoomState = (ZoomState) data;
	}

	public void measureMatrix() {
		// float zoom = zoomState.getZoom();
		// float focusX = zoomState.getFocusX();
		// float focusY = zoomState.getFocusY();
		Log.d("TEST", "zoom = " + String.valueOf(zoom));
		zoom = Math.min(5.0f, Math.max(1.0f, zoom));

		if (zoom == 1.0f) {
			centerX = 0;
			centerY = 0;
			focusX = 0;
			focusY = 0;
		} else {
			centerX = getWidth() / 2;
			centerY = getHeight() / 2;
		}

		Log.d("TEST", String.valueOf(zoom));
		matrix.reset();
		matrix.preTranslate(-centerX, -centerY);
		matrix.postScale(zoom, zoom);
		matrix.postTranslate(focusX, focusY);
	}

	public void transformPaths() {
		measureMatrix();
		ExecutorService executor = Executors
				.newFixedThreadPool(THREAD_POOL_COUNT);
		CountDownLatch countDownLatch = new CountDownLatch(THREAD_POOL_COUNT);
		try {
			int size = Data.editingLine.size();
			for (int i = 0; i < THREAD_POOL_COUNT; i++) {
				int start = (i * (size / THREAD_POOL_COUNT));
				if (start != 0) {
					start = start + 1;
				}
				int count = ((i + 1) * (size / THREAD_POOL_COUNT));
				if (size % THREAD_POOL_COUNT == 1) {
					count = count + 1;
				}
				Log.d("TEST",
						"i = " + String.valueOf(i) + ": start = "
								+ String.valueOf(start) + ": count ="
								+ String.valueOf(count));
				executor.submit(new TransformPathRunnable(Data.editingLine,
						start, count, tmpBitmap[i], matrix, countDownLatch));
				Log.d("TEST", String.valueOf("executor stared"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}

		try {
			countDownLatch.await();
			Log.d("TEST", String.valueOf("countDown finished"));
		} catch (InterruptedException e) {
			Log.d("TEST", String.valueOf("countDown error"));
		}

		Data.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		for (int i = 0; i < THREAD_POOL_COUNT; i++) {
			Data.canvas.drawBitmap(tmpBitmap[i], 0, 0, null);
		}
		invalidate();
		invalidateDrawable(drawable);
		// Log.d("TEST", String.valueOf("invalidate finished"));
	}



}
