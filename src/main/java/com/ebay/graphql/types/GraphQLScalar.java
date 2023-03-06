package com.ebay.graphql.types;

import java.util.Objects;

import com.ebay.graphql.Generated;

/**
 * The STRING enum is used for both explicit String scalar definitions and
 * custom scalar definitions.
 */
public class GraphQLScalar implements GraphQLType {

	private boolean nullable = true;
	private final GraphQLScalarValue value;

	public enum GraphQLScalarValue {
		BOOLEAN, FLOAT, ID, INT, STRING;

		/**
		 * Case insensitive value to enum matching.
		 * 
		 * @param value Value to convert to scalar value.
		 * @return Scalar value or null if not matched.
		 */
		public static GraphQLScalarValue getScalarValueFromString(String value) {
			for (GraphQLScalarValue scalar : GraphQLScalarValue.values()) {
				if (scalar.name().equalsIgnoreCase(value)) {
					return scalar;
				}
			}

			return null;
		}
	}
	
	public GraphQLScalar(GraphQLScalarValue value) {
		Objects.requireNonNull(value, "GrapyhQLScalar may not be initialized to null.");
		this.value = value;
	}
	
	public GraphQLScalar(String value) {
		Objects.requireNonNull(value, "GrapyhQLScalar may not be initialized to null.");

		if (value.trim().endsWith("!")) {
			nullable = false;
			value = value.substring(0, value.length()-1);
		}

		this.value = GraphQLScalarValue.getScalarValueFromString(value);
		if (this.value == null) {
			throw new IllegalArgumentException(String.format("Value %s does not match a known scalar type.", value));
		}
	}
	
	public GraphQLScalarValue getScalarValue() {
		return value;
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

	@Override
	public int hashCode() {
		return Objects.hash(nullable, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphQLScalar other = (GraphQLScalar) obj;
		return nullable == other.nullable && value == other.value;
	}

	@Override
	public String toString() {
		return "GraphQLScalar [nullable=" + nullable + ", value=" + value + "]";
	}

	
}
