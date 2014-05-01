package com.aizak.drawnote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class DrawingView extends View {

	Context context;

	public DrawingView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}
	public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	private void init() {

	}


}
