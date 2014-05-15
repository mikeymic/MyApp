package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.EditingLine;

public abstract class DataModelCommand implements ICommand {
	protected EditingLine editingLine;

	public DataModelCommand(EditingLine editingLine) {
		this.editingLine = editingLine;
	}
}
