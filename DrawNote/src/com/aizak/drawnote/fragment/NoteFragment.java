package com.aizak.drawnote.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import com.aizak.drawnote.activity.ActionBarUtil;
import com.aizak.drawnote.activity.C;
import com.aizak.drawnote.activity.DrawNoteActivity;
import com.aizak.drawnote.activity.FindViewByIdS;
import com.aizak.drawnote.activity.database.DBModel;
import com.aizak.drawnote.activity.database.SerializeManager;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.view.ColorPickerDialog;
import com.aizak.drawnote.view.DrawingView;
import com.aizak.drawnote.view.MyPopupWindow;

/**
 * @author 1218AND
 * 
 */
public class NoteFragment extends Fragment implements FindViewByIdS,
		OnTouchListener {

	public interface OnDeserializeListener {
		public void onDeserialize(byte[] stream);
	}

	private OnDeserializeListener deserializeListener;

	private DrawNoteActivity activity;
	private Context context;

	private DBModel db;

	private String currentNoteName;
	private int currentPageIndex;
	private int pageCount;

	private RelativeLayout toolControl;
	private LinearLayout pageControl;
	private Button pageListButton;

	private DrawingView drawingView;
	private MyPopupWindow popupWindow;

	private boolean isScreenMode = false;

	private ArrayList<Line> lines;

	private boolean overlayFlg;

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		db = new DBModel(context);
		popupWindow = new MyPopupWindow(context);
		if (activity instanceof DrawNoteActivity) {
			this.activity = (DrawNoteActivity) activity;
		}

		if ((activity instanceof OnNoteClickListener) == false) {
			throw new ClassCastException("activity が OnDBListener を実装していません.");
		}
		deserializeListener = (OnDeserializeListener) activity;
		context = activity;
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
		View view = inflater.inflate(R.layout.fragment_note, container, false);
		return view;
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		drawingView = findViewByIdS(R.id.drawing_view);

		String name = getArguments().getString(C.DB.CLM_NOTES_NAME);
		currentNoteName = name;
		pageCount = db.getPageCount(name);
		currentPageIndex = 1;

		lines = drawingView.lines;
		getPage();

		setPageControl();
		setToolControl();
		// getView().post(setDelegate);

		updatePageIndex();

		drawingView.invalidate();

		setTest();//debug
		updateTestText();//debug

		setColorPicker();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see
	 * android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu
	 * , android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
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
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TEST", "NoteFragment#onItemSelected");
		savePage();
		updateTestText();//debug
		int id = item.getItemId();
		switch (id) {
			case R.id.action_insert_new_page:
				if (currentPageIndex < pageCount) {
					db.updatePageIndexWhenInsert(currentNoteName, currentPageIndex, pageCount);
				} else {
					currentPageIndex++;
				}
				db.insertNewPage(currentNoteName, currentPageIndex);
				updatePageInformation();
				break;
			case R.id.action_delete_page:
				db.deletePage(currentNoteName, currentPageIndex);
				if (currentPageIndex < pageCount) {
					db.updatePageIndexWhenDelete(currentNoteName, currentPageIndex,
							pageCount);
				}
				updatePageInformation();
				break;
			case R.id.action_undo:
				drawingView.setDrawMode(DrawingView.MODE_UNDO);
				break;
			case R.id.action_redo:
				drawingView.setDrawMode(DrawingView.MODE_REDO);

				break;
			case R.id.action_fullscreen:
				ActionBarUtil.actionBarSetVisiblity(getActivity(), View.GONE);
				pageControl.setVisibility(View.GONE);
				toolControl.setVisibility(View.VISIBLE);
				isScreenMode = true;
				break;
			case R.id.action_overlay:
				Log.d("TEST", "NoteFragment#onItemSelected#action_overlay");
				overlayFlg = !overlayFlg;
				if (overlayFlg) {
					activity.startOverlayService();
				} else {
					activity.stopOverlayService();
				}
				break;
			case R.id.action_settings:
				mColorPickerDialog.show();
				break;
			default:
				return false;
		}
		drawingView.invalidate();
		updateTestText();//debug
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
			ActionBarUtil.actionBarSetVisiblity(getActivity(), View.VISIBLE);// 後でActivityに移動
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
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
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
		super.onStop();
		if (isScreenMode) {
			ActionBarUtil.actionBarSetVisiblity(getActivity(), View.VISIBLE);
		}
		savePage();
		Log.d("TEST", "NoteFragment#onStop");
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
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
			updateTestText();//debug
			savePage();
			String msg = null;
			int id = v.getId();
			switch (id) {
				case R.id.note_button_page_next:
					if (currentPageIndex == pageCount) {
						msg = activity.getString(R.string.msg_no_next_page);
						break;
					}
					currentPageIndex++;
					break;

				case R.id.note_button_page_previous:
					if (currentPageIndex == 1) {
						msg = activity.getString(R.string.msg_no_previous_page);
						break;
					}
					currentPageIndex--;
					break;
			}

			if (msg != null) {
				Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
			}

			updatePageInformation();
			updateTestText();//debug
		}
	};

	private final OnClickListener OnClickToolControl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			updateTestText();//debug
			int id = v.getId();
			switch (id) {
				case R.id.tool_fullscreen:
					ActionBarUtil
							.actionBarSetVisiblity(getActivity(), View.VISIBLE);
					pageControl.setVisibility(View.VISIBLE);
					toolControl.setVisibility(View.GONE);
					isScreenMode = false;
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
			updateTestText();//debug
		}
	};

	private TextView testName;

	private TextView testIndex;

	private TextView testCount;

	private ColorPickerDialog mColorPickerDialog;

	public void updatePageInformation() {
		getPage();
		updatePageIndex();

		drawingView.setDrawMode(DrawingView.MODE_CLEAR);
		drawingView.invalidate();
	}

	public void savePage() {
		lines.addAll(drawingView.dataModel.lines);
		byte[] stream = SerializeManager.serializeData(lines);
		db.updatePage(currentNoteName, currentPageIndex, stream);
	}

	public void getPage() {
		if (lines != null) {
			lines.clear();
		}
		byte[] stream = db.getPage(currentNoteName, currentPageIndex);
		if (stream != null) {
			lines.addAll((ArrayList<Line>) SerializeManager
					.deserializeData(stream));
		} else {
			lines.clear();
		}
	}

	public void setPageControl() {
		pageControl = findViewByIdS(R.id.page_control);
		pageListButton = findViewByIdS(R.id.note_button_page_list);
		pageListButton.setOnClickListener(OnClickPagesUnit);
		findViewByIdS(R.id.note_button_page_previous).setOnClickListener(
				OnClickPagesUnit);
		findViewByIdS(R.id.note_button_page_next).setOnClickListener(
				OnClickPagesUnit);
	}

	public void updatePageIndex() {
		pageCount = db.getPageCount(currentNoteName);
		pageListButton.setText(String.format(
				getString(R.string.page_current_index), currentPageIndex,
				pageCount));

	}

	public void setToolControl() {
		toolControl = findViewByIdS(R.id.tool_control);
		findViewByIdS(R.id.tool_fullscreen).setOnClickListener(
				OnClickToolControl);
		findViewByIdS(R.id.tool_color).setOnClickListener(OnClickToolControl);
		findViewByIdS(R.id.tool_editer).setOnClickListener(OnClickToolControl);
		findViewByIdS(R.id.tool_pen).setOnClickListener(OnClickToolControl);
		findViewByIdS(R.id.tool_shepe).setOnClickListener(OnClickToolControl);
	}

	public void setTest() {
		testName = findViewByIdS(R.id.test_name);
		testIndex = findViewByIdS(R.id.test_index);
		testCount = findViewByIdS(R.id.test_count);
	}

	public void updateTestText() {
		testName.setText(currentNoteName);
		testIndex.setText(String.valueOf(currentPageIndex));
		testCount.setText(String.valueOf(pageCount));

	}

	public void setColorPicker() {

		mColorPickerDialog = new ColorPickerDialog(activity, new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				drawingView.color = color;
			}
		}, Color.BLACK);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) getActivity().findViewById(id);
	}

}
