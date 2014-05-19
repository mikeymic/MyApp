package com.aizak.drawnote.controller;

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
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.model.loader.ListAdapter;
import com.aizak.drawnote.model.loader.MyCursorLoader;
import com.aizak.drawnote.util.FindViewByIdS;

public class Bookshelf extends Fragment implements FindViewByIdS, OnTouchListener, LoaderCallbacks<Cursor> {

	public interface OnNoteClickListener {
		public void onNoteClicked(String name, int index);
	}

	private Context context;

	private DBControl dbController;/*revi*/

	//View
	private GridView gridView;
	private ListAdapter listAdapter;

	//リスナー
	private OnNoteClickListener noteClickListener;

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if ((activity instanceof DrawNoteActivity) == false) {
			throw new ClassCastException("activity が DrawNoteActivitydではありません.");
		}

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
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("TEST", "BookShelfFragment#onItemSelected");
		dbController.insertNewNote(); //新規作成
		getLoaderManager().restartLoader(0, null, this); //Loader更新
		//listを更新
		return super.onOptionsItemSelected(item);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_bookshelf, container, false);
	}

	//+
	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		listAdapter = new ListAdapter(getActivity(), null, true);

		gridView = (GridView) getView().findViewById(R.id.book_shelf_gridview);
		gridView.setNumColumns(3);// 後で修正。listの大きさによってカラム数が変わるようにしたい
		gridView.setOnItemClickListener(NoteClicked);
		gridView.setAdapter(listAdapter);

		getLoaderManager().initLoader(0, null, this);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("TEST", "BookShelf#onDestroyView");
		getLoaderManager().destroyLoader(0);
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

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() { //getLoaderManagerが呼ばれているなら、ここからLoaderManagerのライフサイクル開始
		super.onStart();
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	//loader methods
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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) getActivity().findViewById(id);
	}

	private final OnItemClickListener NoteClicked = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TextView textName = (TextView) view.findViewById(R.id.row_note_name);
			String name = textName.getText().toString();
			Log.d("NAME#onItemClick", name);
			noteClickListener.onNoteClicked(name, position); /*revi*/
		}
	};

}
