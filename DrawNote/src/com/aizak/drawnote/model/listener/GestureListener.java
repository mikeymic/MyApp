package com.aizak.drawnote.model.listener;

import android.content.Context;
import android.graphics.Point;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.aizak.drawnote.view.DrawingView;

public class GestureListener implements OnTouchListener, OnGestureListener, OnDoubleTapListener, OnScaleGestureListener {

	private final Context context; //Activityから送られてくるSurfaceViewのContext
	private final DrawingView canvas; //Activityから送られてくるSurfaceViewのView

	private Point sPoint;  //タッチ開始時の座標

	/*--------------------<<<コンストラクタ> >> -------------------*/

	public GestureListener(Context context, DrawingView canvas) {
		this.context = context;
		this.canvas = canvas;
	}

	/*--------------------<<<タッチイベント> >> -------------------*/

	@Override
	public boolean onDown(MotionEvent e) {
		Toast.makeText(context, "called onDown()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//タッチ時[ACTION_DOWN]
		/*この後の動き：
		 * 全て呼ばれる
		 */

		sPoint = new Point((int) e.getX(), (int) e.getY());  //ラインの描画の為にタッチの開始位置を取得

		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Toast.makeText(context, "called onShowPress()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//タッチ後、指が動き出す前
		/*この後の動き：
		 * 		スクロール：呼ばれない
		 * 		フィリング：呼ばれる
		 */
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Toast.makeText(context, "called onLongPress()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//長押し
		/*この後の動き：
		 * スクロール：呼ばれない
		 * フィリング：呼ばれない
		 */
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Toast.makeText(
				context,
				"called onFling()" + " : Motion Event1 No = " + String.valueOf(e1.getAction())
						+ " : Motion Event2 No = " + String.valueOf(e2.getAction()), Toast.LENGTH_SHORT).show();
		//タッチ後一定距離を移動、離す[ACTION_UP]
		if (e2.getAction() == MotionEvent.ACTION_UP) {
			isFirstTime = !isFirstTime; //初回フラグの設定
		}
		return false;
	}

	boolean isFirstTime = true;

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
//			Toast.makeText(context, "called onScroll()" + " : Motion Event1 No = " + String.valueOf(e1.getAction()) + " : Motion Event2 No = " + String.valueOf(e2.getAction()), Toast.LENGTH_SHORT).show();

		//タッチしたまま指を滑らせたとき[ACTION_MOVE]

//		// 初回移動
//		if (isFirstTime) {
//			canvas.setNewLine(sPoint);
//			isFirstTime = !isFirstTime; //初回フラグの無効
//		}
//
//		// 初回移動以降
//		else {
//			canvas.addNewPointToLine(new Point((int)e2.getX(), (int)e2.getY()));
//		}
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Toast.makeText(context, "called onSingleTapUp()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//シングルタップ時！ダブルタップ時にも呼ばれる[ACTION_UP]
		/*onSingleTapConfirmed:呼ばれる
		 */
		//おそらく使わない
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Toast.makeText(context,
				"called onSingleTapConfirmed()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//シングルタップ！ダブルタップ時には呼ばれない[ACTION_DOWN]
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Toast.makeText(context, "called onDoubleTap()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//ダブルタップ時 //イベントの取りこぼしがあるので、こちらは使用しない事
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Toast.makeText(context, "called onDoubleTapEvent()" + " : Motion Event No = " + String.valueOf(e.getAction()),
				Toast.LENGTH_SHORT).show();
		//ダブルタップ時（押す、動かす、話す）[ACTION_DOWN, ACTION_MOVE, ACTION_UP]

//	    switch (e.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//		    if (Util.isVisible) {
//		    	Util.actionBarSetVisiblity((Activity) context, View.GONE); //アクションバーの可視化
//			} else {
//				Util.actionBarSetVisiblity((Activity) context, View.VISIBLE); //アクションバーの不可視化
//			}
//			break;
//
//		default:
//			break;
//		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		return false;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

}
