package com.aizak.drawnote.activity.listener;

import java.util.ArrayList;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.database.DBModel;
import com.aizak.drawnote.activity.database.SerializeManager;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.view.MyPopupWindow;

public class OnClickPageUnitListener implements OnClickListener {

	Context context;
	View view;
	MyPopupWindow popupWindow;
	String currentNoteName;
	int currentPageIndex;
	int pageCount;
	DBModel db;
	ArrayList<Line> lines;

	public OnClickPageUnitListener(Context context, View view, ArrayList<Line> lines, String currentNoteName, int currentPageIndex, int pageCount, MyPopupWindow popupWindow) {
		this.context = context;
		this.view = view;
		this.lines = lines;
		this.currentNoteName = currentNoteName;
		this.currentPageIndex = currentPageIndex;
		this.pageCount = pageCount;
		this.popupWindow = popupWindow;
	}

	@Override
	public void onClick(View v) {
			int id = v.getId();
			byte[] stream = null;
			String msg = null;
			switch (id) {
				case R.id.note_button_page_list:
					if (popupWindow.isShowing()) {
						popupWindow.dismiss();
					} else {
						popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
					}
					return;

				case R.id.note_button_page_next:
					if (currentPageIndex == pageCount) {
						msg = context.getString(R.string.msg_no_next_page);
						break;
					}
					currentPageIndex++;
					break;

				case R.id.note_button_page_previous:
					if (currentPageIndex == 1) {
						msg = context.getString(R.string.msg_no_previous_page);
						break;
					}
					currentPageIndex--;
					break;
			}

			if (msg != null) {
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				return;
			}

			//再描画
			stream = db.getPage(currentNoteName, currentPageIndex);
			lines = (ArrayList<Line>) SerializeManager.deserializeData(stream);
			view.invalidate();
			return;
	}

}
