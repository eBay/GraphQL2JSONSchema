package com.ebay.graphql.parser;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ebay.graphql.model.GraphQLSchema;
import com.ebay.graphql.parser.matcher.GraphQLMatcher;
import com.ebay.graphql.parser.matcher.GraphQLMatcher.LineType;
import com.ebay.graphql.types.GraphQLEnum;
import com.ebay.graphql.types.GraphQLObject;
import com.ebay.graphql.types.GraphQLScalar;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;
import com.ebay.graphql.types.GraphQLType;
import com.ebay.graphql.types.FieldKeyValuePair;

public class GraphQLParser {

	private static final String IMPLEMENTS_KEYWORD = " implements ";

	public GraphQLSchema parseGraphQL(File schemaFile) {

		GraphQLSchema completeSchema = new GraphQLSchema();

		if (schemaFile == null || !schemaFile.exists()) {
			return completeSchema;
		}

		// Check folder containing schema file for other graphql schema definitions to
		// load to complete the schema.
		if (schemaFile.isFile()) {
			schemaFile = schemaFile.getParentFile();
		}
		List<File> schemaFiles = getGraphQLSchemaFiles(schemaFile);

		GraphQLSchema schema;
		for (File file : schemaFiles) {
			GraphQLFile graphQLFile = new GraphQLFile(file);
			schema = processLinesOfText(graphQLFile);
			completeSchema.addSchema(schema);
		}

		return completeSchema;
	}
	
