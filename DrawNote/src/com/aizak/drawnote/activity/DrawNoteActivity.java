package com.aizak.drawnote.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;

import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.fragment.NoteFragment;
import com.example.drawnote.R;

public class DrawNoteActivity extends ActionBarActivity implements OnNoteClickListener {

	NoteFragment noteFragment = new NoteFragment();
	BookshelfFragment bookshelfFragment = new BookshelfFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			commitBookShelfFragment();
//		commitNoteFragment();
		}
	}

	private void commitBookShelfFragment() {
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, bookshelfFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
		findViewById(R.id.container).setOnTouchListener(bookshelfFragment);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("Create").setTabListener(onTabClicked));
		actionBar.addTab(actionBar.newTab().setText("Tmplate").setTabListener(onTabClicked));
		actionBar.addTab(actionBar.newTab().setText("Trash").setTabListener(onTabClicked));
	}

	private void commitNoteFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, noteFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null)
				.commit();
		findViewById(R.id.container).setOnTouchListener(noteFragment);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onNoteClicked() {
		commitNoteFragment();
	}

	private final TabListener onTabClicked = new TabListener() {

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction transition) {
			tab.getPosition();

		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction transition) {

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction transition) {

		}
	};

}
