package com.aizak.drawnote.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.aizak.drawnote.activity.database.DatabaseDao;
import com.aizak.drawnote.activity.database.DatabaseHelper;
import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.fragment.NoteFragment;
import com.example.drawnote.R;

public class DrawNoteActivity extends ActionBarActivity implements OnNoteClickListener {

	private final NoteFragment noteFragment = new NoteFragment();
	private final BookshelfFragment bookshelfFragment = new BookshelfFragment();

	private View popupNoteListView;
	private Bitmap Image;

	private final String password = "test-password";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			commitBookShelfFragment();
		}
	}

	//**-------------------- fragmentのコミット--------------------**//

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

	//**-------------------- fragmentからのコールバック --------------------**//
	@Override
	public void onNoteClicked() {
		commitNoteFragment();
	}

	//**-------------------- リスナー --------------------**//
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

	//**------------------ PopupWindow --------------------**//
	/**
	 * @return PopupWindow
	 */
	private PopupWindow createPageListPopupWindow() {
		popupNoteListView = LayoutInflater.from(this).inflate(R.layout.gridview_row_note, null, false);
		PopupWindow popupWindow = new PopupWindow(noteFragment.getView());
		popupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setContentView(popupNoteListView);
		return popupWindow;
	}

	//**------------------ Notification --------------------**//

	private void createNotification() {
		String url = "http://www.google.com";
		Uri uri = Uri.parse(url);
		// 新たにアクティビティーを開始するためにPendingIntentを取得する
		// Intent intent = new Intent(Intent.ACTION_VIEW, uri);

		Intent intent = new Intent(this, NoteFragment.class);

		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

		// Notificationマネージャのインスタンスを取得
		NotificationManager mgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// NotificationBuilderのインスタンスを作成
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext());
		builder.setContentIntent(pi).setTicker("テキスト")// ステータスバーに表示されるテキスト
				.setSmallIcon(R.drawable.ic_launcher)// アイコン
				.setContentTitle("タイトル")// Notificationが開いたとき
				.setContentText("メッセージ")// Notificationが開いたとき
				.setWhen(System.currentTimeMillis())// 通知するタイミング
				.setPriority(Integer.MAX_VALUE);
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_NO_CLEAR
				| Notification.FLAG_ONGOING_EVENT;

		mgr.notify(1, notification);
	}

	//**-------------------- DB操作 --------------------**//

	//ノート//
	public void createNote() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void updateNote() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void deleteNote() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void readNote() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void readNotes() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	//ページ
	public void createPage() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void updatePage() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void deletePage() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getWritableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void readPage() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

	public void readPages() {

		DatabaseHelper helper = new DatabaseHelper(this);
		DatabaseDao dao = new DatabaseDao(helper.getReadableDatabase(password));
		/*
		 * 処理記述
		 */
		helper.close();
	}

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

}