	protected final List<File> getGraphQLSchemaFiles(File directory) {
		
		List<File> graphQLSchemaFiles = new ArrayList<>();
		
		if (!directory.isDirectory()) {
			graphQLSchemaFiles.add(directory);
			return graphQLSchemaFiles;
		}
		
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					graphQLSchemaFiles.addAll(getGraphQLSchemaFiles(file));
				} else {
					graphQLSchemaFiles.add(file);
				}
			}
		}
		
		return graphQLSchemaFiles;
	}

	protected final GraphQLSchema processLinesOfText(GraphQLFile graphQLFile) {

		GraphQLSchema schema = new GraphQLSchema();

		String line;
		LineType lineType;
		@SuppressWarnings("unused")
		String lastDescription = null;

		try {
			while (graphQLFile.hasMoreLines()) {

				line = graphQLFile.getCurrentLine();
				lineType = GraphQLMatcher.getLineType(line);

				switch (lineType) {
				case COMMENT:
					// Advance, but don't process. We don't process comments.
					graphQLFile.getNextLine();
					break;
				case MULTI_LINE_DESCRIPTION_IN_ONE_LINE:
					lastDescription = processSingleLineDescription(graphQLFile);
					break;
				case MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE:
					lastDescription = processMultiLineDescription(graphQLFile);
					break;
				case SINGLE_LINE_DESCRIPTION:
					lastDescription = processSingleLineDescription(graphQLFile);
					break;
				case SCHEMA:
					// Advance - nothing to process.
					graphQLFile.getNextLine();
					break;
				case SCHEMA_QUERY:
					processSchemaQuery(graphQLFile, schema);
					break;
				case SCHEMA_MUTATION:
					processSchemaMutation(graphQLFile, schema);
					break;
				case SCHEMA_SUBSCRIPTION:
					processSchemaSubscription(graphQLFile, schema);
					break;
				case QUERY:
					processQuery(graphQLFile, schema);
					break;
				case MUTATION:
					processMutation(graphQLFile, schema);
					break;
				case SUBSCRIPTION:
					processSubscription(graphQLFile, schema);
					break;
				case OBJECT_DEFINITION:
					String objectTypeName = getObjectTypeName(line);
					if (objectTypeName.equals(schema.getQueryTypeName())) {
						processQuery(graphQLFile, schema);
					} else if (objectTypeName.equals(schema.getMutationTypeName())) {
						processMutation(graphQLFile, schema);
					} else if (objectTypeName.equals(schema.getSubscriptionTypeName())) {
						processSubscription(graphQLFile, schema);
					} else {
						processObject(graphQLFile, schema);
					}
					break;
				case SCALAR_DEFINITION:
					processScalar(graphQLFile, schema);
					break;
				case UNION_DEFINITION:
					processUnion(graphQLFile, schema);
					break;
				case ENUM_DEFINITION:
					processEnum(graphQLFile, schema);
					break;
				case UNMATCHED_TEXT:
				default:
					System.out.println(String.format("Unmatched line of text [%s] [ln: %d].", line,
							graphQLFile.getCurrentLineNumber()));
					// Advance - nothing to process.
					graphQLFile.getNextLine();
					break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return schema;
	}

	/**
	 * Process single line description. Can be one or three " wrapping the line.
	 * 
	 * @param graphQLFile GraphQL file to process.
	 * @return Description.
	 */
	protected final String processSingleLineDescription(GraphQLFile graphQLFile) {
		String line = graphQLFile.getCurrentLineAndThenAdvance();
		line = line.replaceFirst("^\"*", "");
		line = line.replaceFirst("\"*$", "");
		return line.trim();
	}

	/**
	 * Process multiple line description. Turn it into a single line string.
	 * 
	 * @param graphQLFile GraphQL file to process.
	 * @return Multi-line description as a single line stirng.
	 */
	protected final String processMultiLineDescription(GraphQLFile graphQLFile) {

		StringBuilder builder = new StringBuilder();

		String line = graphQLFile.getCurrentLine();
		line = line.replaceFirst("^\"*", "");
		builder.append(line);

		while (graphQLFile.hasMoreLines()) {
			line = graphQLFile.getNextLine().trim();
			if (GraphQLMatcher.getLineType(line) == LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE) {
				line = line.replaceFirst("\"*$", "").trim();
				if (builder.length() > 0 && !line.isEmpty()) {
					builder.append(" ");
				}
				builder.append(line);
				break;
			}
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(line);
		}

		return builder.toString();
	}
	
	protected final void processSchemaQuery(GraphQLFile graphQLFile, GraphQLSchema schema) {
		String line = graphQLFile.getCurrentLineAndThenAdvance();
		line = line.substring(line.indexOf(":") + 1).trim();
		schema.setQueryTypeName(line);
	}

	protected final void processQuery(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		FieldKeyValuePair kvp;
		String line = graphQLFile.getCurrentLine();
		while (GraphQLMatcher.getLineType(line) != LineType.CLOSING_CURLY_BRACE && line != null) {
			kvp = extractOperationApi(graphQLFile);
			schema.addQuery(kvp.getKey(), kvp.getValue());
			line = graphQLFile.peekNextLine();
		}
	}
	
	protected final void processSchemaMutation(GraphQLFile graphQLFile, GraphQLSchema schema) {
		String line = graphQLFile.getCurrentLineAndThenAdvance();
		line = line.substring(line.indexOf(":") + 1).trim();
		schema.setMutationTypeName(line);
	}

	protected final void processMutation(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		FieldKeyValuePair kvp;
		String line = graphQLFile.getCurrentLine();
		while (GraphQLMatcher.getLineType(line) != LineType.CLOSING_CURLY_BRACE && line != null) {
			kvp = extractOperationApi(graphQLFile);
			schema.addMutation(kvp.getKey(), kvp.getValue());
			line = graphQLFile.peekNextLine();
		}
	}
	
	protected final void processSchemaSubscription(GraphQLFile graphQLFile, GraphQLSchema schema) {
		String line = graphQLFile.getCurrentLineAndThenAdvance();
		line = line.substring(line.indexOf(":") + 1).trim();
		schema.setSubscriptionTypeName(line);
	}

	protected final void processSubscription(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		FieldKeyValuePair kvp;
		String line = graphQLFile.getCurrentLine();
		while (GraphQLMatcher.getLineType(line) != LineType.CLOSING_CURLY_BRACE && line != null) {
			kvp = extractOperationApi(graphQLFile);
			schema.addSubscription(kvp.getKey(), kvp.getValue());
			line = graphQLFile.peekNextLine();
		}
	}

	protected final String getObjectTypeName(String currentLine) {
		
		currentLine = currentLine.replaceFirst("^\\s*type\\s", "");
		currentLine = currentLine.replace("{", "");

		// Drop the interface if it exists
		if (currentLine.contains(IMPLEMENTS_KEYWORD)) {
			currentLine = currentLine.substring(0, currentLine.indexOf(IMPLEMENTS_KEYWORD));
		}

		currentLine = currentLine.trim();
		return currentLine;
	}
		
	protected final void processObject(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		String objectName = getObjectTypeName(graphQLFile.getCurrentLine());
		
		GraphQLObject graphQLObject = new GraphQLObject();

		FieldKeyValuePair kvp;
		String line;
		boolean nullable = true;
		while (GraphQLMatcher.getLineType(line = graphQLFile.getNextLine()) != LineType.CLOSING_CURLY_BRACE
				&& line != null) {

			nullable = true;

			if (canIgnoreLine(line)) {
				continue;
			}

			line = line.trim();

			if (line.endsWith("!")) {
				nullable = false;
				line = line.substring(0, line.length() - 1);
			}

			kvp = new FieldKeyValuePair(line);
			GraphQLType value = kvp.getValue();
			if (!nullable) {
				value.makeNonNullable();
			}
			graphQLObject.addField(kvp.getKey(), value);
		}

		schema.addType(objectName, graphQLObject);
	}

	protected final void processScalar(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {
		String line = graphQLFile.getCurrentLineAndThenAdvance();
		line = line.replaceFirst("^^\\s*scalar", "").replaceFirst("@specifiedBy.*$", "").trim();
		schema.addType(line, new GraphQLScalar(GraphQLScalarValue.STRING));
	}

	protected final void processUnion(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		/*
		 * Unions may span multiple lines. Handle both cases.
		 * 
		 * 1) union Food = Apple | Banana | Pear
		 * 
		 * 2) union Food = | Apple | Banana | Pear
		 */
		List<String> unionTypes = new ArrayList<>();
		String unionName = null;

		String line = graphQLFile.getCurrentLine();
		line = line.replaceFirst("union", "");
		String[] union = line.split("=");

		if (union.length > 0) {
			unionName = union[0].trim();
		}

		if (union.length == 2) {
			union = union[1].split("\\|");
			for (String u : union) {
				unionTypes.add(u.trim());
			}
		}

		while (GraphQLMatcher.getLineType(line = graphQLFile.getNextLine()) == LineType.UNION_MEMBER) {
			line = line.replace("|", "").trim();
			unionTypes.add(line);
		}

		if (unionName != null) {
			schema.addUnion(unionName, unionTypes);
		}
	}

	protected final void processEnum(GraphQLFile graphQLFile, GraphQLSchema schema) throws ParseException {

		String enumName = graphQLFile.getCurrentLine();
		enumName = enumName.replace("enum", "");
		enumName = enumName.replace("{", "");
		enumName = enumName.trim();

		GraphQLEnum graphQLEnum = new GraphQLEnum();

		String line;
		while (GraphQLMatcher.getLineType(line = graphQLFile.getNextLine()) != LineType.CLOSING_CURLY_BRACE
				&& line != null) {

			line = line.trim();
			if (!Pattern.matches("^([A-Za-z0-9_]*)$", line)) {
				continue;
			}

			graphQLEnum.addEnumValue(line);
		}

		schema.addType(enumName, graphQLEnum);
	}

	protected final FieldKeyValuePair extractOperationApi(GraphQLFile graphQLFile) throws ParseException {

		/*
		 * Operation APIs can take on two forms:
		 * 
		 * 1) translate(fromLanguage: Language, toLanguage: Language, text: String):
		 * String 2) translate( fromLanguage: Language toLanguage: Language text: String
		 * ): String
		 */

		StringBuilder queryBuilder = new StringBuilder();
		LineType lineType;
		String line;
		boolean insideParameterList = false;

		while (GraphQLMatcher.getLineType(line = graphQLFile.getNextLine()) != LineType.CLOSING_CURLY_BRACE
				&& line != null) {

			// Skip the line that is the opening definition for a schema definition.
			if (line.contains("{")) {
				continue;
			}

			lineType = GraphQLMatcher.getLineType(line);
			if (lineType == LineType.COMMENT || lineType == LineType.SINGLE_LINE_DESCRIPTION
					|| lineType == LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE) {
				continue;
			} else if (lineType == LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE) {
				while (GraphQLMatcher.getLineType(
						line = graphQLFile.getNextLine()) != LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE) {
					// Churn through the multi-line description until we get to the end signature.
					; // NOPMD - ignore this
				}
				continue;
			}

			if (line.contains("(")) {
				insideParameterList = true;
			}

			// DO NOT combine this with the above check as an 'else if'.
			// This allows us to handle the case where the parameter list is defined in a
			// single line.
			if (line.contains(")")) {
				insideParameterList = false;
			}

			if (queryBuilder.length() > 0 && !line.isEmpty()) {
				queryBuilder.append(" ");
			}

			queryBuilder.append(line.trim());

			// When we find a line with a ':' outside the parameter set we have found the
			// type definition and can consider the operation API parsed.
			if (!insideParameterList && line.contains(":")) {
				break;
			}
		}

		return new FieldKeyValuePair(queryBuilder.toString());
	}

	private boolean canIgnoreLine(String line) {

		switch (GraphQLMatcher.getLineType(line)) {
		case COMMENT:
		case SINGLE_LINE_DESCRIPTION:
		case MULTI_LINE_DESCRIPTION_IN_ONE_LINE:
		case MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE:
		case UNMATCHED_TEXT:
			return true;
		default:
			return false;
		}
	}
}
