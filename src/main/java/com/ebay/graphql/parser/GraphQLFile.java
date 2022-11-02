package com.ebay.graphql.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GraphQLFile {

	private int currentLineNumber = 0;
	List<String> linesOfText;

	public GraphQLFile(File schemaFile) {

		Stream<String> rawLines;
		try {
			rawLines = Files.lines(schemaFile.toPath());
		} catch (IOException e) {
			return;
		}

		linesOfText = rawLines.collect(Collectors.toList());
		rawLines.close();
	}
	
	protected GraphQLFile(List<String> linesOfText) {
		this.linesOfText = new ArrayList<>(linesOfText);
	}

	/**
	 * Get the current line number. If on the first line, expect 1 back.
	 * 
	 * @return Current line number. (not 0 indexed)
	 */
	public int getCurrentLineNumber() {
		return currentLineNumber + 1;
	}

	/**
	 * Check if there are more lines of text to read.
	 * 
	 * @return True if there are more lines to read, false otherwise.
	 */
	public boolean hasMoreLines() {
		if (linesOfText != null && currentLineNumber < linesOfText.size() - 1) {
			return true;
		}
		return false;
	}

	/**
	 * Peek at the next line of text without incrementing the line number.
	 * 
	 * @return Next line of text.
	 */
	public String peekNextLine() {
		if (linesOfText != null && currentLineNumber < linesOfText.size()-1) {
			return linesOfText.get(currentLineNumber + 1);
		}

		return null;
	}

	/**
	 * Get the current line of text.
	 * 
	 * @return Current line of text or null if current line of text is not
	 *         available.
	 */
	public String getCurrentLine() {
		if (linesOfText != null && currentLineNumber < linesOfText.size()) {
			return linesOfText.get(currentLineNumber);
		}

		return null;
	}
	
	/**
	 * Get the current line of text and advance line position to next line.
	 * 
	 * @return Current line of text or null if current line of text is not
	 *         available.
	 */
	public String getCurrentLineAndThenAdvance() {
		String line = null;
		if (linesOfText != null && currentLineNumber < linesOfText.size()) {
			 line = linesOfText.get(currentLineNumber);
			 currentLineNumber++;
		}

		return line;
	}

	/**
	 * Get the next line of text.
	 * 
	 * @return Next line of text or null if no more lines of text are available.
	 */
	public String getNextLine() {
		currentLineNumber++;
		if (linesOfText != null && currentLineNumber < linesOfText.size()) {
			return linesOfText.get(currentLineNumber);
		}

		return null;
	}

	/**
	 * Get the total number of lines of text.
	 * 
	 * @return Total number of lines of text.
	 */
	public int getNumberOfLines() {
		if (linesOfText != null) {
			return linesOfText.size();
		}
		return 0;
	}

	/**
	 * Get the line of text specified by the line number.
	 * 
	 * @param lineNumber Line number of the text requested. (not 0 indexed)
	 * @return Text or null if no text was parsed or the requested line number is
	 *         not inside the valid range of line numbers.
	 */
	public String getLine(int lineNumber) {
		if (linesOfText != null && lineNumber > 0 && lineNumber <= linesOfText.size()) {
			return linesOfText.get(lineNumber-1);
		}

		return null;
	}
}
