package com.github.rosmith.nlp.service.parser;

/**
 * @author Smith Djomkam
 * @author Max Nitze
 * @author Fred Tchambo
 * 
 * This class represents a position in a source code.
 */
public class Position {
	/** Lines. */
	private int line;

	/** Columns. */
	private int column;

	/**
	 * Constructor.
	 * 
	 * @param line
	 * @param column
	 */
	public Position(int line, int column) {
		this.line = line;
		this.column = column;
	}

	/**
	 * returns the line of the code.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * returns the column of the code.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * This method continuously count the code position. For this reason the
	 * actual character has to be evaluated.
	 * 
	 * @param c
	 *            the actual character. '\n' jumps to the start of the following
	 *            line '\t' jumps to the next tabulator position (multiple of
	 *            8). '\r' will be ignored.
	 */
	public void next(char c) {
		if (c == '\n') {
			++line;
			column = 1;
		} else if (c == '\t') {
			column += 8 - (column - 1) % 8;
		} else if (c != '\r') {
			++column;
		}
	}
}
