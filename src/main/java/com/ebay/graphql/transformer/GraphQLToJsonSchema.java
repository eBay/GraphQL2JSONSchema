package com.ebay.graphql.transformer;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import com.ebay.graphql.model.GraphQLSchema;
import com.ebay.graphql.types.GraphQLEnum;
import com.ebay.graphql.types.GraphQLList;
import com.ebay.graphql.types.GraphQLList.Dimensionality;
import com.ebay.graphql.types.GraphQLObject;
import com.ebay.graphql.types.GraphQLReference;
import com.ebay.graphql.types.GraphQLScalar;
import com.ebay.graphql.types.GraphQLScalar.GraphQLScalarValue;
import com.ebay.graphql.types.GraphQLType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class GraphQLToJsonSchema {
	
	private JsonNodeFactory factory = new JsonNodeFactory(false);
	private GraphQLSchema graphQLSchema;
	
	public GraphQLToJsonSchema(GraphQLSchema graphQLSchema) {
		Objects.requireNonNull(graphQLSchema, "Initialization with null schema is not permitted.");
		this.graphQLSchema = graphQLSchema;
	}

	public JsonNode convertQuery(String queryName) throws IllegalArgumentException {
		
		GraphQLType query = graphQLSchema.getQuerys().get(queryName);
		if (query == null) {
			throw new IllegalArgumentException(String.format("Query name is unknown in the GraphQL schema. GraphQL query names found: [%s].", graphQLSchema.getQuerys().keySet().toString()));
		}
		
		JsonNode jsonSchema = convertModel(query);
		
		return jsonSchema;
	}
	
	public JsonNode convertMutation(String mutationName) throws IllegalArgumentException  {
		
		GraphQLType mutation = graphQLSchema.getMutations().get(mutationName);
		if (mutation == null) {
			throw new IllegalArgumentException(String.format("Mutation name is unknown in the GraphQL schema. GraphQL mutation names found: [%s].", graphQLSchema.getMutations().keySet().toString()));
		}
		
		JsonNode jsonSchema = convertModel(mutation);
		
		return jsonSchema;
	}
	
	public JsonNode convertSubscription(String subscriptionName) throws IllegalArgumentException  {
		
		GraphQLType subscription = graphQLSchema.getSubscriptions().get(subscriptionName);
		if (subscription == null) {
			throw new IllegalArgumentException(String.format("Subscription name is unknown in the GraphQL schema. GraphQL subscription names found: [%s].", graphQLSchema.getSubscriptions().keySet().toString()));
		}
		
		JsonNode jsonSchema = convertModel(subscription);
		
		return jsonSchema;
	}
	
	protected ObjectNode convertModel(GraphQLType type) {
		
		ObjectNode jsonNode = null;
		
		if (type instanceof GraphQLEnum) {
			jsonNode = convertEnum(type);
		} else if (type instanceof GraphQLList) {
			jsonNode = convertList(type);
		} else if (type instanceof GraphQLObject) {
			jsonNode = convertObject(type);
		} else if (type instanceof GraphQLReference) {
			jsonNode = convertReference(type);
		} else if (type instanceof GraphQLScalar) {
			jsonNode = convertScalar(type);
		} else {
			throw new IllegalStateException("Encountered unknown GraphQLType.");
		}
		
		return jsonNode;
	}
	
	protected ObjectNode convertReference(GraphQLType type) {
		
		GraphQLReference graphQLReference = (GraphQLReference) type;
		String typeName = graphQLReference.getReferenceTypeName();
		GraphQLType referenceType = graphQLSchema.getTypes().get(typeName);
		ObjectNode jsonObject = convertModel(referenceType);
		if (!type.isNullable() && jsonObject.get("type") != null) {
			JsonNode typeValue = jsonObject.get("type");
			int count = 0;
			String elementName = null;
			String tempElementName = null;
			JsonNode node;
			Iterator<JsonNode> elementIterator = typeValue.elements();
			while (elementIterator.hasNext()) {
				count++;
				node = elementIterator.next();
				if (!(node instanceof TextNode)) {
					throw new IllegalStateException("Only TextNodes expected in type definition.");
				}
				
				tempElementName = ((TextNode) node).asText();
				if (!"null".equalsIgnoreCase(tempElementName)) {
					elementName = tempElementName;
				}
			}
			
			if (count > 1) {
				jsonObject.set("type", new TextNode(elementName));
			}
		}
		return jsonObject;
	}
	
	protected ObjectNode convertList(GraphQLType type) {
		GraphQLList graphQLList = (GraphQLList) type;
		
		ObjectNode objectNode = factory.objectNode();
		
		Dimensionality dimension = graphQLList.getDimensionality();
		ObjectNode listType = convertModel(graphQLList.getType());
		
		objectNode.set("type", getTypeDefinition(type));
		
		switch(dimension) {
		case MULTI:
			ObjectNode innerDimension = factory.objectNode();
			innerDimension.set("type", getTypeDefinition(type));
			innerDimension.set("items", factory.objectNode().setAll(listType));
			objectNode.set("items", factory.objectNode().setAll(innerDimension));
			break;
		case SINGLE:
		default:
			objectNode.set("items", factory.objectNode().setAll(listType));
			break;
		}
		
		return objectNode;
	}
	
	protected ObjectNode convertScalar(GraphQLType type) {
		JsonNode scalarNodeType = getTypeDefinition(type);
		ObjectNode scalarNode = factory.objectNode();
		scalarNode.set("type", scalarNodeType);
		return scalarNode;
	}
	
	protected ObjectNode convertEnum(GraphQLType type) {
		GraphQLEnum graphQLEnum = (GraphQLEnum) type;
		
		ArrayNode arrayNode = factory.arrayNode();
		for (String enumValue : graphQLEnum.getEnumValues()) {
			arrayNode.add(enumValue);
		}

		ObjectNode enumNode = factory.objectNode();
		enumNode.set("enum", arrayNode);
		return enumNode;
	}
	
	protected ObjectNode convertObject(GraphQLType type) {
		ObjectNode jsonObject = factory.objectNode();
		jsonObject.set("type", getTypeDefinition(type));
		
		ObjectNode properties = factory.objectNode();
		
		GraphQLObject graphQLObject = (GraphQLObject) type;
		Map<String, GraphQLType> fields = graphQLObject.getFields();
		for (Map.Entry<String, GraphQLType> entry : fields.entrySet()) {
			properties.set(entry.getKey(), convertModel(entry.getValue()));
		}
		
		jsonObject.set("properties", properties);
		return jsonObject;
	}
	
	protected JsonNode getTypeDefinition(GraphQLType type) {
		
		String typeName = null;
		boolean isNullable  = type.isNullable();
		
		// Special cases omitted from processing:
		// - enum will explicitly define null as a valid enum value
		// - reference (inlined in the JSON schema)
		if (type instanceof GraphQLList) {
			typeName = "array";
		} else if (type instanceof GraphQLObject) {
			typeName = "object";
		} else if (type instanceof GraphQLScalar) {
			GraphQLScalarValue val = ((GraphQLScalar) type).getScalarValue();
			switch (val) {
			case BOOLEAN:
				typeName = "boolean";
				break;
			case FLOAT:
				typeName = "number";
				break;
			case INT:
				typeName = "integer";
				break;
			case ID:
			case STRING:
				typeName = "string";
				break;
			}
		} else {
			throw new IllegalStateException("Encountered unknown GraphQLType.");
		}
		
		if (isNullable) {
			return factory.arrayNode().add(typeName).add("null");
		} else {
			return factory.textNode(typeName);
		}
	}
	
	protected String getScalarTypeName(GraphQLScalar scalar) {
		
		Objects.requireNonNull(scalar, "Scalar value MUST NOT be null.");
		
		switch (scalar.getScalarValue()) {
		
		case BOOLEAN:
			return "boolean";
		case FLOAT:
			return "number";
		case ID:
		case STRING:
			return "string";
		case INT:
			return "integer";
		default:
			throw new IllegalStateException("Unknown GraphQL scalar type requested.");
		}
	}
	
//	private void prepareNewJsonSchemaRootNode(String title, ObjectNode rootNode) {
//
//		rootNode.put("$schema", "http://json-schema.org/draft-04/schema#");
//		rootNode.put("title", title);
//		rootNode.put("description", "JSON schema converted from GraphQL schema.");
//	}
}
