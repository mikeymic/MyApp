package com.example.surfacedrawtester;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceDrawingView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

	private SurfaceHolder holder;
	private Thread drawThread;
	private Redraw thread;

	int color = Color.BLUE;
	int width = 3;
	Line line;
	Canvas canvas;
	private MyPaint paint;
	PointF newP = new PointF();

	public SurfaceDrawingView(Context context) {
		super(context);
		init(context);
	}

	public SurfaceDrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SurfaceDrawingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		holder = getHolder();
		holder.setFormat(PixelFormat.TRANSLUCENT);//透明色で塗りつぶす !!これが無いと透過しないので注意
		holder.addCallback(this);

		initBitmap(context);
		initPaint(color, width);

	}

	private void initPaint(int color, int width) {
		paint = new MyPaint();
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStrokeWidth(width);
		paint.setColor(color);
	}

	private void initBitmap(Context context) {
		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		Data.bitmap = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);
		Data.canvas = new Canvas(Data.bitmap);
		Data.canvas.drawColor(Color.argb(0, 255, 255, 255));
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawThread = new Thread(this);
		drawThread.start();
		thread = new Redraw();
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void run() {
		while (thread != null) {
			if ((Data.bitmap != null) & (line != null)) {
				Log.d("TEST", "run staring");

//				Data.canvas.drawPath(line.path, line.paint);
				try {
					thread.join();
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				canvas = holder.lockCanvas();
				canvas.drawBitmap(Data.bitmap, 0, 0, null);
				holder.unlockCanvasAndPost(canvas);

			}
		}

	}

	/* (非 Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		newP.set(event.getX(0), event.getY(0));

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				line = new Line();
				line.moveTo(newP);
				line.setPaint(paint);
				break;
			case MotionEvent.ACTION_MOVE:
				line.lineTo(newP);
				break;
			case MotionEvent.ACTION_UP:
				Data.editingLine.add(line);
				break;
		}
		send();

		return true;
	}

	private void send() {
		thread.mHandler.post(new MyRunnable());

	}
}
