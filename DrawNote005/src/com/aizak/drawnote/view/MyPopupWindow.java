package com.aizak.drawnote.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.aizak.drawnote.R;

public class MyPopupWindow extends PopupWindow {

	public MyPopupWindow(Context context) {
		super(context);
		View contentView = LayoutInflater.from(context).inflate(R.layout.gridview_row_note, null, false);
		setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setContentView(contentView);
	}






}
