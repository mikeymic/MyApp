package com.aizak.drawnote.fragment.handler;

import android.content.Context;

public interface OptionItemActionHander<E> {
	public boolean handle(Context context, E entity);
}
