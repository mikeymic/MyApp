package com.aizak.drawnote.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.controller.service.IntentActivityService;
import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.PageInfo;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.util.ActionBarUtil;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.FindViewByIdS;
import com.aizak.drawnote.view.ColorPickerDialog;
import com.aizak.drawnote.view.DrawingView;
import com.aizak.drawnote.view.MyPopupWindow;

/**
 * @author 1218AND
 * 
 */
public class Note extends Fragment implements FindViewByIdS, OnTouchListener, Observer {

	public interface OnAccessDataBaseListener {
		void OnAccessDataBase(boolean mode);
		// true = save , false = load
	}

	public interface OnOverlayListener {
		public void onOverlayEvent();
	}

	public interface OnActrionBarListener {
		public boolean onActionBarVisiblityChenge(boolean visible, Units units);
	}

	public interface OnBackgroundAlphaListener {
		public void onBackgroundAlphaChanged(int alpha);
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
			pageListButton.setOnClickListener(OnClickPagesUnit);
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
	private OnBackgroundAlphaListener backgroundAlphaListener;
	private OnAccessDataBaseListener accessDataBaseListener;

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

	/*-------------------- << Fragment Method >> --------------------*/
	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d("TEST", "NoteFragment#onAttach");
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
		accessDataBaseListener = (OnAccessDataBaseListener) activity;

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
		Log.d("TEST", "OnCreate()");
		setHasOptionsMenu(true);
		setRetainInstance(true);

		Data.savedLine = new ArrayList<>();
		Data.editingLine = new ArrayList<>();

		info = new PageInfo();
		info.addObserver(this);

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
		Log.d("TEST", "onActivityCreated()");

		drawingView = findViewByIdS(R.id.drawing_view);
		backgroundAlphaListener = drawingView;

		seekBar = findViewByIdS(R.id.alpha_seek_bar);
		seekBar.setOnSeekBarChangeListener(backgroundAlphaChenged);

		// set tools
		units = new Units();
		pageListButton = findViewByIdS(R.id.note_button_page_list);
		setColorPicker();
		setTest();// debug
		popupWindow = new MyPopupWindow(context);

		//表示の更新 Observale
		String noteName = getArguments().getString(C.DB.CLM_NOTES_NAME);
		info.updatePageInfo(noteName, db.getPageCount(noteName), 1);
		//DBからをページを取得 Thread化
		accessDataBaseListener.OnAccessDataBase(false);

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
				int index = info.currentPage;
				if (index < info.pageCount) {
					db.updatePageIndexWhenInsert(info.noteName, info.currentPage, info.pageCount);
				} else {
					index++;
				}
				db.insertNewPage(info.noteName, index);
				// !!!sqliteにきちんと入ったか確認
				if (index == 1000) {
					return true;
				}
				info.setCurrentPage(index);
				break;
			case R.id.action_delete_page:
				db.deletePage(info.noteName, info.currentPage);
				if (info.currentPage < info.pageCount) {
					db.updatePageIndexWhenDelete(info.noteName, info.currentPage, info.pageCount);
				}
				info.setCurrentPage(info.currentPage--);
				break;
			case R.id.action_undo:
				drawingView.setDrawMode(C.DW.MODE_UNDO);
				break;
			case R.id.action_redo:
				drawingView.setDrawMode(C.DW.MODE_REDO);
				break;
			case R.id.action_fullscreen:
				// 後でアニメーションを設定
				isScreenMode = actionBarListener.onActionBarVisiblityChenge(false, units);
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
		return true;
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		Log.d("TEST", "NoteFragment#onDetach");
		super.onDetach();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("TEST", "NoteFragment#onDestroyView");
		if (isScreenMode) {
			ActionBarUtil.setVisiblity(getActivity(), View.VISIBLE);

			//加速度リスナーを解除
			getActivity().stopService(new Intent(context, IntentActivityService.class));

		}
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d("TEST", "NoteFragment#onResume");
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d("TEST", "NoteFragment#onDestroy");
		super.onDestroy();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.d("TEST", "NoteFragment#onPause");
		//サービスに置き換え

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.d("TEST", "NoteFragment#onStart");
		super.onStart();
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
			isScreenMode = actionBarListener.onActionBarVisiblityChenge(false,
					units);
		}
//		savePage();
		accessDataBaseListener.OnAccessDataBase(true);
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

	/*-------------------- << Update Method >> --------------------*/
	public void updatePageInformation() {
		Log.d("TEST", "updatePageIndex()");
//		getPage(cNoteName, cPageIndex);
		updatePageIndex();
		drawingView.setDrawMode(C.DW.MODE_CLEAR);

//		if (saveLines != null) {
//			saveLines.clear();
//		}
	}

	public void updatePageIndex() {
		Log.d("TEST", "updatePageIndex(");
		info.pageCount = db.getPageCount(info.noteName);
		pageListButton.setText(String.format(
				getString(R.string.page_current_index), info.currentPage, info.pageCount));

	}

	/*-------------------- << init >> --------------------*/
	public void setTest() {
		testName = findViewByIdS(R.id.test_name);
		testIndex = findViewByIdS(R.id.test_index);
		testCount = findViewByIdS(R.id.test_count);
	}

	public void updateTestText() {
		testName.setText(info.noteName);
		testIndex.setText(String.valueOf(info.currentPage));
		testCount.setText(String.valueOf(info.pageCount));

	}

	public void setColorPicker() {
		mColorPickerDialog = new ColorPickerDialog(getActivity(), drawingView, Color.BLACK);
	}

	/*-------------------- << Observer Method >> --------------------*/
	@Override
	public void update(Observable observable, Object data) {
		// update the displaying informations
		updatePageInformation();
		updatePageIndex();
		updateTestText();// debug
	}

	/*-------------------- << Listener Instance >> --------------------*/
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
					if (info.currentPage == info.pageCount) {
						msg = getString(R.string.msg_no_next_page);
						break;
					}
					info.setCurrentPage(info.currentPage + 1);
					;
					break;

				case R.id.note_button_page_previous:
					if (info.currentPage == 1) {
						msg = getString(R.string.msg_no_previous_page);
						break;
					}
					info.setCurrentPage(info.currentPage - 1);
					break;
			}

			if (msg != null) {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

}
