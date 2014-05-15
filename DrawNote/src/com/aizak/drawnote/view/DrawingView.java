package com.aizak.drawnote.view;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.aizak.drawnote.model.EditingLine;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.MyPaint;
import com.aizak.drawnote.model.SavedLine;
import com.aizak.drawnote.undomanager.AddLineCommand;
import com.aizak.drawnote.undomanager.CommandInvoker;
import com.aizak.drawnote.undomanager.ICommand;
import com.aizak.drawnote.util.C;

public class DrawingView extends View {

	private int drawMode;

	private int color = Color.BLACK;
	private int width = 3;

	private Line line;
	public EditingLine editingLines;
	public SavedLine savedLines;

	private CommandInvoker invoker;

	private Paint bmpFilter;

	private Bitmap bitmap;
	private Canvas bmpCanvas;

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
		savedLines = new SavedLine();
		editingLines = new EditingLine();
		invoker = new CommandInvoker();

		bmpFilter = new Paint();
		bmpFilter.setFilterBitmap(true);
		bmpFilter.setAntiAlias(true);
		bmpFilter.setDither(true);
		bmpFilter.setMaskFilter(new BlurMaskFilter(0.5f, Blur.NORMAL));

		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		bitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
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
			case C.DW.MODE_CLEAR:
				Log.d("TEST", "through MODE_CLEAR");
				ICommand command =
						new AddLineCommand(editingLines,
								new Line());
				invoker.clear(command);
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
//				bmpCanvas.drawColor(Color.BLUE);
//				if (savedLines != null) {
//					bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//					int sizes = savedLines.getLines().size();
//					for (int i = 0; i < sizes; i++) {
//						savedLines.getLines().get(i).drawLine(bmpCanvas);
//					}
//				}
				break;
			case C.DW.MODE_DRAW:
				line.drawLine(bmpCanvas);
				break;
			case C.DW.MODE_UNDO:
			case C.DW.MODE_REDO:
				bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

				if (savedLines != null) {
					bmpCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					int sizes = savedLines.getLines().size();
					for (int i = 0; i < sizes; i++) {
						savedLines.getLines().get(i).drawLine(bmpCanvas);
					}
				}

				List<Line> lineData = editingLines.getLines();
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

		if (drawMode != C.DW.MODE_DRAW) {
			drawMode = C.DW.MODE_DRAW;
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
//				savedLines.getLines().add(line);
				ICommand command = new AddLineCommand(editingLines, line);
				invoker.invoke(command);
				break;
		}
		invalidate();
		return true;
	}

	public void setBitmap(byte[] data) {
		bitmap = BitmapFactory.decodeByteArray(data, 0, data.length).copy(Config.ARGB_8888, true);
		bmpCanvas = new Canvas(bitmap);
	}

	public EditingLine getEditingLines() {
		return editingLines;
	}

	public SavedLine getSavedLines() {
		return savedLines;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public CommandInvoker getInvoker() {
		return invoker;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setEditingLines(EditingLine editingLines) {
		this.editingLines = editingLines;
	}

	public void setSavedLines(SavedLine savedLines) {
		this.savedLines = savedLines;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setBmpCanvas(Canvas bmpCanvas) {
		this.bmpCanvas = bmpCanvas;
	}

	public Canvas getBmpCanvas() {
		return bmpCanvas;
	}
}
