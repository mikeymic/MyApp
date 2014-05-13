package com.aizak.drawnote.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
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
import android.widget.GridView;
import android.widget.TextView;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.FindViewByIdS;
import com.aizak.drawnote.activity.database.DBModel;
import com.aizak.drawnote.activity.list.ListAdapter;
import com.aizak.drawnote.activity.list.MyCursorLoader;

public class BookshelfFragment extends Fragment implements FindViewByIdS, OnTouchListener, LoaderCallbacks<Cursor> {

	public interface OnNoteClickListener {
		public void onNoteClicked(String name);
	}

	DBModel db;
	private Context context;
	private View view;

	private GridView gridView;

	private OnNoteClickListener noteClickListener;

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
		noteClickListener = (OnNoteClickListener) activity;
		context = activity;
		db = new DBModel(activity);

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
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.bookshelf, menu);

	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TEST", "BookShelfFragment#onItemSelected");
		db.insertNewNote();
//		cursorAdapter.getCursor().requery();//後で変更
		return super.onOptionsItemSelected(item);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
		return view;
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listAdapter = new ListAdapter(getActivity(), null, true);

		gridView = (GridView) getView().findViewById(R.id.book_shelf_gridview);
		gridView.setNumColumns(3);
		gridView.setOnItemClickListener(OnClickNote);
		gridView.setAdapter(listAdapter);

		getLoaderManager().initLoader(0, null, this);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(0);
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
	public void onStart() { //getLoaderManagerが呼ばれているなら、ここからLoaderManagerのライフサイクル開始
		super.onStart();
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new MyCursorLoader(context); //MycursorLoader内部でDBからのCursorをセットしている
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		listAdapter.swapCursor(cursor);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loaderr) {
//		cursorAdapter.swapCursor(null);
		listAdapter.swapCursor(null);

	}


	private final OnItemClickListener OnClickNote = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TextView textName = (TextView) view.findViewById(R.id.row_note_name);
			String name = textName.getText().toString();
			Log.d("NAME#onItemClick", name);
			noteClickListener.onNoteClicked(name);
		}
	};
	private ListAdapter listAdapter;

	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) getActivity().findViewById(id);
	}
}
