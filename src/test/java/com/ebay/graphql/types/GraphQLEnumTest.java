package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

public class GraphQLEnumTest {

	@Test
	public void emptyEnum() {
		GraphQLEnum graphQLEnum = new GraphQLEnum();
		List<String> enumValues = graphQLEnum.getEnumValues();
		assertThat(enumValues, is(equalTo(new ArrayList<>())));
	}
	
	@Test
	public void addEnums() {
		GraphQLEnum graphQLEnum = new GraphQLEnum();
		graphQLEnum.addEnumValue("one");
		graphQLEnum.addEnumValue("two");
		graphQLEnum.addEnumValue("three");
		List<String> enumValues = graphQLEnum.getEnumValues();
		assertThat(enumValues, is(equalTo(Arrays.asList("one", "two", "three"))));
	}
	
	@Test
	public void addDuplicateEnumValue() {
		GraphQLEnum graphQLEnum = new GraphQLEnum();
		graphQLEnum.addEnumValue("one");
		graphQLEnum.addEnumValue("one");
		List<String> enumValues = graphQLEnum.getEnumValues();
		assertThat(enumValues, is(equalTo(Arrays.asList("one"))));
	}
}
