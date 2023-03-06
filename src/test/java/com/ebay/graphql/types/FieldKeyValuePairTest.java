package com.ebay.graphql.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.text.ParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.types.GraphQLList.Dimensionality;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;

public class FieldKeyValuePairTest {

	@DataProvider(name = "illegalKeyValuePairs")
	public Object[][] getIllegalKeyValuePairs() {
		return new Object[][] {
			{ "keyPair" },
			{ "keyPair:" },
			{ "key:Pair:" },
			{ ":keyPair" },
			{ "key|Pair" },
			{ "key\\Pair" },
			{ "key/Pair" },
			{ "key Pair" },
			{ "key,Pair" },
			{ "key.Pair" },
			{ "key?Pair" }
		};
	}
	
	@Test(dataProvider = "illegalKeyValuePairs", expectedExceptions = ParseException.class)
	public void illegalKeyValuePair(String line) throws ParseException {
		new FieldKeyValuePair(line);
	}
	
	@DataProvider(name = "legalKeyValuePairs") 
	public Object[][] getLegalKeyValuePairs() {
		return new Object[][] {
			{ "key:value", "key", new GraphQLReference("value") },
			{ " key:value", "key", new GraphQLReference("value") },
			{ "key:value ", "key", new GraphQLReference("value") },
			{ "key :value", "key", new GraphQLReference("value") },
			{ "key: value", "key", new GraphQLReference("value") },
			{ "key : value", "key", new GraphQLReference("value") },
			{ " key : value ", "key", new GraphQLReference("value") },
			{ "		key		:		value		", "key", new GraphQLReference("value") },
			{ "translate(fromLanguage: Language, toLanguage: Language, text: String): String", "translate(fromLanguage: Language, toLanguage: Language, text: String)", new GraphQLScalar(GraphQLScalarValue.STRING) },
			{ "key:Boolean", "key", new GraphQLScalar(GraphQLScalarValue.BOOLEAN) },
			{ "key:Float", "key", new GraphQLScalar(GraphQLScalarValue.FLOAT) },
			{ "key:ID", "key", new GraphQLScalar(GraphQLScalarValue.ID) },
			{ "key:Int", "key", new GraphQLScalar(GraphQLScalarValue.INT) },
			{ "key:String", "key", new GraphQLScalar(GraphQLScalarValue.STRING) },
			{ "key:[[String]]", "key", new GraphQLList(new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.MULTI) },
		};
	}
	
	@Test(dataProvider = "legalKeyValuePairs")
	public void legalKeyValuePair(String line, String expectedKey, GraphQLType expectedType) throws ParseException {
		FieldKeyValuePair pair = new FieldKeyValuePair(line);
		assertThat(line, pair.getKey(), is(equalTo(expectedKey)));
		assertThat(line, pair.getValue(), is(equalTo(expectedType)));
	}

	@DataProvider(name = "getNonNullableLegalKeyValuePairs")
	public Object[][] getNonNullableLegalKeyValuePairs() {
		return new Object[][] {
				{ " key:Int!" },
				{ " key:Boolean!" },
				{ " key:Float!" },
				{ " key:ID!" },
				{ " key:String!" },
				{ " key:CustomType!" },
				{ " key:[String]!" }
		};
	}

	@Test(dataProvider = "getNonNullableLegalKeyValuePairs")
	public void nonNullableLegalKeyValuePairs(String line) throws ParseException {
		FieldKeyValuePair pair = new FieldKeyValuePair(line);
		assertThat(pair.getValue().isNullable(), is(equalTo(false)));
	}
}
