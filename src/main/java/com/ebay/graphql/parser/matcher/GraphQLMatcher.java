package com.ebay.graphql.parser.matcher;

import java.util.regex.Pattern;

public class GraphQLMatcher {
	
	public enum Nullable {
		NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT("^.*\\[\\[.*\\!\\]\\!\\]\\!\\s*$"),	// [[Int!]!]!
		NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT("^.*\\[\\[.*\\!\\]\\!\\]\\s*$"),				// [[Int!]!]
		NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT("^.*\\[\\[.*\\!\\]\\]\\s*$"),						// [[Int!]]
		NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW("^.*\\[\\[.*\\]\\!\\]\\s*$"),							// [[Int]!]
		NON_NULLABLE_MULTIDIMENSIONAL_ARRAY("^.*\\[\\[.*\\]\\]\\!\\s*$"),								// [[Int]]!
		NON_NULLABLE_LIST_AND_ELEMENT("^.*\\[.*\\!\\]\\!\\s*$"),										// [Int!]!
		NON_NULLABLE_LIST_ELEMENT("^.*\\[.*\\!\\]\\s*$"),												// [Int!]
		NON_NULLABLE_LIST("^.*\\[.*\\]\\!\\s*$"),														// [Int]!
		NON_NULLABLE_SCALAR_OR_OBJECT("^.*\\!\\s*$"), 								// Int!
		NULLABLE(".*");
		
		private final String pattern;
		
		Nullable(String pattern) {
			this.pattern = pattern;
		}
		
		public String getPattern() {
			return pattern;
		}
	}

	public enum LineType {
		COMMENT("^\\s*#.*\\s*$"),
		MULTI_LINE_DESCRIPTION_IN_ONE_LINE("^\\s*\"\"\".*\"\"\"\\s*$"),
		MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE("^.*\"\"\".*\\s*$"),
		SINGLE_LINE_DESCRIPTION("^\\s*\".*\"\\s*$"),
		SCHEMA("^\\s*(extend\\s)?schema\\s*\\{\\s*$"),
		SCHEMA_QUERY("^\\s*query\\s*:.*$"),
		SCHEMA_MUTATION("^\\s*mutation\\s*:.*$"),
		SCHEMA_SUBSCRIPTION("^\\s*subscription\\s*:.*$"),
		QUERY("^\\s*(extend\\s)?type\\sQuery\\s*\\{\\s*$"),
		MUTATION("^\\s*(extend\\s)?type\\sMutation\\s*\\{\\s*$"),
		SUBSCRIPTION("^\\s*(extend\\s)?type\\sSubscription\\s*\\{\\s*$"),
		FIELD_INT("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*Int!?\\s*(@.*)?\\s*$"),
		FIELD_FLOAT("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*Float!?\\s*(@.*)?\\s*$"),
		FIELD_STRING("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*String!?\\s*(@.*)?\\s*$"),
		FIELD_BOOLEAN("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*Boolean!?\\s*(@.*)?\\s*$"),
		FIELD_ID("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*ID!?\\s*(@.*)?\\s*$"),
		FIELD_REFERENCE("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s*:\\s*([A-Za-z0-9_]+)!?\\s*(@.*)?\\s*$"),
		OBJECT_DEFINITION("^\\s*type\\s+([A-Za-z_])([A-Za-z0-9_]*)\\s*(implements.*)?\\s*(@.*)?\\{\\s*$"),
		SCALAR_DEFINITION("^\\s*scalar\\s+([A-Za-z_])([A-Za-z0-9_]*)\\s?(@.*)?$"),
		UNION_DEFINITION("^\\s*union\\s+([A-Za-z_])([A-Za-z0-9_]*)\\s*=\\s*(([A-Za-z_])([A-Za-z0-9_]*)\\s?\\|?\\s?)*\\s*$"),
		UNION_MEMBER("^(\\s*\\|\\s*([A-Za-z_])([A-Za-z0-9_]*))+\\s*$"),
		ENUM_DEFINITION("^\\s*enum\\s+([A-Za-z_])([A-Za-z0-9_]*)\\s*\\{\\s*$"),
		FIELD_LIST("^\\s*([A-Za-z_])([A-Za-z0-9_]*)(\\(.*\\))?\\s?:\\s*\\[\\[?.*\\!?\\]?\\!?\\]\\!?\\s*$"),
		
		// Closing curly braces are expected on their own line.
		CLOSING_CURLY_BRACE("^\\s*}\\s*$"),

		// Keep UNMATCHED_TEXT as the last entry to avoid matching this general catch
		// all until the end.
		UNMATCHED_TEXT("^.*$");

		private final String pattern;

		LineType(String pattern) {
			this.pattern = pattern;
		}

		public String getPattern() {
			return pattern;
		}
	}

	private GraphQLMatcher() {
		// Prevent instantiation.
	}
	
	/**
	 * Check if the field (or line) is marked as deprecated.
	 * 
	 * @param line Line of text to check.
	 * @return True if deprecated, false otherwise.
	 */
	public static boolean isFieldDeprecated(String line) {
		return Pattern.matches("^.*\\s@deprecated\\s*.*$", line);
	}
	
	/**
	 * Check if the line is nullable and return the nullable variant found for the
	 * line.
	 * 
	 * @param line Line to check.
	 * @return Nullable variant found.
	 */
	public static Nullable isNullable(String line) {

		for (Nullable element : Nullable.values()) {
			if (element == Nullable.NULLABLE) {
				continue;
			} else if (Pattern.matches(element.getPattern(), line)) {
				return element;
			}
		}

		return Nullable.NULLABLE;
	}

	/**
	 * Get the line type for the line passed in.
	 * 
	 * @param line Line to match to a line type.
	 * @return LineType matched.
	 */
	public static LineType getLineType(String line) {
		
		if (line == null) {
			return LineType.UNMATCHED_TEXT;
		}

		for (LineType lineType : LineType.values()) {
			if (Pattern.matches(lineType.getPattern(), line)) {
				return lineType;
			}
		}

		// We should never reach this point.
		return LineType.UNMATCHED_TEXT;
	}
}
