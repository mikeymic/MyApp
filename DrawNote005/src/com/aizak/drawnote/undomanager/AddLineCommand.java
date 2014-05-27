package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.Data;
import com.aizak.drawnote.model.Line;

public class AddLineCommand implements ICommand{
	Line line;

	public AddLineCommand(Line line) {
		this.line = line;
	}

	@Override
	public void invoke() {
		Data.editingLine.add(line);
	}

	@Override
	public void redo() {
		Data.editingLine.add(line);
	}

	@Override
	public void undo() {
		Data.editingLine.remove(line);
	}

	@Override
	public void clear() {
		Data.editingLine.clear();
		Data.editingLine.add(line);
	}

}