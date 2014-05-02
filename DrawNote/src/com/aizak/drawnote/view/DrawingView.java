package com.aizak.drawnote.view;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aizak.drawnote.model.DataModel;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.undomanager.ICommand;

public class DrawingView extends View {

	private int drawMode;
	public static final int MODE_CLEAR = 0;
	public static final int MODE_DRAW = 1;
	public static final int MODE_UNDO = 2;
	public static final int MODE_REDO = 3;

	DataModel dataModel;
	CommandInvoker invoker;
	Line line;

	Path aLine;
	Point point;

	private Paint paint;

	private Paint bmpFilter;

	private Bitmap bitmap;
	private Canvas bmpCanvas;

	public DrawingView(Context context) {
		super(context);
		init();
	}

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		dataModel = new DataModel();
		invoker = new CommandInvoker();

		paint = createPaint();

		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
	}

	private Paint createPaint() {
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(6);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.RED);
		return paint;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		bmpCanvas = new Canvas(bitmap);
	}

	public void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
		switch (drawMode) {
			case MODE_CLEAR:
				Log.d("TEST", "through MODE_CLEAR");
				ICommand command =
						new AddLineCommand(dataModel,
								new Line(new Path(), paint, 0, 0));
				invoker.clear(command);
				break;
			case MODE_UNDO:
				invoker.undo();
				break;
			case MODE_REDO:
				invoker.redo();
				break;
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		switch (drawMode) {
			case MODE_CLEAR:
				Log.d("TEST", "through MODE_CLEAR2");
				bmpCanvas.drawColor(Color.BLUE);
				break;
			case MODE_DRAW:
				line.drawLine(bmpCanvas);
				break;
			case MODE_UNDO:
			case MODE_REDO:
				bmpCanvas.drawColor(Color.BLUE);
				List<Line> lines = dataModel.lines;
				int size = lines.size();
				for (int i = 0; i < size; i++) {
					lines.get(i).drawLine(bmpCanvas);
				}
				break;
		}
		canvas.drawBitmap(bitmap, 0, 0, bmpFilter);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (drawMode != MODE_DRAW) {
			drawMode = MODE_DRAW;
		}

		float x = event.getX();
		float y = event.getY();

		int size = event.getHistorySize();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				aLine = new Path();
				line = new Line(aLine, paint, 4, Color.GREEN);
				aLine.moveTo(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				for (int i = 0; i < size; i++) {
					float hisX = event.getHistoricalX(i);
					float hisY = event.getHistoricalY(i);
					aLine.lineTo(hisX, hisY);
				}
				aLine.lineTo(x, y);
				break;
			case MotionEvent.ACTION_UP:
				ICommand command = new AddLineCommand(dataModel, line);
				invoker.invoke(command);
				break;
		}
		invalidate();
		return true;
	}

}
