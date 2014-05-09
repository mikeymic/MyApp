package com.aizak.drawnote.fragment.handler;

import android.view.MenuItem;

import com.aizak.drawnote.R;

public enum FragmentOptionItemAction {

	ACTION_SETTING(R.id.action_settings, new OptionSettingHandler()),
	ACTION_FULLSCREEN(R.id.action_fullscreen, new OptionFullscreenHandler()),
	UNKNOWN(-1, new UnknownActionHandler());

	private final int menuId;
	private final ItemActionHander handler;

	private FragmentOptionItemAction(final int menuId, final ItemActionHander handler) {
		this.menuId = menuId;
		this.handler = handler;
	}

	public static FragmentOptionItemAction valueOf(MenuItem item) {
		for (FragmentOptionItemAction action : values()) {
			if (action.getMenuId() == item.getItemId()) {
				return action;
			}
		}
		return UNKNOWN;
	}

	public int getMenuId() {
		return menuId;
	}

	public ItemActionHander getHander() {
		return handler;
	}

}
