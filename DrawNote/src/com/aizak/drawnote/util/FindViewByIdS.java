package com.aizak.drawnote.util;

import android.view.View;

public interface FindViewByIdS {

	@SuppressWarnings("unchecked")
	public<T extends View> T findViewByIdS(final int id);
}
