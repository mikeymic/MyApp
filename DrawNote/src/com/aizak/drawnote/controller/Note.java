package com.aizak.drawnote.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.util.ActionBarUtil;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.FindViewByIdS;
import com.aizak.drawnote.util.SerializeManager;
import com.aizak.drawnote.view.ColorPickerDialog;
import com.aizak.drawnote.view.DrawingView;
import com.aizak.drawnote.view.MyPopupWindow;

/**
 * @author 1218AND
 * 
 */
public class Note extends Fragment implements FindViewByIdS, OnTouchListener {

	//インターフェース
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

	//リスナー
	private OnOverlayListener overlayListener;
	private OnActrionBarListener actionBarListener;

	//データベース
	private DBControl db;
	private final ArrayList<Line> saveLines = new ArrayList<>();

	//ページ情報
	private String cNoteName;
	private int cPageIndex;
	private int pageCount;

	//View
	public DrawingView drawingView;
	private MyPopupWindow popupWindow;
	private ColorPickerDialog mColorPickerDialog;
	private Units units;
	private Button pageListButton;

	//デバッグ用
	private TextView testName;
	private TextView testIndex;
	private TextView testCount;

	//フラグ
	private boolean isScreenMode = false;

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;

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

		db = new DBControl(context);

		drawingView = findViewByIdS(R.id.drawing_view);

		//ページ情報取得
		cNoteName = getArguments().getString(C.DB.CLM_NOTES_NAME);
		pageCount = db.getPageCount(cNoteName);
		cPageIndex = 1; //現在番号

		getPage(cNoteName, cPageIndex);

		// set tools

		units = new Units();
		pageListButton = findViewByIdS(R.id.note_button_page_list);
		setColorPicker();
		setTest();// debug
		popupWindow = new MyPopupWindow(context);

		// update the displaying informations
		updatePageIndex();
		updateTestText();// debug

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
		savePage();
		int id = item.getItemId();
		switch (id) {
			case R.id.action_insert_new_page:
				int index = cPageIndex;
				if (index < pageCount) {
					db.updatePageIndexWhenInsert(cNoteName, cPageIndex, pageCount);
				} else {
					index++;
				}
				db.insertNewPage(cNoteName, index);
				// !!!sqliteにきちんと入ったか確認
				if (index == 1000) {
					return true;
				}
				cPageIndex = index;
				updatePageInformation();
				break;
			case R.id.action_delete_page:
				db.deletePage(cNoteName, cPageIndex);
				if (cPageIndex < pageCount) {
					db.updatePageIndexWhenDelete(cNoteName, cPageIndex, pageCount);
				}
				cPageIndex--;
				updatePageInformation();
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
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		savePage();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
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
		savePage();
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

	public void updatePageInformation() {

		getPage(cNoteName, cPageIndex);
		updatePageIndex();
		drawingView.setDrawMode(C.DW.MODE_CLEAR);

		if (saveLines != null) {
			saveLines.clear();
		}
	}

	public void savePage() {
		saveLines.clear();
		saveLines.addAll(drawingView.getSavedLines().getLines());
		saveLines.addAll(drawingView.getEditingLines().getLines());
		byte[] lines = SerializeManager.serializeData(saveLines);
		byte[] image = SerializeManager.serializeData(drawingView.getBitmap());
//		db.updatePage(cNoteName, cPageIndex, lines);
		db.updatePage(cNoteName, cPageIndex, lines, image);

	}

	@SuppressWarnings("unchecked")
	public void getPage(String name, int index) {
		if (drawingView.getEditingLines().getLines() != null) {
			drawingView.getEditingLines().getLines().clear();
		}
		if (drawingView.getSavedLines().getLines() != null) {
			drawingView.getSavedLines().getLines().clear();
		}
		if (drawingView.getBitmap() != null) {
			drawingView.getBmpCanvas().drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		}

//		byte[] stream = db.getPage(name, index);
//		if (stream != null) {
//			drawingView.getSavedLines().getLines().addAll((ArrayList<Line>) SerializeManager.deserializeData(stream));
//		} else {
//			saveLines.clear();
//		}
		byte[][] stream = db.getPageWidthImage(name, index);
		if (stream != null) {
			if (stream[0] != null) {
				drawingView.getSavedLines().getLines()
						.addAll((ArrayList<Line>) SerializeManager.deserializeData(stream[0]));
			}
			if (stream[1] != null) {
//				drawingView.setBitmap(BitmapFactory.decodeByteArray(stream[1], 0, stream[1].length));
				drawingView.setBitmap(stream[1]);
				drawingView.setBmpCanvas(new Canvas(drawingView.getBitmap()));
			} else {
				saveLines.clear();
			}
		}
	}

	public void updatePageIndex() {
		pageCount = db.getPageCount(cNoteName);
		pageListButton.setText(String.format(
				getString(R.string.page_current_index), cPageIndex, pageCount));

	}

	public void setTest() {
		testName = findViewByIdS(R.id.test_name);
		testIndex = findViewByIdS(R.id.test_index);
		testCount = findViewByIdS(R.id.test_count);
	}

	public void updateTestText() {
		testName.setText(cNoteName);
		testIndex.setText(String.valueOf(cPageIndex));
		testCount.setText(String.valueOf(pageCount));

	}

	public void setColorPicker() {

		mColorPickerDialog = new ColorPickerDialog(getActivity(),
				new ColorPickerDialog.OnColorChangedListener() {

					@Override
					public void colorChanged(int color) {
						drawingView.setColor(color);
					}
				}, Color.BLACK);
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
			savePage();
			String msg = null;
			int id = v.getId();
			switch (id) {
				case R.id.note_button_page_next:
					if (cPageIndex == pageCount) {
						msg = getString(R.string.msg_no_next_page);
						break;
					}
					cPageIndex++;
					break;

				case R.id.note_button_page_previous:
					if (cPageIndex == 1) {
						msg = getString(R.string.msg_no_previous_page);
						break;
					}
					cPageIndex--;
					break;
			}

			if (msg != null) {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}

			updatePageInformation();
			updateTestText();// debug
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

}