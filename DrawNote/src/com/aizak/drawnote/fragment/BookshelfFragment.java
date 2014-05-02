package com.aizak.drawnote.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.example.drawnote.R;

public class BookshelfFragment extends Fragment implements OnTouchListener {

	public interface OnNoteClickListener {
		public void onNoteClicked();
	}

	OnNoteClickListener noteClickListener;

	private GridView gridView;

	/* (非 Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if ((activity instanceof OnNoteClickListener) == false) {
			throw new ClassCastException("activity が OnOkBtnClickListener を実装していません.");
		}
		noteClickListener = ((OnNoteClickListener) activity);
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
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
		gridView = (GridView) view.findViewById(R.id.book_shelf_gridview);

		String[] data = { "note1", " note2", "note3", "note4", "note5", "note6", "note7", "note8", "note9", "note10", };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.gridview_row_note,
				R.id.row_note_name, data);

		gridView.setNumColumns(3);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(onNoteClicked);

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
		ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
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
		return false;
	}

	private final OnItemClickListener onNoteClicked = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (noteClickListener != null) {
				noteClickListener.onNoteClicked();
			}
		}
	};

}
