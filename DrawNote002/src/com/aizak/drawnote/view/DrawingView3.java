package com.aizak.drawnote.view;

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
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff;
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
import com.aizak.drawnote.model.zoom.ZoomRatio;
import com.aizak.drawnote.model.zoom.ZoomState;
import com.aizak.drawnote.model.zoom.ZoomUtil;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.util.C;

/**
 * @author takashi
 *
 */
/**
 * @author takashi
 *
 */
public class DrawingView3 extends View {

	private static final int TOUCH_TOLERANCE = 4;

	private int drawMode;
	private int color = Color.BLACK;
	private int width = 3;

	private Line line;
	private Line scaledLine = line = new Line();
	private CommandInvoker invoker;

	private Paint scaledPaint = new Paint();
	private Paint bmpFilter;
	private Canvas bmpCanvas;

	// リスナー
	// ScrollListener scrollListener;
	private ZoomListener zoomListener;
	private ScaleGestureDetector scaleGestureDetector;
	private ScrollListener scrollListener;

	// Obverbale
	ZoomRatio zoomRatio;
	ZoomState zoomState;

	private Rect src = new Rect();
	private Rect dst = new Rect();
	private ZoomUtil zoomUtil = new ZoomUtil(src, dst);

	// 座標
	private PointF newP = new PointF();
	private PointF oldP = new PointF();
	private PointF distP = new PointF();

	Matrix matrix;

	private boolean isZoom = false;

	public DrawingView3(Context context) {
		super(context);
		init(context);
	}

	public DrawingView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawingView3(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		invoker = new CommandInvoker();

		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		Data.bitmap = Bitmap.createBitmap(rect.width(), rect.height(),Config.ARGB_8888);
		bmpCanvas = new Canvas(Data.bitmap);
		createBmpPaint();

		zoomListener = new ZoomListener();
		scrollListener = new ScrollListener();
		scaleGestureDetector = new ScaleGestureDetector(context, zoomListener);

		matrix = new Matrix();
		matrix.setScale(1.0f, 1.0f);
		matrix.setTranslate(0, 0);
	}

	private MyPaint createPaint(int color) {
		MyPaint paint = new MyPaint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));
		paint.setStrokeWidth(6);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStyle(Style.STROKE);
		paint.setColor(color);
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
			line.drawLine(bmpCanvas);
			break;
		case C.DW.MODE_UNDO:
		case C.DW.MODE_REDO:
			bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

			int sSize = Data.savedLine.size();
			for (int i = 0; i < sSize; i++) {
				Line line = Data.savedLine.get(i);
				line.drawLine(bmpCanvas);
			}
			int eSize = Data.editingLine.size();
			for (int i = 0; i < eSize; i++) {
				Line line = Data.editingLine.get(i);
				line.drawLine(bmpCanvas);
			}
			break;
		case C.DW.MODE_MOVE:
			bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

			int ssSize = Data.savedLine.size();
			for (int i = 0; i < ssSize; i++) {
				Line line = Data.savedLine.get(i);
				line.path.transform(matrix);
				line.drawLine(bmpCanvas);
			}
			int eeSize = Data.editingLine.size();
			for (int i = 0; i < eeSize; i++) {
				Line line = Data.editingLine.get(i);
				line.path.transform(matrix);
				line.drawLine(bmpCanvas);
			}
			break;
		case C.DW.MODE_ZOOM:
			bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			int sssSize = Data.savedLine.size();
			matrix.postScale(zoomUtil.getScaleFactor(), zoomUtil.getScaleFactor());
			matrix.postTranslate(zoomUtil.getScaleFactor()/10, zoomUtil.getScaleFactor()/10);
			for (int i = 0; i < sssSize; i++) {
				Line line = new Line();
				line.paint = Data.savedLine.get(i).paint;
				Data.savedLine.get(i).path.transform(matrix, line.path);
				bmpCanvas.drawPath(line.path, line.paint);
			}
			int eeeSize = Data.editingLine.size();
			for (int i = 0; i < eeeSize; i++) {
				Line line = new Line();
				line.paint = Data.editingLine.get(i).paint;
				Data.editingLine.get(i).path.transform(matrix, line.path);
				bmpCanvas.drawPath(line.path, line.paint);
			}
		}
		canvas.drawBitmap(Data.bitmap, 0, 0, bmpFilter);


			zoomUtil.calculateZoomRectangles(getWidth(), getHeight(), Data.bitmap.getWidth(), Data.bitmap.getHeight());
//			// btimap描画
//			canvas.drawBitmap(Data.bitmap, src, dst, bmpFilter);
//			scaledPaint.set(line.paint);
//			scaledPaint.setStrokeWidth(line.paint.getStrokeWidth() * zoomUtil.getScaledBrushRatio());
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
	float moveX = 0f;
	float moveY = 0f;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int pointer = event.getPointerCount();

		if (pointer > 1) {
			isZoom = true;
			setDrawMode(C.DW.MODE_ZOOM);
			boolean result = scaleGestureDetector.onTouchEvent(event);
			zoomUtil.setScaleFactor(zoomListener.scaleFactor);
			invalidate();
			return result;
		}

//		if (pointer > 1) {
//			setDrawMode(C.DW.MODE_MOVE);
////			boolean result = scrollListener.onTouch(getRootView(), event);
////			float moveX = scrollListener.moveX;
////			float moveY = scrollListener.moveY;
//
//	    	newP = new PointF(event.getX(), event.getY());
//
//	    	switch (event.getAction()) {
//	    	case MotionEvent.ACTION_DOWN:
//	    		oldP.set(newP);
//
//	    	    break;
//	    	case MotionEvent.ACTION_MOVE:
//
//	    		if (Math.abs(moveX - (newP.x - oldP.x) ) > 10) {
//	    			moveX = newP.x - oldP.x;;
//				}
//	    		if (Math.abs(moveY - (newP.y - oldP.y) ) > 10) {
//	    			moveY = newP.y - oldP.y;
//	    		}
//
//	    		oldP.set(newP);
//	    	    break;
//	    	case MotionEvent.ACTION_UP:
//	    		moveX = 0;
//	    		moveY = 0;
//
//	    	    break;
//	    	}

//	    	matrix.setTranslate(moveX, moveY);
//			invalidate();
//			return true;
//		}

		newP.set(event.getX(), event.getY());

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			line = new Line();
			drawMode = C.DW.MODE_DRAW;
//			line.moveTo(zoomUtil.scaleCoordinates(newP.x, newP.y));
			line.moveTo(newP);
			line.setPaint(createPaint(color));
			oldP.set(newP);
			break;
		case MotionEvent.ACTION_MOVE:

			/*-----------タッチポイントが移動しているかの確認----------*/
			// 移動していない時
			distP.set(Math.abs(newP.x - oldP.x), Math.abs(newP.y - oldP.y));
			if (distP.x < TOUCH_TOLERANCE && distP.y < TOUCH_TOLERANCE) {
				break;
			}

			// 移動している時
//			line.lineTo(zoomUtil.scaleCoordinates(newP.x, newP.y));
			line.lineTo(newP);
			oldP.set(newP);

			break;
		case MotionEvent.ACTION_UP:
//			line.setLastPoint(zoomUtil.scaleCoordinates(newP.x, newP.y));
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
		this.color = color;
	}

	public void setBmpCanvas(Canvas bmpCanvas) {
		this.bmpCanvas = bmpCanvas;
	}
}
