package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.EditingLine;
import com.aizak.drawnote.model.Line;

public class AddLineCommand extends DataModelCommand {

	Line line;

	public AddLineCommand(EditingLine editingLine, Line line) {
		super(editingLine);
		this.line = line;
	}

	@Override
	public void invoke() {
		editingLine.getLines().add(line);
	}

	@Override
	public void redo() {
		editingLine.getLines().add(line);
	}

	@Override
	public void undo() {
		editingLine.getLines().remove(line);
	}

	@Override
	public void clear() {
		editingLine.getLines().clear();
		editingLine.getLines().add(line);
	}

}