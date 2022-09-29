package com.ebay.graphql.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.ebay.graphql.model.GraphQLSchema;
import com.ebay.graphql.types.FieldKeyValuePair;
import com.ebay.graphql.types.GraphQLEnum;
import com.ebay.graphql.types.GraphQLList;
import com.ebay.graphql.types.GraphQLObject;
import com.ebay.graphql.types.GraphQLReference;
import com.ebay.graphql.types.GraphQLScalar;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;
import com.ebay.graphql.types.GraphQLType;
import com.ebay.graphql.types.GraphQLList.Dimensionality;

public class GraphQLParserTest {
	
	GraphQLParser parser = new GraphQLParser();

	@DataProvider(name = "singleLineDescriptionValues")
	public Object[][] singleLineDescriptionValues() {
		return new Object[][] {
			{ "\"TEST DESCRIPTION\"", "TEST DESCRIPTION" },
			{ "\"\"TEST DESCRIPTION\"\"", "TEST DESCRIPTION" },
			{ "\"\"\"TEST DESCRIPTION\"\"\"", "TEST DESCRIPTION" },
			{ "\"TEST \"DESCRIPTION\" stuff\"", "TEST \"DESCRIPTION\" stuff" },
			{ "\" TEST DESCRIPTION \"", "TEST DESCRIPTION" },
			{ "\"	TEST DESCRIPTION	\"", "TEST DESCRIPTION" },
			{ "\"Aa123!@#$%^&*()_+-=[]{};\\':\",	./ <>?\"", "Aa123!@#$%^&*()_+-=[]{};\\':\",	./ <>?" },
		};
	}

	@Test(dataProvider = "singleLineDescriptionValues")
	public void singleLineDescription(String line, String expected) {
		GraphQLFile graphQLFile = getGraphQLFile(line);
		String actualDescription = parser.processSingleLineDescription(graphQLFile);
		assertThat(actualDescription, is(equalTo(expected)));
	}
	
	@DataProvider(name = "multiLineDescriptionValues")
	public Object[][] multiLineDescriptionValues() {
		return new Object[][] {
			{ Arrays.asList("\"\"\"","First line", "second line", "third line.", "\"\"\""), "First line second line third line." },
			{ Arrays.asList("\"\"\"","First line.", "Second line.", "Third line.", "\"\"\""), "First line. Second line. Third line." },
			{ Arrays.asList("\"\"\"","		First line", "		second line", "		third line.", "\"\"\""), "First line second line third line." },
			{ Arrays.asList("\"\"\"","		First line	", "		second line	", "		third line.	", "\"\"\""), "First line second line third line." },
			{ Arrays.asList("\"\"\"First line", "second line", "third line.\"\"\""), "First line second line third line." },
			{ Arrays.asList("\"\"\"","First line", "\"second line\"", "third line.", "\"\"\""), "First line \"second line\" third line." },
		};
	}
	
	@Test(dataProvider = "multiLineDescriptionValues")
	public void multiLineDescription(List<String> description, String expected) {
		GraphQLFile graphQLFile = getGraphQLFile(description);
		String actualDescription = parser.processMultiLineDescription(graphQLFile);
		assertThat(actualDescription, is(equalTo(expected)));
	}
	
