package com.aizak.drawnote.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aizak.drawnote.R;

public class MyPopupWindow extends PopupWindow {

	public ListView list;

	public MyPopupWindow(Context context) {
		super(context);

		RelativeLayout contentView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.popup_page_list,
				null, false);
		setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setContentView(contentView);

		list = (ListView) contentView.findViewById(R.id.page_list_view);

	}

}
