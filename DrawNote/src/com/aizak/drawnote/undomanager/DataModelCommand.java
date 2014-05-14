package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.EditingLine;

public abstract class DataModelCommand implements ICommand {
    protected EditingLine mDataModel;

    public DataModelCommand(EditingLine dataModel) {
        mDataModel = dataModel;
    }
}
