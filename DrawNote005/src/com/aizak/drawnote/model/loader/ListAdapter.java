package com.aizak.drawnote.model.loader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aizak.drawnote.R;
import com.aizak.drawnote.util.C;

public class ListAdapter extends CursorAdapter {

	private class ViewHolder {
		TextView id;
		TextView name;
		TextView cDate;
		TextView uDate;
		TextView pCount;
		ImageView thumb;
	}

	public static final int THUMBNAIL_GRID_VIEW_MODE = 0;
	public static final int DETAIL_LIST_VIEW_MODE = 1;
	public static final int SIMPLE_LIST_VIEW_MODE = 2;
	private int listMode;

	private LayoutInflater mInflater;

	public ListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ListAdapter(Context context, Cursor c, boolean autoRequery,
			int listMode) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listMode = listMode;
	}

	public void setListMode(int listMode) {
		this.listMode = listMode;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// Viewを再利用してデータをセットします
		ViewHolder holder = (ViewHolder) view.getTag();

		final int id = cursor.getInt(cursor
				.getColumnIndexOrThrow(C.DB.CLM_PAGES_ID));
		final String name = cursor.getString(cursor
				.getColumnIndexOrThrow(C.DB.CLM_NOTES_NAME));
		final String pCount = cursor.getString(cursor
				.getColumnIndexOrThrow(C.DB.CLM_NOTES_PAGE_COUNT));

		holder.id.setText(String.valueOf(id));
		holder.name.setText(name);
		holder.pCount.setText(context.getString(R.string.note_info_page_count,
				pCount));

		if (listMode == DETAIL_LIST_VIEW_MODE
				|| listMode == THUMBNAIL_GRID_VIEW_MODE) {
			final String cDate = cursor.getString(cursor
					.getColumnIndexOrThrow(C.DB.CLM_NOTES_CREATE_DATE));
			final String uDate = cursor.getString(cursor
					.getColumnIndexOrThrow(C.DB.CLM_NOTES_UPDATE_DATE));

			holder.cDate.setText(context.getString(
					R.string.note_info_create_date, cDate));
			holder.uDate.setText(context.getString(
					R.string.note_info_update_date, uDate));

		}

		if (listMode == THUMBNAIL_GRID_VIEW_MODE) {
			final byte[] b = cursor.getBlob(cursor
					.getColumnIndexOrThrow(C.DB.CLM_NOTES_THUMBNAIL));

			Bitmap thumb = null;
			if (b != null) {
				thumb = BitmapFactory.decodeByteArray(b, 0, b.length);
			} else {
				thumb = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.blank);
			}

			holder.thumb.setImageBitmap(thumb);
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

		View view = new View(context);

		switch (listMode) {
		case THUMBNAIL_GRID_VIEW_MODE:
			view = createGridView();
			break;
		case DETAIL_LIST_VIEW_MODE:
			view = createDetailListView();
			break;
		case SIMPLE_LIST_VIEW_MODE:
			view = createSimpleListView();
			break;
		}
		return view;
	}

	private View createGridView() {
		final View view = mInflater.inflate(
				R.layout.row_note_list_thumbnail_gridview, null);
		ViewHolder holder = new ViewHolder();
		holder.id = (TextView) view.findViewById(R.id.row_note_index);
		holder.name = (TextView) view.findViewById(R.id.row_note_name);
		holder.cDate = (TextView) view.findViewById(R.id.row_note_create_date);
		holder.uDate = (TextView) view.findViewById(R.id.row_note_update_date);
		holder.pCount = (TextView) view.findViewById(R.id.row_note_page_count);
		holder.thumb = (ImageView) view.findViewById(R.id.row_note_image);
		view.setTag(holder);
		return view;
	}

	private View createDetailListView() {
		final View view = mInflater.inflate(
				R.layout.row_note_list_detail_listview, null);
		ViewHolder holder = new ViewHolder();
		holder.id = (TextView) view.findViewById(R.id.row_note_detail_id);
		holder.name = (TextView) view.findViewById(R.id.row_note_detail_name);
		holder.cDate = (TextView) view
				.findViewById(R.id.row_note_detail_create_date);
		holder.uDate = (TextView) view
				.findViewById(R.id.row_note_detail_update_date);
		holder.pCount = (TextView) view
				.findViewById(R.id.row_note_detail_page_count);
		view.setTag(holder);
		return view;
	}

	private View createSimpleListView() {
		final View view = mInflater.inflate(
				R.layout.row_note_list_simple_listview, null);
		ViewHolder holder = new ViewHolder();
		holder.id = (TextView) view.findViewById(R.id.row_note_simple_id);
		holder.name = (TextView) view.findViewById(R.id.row_note_simple_name);
		holder.pCount = (TextView) view.findViewById(R.id.row_note_simple_page_count);
		view.setTag(holder);
		return view;
	}

}
