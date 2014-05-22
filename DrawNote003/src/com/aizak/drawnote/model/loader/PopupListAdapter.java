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

public class PopupListAdapter extends CursorAdapter {

	private class ViewHolder {
		TextView id;
		TextView name;
		TextView cDate;
		TextView uDate;
		TextView pCount;
		ImageView thumb;
	}

	private final LayoutInflater mInflater;

	public PopupListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();

		// Cursorからデータを取り出します
		final int id = cursor.getInt(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_INDEX));
		final String name = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_NAME));
		final String cDate = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_CREATE_DATE));
		final String uDate = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_UPDATE_DATE));
		final String pCount = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_NOTES_PAGE_COUNT));
		final byte[] b = cursor.getBlob(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_IMAGE));
		Bitmap thumb = null;
		if (b != null) {
			thumb = BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.blank);
		}

		// 画面にセットします
		holder.id.setText(String.valueOf(id));
		holder.name.setText(name);
		holder.cDate.setText(context.getString(R.string.note_info_create_date, cDate));
		holder.uDate.setText(context.getString(R.string.note_info_update_date, uDate));
		holder.pCount.setText(context.getString(R.string.note_info_page_count, pCount));
		holder.thumb.setImageBitmap(thumb);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		final View view = mInflater.inflate(R.layout.popup_page_row, null);
		ViewHolder holder = new ViewHolder();
		holder.id = (TextView) view.findViewById(R.id.row_page_index);
		holder.name = (TextView) view.findViewById(R.id.row_page_name);
		holder.cDate = (TextView) view.findViewById(R.id.row_page_create_date);
		holder.uDate = (TextView) view.findViewById(R.id.row_page_update_date);
		holder.pCount = (TextView) view.findViewById(R.id.row_page_count);
		holder.thumb = (ImageView) view.findViewById(R.id.row_page_image);

		view.setTag(holder);
		return null;
	}

}
