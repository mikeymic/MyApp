package com.aizak.drawnote.undomanager;

import com.aizak.drawnote.model.DataModel;

public abstract class DataModelCommand implements ICommand {
    protected DataModel mDataModel;

    public DataModelCommand(DataModel dataModel) {
        mDataModel = dataModel;
    }
}
