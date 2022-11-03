# GraphQL 2 JSON Schema
Java library for converting GraphQL Schema to JSON Schema.  

[Maven Central](https://central.sonatype.dev/artifact/com.ebay.nst/graphql-to-jsonschema/1.0.3/versions)

## Overview
To enable GraphQL schema for contract verification testing we need a way to leverage GraphQL schemas with the JSON schema validator used in many validation tools. This library provides that translation capability.

## Spec Compliance
The specs versioned supported by this library are:  
[GraphQL October 2021](http://spec.graphql.org/October2021/)  
[JSON Schema Draft v4](https://datatracker.ietf.org/doc/html/draft-zyp-json-schema-04) to match the [JSON Schema Validator](https://github.com/java-json-tools/json-schema-validator)

## Usage
This library assumes that the GraphQL schema is valid and compliant with the spec version defined in the **Spec Compliance** section above. Closing curly braces `}` are expected on their own line.

The GraphQL2JSONSchema library contains a parser and a transformer. The first step is to parse your GraphQL schema.

```java
GraphQLParser graphQLParser = new GraphQLParser();
GraphQLSchema graphQLSchema = graphQLParser.parseGraphQL(new File("path/to/your/schema");
```

The second step is to convert the parsed GraphQL schema into JSON schema. The parsed GraphQL schema contains all of the defined query, mutation and subscription APIs. You MUST specify which GraphQL API you wish to transform by the operation type (`OperationType` [QUERY, MUTATION, SUBSCRIPTION]) and operation name (`String`). The operation name MUST be the full operation name as defined in the schema (EG: `getUserById(id: string): UserDetail`).  

```java
GraphQLToJsonSchema converter = new GraphQLToJsonSchema(graphQLSchema);
JsonNode rootNode = null;

switch(operationType) {

case QUERY:
	rootNode = converter.convertQuery(operationName);
	break;
case MUTATION:
	rootNode = converter.convertMutation(operationName);
	break;
case SUBSCRIPTION:
	rootNode = converter.convertSubscription(operationName);
	break;
}
```

## Developer Notes
* Custom scalar types are evaluated as strings.
* Object, interface, union, enum and scalar extensions are not currently supported.
* Interface definitions are not currently supported, because, all interface fields are redefined on the object that implements the interfaces.
* Custom type definitions mapped into the schema for Query, Mutation and Subscription are NOT currently supported (IE: type Query, type Mutation and type Subscription are expected to be upheld in the definition).
* Response validation assumes the response payload has been extracted from the 'data' field at the root of the response model.
