package com.ebay.graphql.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.text.ParseException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.parser.GraphQLParserUtil;

public class GraphQLParserUtilTest {
	
	@DataProvider(name = "keyValues")
	public Object[][] keyValues() {
		return new Object[][] {
			{ "test:", "test" },
			{ "test:value", "test" },
			{ " test:value", "test" },
			{ "test:value ", "test" },
			{ " test : value ", "test" },
			{ " 	test 	: 	value 	", "test" },
			{ " test:value ", "test" },
			{ "test : value", "test" },
			{ "test:value", "test" },
			{ "test: value", "test" },
			{ "test(one: String, two: Int): value", "test(one: String, two: Int)" },
		};
	}

	@Test(dataProvider = "keyValues")
	public void keyExtraction(String line, String expectedKey) throws ParseException {
		String key = GraphQLParserUtil.getKeyForLine(line);
		assertThat(key, is(equalTo(expectedKey)));
	}
	
	@DataProvider(name = "keyValueFailures")
	public Object[][] keyValueFailures() {
		return new Object[][] {
			{ "" },
			{ "escape" },
		};
	}
	
	@Test(dataProvider =  "keyValueFailures", expectedExceptions = ParseException.class)
	public void keyExtractionFailure(String line) throws ParseException {
		GraphQLParserUtil.getKeyForLine(line);
	}
	
	@DataProvider(name = "valueValues")
	public Object[][] valueValues() {
		return new Object[][] {
			{ "escape(one: String, two: Int):[[[FOO]]]", "[[[FOO]]]" },
			{ "escape(one: String, two: Int):[[FOO]]", "[[FOO]]" },
			{ "escape(one: String, two: Int):[FOO]", "[FOO]" },
			{ "escape(one: String, two: Int):FOO", "FOO" },
			{ "escape(one: String, two: Int): FOO", "FOO" },
			{ "escape(one: String, two: Int): String", "String" },
			{ "escape(one: String, two: Int):[", "[" },
			{ "escape(one: String, two: Int): [", "[" },
			{ "foo: bar", "bar" },
			{ "test:value", "value" },
			{ " test:value ", "value" },
			{ " test : value ", "value" },
			{ " test 	: 	value ", "value" },
			{ "foo: bar!", "bar!" },
			{ "foo: [bar!]", "[bar!]" },
			{ "foo: [bar]!", "[bar]!" },
			{ "foo: [[bar!]]", "[[bar!]]" },
			{ "foo: [[bar!]!]", "[[bar!]!]" },
			{ "foo: [[bar!]!]!", "[[bar!]!]!" },
		};
	}
	
	@Test(dataProvider = "valueValues")
	public void valueExtraction(String line, String expectedValue) throws ParseException {
		String value = GraphQLParserUtil.getValueForLine(line);
		assertThat(value, is(equalTo(expectedValue)));
	}
	
	@DataProvider(name = "valueValueFailures")
	public Object[][] valueValueFailures() {
		return new Object[][] {
			{ "" },
			{ "foo:" },
			{ "escape(one: String, two: Int):" },
			{ "escape(one: String, two: Int)" },
		};
	}
	
	@Test(dataProvider = "valueValueFailures", expectedExceptions = ParseException.class)
	public void valueExtractionFailure(String line) throws ParseException {
		GraphQLParserUtil.getValueForLine(line);
	}
}
