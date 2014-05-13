package com.aizak.drawnote.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.database.DBModel;
import com.aizak.drawnote.activity.database.SerializeManager;
import com.aizak.drawnote.activity.listener.MyTabListener;
import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.fragment.NoteFragment;
import com.aizak.drawnote.service.OverlayService;
import com.aizak.drawnote.view.MyNotification;
import com.aizak.drawnote.view.MyPopupWindow;

public class DrawNoteActivity extends ActionBarActivity implements
		FindViewByIdS, OnNoteClickListener {

	// DB
	private final DBModel db = new DBModel(this);
	private final String password = "test-password";

	// View
	private MyPopupWindow popupWindow;

	// Fagment
	private final NoteFragment noteFragment = new NoteFragment();
	private final BookshelfFragment bookshelfFragment = new BookshelfFragment();

	// Data
	public Bitmap Image;

	// Listener
	private final MyTabListener onTabClick = new MyTabListener();

	/*-------------------- << Activity Method >> --------------------*/
	/*
	 * (非 Javadoc)
	 *
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

		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		Image = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/*-------------------- << fragmentのコミット >> --------------------*/
	private void commitBookShelfFragment() {
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, bookshelfFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
		findViewById(R.id.container).setOnTouchListener(bookshelfFragment);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText("Create")
				.setTabListener(onTabClick));
		actionBar.addTab(actionBar.newTab().setText("Tmplate")
				.setTabListener(onTabClick));
		actionBar.addTab(actionBar.newTab().setText("Trash")
				.setTabListener(onTabClick));

	}

	private void commitNoteFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, noteFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null).commit();
		findViewById(R.id.container).setOnTouchListener(noteFragment);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
	}

	/*-------------------- << fragmentからのコールバック >> --------------------*/
	@Override
	public void onNoteClicked(String name) {
		Log.d("NAME#onNoteClicked", name);
		Bundle bundle = new Bundle();
		bundle.putString(C.DB.CLM_NOTES_NAME, name);
		noteFragment.setArguments(bundle);
		commitNoteFragment();
	}

	/*------------------ << Notification >> --------------------*/
	private void createNotification() {
		// Notificationマネージャのインスタンスを取得
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		MyNotification notification = new MyNotification(this);
		mgr.notify(1, notification);
	}

	/*------------------ << DB操作 >> --------------------*/

	/*------------------ << オーバーレイ >> --------------------*/
	/**
	 * オーバーレイ出力
	 */
	public void startOverlayService() {
		Log.d("TEST", "DrawNoteActiviry#startOverlayService");

		int viewTop = adjustMarginForOverlay();

		byte[] stream = SerializeManager.serializeData(Image);
		Intent intent = new Intent(DrawNoteActivity.this, OverlayService.class)
				.putExtra(C.OVERLAY.EXTRA_IMAGE, stream)
				.putExtra(C.OVERLAY.EXTRA_TOP, viewTop)
				.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)// For GINGERBREAD
				.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // For GINGERBREAD
		startService(intent);
	}

	public void stopOverlayService() {
		stopService(new Intent(DrawNoteActivity.this, OverlayService.class));
	}

	public void startDemonOverlayService() {

		Intent intent = new Intent();

		startService(intent);

	}

	// オーバーレイのズレ防止 (上部にActionBarが存在する場合)
	public int adjustMarginForOverlay() {

		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		int displayHeight = rect.bottom;
		int statusBarHeight = rect.top;
		int viewHeight = noteFragment.getView().getHeight();

		return displayHeight - viewHeight - statusBarHeight;
	}


	// **-------------------- getter/setter --------------------**//
	/**
	 * @return image
	 */
	public Bitmap getImage() {
		return Image;
	}

	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) findViewById(id);
	}

}