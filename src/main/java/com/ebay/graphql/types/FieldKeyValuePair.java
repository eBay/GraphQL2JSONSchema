package com.ebay.graphql.types;

import java.text.ParseException;
import java.util.Objects;

import com.ebay.graphql.Generated;
import com.ebay.graphql.parser.GraphQLParserUtil;
import com.ebay.graphql.parser.matcher.GraphQLMatcher;
import com.ebay.graphql.parser.matcher.GraphQLMatcher.LineType;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class FieldKeyValuePair {

	private String key;
	private GraphQLType value;

	public FieldKeyValuePair(String key, GraphQLType value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Initialize from a string with the format <key>:<value>
	 * 
	 * @param lineOfText Text to parse.
	 * @throws ParseException If lineOfText violates expected format.
	 */
	public FieldKeyValuePair(String lineOfText) throws ParseException {
		
		LineType lineType = GraphQLMatcher.getLineType(lineOfText);
		
		switch(lineType) {
		case FIELD_BOOLEAN:
			value = new  GraphQLScalar(GraphQLScalarValue.BOOLEAN);
			break;
		case FIELD_FLOAT:
			value = new  GraphQLScalar(GraphQLScalarValue.FLOAT);
			break;
		case FIELD_INT:
			value = new  GraphQLScalar(GraphQLScalarValue.INT);
			break;
		case FIELD_ID:
			value = new  GraphQLScalar(GraphQLScalarValue.ID);
			break;
		case FIELD_LIST:
			value = new GraphQLList(GraphQLParserUtil.getValueForLine(lineOfText));
			break;
		case FIELD_STRING:
			value = new  GraphQLScalar(GraphQLScalarValue.STRING);
			break;
		case FIELD_REFERENCE:
			value = new GraphQLReference(GraphQLParserUtil.getValueForLine(lineOfText));
			break;
		default:
			throw new ParseException(String.format("Line of text [%s] is not parsable into a key value pair.", lineOfText), -1);
		}
		
		// Extract the key
		key = GraphQLParserUtil.getKeyForLine(lineOfText);
	}

	public String getKey() {
		return key;
	}

	public GraphQLType getValue() {
		return value;
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldKeyValuePair other = (FieldKeyValuePair) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Generated
	@Override
	public String toString() {
		return "FieldKeyValuePair [key=" + key + ", value=" + value + "]";
	}
}
