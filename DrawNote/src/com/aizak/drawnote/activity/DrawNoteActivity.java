package com.aizak.drawnote.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.NoteFragment;
import com.example.drawnote.R;

public class DrawNoteActivity extends ActionBarActivity {

	NoteFragment noteFragment = new NoteFragment();
	BookshelfFragment bookshelfFragment = new BookshelfFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
//		commitBookShelfFragment();
		commitNoteFragment();
		}
	}

	private void commitBookShelfFragment() {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, bookshelfFragment)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
			findViewById(R.id.container).setOnTouchListener(bookshelfFragment);
	}

	private void commitNoteFragment() {
		getSupportFragmentManager().beginTransaction()
					.add(R.id.container, noteFragment)
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		.commit();
		findViewById(R.id.container).setOnTouchListener(noteFragment);
	}


}
