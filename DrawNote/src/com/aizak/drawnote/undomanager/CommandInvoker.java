package com.aizak.drawnote.undomanager;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandInvoker {
	private Deque<ICommand> undoBuffer;
	private Deque<ICommand> redoBuffer;

	public CommandInvoker() {
		undoBuffer = new ArrayDeque<ICommand>();
		redoBuffer = new ArrayDeque<ICommand>();
	}

	public void invoke(ICommand command) {
		command.invoke();
		redoBuffer.clear();
		undoBuffer.addFirst(command);
	}

	public void undo() {
		if (undoBuffer.isEmpty()) {
			return;
		}
		ICommand command = undoBuffer.removeFirst();
		command.undo();
		redoBuffer.addFirst(command);
	}

	public void redo() {
		if (redoBuffer.isEmpty()) {
			return;
		}

		ICommand command = redoBuffer.removeFirst();
		command.redo();
		undoBuffer.addFirst(command);
	}

	public void clear(ICommand command) {

		if (redoBuffer.isEmpty()) {
			return;
		} else {
			for (int i = 0; i <= redoBuffer.size(); i++) {
				ICommand ic = redoBuffer.removeFirst();
				undoBuffer.addLast(ic);//後ろに追加
			}
			redoBuffer.clear();
		}
		command.clear();
		redoBuffer.addFirst(command);
	}
}