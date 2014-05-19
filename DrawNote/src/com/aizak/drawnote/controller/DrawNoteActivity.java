package com.aizak.drawnote.controller;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.aizak.drawnote.R;
import com.aizak.drawnote.controller.Bookshelf.OnNoteClickListener;
import com.aizak.drawnote.controller.Note.OnActrionBarListener;
import com.aizak.drawnote.controller.Note.OnOverlayListener;
import com.aizak.drawnote.controller.Note.Units;
import com.aizak.drawnote.controller.service.OverlayService;
import com.aizak.drawnote.model.listener.MyTabListener;
import com.aizak.drawnote.util.ActionBarUtil;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.FindViewByIdS;
import com.aizak.drawnote.util.SerializeManager;
import com.aizak.drawnote.view.MyNotification;
import com.aizak.drawnote.view.MyPopupWindow;

public class DrawNoteActivity extends ActionBarActivity implements
		FindViewByIdS, OnNoteClickListener, OnOverlayListener, OnActrionBarListener {

	//sqlite
	private final String password = "test-password";

	// View
	private MyPopupWindow popupWindow;

	// Fagment
	private final Note noteFragment = new Note();
	private final Bookshelf bookshelfFragment = new Bookshelf();

	// Listener
	private final MyTabListener onTabClick = new MyTabListener();

	//flg for overlay service
	private boolean requestOverlay;

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
		if (savedInstanceState == null) { //fragment生成
			commitBookShelfFragment();
		}
	}

	/*-------------------- << fragmentからのコールバック >> --------------------*/
	//+
	@Override
	public void onNoteClicked(String name, int index) {
		Log.d("NAME#onNoteClicked", name);
		Bundle bundle = new Bundle();
		bundle.putString(C.DB.CLM_NOTES_NAME, name); //選択したノートと最後に選択したページを代入
		bundle.putInt(C.DB.CLM_PAGES_INDEX, index); //選択したノートと最後に選択したページを代入
		noteFragment.setArguments(bundle); //Fragmentに代入した値を渡す
		commitNoteFragment(); //Fragment入れ替え、エディターを開く
	}

	//+
	@Override
	public void onOverlayEvent() {
		Log.d("TEST", "DrawnoteActivity#onOverlayEvent()");
		requestOverlay = !requestOverlay; //flgの切り替え
		if (requestOverlay) {
			startOverlayService(); //開始
		} else {
			stopOverlayService(); //停止
		}
	}

	//=
	@Override
	public boolean onActionBarVisiblityChenge(boolean visible, Units units) {
		boolean isvisible = false;
		if (visible) {
			isvisible = true;
			ActionBarUtil.setVisiblity(this, View.VISIBLE);
			if (units == null) {
				return isvisible;
			}

			units.setNormalViewVisiblity(View.VISIBLE);
			units.setFullscreenViewVisiblity(View.GONE);
		} else {
			isvisible = false;
			ActionBarUtil.setVisiblity(this, View.GONE);
			if (units == null) {
				return isvisible;
			}

			units.setNormalViewVisiblity(View.GONE);
			units.setFullscreenViewVisiblity(View.VISIBLE);

		}
		return isvisible;
	}

	//+
	@SuppressWarnings("unchecked")
	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) findViewById(id);
	}

	/*-------------------- << fragmentのコミット >> --------------------*/
	//+
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

	//+
	private void commitNoteFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, noteFragment)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null).commit();
		findViewById(R.id.container).setOnTouchListener(noteFragment);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
	}

	/*------------------ << Notification >> --------------------*/
	//+
	private void createNotification() {
		// Notificationマネージャのインスタンスを取得
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		MyNotification notification = new MyNotification(this);
		mgr.notify(1, notification);
	}

	/*------------------ << オーバーレイ >> --------------------*/
	/**
	 * オーバーレイ出力
	 */
	//=
	public void startOverlayService() {
		Log.d("TEST", "DrawNoteActiviry#startOverlayService");

		int viewTop = adjustMarginForOverlay();

		Bitmap bitmap = noteFragment.drawingView.getBitmap();
		byte[] stream = SerializeManager.serializeData(bitmap);
		Intent intent = new Intent(DrawNoteActivity.this, OverlayService.class)
				.putExtra(C.OVERLAY.EXTRA_IMAGE, stream)
				.putExtra(C.OVERLAY.EXTRA_TOP, viewTop)
				.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)// For GINGERBREAD
				.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION); // For GINGERBREAD
		startService(intent);
	}

	//=
	public void stopOverlayService() {
		stopService(new Intent(DrawNoteActivity.this, OverlayService.class));
	}

	//-
	public void startDemonOverlayService() {

		Intent intent = new Intent();

		startService(intent);

	}

	// オーバーレイのズレ防止 (上部にActionBarが存在する場合)
	//+
	public int adjustMarginForOverlay() {

		Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		int displayHeight = rect.bottom;
		int statusBarHeight = rect.top;
		int viewHeight = noteFragment.getView().getHeight();

		return displayHeight - viewHeight - statusBarHeight;
	}

}