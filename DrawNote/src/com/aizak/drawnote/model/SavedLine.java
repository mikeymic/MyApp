package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.List;

public class SavedLine {
	public static List<Line> lines;

	public SavedLine() {
		lines = new ArrayList<Line>();
	}

	public static List<Line> getLines() {
		return lines;
	}

	public static void setLines(List<Line> lines) {
		SavedLine.lines = lines;
	}
}
