package com.aizak.drawnote.controller;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.aizak.drawnote.R;
import com.aizak.drawnote.model.PageInfo;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.model.loader.MyCursorLoader;
import com.aizak.drawnote.model.loader.PopupListAdapter;
import com.aizak.drawnote.util.ActionBarUtil;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.FindViewByIdS;
import com.aizak.drawnote.view.BackgroudAlphaSeekBar.OnAlphaChangedListener;
import com.aizak.drawnote.view.ColorPickerDialog;
import com.aizak.drawnote.view.DrawingView;
import com.aizak.drawnote.view.MyPopupWindow;

/**
 * @author 1218AND
 * 
 */
public class Note extends Fragment implements FindViewByIdS, Observer, LoaderCallbacks<Cursor>, OnTouchListener {

	public interface OnOverlayListener {
		public void onOverlayEvent();
	}

	public interface OnActrionBarListener {
		public boolean onActionBarVisiblityChenge(boolean visible, Units units);
	}

	public class Units {

		private RelativeLayout toolControl;
		private LinearLayout pageControl;

		public Units() {
			setPageControl();
			setToolControl();

		}

		private void setPageControl() {
			pageControl = findViewByIdS(R.id.page_control);
			pageListButton = findViewByIdS(R.id.note_button_page_list);
			pageListButton.setOnClickListener(OnClickPageList);
			findViewByIdS(R.id.note_button_page_previous).setOnClickListener(OnClickPagesUnit);
			findViewByIdS(R.id.note_button_page_next).setOnClickListener(OnClickPagesUnit);
		}

		private void setToolControl() {
			toolControl = findViewByIdS(R.id.tool_control);
			findViewByIdS(R.id.tool_fullscreen).setOnClickListener(
					OnClickToolControl);
			findViewByIdS(R.id.tool_color).setOnClickListener(
					OnClickToolControl);
			findViewByIdS(R.id.tool_editer).setOnClickListener(
					OnClickToolControl);
			findViewByIdS(R.id.tool_pen).setOnClickListener(OnClickToolControl);
			findViewByIdS(R.id.tool_shepe).setOnClickListener(
					OnClickToolControl);
		}

		public void setNormalViewVisiblity(int visibility) {
			pageControl.setVisibility(visibility);
		}

		public void setFullscreenViewVisiblity(int visibility) {
			toolControl.setVisibility(visibility);
		}

	}

	private Context context;

	private OnOverlayListener overlayListener;
	private OnActrionBarListener actionBarListener;
	private OnAlphaChangedListener backgroundAlphaListener;

	private DBControl db;
	private PageInfo info;

	private DrawingView drawingView;
	private SeekBar seekBar;
	private MyPopupWindow popupWindow;
	private ColorPickerDialog mColorPickerDialog;
	private Units units;

	private Button pageListButton;
	private TextView testName;
	private TextView testIndex;
	private TextView testCount;

	private boolean isScreenMode = false;

	private PopupListAdapter popupListAdapter;

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		db = new DBControl(context);

		if ((activity instanceof OnOverlayListener) == false) {
			throw new ClassCastException(
					"activity が OnOverlayListener を実装していません.");
		}
		if ((activity instanceof OnActrionBarListener) == false) {
			throw new ClassCastException(
					"activity が OnActrionBarListenerを実装していません.");
		}
		overlayListener = (OnOverlayListener) activity;
		actionBarListener = (OnActrionBarListener) activity;

		ActionBarUtil.actionBarUpsideDown(activity);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_note, container, false);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// getView().post(setDelegate);

		drawingView = findViewByIdS(R.id.drawing_view);
		backgroundAlphaListener = (OnAlphaChangedListener) drawingView;

		seekBar = findViewByIdS(R.id.alpha_seek_bar);
		seekBar.setOnSeekBarChangeListener(backgroundAlphaChenged);

		mColorPickerDialog = new ColorPickerDialog(getActivity(), drawingView, Color.BLACK);

		//first load
		info.name = getArguments().getString(C.DB.CLM_NOTES_NAME);
		info.count = db.getPageCount("");
		info.index = 1;

		//getpage

		// set tools

		units = new Units();
		pageListButton = findViewByIdS(R.id.note_button_page_list);
		setTest();// debug

		// update the displaying informations
