package com.aizak.drawnote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class BackgroudAlphaSeekBar extends SeekBar {

	public interface OnAlphaChangedListener {
		public void onBackgroundAlphaChanged(int alpha);
	}

	public BackgroudAlphaSeekBar(Context context, OnAlphaChangedListener listener) {
		super(context);
	}

	public BackgroudAlphaSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BackgroudAlphaSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
