package com.ebay.graphql.parser.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.parser.matcher.GraphQLMatcher.LineType;
import com.ebay.graphql.parser.matcher.GraphQLMatcher.Nullable;

public class GraphQLMatcherTest {

	@DataProvider(name = "lineTypeMatchingData")
	public Object[][] lineTypeMatchingData() {
		return new Object[][] {
			
			// COMMENT
			{ "#", LineType.COMMENT },
			{ " #", LineType.COMMENT },
			{ "# ", LineType.COMMENT },
			{ " # ", LineType.COMMENT },
			{ " # Comment with text.", LineType.COMMENT },
			{ "###", LineType.COMMENT },
			{ "		# 		Extra white space comment", LineType.COMMENT },
			{ "		# 		Extra white space comment\n", LineType.COMMENT },
			
			// MULTI_LINE_DESCRIPTIOIN_OPEN_OR_CLOSE_SIGNATURE
			{ "\"\"\"", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			{ " \"\"\"", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			{ "		\"\"\"", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			{ "	\"\"\" EXTRA TEXT", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			{ " EXTRA TEXT \"\"\"", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			{ "\"\"\"\n", LineType.MULTI_LINE_DESCRIPTION_OPEN_OR_CLOSE_SIGNATURE },
			
			// MULTI_LINE_DESCRIPTION_IN_ONE_LINE
			{ "\"\"\" DESCRIPTION IN ONE LINE \"\"\"", LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE },
			{ " \"\"\" DESCRIPTION IN ONE LINE \"\"\"", LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE },
			{ "		\"\"\" DESCRIPTION IN ONE LINE \"\"\"", LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE },
			{ "		\"\"\" DESCRIPTION IN ONE LINE \"\"\"		", LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE },
			{ "	\"\"\" DESCRIPTION IN ONE LINE \"\"\"\n", LineType.MULTI_LINE_DESCRIPTION_IN_ONE_LINE },
			
			// SINGLE_LINE_DESCRIPTION
			{ "\" DESCRIPTION IN ONE LINE \"", LineType.SINGLE_LINE_DESCRIPTION },
			{ " \" DESCRIPTION IN ONE LINE \"", LineType.SINGLE_LINE_DESCRIPTION },
			{ "\" DESCRIPTION IN ONE LINE \" ", LineType.SINGLE_LINE_DESCRIPTION },
			{ "		\" DESCRIPTION IN ONE LINE \"", LineType.SINGLE_LINE_DESCRIPTION },
			{ "\" DESCRIPTION IN ONE LINE \"		", LineType.SINGLE_LINE_DESCRIPTION },
			{ "\" DESCRIPTION IN ONE LINE \"\n", LineType.SINGLE_LINE_DESCRIPTION },
			
			// SCHEMA
			{ "schema {", LineType.SCHEMA },
			{ " schema {", LineType.SCHEMA },
			{ "schema { ", LineType.SCHEMA },
			{ "schema{", LineType.SCHEMA },
			{ "schema    {", LineType.SCHEMA },
			{ "extend schema {", LineType.SCHEMA },
			{ " extend schema {", LineType.SCHEMA },
			
			// SCHEMA_QUERY
			{ "query: MyQueryRootType", LineType.SCHEMA_QUERY },
			{ " query: MyQueryRootType", LineType.SCHEMA_QUERY },
			{ "query: MyQueryRootType ", LineType.SCHEMA_QUERY },
			{ "query : MyQueryRootType", LineType.SCHEMA_QUERY },
			{ "query:MyQueryRootType", LineType.SCHEMA_QUERY },
			
			// SCHEMA_MUTATION
			{ "mutation: MyMutationRootType", LineType.SCHEMA_MUTATION },
			{ " mutation: MyMutationRootType", LineType.SCHEMA_MUTATION },
			{ "mutation: MyMutationRootType ", LineType.SCHEMA_MUTATION },
			{ "mutation : MyMutationRootType", LineType.SCHEMA_MUTATION },
			{ "mutation:MyMutationRootType", LineType.SCHEMA_MUTATION },
			
			// SCHEMA_SUBSCRIPTION
			{ "subscription: MySubscriptionRootType", LineType.SCHEMA_SUBSCRIPTION },
			{ " subscription: MySubscriptionRootType", LineType.SCHEMA_SUBSCRIPTION },
			{ "subscription: MySubscriptionRootType ", LineType.SCHEMA_SUBSCRIPTION },
			{ "subscription : MySubscriptionRootType", LineType.SCHEMA_SUBSCRIPTION },
			{ "subscription:MySubscriptionRootType", LineType.SCHEMA_SUBSCRIPTION },
			
			// QUERY
			{ "type Query {", LineType.QUERY },
			{ " type Query {", LineType.QUERY },
			{ "type Query { ", LineType.QUERY },
			{ "type Query{", LineType.QUERY },
			{ "type Query    {", LineType.QUERY },
			{ "extend type Query {", LineType.QUERY },
			{ " extend type Query {", LineType.QUERY },
			
			// MUTATION
			{ "type Mutation {", LineType.MUTATION },
			{ " type Mutation {", LineType.MUTATION },
			{ "type Mutation { ", LineType.MUTATION },
			{ "type Mutation{", LineType.MUTATION },
			{ "type Mutation    {", LineType.MUTATION },
			{ "extend type Mutation {", LineType.MUTATION },
			{ " extend type Mutation {", LineType.MUTATION },
			
			// SUBSCRIPTION
			{ "type Subscription {", LineType.SUBSCRIPTION },
			{ " type Subscription {", LineType.SUBSCRIPTION },
			{ "type Subscription { ", LineType.SUBSCRIPTION },
			{ "type Subscription{", LineType.SUBSCRIPTION },
			{ "type Subscription    {", LineType.SUBSCRIPTION },
			{ "extend type Subscription {", LineType.SUBSCRIPTION },
			{ " extend type Subscription {", LineType.SUBSCRIPTION },
			
			// SCALAR_INT
			{ "foo: Int", LineType.FIELD_INT },
			{ "foo: Int @external", LineType.FIELD_INT },
			{ "Int: Int", LineType.FIELD_INT },
			{ " foo: Int", LineType.FIELD_INT },
			{ "		foo: Int", LineType.FIELD_INT },
			{ "foo: Int ", LineType.FIELD_INT },
			{ "foo: Int		", LineType.FIELD_INT },
			{ "_foo: Int", LineType.FIELD_INT },
			{ "foo_bar: Int", LineType.FIELD_INT },
			{ "_foo_BAR: Int", LineType.FIELD_INT },
			{ "_foo_BAR_: Int", LineType.FIELD_INT },
			{ "foo:Int", LineType.FIELD_INT },
			{ "picture(size: Int): Int", LineType.FIELD_INT },
			{ "picture(height: Int, width: Int): Int", LineType.FIELD_INT },
			
			// FIELD_INT
			{ "foo: Int!", LineType.FIELD_INT },
			{ "foo: Int! @external", LineType.FIELD_INT },
			{ "Int: Int!", LineType.FIELD_INT },
			{ " foo: Int!", LineType.FIELD_INT },
			{ "		foo: Int!", LineType.FIELD_INT },
			{ "foo: Int! ", LineType.FIELD_INT },
			{ "foo: Int!		", LineType.FIELD_INT },
			{ "_foo: Int!", LineType.FIELD_INT },
			{ "foo_bar: Int!", LineType.FIELD_INT },
			{ "_foo_BAR: Int!", LineType.FIELD_INT },
			{ "_foo_BAR_: Int!", LineType.FIELD_INT },
			{ "foo:Int!", LineType.FIELD_INT },
			{ "picture(size: Int): Int!", LineType.FIELD_INT },
			{ "picture(height: Int, width: Int): Int!", LineType.FIELD_INT },
			
			// SCALAR_FLOAT
			{ "foo: Float", LineType.FIELD_FLOAT },
			{ "foo: Float @external", LineType.FIELD_FLOAT },
			{ "Float: Float", LineType.FIELD_FLOAT },
			{ " foo: Float", LineType.FIELD_FLOAT },
			{ "		foo: Float", LineType.FIELD_FLOAT },
			{ "foo: Float ", LineType.FIELD_FLOAT },
			{ "foo: Float		", LineType.FIELD_FLOAT },
			{ "_foo: Float", LineType.FIELD_FLOAT },
			{ "foo_bar: Float", LineType.FIELD_FLOAT },
			{ "_foo_BAR: Float", LineType.FIELD_FLOAT },
			{ "_foo_BAR_: Float", LineType.FIELD_FLOAT },
			{ "foo:Float", LineType.FIELD_FLOAT },
			{ "picture(size: Int): Float", LineType.FIELD_FLOAT },
			{ "picture(height: Int, width: Int): Float", LineType.FIELD_FLOAT },
			
			// SCALAR_FLOAT_NON_NULLABLE
			{ "foo: Float!", LineType.FIELD_FLOAT },
			{ "foo: Float! @external", LineType.FIELD_FLOAT },
			{ "Float: Float!", LineType.FIELD_FLOAT },
			{ " foo: Float!", LineType.FIELD_FLOAT },
			{ "		foo: Float!", LineType.FIELD_FLOAT },
			{ "foo: Float! ", LineType.FIELD_FLOAT },
			{ "foo: Float!		", LineType.FIELD_FLOAT },
			{ "_foo: Float!", LineType.FIELD_FLOAT },
			{ "foo_bar: Float!", LineType.FIELD_FLOAT },
			{ "_foo_BAR: Float!", LineType.FIELD_FLOAT },
			{ "_foo_BAR_: Float!", LineType.FIELD_FLOAT },
			{ "foo:Float!", LineType.FIELD_FLOAT },
			{ "picture(size: Int): Float!", LineType.FIELD_FLOAT },
			{ "picture(height: Int, width: Int): Float!", LineType.FIELD_FLOAT },
			
			// SCALAR_STRING
			{ "foo: String", LineType.FIELD_STRING },
			{ "foo: String @external", LineType.FIELD_STRING },
			{ "String: String", LineType.FIELD_STRING },
			{ " foo: String", LineType.FIELD_STRING },
			{ "		foo: String", LineType.FIELD_STRING },
			{ "foo: String ", LineType.FIELD_STRING },
			{ "foo: String		", LineType.FIELD_STRING },
			{ "_foo: String", LineType.FIELD_STRING },
			{ "foo_bar: String", LineType.FIELD_STRING },
			{ "_foo_BAR: String", LineType.FIELD_STRING },
			{ "_foo_BAR_: String", LineType.FIELD_STRING },
			{ "foo:String", LineType.FIELD_STRING },
			{ "picture(size: Int): String", LineType.FIELD_STRING },
			{ "picture(height: Int, width: Int): String", LineType.FIELD_STRING },
			
			// FIELD_STRING
			{ "foo: String!", LineType.FIELD_STRING },
			{ "foo: String! @external", LineType.FIELD_STRING },
			{ "String: String!", LineType.FIELD_STRING },
			{ " foo: String!", LineType.FIELD_STRING },
			{ "		foo: String!", LineType.FIELD_STRING },
			{ "foo: String! ", LineType.FIELD_STRING },
			{ "foo: String!		", LineType.FIELD_STRING },
			{ "_foo: String!", LineType.FIELD_STRING },
			{ "foo_bar: String!", LineType.FIELD_STRING },
			{ "_foo_BAR: String!", LineType.FIELD_STRING },
			{ "_foo_BAR_: String!", LineType.FIELD_STRING },
			{ "foo:String!", LineType.FIELD_STRING },
			{ "picture(size: Int): String!", LineType.FIELD_STRING },
			{ "picture(height: Int, width: Int): String!", LineType.FIELD_STRING },
			
			// SCALAR_BOOLEAN
			{ "foo: Boolean", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean @external", LineType.FIELD_BOOLEAN },
			{ "Boolean: Boolean", LineType.FIELD_BOOLEAN },
			{ " foo: Boolean", LineType.FIELD_BOOLEAN },
			{ "		foo: Boolean", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean ", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean		", LineType.FIELD_BOOLEAN },
			{ "_foo: Boolean", LineType.FIELD_BOOLEAN },
			{ "foo_bar: Boolean", LineType.FIELD_BOOLEAN },
			{ "_foo_BAR: Boolean", LineType.FIELD_BOOLEAN },
			{ "_foo_BAR_: Boolean", LineType.FIELD_BOOLEAN },
			{ "foo:Boolean", LineType.FIELD_BOOLEAN },
			{ "picture(size: Int): Boolean", LineType.FIELD_BOOLEAN },
			{ "picture(height: Int, width: Int): Boolean", LineType.FIELD_BOOLEAN },
			
			// FIELD_BOOLEAN
			{ "foo: Boolean!", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean! @external", LineType.FIELD_BOOLEAN },
			{ "Boolean: Boolean!", LineType.FIELD_BOOLEAN },
			{ " foo: Boolean!", LineType.FIELD_BOOLEAN },
			{ "		foo: Boolean!", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean! ", LineType.FIELD_BOOLEAN },
			{ "foo: Boolean!		", LineType.FIELD_BOOLEAN },
			{ "_foo: Boolean!", LineType.FIELD_BOOLEAN },
			{ "foo_bar: Boolean!", LineType.FIELD_BOOLEAN },
			{ "_foo_BAR: Boolean!", LineType.FIELD_BOOLEAN },
			{ "_foo_BAR_: Boolean!", LineType.FIELD_BOOLEAN },
			{ "foo:Boolean!", LineType.FIELD_BOOLEAN },
			{ "picture(size: Int): Boolean!", LineType.FIELD_BOOLEAN },
			{ "picture(height: Int, width: Int): Boolean!", LineType.FIELD_BOOLEAN },
			
			// FIELD_ID
			{ "foo: ID", LineType.FIELD_ID },
			{ "foo: ID @external", LineType.FIELD_ID },
			{ "ID: ID", LineType.FIELD_ID },
			{ " foo: ID", LineType.FIELD_ID },
			{ "		foo: ID", LineType.FIELD_ID },
			{ "foo: ID ", LineType.FIELD_ID },
			{ "foo: ID		", LineType.FIELD_ID },
			{ "_foo: ID", LineType.FIELD_ID },
			{ "foo_bar: ID", LineType.FIELD_ID },
			{ "_foo_BAR: ID", LineType.FIELD_ID },
			{ "_foo_BAR_: ID", LineType.FIELD_ID },
			{ "foo:ID", LineType.FIELD_ID },
			{ "picture(size: Int): ID", LineType.FIELD_ID },
			{ "picture(height: Int, width: Int): ID", LineType.FIELD_ID },
			
			// FIELD_ID
			{ "foo: ID!", LineType.FIELD_ID },
			{ "ID: ID!", LineType.FIELD_ID },
			{ "ID: ID! @external", LineType.FIELD_ID },
			{ " foo: ID!", LineType.FIELD_ID },
			{ "		foo: ID!", LineType.FIELD_ID },
			{ "foo: ID! ", LineType.FIELD_ID },
			{ "foo: ID!		", LineType.FIELD_ID },
			{ "_foo: ID!", LineType.FIELD_ID },
			{ "foo_bar: ID!", LineType.FIELD_ID },
			{ "_foo_BAR: ID!", LineType.FIELD_ID },
			{ "_foo_BAR_: ID!", LineType.FIELD_ID },
			{ "foo:ID!", LineType.FIELD_ID },
			{ "picture(size: Int): ID!", LineType.FIELD_ID },
			{ "picture(height: Int, width: Int): ID!", LineType.FIELD_ID },
			
			// FIELD_REFERENCE
			{ "foo: uu_ID", LineType.FIELD_REFERENCE },
			{ "foo: uu_ID @external", LineType.FIELD_REFERENCE },
			{ "foo: uu_ID! @external", LineType.FIELD_REFERENCE },
			{ "foo: _UUID", LineType.FIELD_REFERENCE },
			{ "foo: _uu_id", LineType.FIELD_REFERENCE },
			{ "foo: URL", LineType.FIELD_REFERENCE },
			{ "URL: URL", LineType.FIELD_REFERENCE },
			{ " foo: URL", LineType.FIELD_REFERENCE },
			{ "		foo: URL", LineType.FIELD_REFERENCE },
			{ "foo: URL ", LineType.FIELD_REFERENCE },
			{ "foo: URL		", LineType.FIELD_REFERENCE },
			{ "_foo: URL", LineType.FIELD_REFERENCE },
			{ "foo_bar: URL", LineType.FIELD_REFERENCE },
			{ "_foo_BAR: URL", LineType.FIELD_REFERENCE },
			{ "_foo_BAR_: URL", LineType.FIELD_REFERENCE },
			{ "foo:URL", LineType.FIELD_REFERENCE },
			{ "picture(size: Int): URL", LineType.FIELD_REFERENCE },
			{ "picture(height: Int, width: Int): URL", LineType.FIELD_REFERENCE },
			{ "		key		:		value		", LineType.FIELD_REFERENCE },
			
			// OBJECT_DEFINITION
			{ "type Person {", LineType.OBJECT_DEFINITION },
			{ " type Person {", LineType.OBJECT_DEFINITION },
			{ "type Person { ", LineType.OBJECT_DEFINITION },
			{ "type _Person {", LineType.OBJECT_DEFINITION },
			{ "type person {", LineType.OBJECT_DEFINITION },
			{ "type person_ {", LineType.OBJECT_DEFINITION },
			{ "type Person_OBJ {", LineType.OBJECT_DEFINITION },
			{ "type PERSON2 {", LineType.OBJECT_DEFINITION },
			{ "type PERSON_2 {", LineType.OBJECT_DEFINITION },
			{ "type Person implements Name {", LineType.OBJECT_DEFINITION },
			{ "type Person implements Name & Age {", LineType.OBJECT_DEFINITION },
			{ "type Person implements Name{", LineType.OBJECT_DEFINITION },
			{ "type DisSpecification @shareable {", LineType.OBJECT_DEFINITION },
			{ "type ProListing @key(fields: \"listing { id }\") {", LineType.OBJECT_DEFINITION },
			{ "type Listing @extends @key(fields: \"id status\") {", LineType.OBJECT_DEFINITION },
			
			// SCALAR_DEFINITION
			{ "scalar UUID", LineType.SCALAR_DEFINITION },
			{ " scalar UUID", LineType.SCALAR_DEFINITION },
			{ "scalar UUID", LineType.SCALAR_DEFINITION },
			{ "scalar UUID @specifiedBy(url: \"https://test.com\")", LineType.SCALAR_DEFINITION },
			{ "scalar  UUID", LineType.SCALAR_DEFINITION },
			
			// UNION
			{ "union FOO = APPLE", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE | BANANA", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE | BANANA | PEAR", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE|BANANA", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE |", LineType.UNION_DEFINITION },
			{ " union FOO = APPLE | BANANA", LineType.UNION_DEFINITION },
			{ "		union FOO = APPLE | BANANA", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE | BANANA ", LineType.UNION_DEFINITION },
			{ "union FOO = APPLE | BANANA		", LineType.UNION_DEFINITION },
			{ "union FOO=APPLE|BANANA", LineType.UNION_DEFINITION },
			{ "union FOO =", LineType.UNION_DEFINITION },
			
			// UNION_MEMBER
			{ "| BANANA", LineType.UNION_MEMBER },
			{ " | BANANA", LineType.UNION_MEMBER },
			{ "		| BANANA", LineType.UNION_MEMBER },
			{ "| BANANA ", LineType.UNION_MEMBER },
			{ "| BANANA		", LineType.UNION_MEMBER },
			{ "| BANANA | PEAR", LineType.UNION_MEMBER },
			
			// ENUM
			{ "enum Direction {", LineType.ENUM_DEFINITION },
			{ " enum Direction {", LineType.ENUM_DEFINITION },
			{ "enum Direction { ", LineType.ENUM_DEFINITION },
			{ "		enum Direction {", LineType.ENUM_DEFINITION },
			{ "enum Direction {		", LineType.ENUM_DEFINITION },
			{ "enum Direction{", LineType.ENUM_DEFINITION },
			{ "enum Direction 	{", LineType.ENUM_DEFINITION },
			{ "enum 	Direction {", LineType.ENUM_DEFINITION },
			
			// LIST
			{ "list: [Int]", LineType.FIELD_LIST },
			{ " list: [Int]", LineType.FIELD_LIST },
			{ "list: [Int] ", LineType.FIELD_LIST },
			{ "		list: [Int]", LineType.FIELD_LIST },
			{ "list: [Int]		", LineType.FIELD_LIST },
			{ "list: [Int!]", LineType.FIELD_LIST },
			{ "list: [Int]!", LineType.FIELD_LIST },
			{ "list:[Int]", LineType.FIELD_LIST },
			{ "list:[Int!]", LineType.FIELD_LIST },
			{ "list: [Int]!", LineType.FIELD_LIST },
			{ "list: [Foo]", LineType.FIELD_LIST },
			{ "list(size: Int): [Foo]", LineType.FIELD_LIST },
			{ "list(width: Int, height: Int): [Foo]", LineType.FIELD_LIST },
			{ "matrix: [[Int]]", LineType.FIELD_LIST },
			{ " matrix: [[Int]]", LineType.FIELD_LIST },
			{ "matrix: [[Int]] ", LineType.FIELD_LIST },
			{ "		matrix: [[Int]]", LineType.FIELD_LIST },
			{ "matrix: [[Int]]		", LineType.FIELD_LIST },
			{ "matrix: [[Int!]]", LineType.FIELD_LIST },
			{ "matrix: [[Int]!]", LineType.FIELD_LIST },
			{ "matrix: [[Int]]!", LineType.FIELD_LIST },
			{ "matrix: [[Int]!]!", LineType.FIELD_LIST },
			{ "matrix: [[Int!]]!", LineType.FIELD_LIST },
			{ "matrix: [[Int!]!]", LineType.FIELD_LIST },
			{ "matrix: [[Int!]!]!", LineType.FIELD_LIST },
			{ "matrix:[[Int]]", LineType.FIELD_LIST },
			{ "matrix(size: Int): [[Int]]", LineType.FIELD_LIST },
			{ "matrix(width: Int, height: Int): [[Int]]", LineType.FIELD_LIST },
			
			// CLOSING_CURLY_BRACE
			{ "}", LineType.CLOSING_CURLY_BRACE },
			{ " }", LineType.CLOSING_CURLY_BRACE },
			{ "		}", LineType.CLOSING_CURLY_BRACE },
			{ "} ", LineType.CLOSING_CURLY_BRACE },
			{ "}	", LineType.CLOSING_CURLY_BRACE },
			{ "		}	", LineType.CLOSING_CURLY_BRACE },
			
			// UNMATCHED_TEXT
			{ "	", LineType.UNMATCHED_TEXT },
			{ "		", LineType.UNMATCHED_TEXT },
			{ "Some random text", LineType.UNMATCHED_TEXT },
		};
	}
	
	@Test(dataProvider = "lineTypeMatchingData")
	public void lineTypeMatchingTest(String line, LineType expectedLineType) {
		LineType actualLineType = GraphQLMatcher.getLineType(line);
		assertThat(String.format("[%s] does not yield expected LineType.", line), actualLineType, is(equalTo(expectedLineType)));
	}
	
	@Test
	public void lineTypeMatchingForNullLine() {
		LineType actualLineType = GraphQLMatcher.getLineType(null);
		assertThat(actualLineType, is(equalTo(LineType.UNMATCHED_TEXT)));
	}
	
	@DataProvider(name = "deprecatedLineValues")
	public Object[][] deprecatedLineValues() {
		return new Object[][] {
			{ "foo: String @deprecated", true },
			{ " foo: String @deprecated", true },
			{ "foo: String @deprecated ", true },
			{ "foo: String @deprecated		", true },
			{ "foo: String @Deprecated", false },
			{ "foo: String", false },
			{ "foo: String @CustomAnnotation", false },
			{ "foo: String @deprecate", false },
			{ "foo: String @", false },
		};
	}
	
	@Test(dataProvider = "deprecatedLineValues")
	public void deprecatedLineCheck(String line, boolean expectedResult) {
		boolean isDeprecated = GraphQLMatcher.isFieldDeprecated(line);
		assertThat(isDeprecated, is(equalTo(expectedResult)));
	}
	
	@DataProvider(name = "nullableTestValues")
	public Object[][] nullableTestValues() {
		return new Object[][] {

			// NULLABLE
			{ "foo: Int", Nullable.NULLABLE },
			{ "picture(size: Int): Int", Nullable.NULLABLE },
			{ "picture(height: Int, width: Int): Int", Nullable.NULLABLE },
			
			// NON_NULLABLE_SCALAR_OR_OBJECT
			{ "foo: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "		foo: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "foo: Int! ", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "foo: Int!		", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "_foo: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "foo_bar: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "_foo_BAR: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "_foo_BAR_: Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "foo:Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(size: Int): Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(height: Int, width: Int): Int!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			
			{ "foo: Float!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(size: Int): Float!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(height: Int, width: Int): Float!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },

			{ "foo: String!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(size: Int): String!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },

			{ "foo: Boolean!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(height: Int, width: Int): Boolean!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			
			{ "foo: ID!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(height: Int, width: Int): ID!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			
			{ "foo: uu_ID!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			{ "picture(height: Int, width: Int): URL!", Nullable.NON_NULLABLE_SCALAR_OR_OBJECT },
			
			// NON_NULLABLE_LIST
			{ "foo: [Int]!", Nullable.NON_NULLABLE_LIST },
			{ " foo: [Int]!", Nullable.NON_NULLABLE_LIST },
			{ "foo: [Int]! ", Nullable.NON_NULLABLE_LIST },
			{ "foo:[Int]!", Nullable.NON_NULLABLE_LIST },
			
			// NON_NULLABLE_LIST_ELEMENT
			{ "foo: [Int!]", Nullable.NON_NULLABLE_LIST_ELEMENT },
			{ " foo: [Int]!", Nullable.NON_NULLABLE_LIST },
			{ "foo: [Int]! ", Nullable.NON_NULLABLE_LIST },
			{ "foo:[Int]!", Nullable.NON_NULLABLE_LIST },
			
			// NON_NULLABLE_LIST_AND_ELEMENT
			{ "foo: [Int!]!", Nullable.NON_NULLABLE_LIST_AND_ELEMENT },
			{ " foo: [Int!]!", Nullable.NON_NULLABLE_LIST_AND_ELEMENT },
			{ "foo: [Int!]! ", Nullable.NON_NULLABLE_LIST_AND_ELEMENT },
			{ "foo:[Int!]!", Nullable.NON_NULLABLE_LIST_AND_ELEMENT },
			
			// NON_NULLABLE_MULTIDIMENSIONAL_ARRAY
			{ "foo: [[Int]]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY },
			{ " foo: [[Int]]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY },
			{ "foo: [[Int]]! ", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY },
			{ "foo:[[Int]]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY },
			
			// NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW
			{ "foo: [[Int]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW },
			{ " foo: [[Int]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW },
			{ "foo: [[Int]!] ", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW },
			{ "foo:[[Int]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW },
			
			// NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT
			{ "foo: [[Int!]]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT },
			{ " foo: [[Int!]]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT },
			{ "foo: [[Int!]] ", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT },
			{ "foo:[[Int!]]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ELEMENT },
			
			// NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT
			{ "foo: [[Int!]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT },
			{ " foo: [[Int!]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT },
			{ "foo: [[Int!]!] ", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT },
			{ "foo:[[Int!]!]", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_ROW_OR_ELEMENT },
			
			// NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT
			{ "foo: [[Int!]!]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT },
			{ " foo: [[Int!]!]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT },
			{ "foo: [[Int!]!]! ", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT },
			{ "foo:[[Int!]!]!", Nullable.NON_NULLABLE_MULTIDIMENSIONAL_ARRAY_COLUMN_ROW_OR_ELEMENT },
		};
	}
	
	@Test(dataProvider = "nullableTestValues")
	public void nullableLineCheck(String line, Nullable expectedResult) {
		
		Nullable result = GraphQLMatcher.isNullable(line);
		assertThat(result, is(equalTo(expectedResult)));
	}
}