//		updateTestText();// debug

		drawingView.invalidate();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.note, menu);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TEST", "NoteFragment#onItemSelected");
		int id = item.getItemId();
		switch (id) {
			case R.id.action_insert_new_page:
				break;
			case R.id.action_delete_page:
				break;
			case R.id.action_undo:
				drawingView.setDrawMode(C.DW.MODE_UNDO);
				break;
			case R.id.action_redo:
				drawingView.setDrawMode(C.DW.MODE_REDO);
				break;
			case R.id.action_fullscreen:
				// 後でアニメーションを設定
				isScreenMode = actionBarListener.onActionBarVisiblityChenge(false,
						units);
				break;
			case R.id.action_overlay:
				overlayListener.onOverlayEvent(); // activityへcallback
				break;
			case R.id.action_settings:
				mColorPickerDialog.show();
				break;
			default:
				return false;
		}
		drawingView.invalidate();
		updateTestText();// debug
		return true;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (isScreenMode) {
			ActionBarUtil.setVisiblity(getActivity(), View.VISIBLE);
		}
		getLoaderManager().destroyLoader(0);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		//サービスに置き換え

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		setListPopupWindow();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.d("TEST", "NoteFragment#onStop");
		super.onStop();
		if (isScreenMode) {
			isScreenMode = actionBarListener.onActionBarVisiblityChenge(false, units);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) getActivity().findViewById(id);
	}

	public void setTest() {
		testName = findViewByIdS(R.id.test_name);
		testIndex = findViewByIdS(R.id.test_index);
		testCount = findViewByIdS(R.id.test_count);
	}

	public void updateTestText() {
		testName.setText(info.name);
		testIndex.setText(String.valueOf(info.index));
		testCount.setText(String.valueOf(info.count));
	}

	public void setListPopupWindow() {
		popupWindow = new MyPopupWindow(context);
		popupListAdapter = new PopupListAdapter(getActivity(), null, true);
		popupWindow.list.setAdapter(popupListAdapter);
	}

	// ** Delegeteの設定**//
	private final Runnable setDelegate = new Runnable() {

		@Override
		public void run() {
			Rect delegateArea = new Rect();
			Button delegate = pageListButton;
			delegate.getHitRect(delegateArea);
			delegateArea.union(30, 30, 30, 30);
			TouchDelegate expandedArea = new TouchDelegate(delegateArea,
					delegate);
			delegate.setTouchDelegate(expandedArea);
		}
	};

	private final OnClickListener OnClickPagesUnit = new OnClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			updateTestText();// debug
			String msg = null;
			int id = v.getId();
			switch (id) {
				case R.id.note_button_page_next:
					break;
				case R.id.note_button_page_previous:
					break;
			}
		}
	};

	private final OnClickListener OnClickToolControl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			updateTestText();// debug
			int id = v.getId();
			switch (id) {
				case R.id.tool_fullscreen:
					isScreenMode = actionBarListener.onActionBarVisiblityChenge(
							true, units);
					break;
				case R.id.tool_color:

					break;
				case R.id.tool_editer:

					break;
				case R.id.tool_pen:

					break;
				case R.id.tool_shepe:

					break;

				default:
					break;
			}
			updateTestText();// debug
		}
	};

	private final OnClickListener OnClickPageList = new OnClickListener() {

		@Override
		public void onClick(View v) {
			popupWindow.showAtLocation(units.pageControl, Gravity.TOP | Gravity.LEFT, 150, 150);

		}
	};

	private final OnSeekBarChangeListener backgroundAlphaChenged = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			backgroundAlphaListener.onBackgroundAlphaChanged(progress);
		}
	};

	//loader methods
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		MyCursorLoader cursorLoader = new MyCursorLoader(context, info.name); //MycursorLoader内部でDBからのCursorをセットしている
		return cursorLoader;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		popupListAdapter.swapCursor(cursor);
		popupListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		popupListAdapter.swapCursor(null);

	}

	@Override
	public void update(Observable observable, Object data) {

	}

}
