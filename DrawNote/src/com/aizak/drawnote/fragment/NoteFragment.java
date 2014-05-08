package com.aizak.drawnote.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageButton;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.C;
import com.aizak.drawnote.activity.database.DatabaseControl;

/**
 * @author 1218AND
 * 
 */
public class NoteFragment extends Fragment implements OnTouchListener {

	Context context;

	DatabaseControl db;
	private Button pageListButton;

	int currentPageIndex;
	int pageCount;

	private ImageButton pagePreviousButton;
	private ImageButton pageNextButton;

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity;
		db = new DatabaseControl(context);
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
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_note, container, false);
		return view;
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String name = getArguments().getString(C.DB.CLM_NOTES_NAME);
		Log.d("NAME#onActivityCreated", name);

		db.readPage(name, 1);
		currentPageIndex = 1;
		pageCount = db.getPageCount(name);

		View v = getView();

		pageListButton = (Button) v.findViewById(R.id.note_button_page_list);
		pageNextButton = (ImageButton) v.findViewById(R.id.note_button_page_next);
		pagePreviousButton = (ImageButton) v.findViewById(R.id.note_button_page_previous);
//		getView().post(setDelegate);
		pageListButton.setText(String.format(getString(R.string.page_current_index), currentPageIndex, pageCount));

		pageListButton.setOnClickListener(OnCliCKPagesUnit);
		pageNextButton.setOnClickListener(OnCliCKPagesUnit);
		pagePreviousButton.setOnClickListener(OnCliCKPagesUnit);

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

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.note, menu);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;

		//フルスクリーンボタンの場合フルスクリーン開始
		//フルスクリーン開始後、ツールポップアップを（表示）[onTouchEventにて表示／非表示の切り替え]

	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();

		//フルスクリーン解除
	}

	/* (非 Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

	//** Delegeteの設定**//
	private final Runnable setDelegate = new Runnable() {

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

	private final OnClickListener OnCliCKPagesUnit = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			byte[] stream = null;
			switch (id) {
				case R.id.note_button_page_list:
					if (true) {//表示されているなら非表示に
						//非表示
					} else {//非表示なら表示する
						//PopupWindowのShowメソッド
					}
					//PopupWindow表示
					return;
				case R.id.note_button_page_next:
					currentPageIndex++;
					stream = null;
					//currentpageindexの数でページを検索する
					if (stream == null) {//ページがなければ新規作成
						//DBに新しい行をインサート
					}
					//デシリアライズ
					break;
				case R.id.note_button_page_previous:
					currentPageIndex--;
					stream = null;
					if (currentPageIndex == 1) {
						//これより前のページはなし
						//Toast表示
						return;
					}
					//currentpageindexの数でページを検索する
					//デシリアライズ
					break;
			//再描画
			}

		}
	};

}
