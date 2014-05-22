package com.aizak.drawnote.model.listener;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ScrollListener implements OnTouchListener {

    PointF oldP = new PointF();
    public float moveX = 1.0f;
    public float moveY = 1.0f;

    @Override
    public boolean onTouch(View view, MotionEvent event) {

    	PointF newP = new PointF(event.getX(), event.getY());

	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN:
		oldP.set(newP);

	    break;
	case MotionEvent.ACTION_MOVE:
		moveX = oldP.x - newP.x;
		moveY = oldP.y - newP.y;
	    break;
	case MotionEvent.ACTION_UP:

	    break;
	}
	return true;
    }

}
