package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.text.ParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.types.GraphQLList.Dimensionality;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class GraphQLListTest {

	@DataProvider(name = "validListValues")
	public Object[][] validListValues() {
		return new Object[][] {
			{ "[Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ " [Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ "[Int] ", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ "		[Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ "[[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI },
			{ " [[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI },
			{ "[[Int]] ", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI },
			{ "		[[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI },
			
			{ "[ID]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE },
			{ "[Boolean]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE },
			{ "[Float]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE },
			{ "[String]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE },
			
			{ "[INT]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ "[ID]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE },
			{ "[BOOLEAN]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE },
			{ "[FLOAT]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE },
			{ "[STRING]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE },
			{ "[int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE },
			{ "[id]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE },
			{ "[boolean]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE },
			{ "[float]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE },
			{ "[string]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE },
			
			{ "[Test]", new GraphQLReference("Test"), Dimensionality.SINGLE },
			{ "[[Test]]", new GraphQLReference("Test"), Dimensionality.MULTI },
		};
	}
	
	@Test(dataProvider = "validListValues")
	public void parseValidLists(String line, GraphQLType expectedType, Dimensionality expectedDimensioniality) throws ParseException {
		GraphQLList list = new GraphQLList(line);
		GraphQLList expectedList = new GraphQLList(expectedType, expectedDimensioniality);
		assertThat(list, is(equalTo(expectedList)));
		assertThat(list.getType(), is(equalTo(expectedList.getType())));
		assertThat(list.getDimensionality(), is(equalTo(expectedList.getDimensionality())));
	}
	
	@DataProvider(name = "invalidListValues")
	public Object[][] invalidListValues() {
		return new Object[][] {
			{ null },
			{ "" },
			{ "[]" },
			{ "[[]]" },
			{ "foo: [Int]" },
			{ "foo" },
			{ ": [Int]" },
			{ "[[[Int]]]" },
			{ "[]Int[]" },
		};
	}
	
	@Test(dataProvider = "invalidListValues", expectedExceptions = ParseException.class)
	public void parseInvalidLists(String line) throws ParseException {
		new GraphQLList(line);
	}
}
