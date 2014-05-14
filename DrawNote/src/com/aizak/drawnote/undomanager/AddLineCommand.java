package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.EditingLine;
import com.aizak.drawnote.model.Line;

import android.util.Log;

public class AddLineCommand extends DataModelCommand {

    Line line;

    public AddLineCommand(EditingLine dataModel, Line line) {
        super(dataModel);
        this.line = line;
    }

    public void invoke() {
        mDataModel.lines.add(line);
    }

    public void redo() {
        mDataModel.lines.add(line);
    }

    public void undo() {
        mDataModel.lines.remove(line);
    }


	public void clear() {
		mDataModel.lines.clear();
		mDataModel.lines.add(line);
		Log.d("TEST", "through Command CLEAR");
	}

}