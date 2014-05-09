package com.aizak.drawnote.fragment.handler;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.aizak.drawnote.activity.ActionBarUtil;

public class OptionFullscreenHandler implements ItemActionHander<View> {

	@Override
	public boolean handle(Context context, View entity) {
		Toast.makeText(context, "TEST# fullscreen mode ", Toast.LENGTH_SHORT).show();

		int visiblity = 0;
		if (ActionBarUtil.isVisible) {
			visiblity = View.GONE;
		} else {
			visiblity = View.VISIBLE;
		}

		ActionBarUtil.actionBarSetVisiblity((Activity) context, visiblity);
		entity.setVisibility(visiblity);
		return true;
	}
}
