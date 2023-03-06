package com.ebay.graphql.types;

import java.util.Objects;

import com.ebay.graphql.Generated;

public class GraphQLReference implements GraphQLType {

	private String referenceTypeName;
	private boolean nullable = true;

	public GraphQLReference(String referenceTypeName) {

		if (referenceTypeName.trim().endsWith("!")) {
			nullable = false;
			referenceTypeName = referenceTypeName.substring(0, referenceTypeName.length()-1);
		}

		this.referenceTypeName = referenceTypeName.trim();
	}

	public String getReferenceTypeName() {
		return referenceTypeName;
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(nullable, referenceTypeName);
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
		GraphQLReference other = (GraphQLReference) obj;
		return nullable == other.nullable && Objects.equals(referenceTypeName, other.referenceTypeName);
	}

	@Generated
	@Override
	public String toString() {
		return "GraphQLReference [referenceTypeName=" + referenceTypeName + ", nullable=" + nullable + "]";
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
