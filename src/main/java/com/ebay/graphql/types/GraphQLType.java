package com.ebay.graphql.types;

public interface GraphQLType {

	/**
	 * Make the type non nullable. All types are nullable by default.
	 */
	void makeNonNullable();

	/**
	 * Check if the type is nullable.
	 * 
	 * @return True if nullable, false otherwise.
	 */
	boolean isNullable();
}
