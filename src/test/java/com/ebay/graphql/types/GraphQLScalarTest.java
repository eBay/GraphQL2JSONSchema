package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class GraphQLScalarTest {

	@DataProvider(name = "scalarTestEnumValues")
	public Object[][] scalarTestEnumValues() {
		return new Object[][] {
			{ "Boolean", GraphQLScalarValue.BOOLEAN },
			{ "Float", GraphQLScalarValue.FLOAT },
			{ "Id", GraphQLScalarValue.ID },
			{ "Int", GraphQLScalarValue.INT },
			{ "String", GraphQLScalarValue.STRING },
			{ "int", GraphQLScalarValue.INT },
			{ "INT", GraphQLScalarValue.INT },
		};
	}
	
	@Test(dataProvider = "scalarTestEnumValues")
	public void testScalarValueToEnumConversion(String value, GraphQLScalarValue expectedScalar) {
		GraphQLScalarValue actualScalar = GraphQLScalarValue.getScalarValueFromString(value);
		assertThat(actualScalar, is(equalTo(expectedScalar)));
	}
	
	@Test
	public void testUnknownScalarType() {
		GraphQLScalarValue actualScalar = GraphQLScalarValue.getScalarValueFromString("Foo");
		assertThat(actualScalar, is(nullValue()));
	}
	
	@DataProvider(name = "scalarTestStringValues")
	public Object[][] scalarTestValues() {
		return new Object[][] {
			{ "Boolean", new GraphQLScalar(GraphQLScalarValue.BOOLEAN) },
			{ "Float", new GraphQLScalar(GraphQLScalarValue.FLOAT) },
			{ "Id", new GraphQLScalar(GraphQLScalarValue.ID) },
			{ "Int", new GraphQLScalar(GraphQLScalarValue.INT) },
			{ "String", new GraphQLScalar(GraphQLScalarValue.STRING) },
			{ "int", new GraphQLScalar(GraphQLScalarValue.INT) },
			{ "INT", new GraphQLScalar(GraphQLScalarValue.INT) },
		};
	}
	
	@Test(dataProvider = "scalarTestStringValues")
	public void testScalarStringValueToGraphQLScalarInstance(String value, GraphQLScalar expectedScalar) {
		GraphQLScalar actualScalar = new GraphQLScalar(value);
		assertThat(actualScalar, is(equalTo(expectedScalar)));
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void unknownScalarStringInitialization() throws Exception {
		new GraphQLScalar("Foo");
		
	}
	
	@Test(expectedExceptions = NullPointerException.class)
	public void nullScalarStringInitialization() throws Exception {
		String value = null;
		new GraphQLScalar(value);
	}

	@Test
	public void nonNullableValueParsing() throws Exception {
		GraphQLScalar scalar = new GraphQLScalar("Boolean!");
		assertThat(scalar.isNullable(), is(equalTo(false)));
	}
}
