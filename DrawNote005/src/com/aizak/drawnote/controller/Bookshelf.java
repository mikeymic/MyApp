package com.aizak.drawnote.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aizak.drawnote.R;
import com.aizak.drawnote.controller.service.DeamonAcceleroIntentService;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.model.loader.ListAdapter;
import com.aizak.drawnote.model.loader.MyCursorLoader;
import com.aizak.drawnote.util.FindViewByIdS;

public class Bookshelf extends Fragment implements FindViewByIdS, OnTouchListener, LoaderCallbacks<Cursor> {

	//画面の遷移
	public interface OnNoteClickListener {
		public void onNoteClicked(String name);
	}

	private OnNoteClickListener noteClickListener;

	private Context context;

	private DBControl dbController; //DB

	//リスト
	private GridView gridView;
	private ListView listView;
	private ListAdapter listAdapter;

	/*-------------------- << Fragment Method >> --------------------*/
	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if ((activity instanceof OnNoteClickListener) == false) {
			throw new ClassCastException("activity が OnNoteClickListener を実装していません.");
		}
		else if ((activity instanceof OnNoteClickListener) == false) {
			throw new ClassCastException("activity が OnDBListener を実装していません.");
		}

		context = activity;
		noteClickListener = (OnNoteClickListener) activity;
		dbController = new DBControl(activity);

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
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.bookshelf, menu);

	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_bookshelf, container, false);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listAdapter = new ListAdapter(getActivity(), null, true);
		createGridView();
		createListView();


		setGridView();
//		setDetailListView();
		updateLoader();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() { //getLoaderManagerが呼ばれているなら、ここからLoaderManagerのライフサイクル開始
		super.onStart();
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("TEST", "BookShelf#onDestroyView");
		getLoaderManager().destroyLoader(0);

		//加速度リスナーを解除
		getActivity().stopService(new Intent(context, DeamonAcceleroIntentService.class));

	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		if (listAdapter.getCursor() != null) {
			listAdapter.getCursor().close();
		}

	}

	/*-------------------- << Touch Methods >> --------------------*/
	int count = 0; //debug for list mode change;    after change to popupwindow for chenging
	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TEST", "BookShelfFragment#onItemSelected");
		int id = item.getItemId();

		switch (id) {
		case R.id.action_insert_new_note:
			dbController.insertNewNote();
			break;
		case R.id.action_change_list_mode:
			count++;
			if (count > 3) {
				count = 1;
			}

			FrameLayout root =((FrameLayout) getView());
			root.removeViewAt(root.getChildCount()-1);
			switch (count) {
			case 1:
				setDetailListView();
				break;
			case 2:
				setSimpleListView();
				break;
			case 3:
				setGridView();
				break;
			}
			break;
		default:
			return false;
		}

		getLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	/*-------------------- << Grid&ListView Methods >> --------------------*/

	private void createGridView() {
		gridView = (GridView) LayoutInflater.from(getActivity()).inflate(R.layout.note_grid_view, null);
		int width = getView().getWidth();
		int columns = 2;
		if ((width / 2) > 400) {
			columns = 1;
		}
		gridView.setNumColumns(columns);// 後で修正。listの大きさによってカラム数が変わるようにしたい
		gridView.setOnItemClickListener(NoteClicked);
		gridView.setOnItemLongClickListener(LongClickNote);
		gridView.setAdapter(listAdapter);
	}

	private void createListView() {
		listView = (ListView) LayoutInflater.from(getActivity()).inflate(R.layout.note_list_view, null);
		listView.setOnItemClickListener(NoteClicked);
		listView.setOnItemLongClickListener(LongClickNote);
		listView.setAdapter(listAdapter);
	}

	private void setGridView() {
		listAdapter.setListMode(ListAdapter.THUMBNAIL_GRID_VIEW_MODE);
		listAdapter.newView(context, null, null);
		((FrameLayout) getView()).addView(gridView);
	}

	private void setDetailListView() {
		listAdapter.setListMode(ListAdapter.DETAIL_LIST_VIEW_MODE);
		listAdapter.newView(context, null, null);
		((FrameLayout) getView()).addView(listView);
		listView.setAdapter(listAdapter);
	}

	private void setSimpleListView() {
		listAdapter.setListMode(ListAdapter.SIMPLE_LIST_VIEW_MODE);
		listAdapter.newView(context, null, null);
		((FrameLayout) getView()).addView(listView);
		listView.setAdapter(listAdapter);
	}


	/*-------------------- << Loader Methods >> --------------------*/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		MyCursorLoader cursorLoader = new MyCursorLoader(context); //MycursorLoader内部でDBからのCursorをセットしている
		return cursorLoader;

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		listAdapter.swapCursor(cursor);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		listAdapter.swapCursor(null);

	}

	private void updateLoader() {
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().restartLoader(0, null, this);
	}


	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) getActivity().findViewById(id);
	}

	/*-------------------- << Listener Instance >> --------------------*/

	private final OnItemClickListener NoteClicked = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TextView textName = (TextView) view.findViewById(R.id.row_note_name);
			String name = textName.getText().toString();
			Log.d("NAME#onItemClick", name);
			noteClickListener.onNoteClicked(name);
		}
	};

	//ダイアログからの削除に切り替える
	private final OnItemLongClickListener LongClickNote = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			TextView name = (TextView) view.findViewById(R.id.row_note_name);
			String noteName = name.getText().toString();
			dbController.deleteNote(noteName);
			updateLoader();
			Toast.makeText(context, "ノートを削除しました", Toast.LENGTH_SHORT).show();

			return true;
		}
	};

}
