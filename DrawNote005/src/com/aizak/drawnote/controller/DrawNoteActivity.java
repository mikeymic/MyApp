package com.aizak.drawnote.controller;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.aizak.drawnote.R;
import com.aizak.drawnote.controller.Bookshelf.OnNoteClickListener;
import com.aizak.drawnote.controller.Note.OnAccessDataBaseListener;
import com.aizak.drawnote.controller.Note.OnActrionBarListener;
import com.aizak.drawnote.controller.Note.OnOverlayListener;
import com.aizak.drawnote.controller.Note.Units;
import com.aizak.drawnote.controller.service.IntentActivityService;
import com.aizak.drawnote.controller.service.OverlayService;
import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;
import com.aizak.drawnote.model.database.DBControl;
import com.aizak.drawnote.model.listener.AcceleroListener.OnAcceleroListener;
import com.aizak.drawnote.model.listener.MyTabListener;
import com.aizak.drawnote.model.thread.MyThread;
import com.aizak.drawnote.util.ActionBarUtil;
import com.aizak.drawnote.util.C;
import com.aizak.drawnote.util.FindViewByIdS;
import com.aizak.drawnote.util.SerializeManager;
import com.aizak.drawnote.view.MyNotificationBuilder;
import com.aizak.drawnote.view.MyPopupWindow;

public class DrawNoteActivity extends ActionBarActivity implements
		FindViewByIdS, OnNoteClickListener, OnOverlayListener, OnActrionBarListener, OnAccessDataBaseListener {

	private final String password = "test-password";

	// View
	private MyPopupWindow popupWindow;

	// Fagment
	private final Note noteFragment = new Note();
	private final Bookshelf bookshelfFragment = new Bookshelf();

	// Listener
	private final MyTabListener onTabClick = new MyTabListener();

	private boolean requestOverlay;

	public MyThread myThread = new MyThread();

	private final ArrayList<Line> saveLines = new ArrayList<>();

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
		myThread.start();
		popupWindow = new MyPopupWindow(this);
		createNotification();

	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.d("TEST", "DrawNoteActivity#onResume");
		super.onResume();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.d("TEST", "DrawNoteActivity#onPause");
		super.onPause();
	}

	/* (非 Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("TEST", "DrawNoteActivity#onStart");
		stopService(new Intent(this, IntentActivityService.class));
	}

	/* (非 Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		Log.d("TEST", "DrawNoteActivity#onRestart");
		super.onRestart();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see android.support.v7.app.ActionBarActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("TEST", "DrawNoteActivity#onStop");
		flg = true;
		IntentActivityService.setFlg(flg);
		Intent intent = new Intent(this, IntentActivityService.class);
		startService(intent);
		super.onStop();
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

	@Override
	public void onOverlayEvent() {
		Log.d("TEST", "DrawnoteActivity#onOverlayEvent()");
		requestOverlay = !requestOverlay;
		if (requestOverlay) {
			startOverlayService();
		} else {
			stopOverlayService();
		}
	}

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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends View> T findViewByIdS(int id) {
		return (T) findViewById(id);
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
//		actionBar.addTab(actionBar.newTab().setText("Tmplate")
//				.setTabListener(onTabClick));
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

	/*------------------ << Notification >> --------------------*/
	private void createNotification() {
		// Notificationマネージャのインスタンスを取得
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new MyNotificationBuilder(getApplicationContext()).build();
		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
		mgr.notify(1, notification);
	}

	/*--------------------<<<加速度検知時の処理> >> -------------------*/
	private boolean flg = false;
	private final OnAcceleroListener OnDetectAccelero = new OnAcceleroListener() {
		@Override
		public void onAccelero() {
			if (flg) {
				Intent intent = new Intent(DrawNoteActivity.this, null);
				startActivity(intent);
			}

		}
	};

	/*------------------ << オーバーレイ >> --------------------*/
	/**
	 * オーバーレイ出力 ファイルは保存することにした
	 */
	public void startOverlayService() {
		Log.d("TEST", "DrawNoteActiviry#startOverlayService");

		int viewTop = adjustMarginForOverlay();

//		Bitmap bitmap = ((DrawingView) noteFragment.getView()).getBitmap();
//		byte[] stream = SerializeManager.serializeData(bitmap);
		Intent intent = new Intent(DrawNoteActivity.this, OverlayService.class)
				//				.putExtra(C.OVERLAY.EXTRA_IMAGE, stream)
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

	//あともう一つ
	@Override
	public void OnAccessDataBase(boolean mode) {

		if (mode == true) {
			myThread.handler.post(new Runnable() {

				@Override
				public void run() {
					DBControl db = new DBControl(DrawNoteActivity.this);
					saveLines.clear();
					saveLines.addAll(Data.savedLine);
					saveLines.addAll(Data.editingLine);
					Bitmap bitmap = Data.bitmap.copy(Config.ARGB_8888, true);
					Bitmap thumb = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4,
							false);

					byte[] lines = SerializeManager.serializeData(saveLines);
					byte[] image = SerializeManager.serializeData(bitmap);
					byte[] thumbnail = SerializeManager.serializeData(thumb);
					db.updatePage("test2", 1, lines, image, thumbnail, 1);

				}
			});
		} else {
			myThread.handler.post(new Runnable() {

				@Override
				public void run() {
					DBControl db = new DBControl(DrawNoteActivity.this);
					if (Data.savedLine != null) {
						Data.savedLine.clear();
					}
					if (Data.editingLine != null) {
						Data.editingLine.clear();
					}
					if (Data.bitmap != null) {
						Data.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
					}

					byte[][] stream = db.getPageWidthImage("test2", 1);
					if (stream != null) {
						if (stream[0] != null) {
							Data.savedLine.addAll((ArrayList<Line>) SerializeManager.deserializeData(stream[0]));
						}
						if (stream[1] != null) {
							Bitmap bitmap = BitmapFactory.decodeByteArray(stream[1], 0, stream[1].length);
							Data.bitmap = bitmap.copy(Config.ARGB_8888, true);
							Data.canvas.setBitmap(Data.bitmap);
						} else {
							saveLines.clear();
						}
					}
				}
			});

			try {
				myThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			noteFragment.getView().invalidate();
		}
	}
}