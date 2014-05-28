package com.aizak.drawnote.model.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizak.drawnote.R;
import com.aizak.drawnote.util.C;

public class DetailListViewAdapter extends CursorAdapter {

	public static final int THUMB_GRID_VIEW = 0;
	public static final int DETAIL_LIST_VIEW = 1;
	public static final int DETAIL_SIMPLE_VIEW = 2;
	private int listMode;

	private class ViewHolder {
		TextView id;
		TextView name;
		TextView cDate;
		TextView uDate;
		TextView pCount;
	}

	private LayoutInflater mInflater;

	public DetailListViewAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public DetailListViewAdapter(Context context, Cursor c, boolean autoRequery, int listMode) {
		super(context, c, autoRequery);
		 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 this.listMode = listMode;

	}

	public void setListMode(int listMode) {
		this.listMode = listMode;
	}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Viewを再利用してデータをセットします
        ViewHolder holder = (ViewHolder) view.getTag();

        // Cursorからデータを取り出します
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_NOTES_NAME));
        final String cDate = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_NOTES_CREATE_DATE));
        final String uDate = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_NOTES_UPDATE_DATE));
        final String pCount = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_NOTES_PAGE_COUNT));

        // 画面にセットします
        holder.id.setText(String.valueOf(id));
        holder.name.setText(name);
        holder.cDate.setText(context.getString(R.string.note_info_create_date, cDate));
        holder.uDate.setText(context.getString(R.string.note_info_update_date, uDate));
        holder.pCount.setText(context.getString(R.string.note_info_page_count, pCount));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        // 新しくViewを作ります
        final View view = mInflater.inflate(R.layout.row_note_list_thumbnail_gridview, null);
        ViewHolder holder = new ViewHolder();
        holder.id = (TextView) view.findViewById(R.id.row_note_index);
        holder.name = (TextView) view.findViewById(R.id.row_note_name);
        holder.cDate = (TextView) view.findViewById(R.id.row_note_create_date);
        holder.uDate = (TextView) view.findViewById(R.id.row_note_update_date);
        holder.pCount = (TextView) view.findViewById(R.id.row_note_page_count);

        view.setTag(holder);

        return view;
    }


}
