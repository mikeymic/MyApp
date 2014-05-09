package com.aizak.drawnote.fragment.handler;

import android.content.Context;

public class UnknownActionHandler implements ItemActionHander<Void> {

	@Override
	public boolean handle(Context context, Void entity) {
		return false;
	}

}
