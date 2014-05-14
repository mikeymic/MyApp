package com.aizak.drawnote.model;

import java.util.ArrayList;
import java.util.List;

public class EditingLine {
    public static List<Line> lines;

    public EditingLine() {
        lines = new ArrayList<Line>();
    }

	public static List<Line> getLines() {
		return lines;
	}

	public static void setLines(List<Line> lines) {
		EditingLine.lines = lines;
	}
}