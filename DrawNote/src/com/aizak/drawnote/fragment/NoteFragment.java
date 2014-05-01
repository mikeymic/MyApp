package com.aizak.drawnote.fragment;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.aizak.drawnote.activity.ActionBarUtil;
import com.example.drawnote.R;

public class NoteFragment extends Fragment implements OnTouchListener{

	private Button pageListButton;

	int currentPage = 2;
	int pageCount = 10;

	private boolean isScreenMode;

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO 自動生成されたメソッド・スタブ
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	Runnable setDelegate = new Runnable() {

		@Override
		public void run() {
            Rect delegateArea = new Rect();
            Button delegate = pageListButton;
            delegate.getHitRect(delegateArea);
            delegateArea.union(30, 30, 30, 30);
            TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);
            delegate.setTouchDelegate(expandedArea);
		}
	};

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.note, menu);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note, container, false);
		pageListButton = (Button) view.findViewById(R.id.note_button_page_list);
		pageListButton.setOnClickListener(onClickPageListButton);
		view.post(setDelegate);
		createPopUpWindow();
		return view;
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDetach();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		updateNotePage();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		currentPage++;
		updateNotePage();

		return false;
	}

	private void updateNotePage() {
		pageListButton.setText(String.format(getString(R.string.note_current_page), currentPage, pageCount));

	}

	private void OnScreenMode() {
		if (isScreenMode) {
			ActionBarUtil.actionBarSetVisiblity(getActivity(), View.GONE);
			pageListButton.setVisibility(View.GONE);
		} else {
			ActionBarUtil.actionBarSetVisiblity(getActivity(), View.VISIBLE);
			pageListButton.setVisibility(View.VISIBLE);
		}
	}


	private OnClickListener onClickPageListButton = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(popupWindow.isShowing()) {
				popupWindow.dismiss();
			} else {
//				popupWindow.showAsDropDown(pageListButton,0 ,
//						-(pageListButton.getHeight()*3));
				popupWindow.showAtLocation(getView(), Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,
						0, (pageListButton.getHeight()*2));
				}
		}
	};

	private PopupWindow popupWindow;

	private View contentView;

	private PopupWindow createPopUpWindow() {
		contentView = LayoutInflater.from(getActivity()).inflate(R.layout.gridview_row_note, null, false);
		popupWindow = new PopupWindow(getView());
		popupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setBackgroundDrawable(new Drawable() {

			@Override
			public void setColorFilter(ColorFilter cf) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void setAlpha(int alpha) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public int getOpacity() {
				// TODO 自動生成されたメソッド・スタブ
				return 0;
			}

			@Override
			public void draw(Canvas canvas) {
				canvas.drawColor(Color.BLUE);

			}
		});
		popupWindow.setContentView(contentView);
		return popupWindow;
		}

}
