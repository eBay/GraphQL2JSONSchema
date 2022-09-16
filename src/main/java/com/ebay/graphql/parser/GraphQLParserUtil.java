package com.ebay.graphql.parser;

import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GraphQLParserUtil {
	
	private static final String LINE_PARSE_ERROR = "Line [%s] cannot be parsed as <key>:<value> pair.";
	private static final String INVALID_LINE_ERROR = "Line does not contain a valid key value pair.";
	private static final String VALID_LINE_PATTERN = "^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*([\\[]*)(([A-Za-z_])([A-Za-z0-9_!]*))?([\\]!]*)\\s*$";

	/**
	 * Get the map key from the given line. The key includes everything to the left
	 * of the last : found in the line.
	 * 
	 * @param line line to extract key from.
	 * @return key value.
	 * @throws ParseException if extracting the key is not possible.
	 */
	public static String getKeyForLine(String line) throws ParseException {
		
		if (!Pattern.matches(VALID_LINE_PATTERN, line)) {
			throw new ParseException(INVALID_LINE_ERROR, -1);
		}

		try {
			line = line.replaceAll(":\\s*([\\[]*)(([A-Za-z_])([A-Za-z0-9_!]*))?([\\]!]*)\\s*$", "").trim();
		} catch (PatternSyntaxException e) {
			throw new ParseException(String.format(LINE_PARSE_ERROR, line), -1);
		}

		if (line.isEmpty()) {
			throw new ParseException(String.format("Key cannot be parsed from line [%s].", line), -1);
		}

		return line;
	}

	/**
	 * Get the map value from the given line. The value includes everything to the
	 * right of the last : found in the line.
	 * 
	 * @param line line to extract value from.
	 * @return value.
	 * @throws ParseException if extracting the value is not possible.
	 */
	public static String getValueForLine(String line) throws ParseException {
		
		if (!Pattern.matches(VALID_LINE_PATTERN, line)) {
			throw new ParseException(INVALID_LINE_ERROR, -1);
		}

		try {
			line = line.replaceAll("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:?", "").trim();
		} catch (PatternSyntaxException e) {
			throw new ParseException(String.format(LINE_PARSE_ERROR, line), -1);
		}

		if (line.isEmpty()) {
			throw new ParseException(String.format("Value cannot be parsed from line [%s].", line), -1);
		}

		return line;
	}
}
