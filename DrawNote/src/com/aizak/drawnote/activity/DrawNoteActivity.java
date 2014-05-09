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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.aizak.drawnote.R;
import com.aizak.drawnote.activity.database.DatabaseModel;
import com.aizak.drawnote.fragment.BookshelfFragment;
import com.aizak.drawnote.fragment.BookshelfFragment.OnDBListener;
import com.aizak.drawnote.fragment.BookshelfFragment.OnNoteClickListener;
import com.aizak.drawnote.fragment.NoteFragment;

public class DrawNoteActivity extends ActionBarActivity implements OnNoteClickListener, OnDBListener {

	private final NoteFragment noteFragment = new NoteFragment();
	private final BookshelfFragment bookshelfFragment = new BookshelfFragment();

	DatabaseModel db = new DatabaseModel(this);

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
	public void onNoteClicked(String name) {
		Log.d("NAME#onNoteClicked", name);
		Bundle bundle = new Bundle();
		bundle.putString(C.DB.CLM_NOTES_NAME, name);
		noteFragment.setArguments(bundle);
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
	public void onDBControl(View view, MenuItem item) {
		if (view == null) {
			db.createNote();
		}
	}

}