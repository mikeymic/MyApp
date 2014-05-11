package com.aizak.drawnote.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.database.DBModel;
import com.aizak.drawnote.activity.listener.MyTabListener;
import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.fragment.NoteFragment;
import com.aizak.drawnote.fragment.NoteFragment.OnDeserializeListener;
import com.aizak.drawnote.view.MyNotification;
import com.aizak.drawnote.view.MyPopupWindow;

public class DrawNoteActivity extends ActionBarActivity implements FindViewByIdS, OnNoteClickListener, OnDeserializeListener {

	//**-------------------- fragment --------------------**//
	private final NoteFragment noteFragment = new NoteFragment();
	private final BookshelfFragment bookshelfFragment = new BookshelfFragment();

	//**-------------------- DB --------------------**//
	private DBModel db = new DBModel(this);
	private final String password = "test-password";

	//**-------------------- View --------------------**//
	private MyPopupWindow popupWindow;

	//**-------------------- drawring data --------------------**//
	private Bitmap Image;

	//**-------------------- リスナー --------------------**//
	private final MyTabListener onTabClick = new MyTabListener();

	//**-------------------- Activity Method --------------------**//
	/* (非 Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			commitBookShelfFragment();
		}
		popupWindow = new MyPopupWindow(this);
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}



	//**-------------------- fragmentのコミット --------------------**//
	private void commitBookShelfFragment() {
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, bookshelfFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
		findViewById(R.id.container).setOnTouchListener(bookshelfFragment);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("Create").setTabListener(onTabClick));
		actionBar.addTab(actionBar.newTab().setText("Tmplate").setTabListener(onTabClick));
		actionBar.addTab(actionBar.newTab().setText("Trash").setTabListener(onTabClick));

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

	//**-------------------- fragmentからのコールバック --------------------**//
	@Override
	public void onNoteClicked(String name) {
		Log.d("NAME#onNoteClicked", name);
		Bundle bundle = new Bundle();
		bundle.putString(C.DB.CLM_NOTES_NAME, name);
		noteFragment.setArguments(bundle);
		commitNoteFragment();
	}

	//**------------------ Notification --------------------**//
	private void createNotification() {
		// Notificationマネージャのインスタンスを取得
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		MyNotification notification = new MyNotification(this);
		mgr.notify(1, notification);
	}

	//**------------------ DB操作 --------------------**//

	//*------------------ オーバーレイ --------------------*//
	public void startOverlayService() {

		Intent intent = new Intent();

		startService(intent);

	}

	public void stopOverlayService() {

		Intent intent = new Intent();

		startService(intent);

	}

	public void startDemonOverlayService() {

		Intent intent = new Intent();

		startService(intent);

	}

	@Override
	public void onDeserialize(byte[] stream) {
	}


	//**-------------------- getter/setter --------------------**//
	/**
	 * @return image
	 */
	public Bitmap getImage() {
		return Image;
	}


	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T)findViewById(id);
	}

}