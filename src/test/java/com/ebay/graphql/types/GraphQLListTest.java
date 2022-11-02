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
		
		GraphQLReference nonNullableArrayElement = new GraphQLReference("Test");
		nonNullableArrayElement.makeNonNullable();
		
		return new Object[][] {
			{ "[Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ " [Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ "[Int] ", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ "		[Int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ "[[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI, true, true },
			{ " [[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI, true, true },
			{ "[[Int]] ", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI, true, true },
			{ "		[[Int]]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.MULTI, true, true },

			{ "[ID]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE, false, true },
			{ "[Boolean]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE, false, true },
			{ "[Float]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE, false, true },
			{ "[String]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE, false, true },

			{ "[INT]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ "[ID]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE, false, true },
			{ "[BOOLEAN]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE, false, true },
			{ "[FLOAT]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE, false, true },
			{ "[STRING]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE, false, true },
			{ "[int]", new GraphQLScalar(GraphQLScalarValue.INT), Dimensionality.SINGLE, false, true },
			{ "[id]", new GraphQLScalar(GraphQLScalarValue.ID), Dimensionality.SINGLE, false, true },
			{ "[boolean]", new GraphQLScalar(GraphQLScalarValue.BOOLEAN), Dimensionality.SINGLE, false, true },
			{ "[float]", new GraphQLScalar(GraphQLScalarValue.FLOAT), Dimensionality.SINGLE, false, true },
			{ "[string]", new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE, false, true },

			{ "[Test]", new GraphQLReference("Test"), Dimensionality.SINGLE, false, true },
			{ "[[Test]]", new GraphQLReference("Test"), Dimensionality.MULTI, true, true },

			{ "[Test!]", nonNullableArrayElement, Dimensionality.SINGLE, false, true },
			{ "[Test!]!", nonNullableArrayElement, Dimensionality.SINGLE, false, false },
			{ "[[Test!]]", nonNullableArrayElement, Dimensionality.MULTI, true, true },
			{ "[[Test!]!]", nonNullableArrayElement, Dimensionality.MULTI, false, true },
			{ "[[Test!]!]!", nonNullableArrayElement, Dimensionality.MULTI, false, false },
			{ "[[Test!]]!", nonNullableArrayElement, Dimensionality.MULTI, true, false },
		};
	}
	
	@Test(dataProvider = "validListValues")
	public void parseValidLists(String line, GraphQLType expectedType, Dimensionality expectedDimensioniality, boolean expectedInnerDimensionNullable, boolean expectedNullable) throws ParseException {
		GraphQLList list = new GraphQLList(line);
		assertThat(list.getType(), is(equalTo(expectedType)));
		assertThat(list.getDimensionality(), is(equalTo(expectedDimensioniality)));
		assertThat(list.isInnerDimensionNullable(), is(equalTo(expectedInnerDimensionNullable)));
		assertThat(list.isNullable(), is(equalTo(expectedNullable)));
	}
	
	@DataProvider(name = "invalidListValues")
	public Object[][] invalidListValues() {
		return new Object[][] {
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
	
	@Test(expectedExceptions = ParseException.class)
	public void parseInvalidNullList() throws ParseException {
		new GraphQLList(null);
	}
}