	@DataProvider(name = "objectValues")
	public Object[][] objectValues() {
		
		GraphQLObject firstObject = new GraphQLObject();
		firstObject.addField("name", new GraphQLScalar(GraphQLScalarValue.STRING));
		firstObject.addField("age", new GraphQLScalar(GraphQLScalarValue.INT));
		firstObject.addField("picture", new GraphQLReference("Url"));
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addType("Person", firstObject);
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addType("Person", new GraphQLObject());
		
		GraphQLObject thirdObject = new GraphQLObject();
		thirdObject.addField("name", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		GraphQLSchema thirdSchema = new GraphQLSchema();
		thirdSchema.addType("Person", thirdObject);
		
		GraphQLReference fourthObjectReference = new GraphQLReference("Url");
		fourthObjectReference.makeNonNullable();
		
		GraphQLObject fourthObject = new GraphQLObject();
		fourthObject.addField("picture", fourthObjectReference);
		
		GraphQLSchema fourthSchema = new GraphQLSchema();
		fourthSchema.addType("Person", fourthObject);
		
		GraphQLObject fifthObject = new GraphQLObject();
		fifthObject.addField("name", new GraphQLScalar(GraphQLScalarValue.STRING))
		;
		
		GraphQLSchema fifthSchema = new GraphQLSchema();
		fifthSchema.addType("Person", fifthObject);
		
		return new Object[][] {
			{ Arrays.asList("type Person {", "\tname: String", "\tage: Int", "\tpicture: Url", "}"), firstSchema },
			{ Arrays.asList("type Person {", "}"), secondSchema },
			{ Arrays.asList("	type Person {	", "	}	"), secondSchema },
			{ Arrays.asList("type Person {", "\tname: String", "}"), thirdSchema },
			{ Arrays.asList("type Person {", "\tpicture: Url!", "}"), fourthSchema },
			{ Arrays.asList("type Person implements Style {", "\tname: String", "}"), fifthSchema }
		};
	}
	
	@Test(dataProvider = "objectValues")
	public void parseObject(List<String> lines, GraphQLSchema expectedSchema) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processObject(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void parseObjectWithNonNullableFields() throws ParseException {
		
		GraphQLObject expectedObject = new GraphQLObject();
		
		GraphQLType name = new GraphQLScalar(GraphQLScalarValue.STRING);
		name.makeNonNullable();
		expectedObject.addField("name", name);
		
		expectedObject.addField("age", new GraphQLScalar(GraphQLScalarValue.INT));
		
		GraphQLType picture = new GraphQLReference("Url");
		picture.makeNonNullable();
		expectedObject.addField("picture", picture);
		
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addType("Person", expectedObject);
		
		List<String> lines = Arrays.asList("type Person {", "\tname: String!", "\tage: Int", "\tpicture: Url!", "}");
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processObject(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@DataProvider(name = "scalarTestValues")
	public Object[][] getScalarTestValues() {
		return new Object[][] {
			{ Arrays.asList("scalar UUID"), "UUID" },
			{ Arrays.asList(" scalar UUID"), "UUID" },
			{ Arrays.asList("	scalar UUID"), "UUID" },
			{ Arrays.asList("scalar UUID "), "UUID" },
			{ Arrays.asList("scalar UUID	"), "UUID" },
			{ Arrays.asList(" scalar UUID "), "UUID" },
			{ Arrays.asList("	scalar UUID	"), "UUID" },
			{ Arrays.asList("scalar URL"), "URL" },
			{ Arrays.asList("scalar url"), "url" },
			{ Arrays.asList("scalar UUID123"), "UUID123" },
			{ Arrays.asList("scalar UUID @specifiedBy(url: \"https://tools.ietf.org/html/rfc4122\")"), "UUID" },
		};
	}
	
	@Test(dataProvider = "scalarTestValues")
	public void parseScalar(List<String> lines, String expectedKey) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processScalar(graphQLFile, actualSchema);
		Map<String, GraphQLType> types = actualSchema.getTypes();
		assertThat(types.size(), is(equalTo(1)));
		assertThat(types.get(expectedKey), is(equalTo(new GraphQLScalar(GraphQLScalarValue.STRING))));
	}
	
	@DataProvider(name = "unionValues")
	public Object[][] unionValues() {
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addUnion("SearchResult", Arrays.asList("Photo", "Person"));
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addUnion("SearchResult", Arrays.asList("Photo", "Person"));
		
		return new Object[][] {
			{ Arrays.asList("union SearchResult = Photo | Person"), firstSchema },
			{ Arrays.asList("union SearchResult =", "| Photo", "| Person"), secondSchema },
		};
	}
	
	@Test(dataProvider = "unionValues")
	public void parseUnion(List<String> lines, GraphQLSchema expected) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processUnion(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expected)));
	}
	
