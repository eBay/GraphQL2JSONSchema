package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class GraphQLObjectTest {

	@Test
	public void getFieldsWhenEmpty() {
		GraphQLObject graphQLObject = new GraphQLObject();
		Map<String, GraphQLType> fields = graphQLObject.getFields();
		assertThat(fields, is(notNullValue()));
		assertThat(fields.size(), is(equalTo(0)));
	}
	
	@Test
	public void addFields() {
		GraphQLObject graphQLObject = new GraphQLObject();
		graphQLObject.addField("int", new GraphQLScalar(GraphQLScalarValue.INT));
		graphQLObject.addField("string", new GraphQLScalar(GraphQLScalarValue.STRING));
		graphQLObject.addField("ref", new GraphQLReference("URL"));
		Map<String, GraphQLType> fields = graphQLObject.getFields();
		
		Map<String, GraphQLType> expectedFields = new HashMap<>();
		expectedFields.put("int", new GraphQLScalar(GraphQLScalarValue.INT));
		expectedFields.put("string", new GraphQLScalar(GraphQLScalarValue.STRING));
		expectedFields.put("ref", new GraphQLReference("URL"));
		
		assertThat(fields, is(equalTo(expectedFields)));
	}
}
