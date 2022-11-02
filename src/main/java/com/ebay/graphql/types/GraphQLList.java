package com.ebay.graphql.types;

import java.text.ParseException;
import java.util.Objects;
import java.util.regex.Pattern;

import com.ebay.graphql.Generated;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class GraphQLList implements GraphQLType {

	private GraphQLType type;
	private Dimensionality dimensionality;
	private boolean nullable = true;
	private boolean isInnerDimensionNullable = true;

	public enum Dimensionality {
		SINGLE, MULTI
	}

	/**
	 * Given a parsed value (portion to the right of the colon) extract the list
	 * type and dimensionality.
	 * 
	 * @param listSignature list signature to parse.
	 * @throws ParseException if there was an error parsing the list.
	 */
	public GraphQLList(String listSignature) throws ParseException {
		parseList(listSignature);
	}

	/**
	 * Create an instance with the specified type name and diemensionality.
	 * 
	 * @param type           Named type of the list (Int, Float, Url [custom])
	 * @param dimensionality Supports single and multi-dimensional lists (array)
	 */
	public GraphQLList(GraphQLType type, Dimensionality dimensionality) {
		this.type = type;
		this.dimensionality = dimensionality;
	}

	public GraphQLType getType() {
		return type;
	}

	public Dimensionality getDimensionality() {
		return dimensionality;
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(dimensionality, nullable, isInnerDimensionNullable, type);
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
		GraphQLList other = (GraphQLList) obj;
		return dimensionality == other.dimensionality && nullable == other.nullable
				&& isInnerDimensionNullable == other.isInnerDimensionNullable && Objects.equals(type, other.type);
	}

	@Generated
	@Override
	public String toString() {
		return "GraphQLList [type=" + type + ", dimensionality=" + dimensionality + ", nullable=" + nullable
				+ ", isInnerDimensionNullable=" + isInnerDimensionNullable + "]";
	}

	/**
	 * Parse the list values from the list signature.
	 * 
	 * @param listSignature list signature to parse.
	 * @throws ParseException if there was an issue parsing the list signature.
	 */
	private void parseList(String listSignature) throws ParseException {
		
		if (listSignature == null) {
			throw new ParseException("Unable to parse list from null string.", -1);
		}

		listSignature = listSignature.trim();

		if (Pattern.matches("^([\\[]{2})\\s*([A-Za-z_])([A-Za-z0-9_]*)([\\!])?\\s*(\\]\\]|\\]\\!\\])([\\!])?$",
				listSignature)) {
			dimensionality = Dimensionality.MULTI;
			if (Pattern.matches("^.*(\\]\\!\\])([\\!])?$", listSignature)) {
				isInnerDimensionNullable = false;
			}
		} else if (Pattern.matches("^([\\[]{1})\\s*([A-Za-z_])([A-Za-z0-9_])*([\\!])?\\s*([\\]]{1})([\\!])?$",
				listSignature)) {
			dimensionality = Dimensionality.SINGLE;
		} else {
			throw new ParseException(
					String.format("Parsing of list value is not possible with list definition: %s.", listSignature),
					-1);
		}

		if (listSignature.endsWith("!")) {
			nullable = false;
		}
		
		listSignature = listSignature.replaceFirst("^\\[*", "");
		listSignature = listSignature.substring(0, listSignature.indexOf("]"));

		boolean isNullable = true;
		if (listSignature.endsWith("!")) {
			listSignature = listSignature.substring(0, listSignature.length() - 1);
			isNullable = false;
		}

		if ("Boolean".equalsIgnoreCase(listSignature)) {
			type = new GraphQLScalar(GraphQLScalarValue.BOOLEAN);
		} else if ("Float".equalsIgnoreCase(listSignature)) {
			type = new GraphQLScalar(GraphQLScalarValue.FLOAT);
		} else if ("Int".equalsIgnoreCase(listSignature)) {
			type = new GraphQLScalar(GraphQLScalarValue.INT);
		} else if ("ID".equalsIgnoreCase(listSignature)) {
			type = new GraphQLScalar(GraphQLScalarValue.ID);
		} else if ("String".equalsIgnoreCase(listSignature)) {
			type = new GraphQLScalar(GraphQLScalarValue.STRING);
		} else {
			type = new GraphQLReference(listSignature);
		}
		
		if (!isNullable) {
			type.makeNonNullable();
		}
	}

	@Generated
	@Override
	public void makeNonNullable() {
		nullable = false;
	}

	@Generated
	@Override
	public boolean isNullable() {
		return nullable;
	}
	
	/**
	 * For two-dimensional arrays it is possible to have the inner dimension be non
	 * nullable. Check if the inner dimension is nullable or not.
	 * 
	 * EG: [[Test]!] - The inner array is non-nullable.
	 * 
	 * @return True if array is two-dimensional and inner array is nullable, false
	 *         otherwise.
	 */
	public boolean isInnerDimensionNullable() {
		if (dimensionality == Dimensionality.MULTI) {
			return isInnerDimensionNullable;
		}
		return false;
	}

	/**
	 * Make the inner list, of a multi-dimensional list, non nullable.
	 */
	public void makeInnerDimensionNonNullable() {
		isInnerDimensionNullable = false;
	}
}