	@DataProvider(name = "enumValues")
	public Object[][] enumValues() {
		
		GraphQLEnum firstEnum = new GraphQLEnum();
		firstEnum.addEnumValue("NORTH");
		firstEnum.addEnumValue("EAST");
		firstEnum.addEnumValue("SOUTH");
		firstEnum.addEnumValue("WEST");
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addType("Direction", firstEnum);
		
		GraphQLEnum secondEnum = new GraphQLEnum();
		secondEnum.addEnumValue("ONE");
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addType("Count", secondEnum);
		
		GraphQLSchema thirdSchema = new GraphQLSchema();
		thirdSchema.addType("Empty", new GraphQLEnum());
		
		return new Object[][] {
			{ Arrays.asList("enum Direction {", "\tNORTH", "\tEAST", "\tSOUTH", "\tWEST", "}"), firstSchema },
			{ Arrays.asList("enum Count {", "\tONE", "}"), secondSchema },
			{ Arrays.asList("enum Empty {", "}"), thirdSchema },
		};
	}
	
	@Test(dataProvider = "enumValues")
	public void parseEnum(List<String> lines, GraphQLSchema expected) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processEnum(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expected)));
	}
	
	@Test
	public void addEnumsAndAvoidComments() throws ParseException {
		
		List<String> input = Arrays.asList("enum Direction {", "# Comment", "\"Description\"", "\"\"\"", "Multi-line description.", "\"\"\"", "one", "two", "three");
		GraphQLFile graphQLFile = getGraphQLFile(input);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processEnum(graphQLFile, actualSchema);
		
		GraphQLEnum expectedEnum = new GraphQLEnum();
		expectedEnum.addEnumValue("one");
		expectedEnum.addEnumValue("two");
		expectedEnum.addEnumValue("three");
		
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addType("Direction", expectedEnum);
		
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@DataProvider(name = "extractOperationAPIValues")
	public Object[][] extractOperationAPIValues() {
		
		FieldKeyValuePair firstPair = new FieldKeyValuePair("translate(fromLanguage: Language, toLanguage: Language, text: String)", new GraphQLScalar(GraphQLScalarValue.STRING));
		FieldKeyValuePair secondPair = new FieldKeyValuePair("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLScalar(GraphQLScalarValue.STRING));
		FieldKeyValuePair thirdPair = new FieldKeyValuePair("foo", new GraphQLScalar(GraphQLScalarValue.INT));
		FieldKeyValuePair fourthPair = new FieldKeyValuePair("foo", new GraphQLReference("UUID"));
		
		return new Object[][] {
			{ Arrays.asList("type Query {", "\ttranslate(fromLanguage: Language, toLanguage: Language, text: String): String", "}"), firstPair },
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string.", "\"\"\"", "\ttranslate(", "\t\t\"The original language.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language.\"" , "\t\ttoLanguage: Language", "\t\t\"The text.\"", "\t\ttext: String", "\t): String", "}"), secondPair },
			{ Arrays.asList("type Query {", "\tfoo: Int", "}"), thirdPair },
			{ Arrays.asList("type Query {", "\tfoo: UUID", "}"), fourthPair },
		};
	}
	
	@Test(dataProvider = "extractOperationAPIValues")
	public void parseOperationAPIValues(List<String> lines, FieldKeyValuePair expected) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(lines);
		FieldKeyValuePair actualPair = parser.extractOperationApi(graphQLFile);
		assertThat(actualPair, is(equalTo(expected)));
	}

	@DataProvider(name = "parseQueryValues")
	public Object[][] parseQueryValues() {
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addQuery("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addQuery("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLReference("UUID"));
		
		GraphQLSchema thirdSchema = new GraphQLSchema();
		thirdSchema.addQuery("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		
		GraphQLSchema fourthSchema = new GraphQLSchema();
		fourthSchema.addQuery("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		fourthSchema.addQuery("getVersion", new GraphQLScalar(GraphQLScalarValue.STRING));
		fourthSchema.addQuery("isLive", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		fourthSchema.addQuery("getAccount", new GraphQLReference("Account"));
		
		GraphQLSchema fifthSchema = new GraphQLSchema();
		fifthSchema.addQuery("translate(fromLanguage: Language, toLanguage: Language, text: String)", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		return new Object[][] {
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): String", "}"), firstSchema },
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): UUID", "}"), secondSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "}"), thirdSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "\tgetVersion: String", "\tisLive: Boolean", "\tgetAccount: Account", "}"), fourthSchema },
			{ Arrays.asList("type Query {", "\ttranslate(fromLanguage: Language, toLanguage: Language, text: String): String", "}"), fifthSchema },
		};
	}
	
	@Test(dataProvider = "parseQueryValues")
	public void parseQuery(List<String> queryDefinition, GraphQLSchema expectedSchema) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(queryDefinition);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processQuery(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void parseSchemaQuery() throws ParseException {

		List<String> lines = Arrays.asList("schema {", "\tquery: Foo", "}", "", "type Foo {", "\ttestApi(query: String, types: [String]): String", "}");
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addQuery("testApi(query: String, types: [String])", new GraphQLScalar(GraphQLScalarValue.STRING));

		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = parser.processLinesOfText(graphQLFile);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@DataProvider(name = "parseMutationValues")
	public Object[][] parseMutationValues() {
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addMutation("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addMutation("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLReference("UUID"));
		
		GraphQLSchema thirdSchema = new GraphQLSchema();
		thirdSchema.addMutation("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		
		GraphQLSchema fourthSchema = new GraphQLSchema();
		fourthSchema.addMutation("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		fourthSchema.addMutation("getVersion", new GraphQLScalar(GraphQLScalarValue.STRING));
		fourthSchema.addMutation("isLive", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		fourthSchema.addMutation("getAccount", new GraphQLReference("Account"));
		
		GraphQLSchema fifthSchema = new GraphQLSchema();
		fifthSchema.addMutation("translate(fromLanguage: Language, toLanguage: Language, text: String)", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		return new Object[][] {
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): String", "}"), firstSchema },
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): UUID", "}"), secondSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "}"), thirdSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "\tgetVersion: String", "\tisLive: Boolean", "\tgetAccount: Account", "}"), fourthSchema },
			{ Arrays.asList("type Query {", "\ttranslate(fromLanguage: Language, toLanguage: Language, text: String): String", "}"), fifthSchema },
		};
	}
	
	@Test(dataProvider = "parseMutationValues")
	public void parseMutation(List<String> queryDefinition, GraphQLSchema expectedSchema) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(queryDefinition);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processMutation(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void parseSchemaMutation() throws ParseException {

		List<String> lines = Arrays.asList("schema {", "\tmutation: Foo", "}", "", "type Foo {", "\ttestApi(query: String, types: [String]): String", "}");
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addMutation("testApi(query: String, types: [String])", new GraphQLScalar(GraphQLScalarValue.STRING));

		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = parser.processLinesOfText(graphQLFile);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@DataProvider(name = "parseSubscriptionValues")
	public Object[][] parseSubscriptionValues() {
		
		GraphQLSchema firstSchema = new GraphQLSchema();
		firstSchema.addSubscription("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		GraphQLSchema secondSchema = new GraphQLSchema();
		secondSchema.addSubscription("translate( fromLanguage: Language toLanguage: Language text: String )", new GraphQLReference("UUID"));
		
		GraphQLSchema thirdSchema = new GraphQLSchema();
		thirdSchema.addSubscription("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		
		GraphQLSchema fourthSchema = new GraphQLSchema();
		fourthSchema.addSubscription("getText", new GraphQLScalar(GraphQLScalarValue.ID));
		fourthSchema.addSubscription("getVersion", new GraphQLScalar(GraphQLScalarValue.STRING));
		fourthSchema.addSubscription("isLive", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		fourthSchema.addSubscription("getAccount", new GraphQLReference("Account"));
		
		GraphQLSchema fifthSchema = new GraphQLSchema();
		fifthSchema.addSubscription("translate(fromLanguage: Language, toLanguage: Language, text: String)", new GraphQLScalar(GraphQLScalarValue.STRING));
		
		return new Object[][] {
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): String", "}"), firstSchema },
			{ Arrays.asList("type Query {", "\"\"\"", "Translates a string from a give language into a different language.", "\"\"\"", "\ttranslate(", "\t\t\"The original language that 'text' is provided in.\"", "\t\tfromLanguage: Language", "", "\t\t\"The translated language to be returned.\"", "\t\ttoLanguage: Language", "", "\t\t\"The text to be translated.\"", "\t\ttext: String", "\t): UUID", "}"), secondSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "}"), thirdSchema },
			{ Arrays.asList("type Query {", "\tgetText: ID", "\tgetVersion: String", "\tisLive: Boolean", "\tgetAccount: Account", "}"), fourthSchema },
			{ Arrays.asList("type Query {", "\ttranslate(fromLanguage: Language, toLanguage: Language, text: String): String", "}"), fifthSchema },
		};
	}
	
	@Test(dataProvider = "parseSubscriptionValues")
	public void parseSubscription(List<String> queryDefinition, GraphQLSchema expectedSchema) throws ParseException {
		GraphQLFile graphQLFile = getGraphQLFile(queryDefinition);
		GraphQLSchema actualSchema = new GraphQLSchema();
		parser.processSubscription(graphQLFile, actualSchema);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void parseSchemaSubscription() {

		List<String> lines = Arrays.asList("schema {", "\tsubscription: Foo", "}", "", "type Foo {", "\ttestApi(query: String, types: [String]): String", "}");
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addSubscription("testApi(query: String, types: [String])", new GraphQLScalar(GraphQLScalarValue.STRING));

		GraphQLFile graphQLFile = getGraphQLFile(lines);
		GraphQLSchema actualSchema = parser.processLinesOfText(graphQLFile);
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void enrollmentGraphQL() throws IOException, URISyntaxException {

		File file = getGraphQLResourceFile("com/ebay/graphql/model/test.graphqls");
		GraphQLSchema actualSchema = parser.parseGraphQL(file);
		
		// Define the expected parsed schema
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addQuery("isEnrolled(enrolledInput: EnrolledInput)", new GraphQLReference("EnrollmentStatusOutput"));
		expectedSchema.addMutation("preEnroll", new GraphQLReference("PreEnrollmentOutput"));
		expectedSchema.addMutation("completeEnrollment(completeInput: CompleteInput)", new GraphQLReference("EnrollmentOutput"));
		expectedSchema.addMutation("unEnroll", new GraphQLReference("EnrollmentStatusOutput"));
		
		GraphQLObject enrollmentStatusOutput = new GraphQLObject();
		GraphQLReference enrollmentStatus = new GraphQLReference("EnrollmentStatusEnum");
		enrollmentStatus.makeNonNullable(); // Non nullable in all references - used in PreEnrollmentOutput too.
		enrollmentStatusOutput.addField("enrollmentStatus", enrollmentStatus);
		enrollmentStatusOutput.addField("didPassRISK", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		enrollmentStatusOutput.addField("enrollmentTime", new GraphQLScalar(GraphQLScalarValue.STRING));
		enrollmentStatusOutput.addField("enrollmentLocales", new GraphQLList(new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE));
		
		expectedSchema.addType("EnrollmentStatusOutput", enrollmentStatusOutput);

		GraphQLObject preEnrollmentOutput = new GraphQLObject();
		preEnrollmentOutput.addField("enrollmentStatus", enrollmentStatus);
		preEnrollmentOutput.addField("preEnrolled", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		
		expectedSchema.addType("PreEnrollmentOutput", preEnrollmentOutput);
		
		GraphQLObject enrollmentOutput = new GraphQLObject();
		enrollmentOutput.addField("enrollmentStatus", enrollmentStatus);
		enrollmentOutput.addField("preEnrolled", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		
		expectedSchema.addType("EnrollmentOutput", enrollmentOutput);
		
		GraphQLEnum enrollmentStatusEnum = new GraphQLEnum();
		enrollmentStatusEnum.addEnumValue("ENROLLED");
		enrollmentStatusEnum.addEnumValue("NOT_ENROLLED");
		
		expectedSchema.addType("EnrollmentStatusEnum", enrollmentStatusEnum);
	
		// Compare
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void enrollmentGraphQLFromMultipleSchemaFragments() throws IOException, URISyntaxException {

		File file = getGraphQLResourceFile("com/ebay/graphql/models/schema.graphqls");
		GraphQLSchema actualSchema = parser.parseGraphQL(file);
		
		// Define the expected parsed schema
		GraphQLSchema expectedSchema = new GraphQLSchema();
		expectedSchema.addQuery("isEnrolled(enrolledInput: EnrolledInput)", new GraphQLReference("EnrollmentStatusOutput"));
		expectedSchema.addMutation("preEnroll", new GraphQLReference("PreEnrollmentOutput"));
		expectedSchema.addMutation("completeEnrollment(completeInput: CompleteInput)", new GraphQLReference("EnrollmentOutput"));
		expectedSchema.addMutation("unEnroll", new GraphQLReference("EnrollmentStatusOutput"));
		
		GraphQLObject enrollmentStatusOutput = new GraphQLObject();
		GraphQLReference enrollmentStatus = new GraphQLReference("EnrollmentStatusEnum");
		enrollmentStatus.makeNonNullable(); // Non nullable in all references - used in PreEnrollmentOutput too.
		enrollmentStatusOutput.addField("enrollmentStatus", enrollmentStatus);
		enrollmentStatusOutput.addField("didPassRISK", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		enrollmentStatusOutput.addField("enrollmentTime", new GraphQLScalar(GraphQLScalarValue.STRING));
		enrollmentStatusOutput.addField("enrollmentLocales", new GraphQLList(new GraphQLScalar(GraphQLScalarValue.STRING), Dimensionality.SINGLE));
		
		expectedSchema.addType("EnrollmentStatusOutput", enrollmentStatusOutput);

		GraphQLObject preEnrollmentOutput = new GraphQLObject();
		preEnrollmentOutput.addField("enrollmentStatus", enrollmentStatus);
		preEnrollmentOutput.addField("preEnrolled", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		
		expectedSchema.addType("PreEnrollmentOutput", preEnrollmentOutput);
		
		GraphQLObject enrollmentOutput = new GraphQLObject();
		enrollmentOutput.addField("enrollmentStatus", enrollmentStatus);
		enrollmentOutput.addField("preEnrolled", new GraphQLScalar(GraphQLScalarValue.BOOLEAN));
		
		expectedSchema.addType("EnrollmentOutput", enrollmentOutput);
		
		GraphQLEnum enrollmentStatusEnum = new GraphQLEnum();
		enrollmentStatusEnum.addEnumValue("ENROLLED");
		enrollmentStatusEnum.addEnumValue("NOT_ENROLLED");
		
		expectedSchema.addType("EnrollmentStatusEnum", enrollmentStatusEnum);
	
		// Compare
		assertThat(actualSchema, is(equalTo(expectedSchema)));
	}
	
	@Test
	public void cdcSchema() throws IOException, URISyntaxException {

		File file = getGraphQLResourceFile("com/ebay/graphql/cdc/cdcschema.graphql");
		GraphQLSchema actualSchema = parser.parseGraphQL(file);

		// Schema query and mutation MUST be populated
		Map<String, GraphQLType> querys = actualSchema.getQuerys();
		Map<String, GraphQLType> mutations = actualSchema.getMutations();

		assertThat("Query results MUST NOT be empty.", querys.isEmpty(), is(equalTo(false)));
		assertThat("Mutation results MUST NOT be empty.", mutations.isEmpty(), is(equalTo(false)));
	}

	// -------------------------------------------
	// Helper methods for generating GraphQLFiles.
	// -------------------------------------------
	
	private File getGraphQLResourceFile(String resource) throws IOException, URISyntaxException {
		URL url = this.getClass().getClassLoader().getResource(resource);
		return new File(url.toURI());
	}
	
	private GraphQLFile getGraphQLFile(String line) {
		return new GraphQLFile(Arrays.asList(line));
	}
	
	private GraphQLFile getGraphQLFile(List<String> lines) {
		return new GraphQLFile(lines);
	}
}
