package com.ebay.graphql.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ebay.graphql.Generated;

public class GraphQLEnum implements GraphQLType {

	List<String> enumValues = new ArrayList<>();
	private boolean nullable = true;

	/**
	 * Add an enum value. If will only be added if it doesn't already exist.
	 * 
	 * @param enumValue enum value to add.
	 */
	public void addEnumValue(String enumValue) {
		if (!enumValues.contains(enumValue)) {
			enumValues.add(enumValue);
		}
	}

	public List<String> getEnumValues() {
		return Collections.unmodifiableList(enumValues);
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(enumValues, nullable);
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
		GraphQLEnum other = (GraphQLEnum) obj;
		return Objects.equals(enumValues, other.enumValues) && nullable == other.nullable;
	}

	@Generated
	@Override
	public String toString() {
		return "GraphQLEnum [enumValues=" + enumValues + ", nullable=" + nullable + "]";
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

}
