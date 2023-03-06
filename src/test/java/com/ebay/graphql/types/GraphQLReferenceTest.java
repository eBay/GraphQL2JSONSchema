package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class GraphQLReferenceTest {

	@DataProvider(name = "getReferenceTypeValues")
	public Object[][] getReferenceTypeValues() {
		return new Object[][] {
			{ "URL", "URL" },
			{ " URL", "URL" },
			{ "		URL", "URL" },
			{ "URL ", "URL" },
			{ "URL		", "URL" },
			{ " URL ", "URL" },
			{ " 	URL	 ", "URL" },
			{ "url", "url" },
		};
	}
	
	@Test(dataProvider = "getReferenceTypeValues")
	public void parseReferenceTypeValue(String value, String expectedValue) {
		GraphQLReference reference = new GraphQLReference(value);
		GraphQLReference expectedReference = new GraphQLReference(expectedValue);
		assertThat(reference, is(equalTo(expectedReference)));
		assertThat(reference.getReferenceTypeName(), is(equalTo(expectedReference.getReferenceTypeName())));
	}

	@Test
	public void nonNullableValueParsing() throws Exception {
		GraphQLReference reference = new GraphQLReference("CustomReferenceType!");
		assertThat(reference.isNullable(), is(equalTo(false)));
	}
}
