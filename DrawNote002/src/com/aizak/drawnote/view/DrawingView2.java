package com.aizak.drawnote.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
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
import com.aizak.drawnote.model.zoom.ZoomListener;
import com.aizak.drawnote.model.zoom.ZoomRatio;
import com.aizak.drawnote.model.zoom.ZoomState;
import com.aizak.drawnote.model.zoom.ZoomUtil;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.undomanager.ICommand;
import com.aizak.drawnote.util.C;

/**
 * @author takashi
 *
 */
/**
 * @author takashi
 *
 */
public class DrawingView2 extends View {

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

    private boolean isZoom = false;


	public DrawingView2(Context context) {
		super(context);
		init(context);
	}

	public DrawingView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DrawingView2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		invoker = new CommandInvoker();

		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
		bmpFilter.setAntiAlias(true);
		bmpFilter.setDither(true);
		bmpFilter.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));

		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		Data.bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		bmpCanvas = new Canvas(Data.bitmap);

		zoomListener = new ZoomListener();
		scaleGestureDetector = new ScaleGestureDetector(context, zoomListener);

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
				if (isZoom) {
					scaledLine.drawLine(bmpCanvas);
				} else {
				line.drawLine(bmpCanvas);
				}
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
		}

		if (!isZoom) {
		    /** ---------- 通常描画 ---------- **/
		    // 作り終わったpathはACTION_UPでbtimapに描画
		    // btimap描画
		    canvas.drawBitmap(Data.bitmap, 0, 0, bmpFilter);
		    // 作成中のPathをCanvasに描画（終わったらBitmapに）
		    /** ---------- 終了 ---------- **/

		} else if (isZoom) {
		    /** ---------- zoom中 ---------- **/

		    // Rectの範囲を判別
		    zoomUtil.calculateZoomRectangles(getWidth(), getHeight(), Data.bitmap.getWidth(), Data.bitmap.getHeight());

		    // btimap描画
		    canvas.drawBitmap(Data.bitmap, src, dst, bmpFilter);
		    // ズーム中に作成中のPathを描画（ズーム中なので線の太さを変える）
		    // 終わったらBitmapへ
		    scaledPaint.set(line.paint);
		    scaledPaint.setStrokeWidth(line.paint.getStrokeWidth() * zoomUtil.getScaledBrushRatio());
		    canvas.drawPath(line.path, scaledPaint);
		    /** ---------- 終了 ---------- **/
		}
	}

	/* (非 Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		zoomUtil.updateZoomRatio(w, h, Data.bitmap.getWidth(), Data.bitmap.getHeight());
		zoomUtil.updateViewRectF(getLeft(), getTop(), getRight(), getBottom());
	}

	/* (非 Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int pointer = event.getPointerCount();

		if (pointer > 1) {
		    isZoom = true;
		    boolean result = scaleGestureDetector.onTouchEvent(event);
		    zoomUtil.setScaleFactor(zoomListener.scaleFactor);
		    invalidate();
		    return result;
		}

		if (drawMode != C.DW.MODE_DRAW) {
			drawMode = C.DW.MODE_DRAW;
		}

		newP.set(event.getX(), event.getY());


		int size = event.getHistorySize();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

			    if (isZoom) {
					/** ---------- zoom中 ---------- **/
//			    	scaledLine = new Line();
					PointF scaledPoint = zoomUtil.scaleCoordinates(newP.x, newP.y);
					scaledLine.setPaint(createPaint(color));
					scaledLine.moveTo(scaledPoint);
				    }
			    /** ---------- 通常描画 ---------- **/
				line = new Line();
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

			    if (isZoom) {
					/** ---------- zoom中 ---------- **/
					PointF scaledPoint = zoomUtil.scaleCoordinates(newP.x, newP.y);
					scaledLine.lineTo(scaledPoint);
				    }
			    /** ---------- 通常描画 ---------- **/
//				for (int i = 0; i < size; i++) {
//					float hisX = event.getHistoricalX(i);
//					float hisY = event.getHistoricalY(i);
//					line.lineTo(hisX, hisY);
//				}
			    line.lineTo(newP);
			    oldP.set(newP);

				break;
			case MotionEvent.ACTION_UP:

				ICommand command;
			    if (isZoom) {
					/** ---------- zoom中 ---------- **/
			    	PointF scaledPoint = zoomUtil.scaleCoordinates(newP.x, newP.y);
					scaledLine.setLastPoint(scaledPoint);
					command = new AddLineCommand(scaledLine);
				    } else {
					/** ---------- 通常描画 ---------- **/
					line.setLastPoint(newP);
					command = new AddLineCommand(line);
				    }
			    invoker.invoke(command);
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
