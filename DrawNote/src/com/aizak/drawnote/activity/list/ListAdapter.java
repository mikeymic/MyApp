package com.aizak.drawnote.activity.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizak.drawnote.R;
import com.aizak.drawnote.util.C;

public class ListAdapter extends CursorAdapter {

	private class ViewHolder {
		TextView id;
		TextView name;
		TextView cDate;
		TextView uDate;
	}

	private LayoutInflater mInflater;

	public ListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		 mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Viewを再利用してデータをセットします
        ViewHolder holder = (ViewHolder) view.getTag();

        // Cursorからデータを取り出します
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_ID));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(C.DB.CLM_PAGES_NAME));
        final String cDate = cursor.getString(cursor
                .getColumnIndexOrThrow(C.DB.CLM_PAGES_CREATE_DATE));
        final String uDate = cursor.getString(cursor
                .getColumnIndexOrThrow(C.DB.CLM_PAGES_UPDATE_DATE));

        // 画面にセットします
        holder.id.setText(String.valueOf(id));
        holder.name.setText(name);
        holder.cDate.setText(cDate);
        holder.uDate.setText(uDate);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        // 新しくViewを作ります
        final View view = mInflater.inflate(R.layout.gridview_row_note, null);
        ViewHolder holder = new ViewHolder();
        holder.id = (TextView) view.findViewById(R.id.row_note_index);
        holder.name = (TextView) view.findViewById(R.id.row_note_name);
        holder.cDate = (TextView) view.findViewById(R.id.row_note_create_date);
        holder.uDate = (TextView) view.findViewById(R.id.row_note_update_date);

        view.setTag(holder);

        return view;
    }


}
