package com.aizak.drawnote.fragment.handler;

import android.content.Context;
import android.widget.Toast;

public class OptionSettingHandler implements ItemActionHander<Void> {

	@Override
	public boolean handle(Context context, Void entity) {
		Toast.makeText(context, "TEST# action setting ", Toast.LENGTH_SHORT).show();
		return true;
	}

}
