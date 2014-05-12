package com.aizak.drawnote.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aizak.drawnote.activity.DrawNoteActivity;
import com.aizak.drawnote.model.DataModel;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.MyPaint;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.undomanager.ICommand;

public class DrawingView extends View {

	private int drawMode;
	public static final int MODE_CLEAR = 0;
	public static final int MODE_DRAW = 1;
	public static final int MODE_UNDO = 2;
	public static final int MODE_REDO = 3;

	public int color = Color.BLACK;

	public DataModel dataModel;
	CommandInvoker invoker;
	Line line;

	private Paint bmpFilter;

	private Bitmap bitmap;
	private Canvas bmpCanvas;
	public ArrayList<Line> lines = new ArrayList<Line>();

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
		dataModel = new DataModel();
		invoker = new CommandInvoker();

		if (context instanceof DrawNoteActivity) {
			bitmap = ((DrawNoteActivity) context).getImage();
		}
		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
		bmpFilter.setAntiAlias(true);
		bmpFilter.setDither(true);
		bmpFilter.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));
		bitmap = ((DrawNoteActivity) context).Image;
		bmpCanvas = new Canvas(bitmap);
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
			case MODE_CLEAR:
				Log.d("TEST", "through MODE_CLEAR");
				ICommand command =
						new AddLineCommand(dataModel,
								new Line());
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
				Log.d("TEST", "DrawingView#onDraw#MODE_CLEAR");
//				bmpCanvas.drawColor(Color.BLUE);
				if (lines != null) {
					bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					int sizes = lines.size();
					for (int i = 0; i < sizes; i++) {
						lines.get(i).drawLine(bmpCanvas);
					}
				}
				break;
			case MODE_DRAW:
				line.drawLine(bmpCanvas);
				break;
			case MODE_UNDO:
			case MODE_REDO:
				bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				if (lines != null) {
					bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					int sizes = lines.size();
					for (int i = 0; i < sizes; i++) {
						lines.get(i).drawLine(bmpCanvas);
					}
				}

				List<Line> lineData = dataModel.lines;
				int size = lineData.size();
				for (int i = 0; i < size; i++) {
					lineData.get(i).drawLine(bmpCanvas);
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
				line = new Line();
				line.setPaint(createPaint(color));
				line.moveTo(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				for (int i = 0; i < size; i++) {
					float hisX = event.getHistoricalX(i);
					float hisY = event.getHistoricalY(i);
					line.lineTo(hisX, hisY);
				}
				line.lineTo(x, y);
				break;
			case MotionEvent.ACTION_UP:
				line.setLastPoint(x, y);
//				lines.add(line);
				ICommand command = new AddLineCommand(dataModel, line);
				invoker.invoke(command);
				break;
		}
		invalidate();
		return true;
	}

}
