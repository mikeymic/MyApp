package com.aizak.drawnote.activity;

import android.view.View;

public interface FindViewByIdS {

	@SuppressWarnings("unchecked")
	public<T extends View> T findViewByIdS(final int id);
}
