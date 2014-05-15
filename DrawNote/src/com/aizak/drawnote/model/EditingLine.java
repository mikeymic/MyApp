package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.List;

public class EditingLine {
	private List<Line> lines;

	public EditingLine() {
		lines = new ArrayList<Line>();
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}
}