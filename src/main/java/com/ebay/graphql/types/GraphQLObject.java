package com.ebay.graphql.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.ebay.graphql.Generated;

public class GraphQLObject implements GraphQLType {

	private Map<String, GraphQLType> fields = new HashMap<>();
	private boolean nullable = true;

	public void addField(String fieldName, GraphQLType fieldType) {
		fields.put(fieldName, fieldType);
	}

	public Map<String, GraphQLType> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(fields, nullable);
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
		GraphQLObject other = (GraphQLObject) obj;
		return Objects.equals(fields, other.fields) && nullable == other.nullable;
	}

	@Generated
	@Override
	public String toString() {
		return "GraphQLObject [fields=" + fields + ", nullable=" + nullable + "]";
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
