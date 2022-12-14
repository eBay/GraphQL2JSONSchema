package com.ebay.graphql.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ebay.graphql.Generated;
import com.ebay.graphql.types.GraphQLType;

public class GraphQLSchema {

	private String schemaQueryType = "Query";
	private String schemaMutationType = "Mutation";
	private String schemaSubscriptionType = "Subscription";

	private Map<String, GraphQLType> query = new HashMap<>();
	private Map<String, GraphQLType> mutation = new HashMap<>();
	private Map<String, GraphQLType> subscription = new HashMap<>();

	// Types tracks all Object, Scalar and Enum definitions in the schema.
	private Map<String, GraphQLType> types = new HashMap<>();

	// Tracks union definitions in the schema.
	private Map<String, List<String>> unions = new HashMap<>();
	
	private String queryTypeName;
	private String mutationTypeName;
	private String subscriptionTypeName;
	
	public void addSchema(GraphQLSchema schema) {
		
		Map<String, GraphQLType> mutations = schema.getMutations();
		for (Map.Entry<String, GraphQLType> entry : mutations.entrySet()) {
			addMutation(entry.getKey(), entry.getValue());
		}
		
		Map<String, GraphQLType> queries = schema.getQuerys();
		for (Map.Entry<String, GraphQLType> entry : queries.entrySet()) {
			addQuery(entry.getKey(), entry.getValue());
		}
		
		Map<String, GraphQLType> subscriptions = schema.getSubscriptions();
		for (Map.Entry<String, GraphQLType> entry : subscriptions.entrySet()) {
			addSubscription(entry.getKey(), entry.getValue());
		}
		
		Map<String, GraphQLType> types = schema.getTypes();
		for (Map.Entry<String, GraphQLType> entry : types.entrySet()) {
			addType(entry.getKey(), entry.getValue());
		}
		
		Map<String, List<String>> unions = schema.getUnions();
		for (Map.Entry<String, List<String>> entry : unions.entrySet()) {
			addUnion(entry.getKey(), entry.getValue());
		}
	}
	
	public String getQueryTypeName() {
		return queryTypeName;
	}

	public void setQueryTypeName(String queryTypeName) {
		this.queryTypeName = queryTypeName;
	}

	public String getMutationTypeName() {
		return mutationTypeName;
	}

	public void setMutationTypeName(String mutationTypeName) {
		this.mutationTypeName = mutationTypeName;
	}

	public String getSubscriptionTypeName() {
		return subscriptionTypeName;
	}

	public void setSubscriptionTypeName(String subscriptionTypeName) {
		this.subscriptionTypeName = subscriptionTypeName;
	}

	public String getSchemaQueryType() {
		return schemaQueryType;
	}

	public void setSchemaQueryType(String schemaQueryType) {
		this.schemaQueryType = schemaQueryType;
	}

	public String getSchemaMutationType() {
		return schemaMutationType;
	}

	public void setSchemaMutationType(String schemaMutationType) {
		this.schemaMutationType = schemaMutationType;
	}

	public String getSchemaSubscriptionType() {
		return schemaSubscriptionType;
	}

	public void setSchemaSubscriptionType(String schemaSubscriptionType) {
		this.schemaSubscriptionType = schemaSubscriptionType;
	}

	public void addQuery(String name, GraphQLType type) {
		query.put(name, type);
	}

	public Map<String, GraphQLType> getQuerys() {
		return Collections.unmodifiableMap(query);
	}

	public void addMutation(String name, GraphQLType type) {
		mutation.put(name, type);
	}

	public Map<String, GraphQLType> getMutations() {
		return Collections.unmodifiableMap(mutation);
	}

	public void addSubscription(String name, GraphQLType type) {
		subscription.put(name, type);
	}

	public Map<String, GraphQLType> getSubscriptions() {
		return Collections.unmodifiableMap(subscription);
	}

	public void addType(String name, GraphQLType type) {
		types.put(name, type);
	}

	public Map<String, GraphQLType> getTypes() {
		return Collections.unmodifiableMap(types);
	}

	public void addUnion(String name, List<String> types) {
		unions.put(name, types);
	}

	public Map<String, List<String>> getUnions() {
		return Collections.unmodifiableMap(unions);
	}

	@Generated
	@Override
	public int hashCode() {
		return Objects.hash(mutation, query, schemaMutationType, schemaQueryType, schemaSubscriptionType, subscription,
				types, unions);
	}

	@Generated
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphQLSchema other = (GraphQLSchema) obj;
		return Objects.equals(mutation, other.mutation) && Objects.equals(query, other.query)
				&& Objects.equals(schemaMutationType, other.schemaMutationType)
				&& Objects.equals(schemaQueryType, other.schemaQueryType)
				&& Objects.equals(schemaSubscriptionType, other.schemaSubscriptionType)
				&& Objects.equals(subscription, other.subscription) && Objects.equals(types, other.types)
				&& Objects.equals(unions, other.unions);
	}

	@Generated
	@Override
	public String toString() {
		return "GraphQLSchema [schemaQueryType=" + schemaQueryType + ", schemaMutationType=" + schemaMutationType
				+ ", schemaSubscriptionType=" + schemaSubscriptionType + ", query=" + query + ", mutation=" + mutation
				+ ", subscription=" + subscription + ", types=" + types + ", unions=" + unions + "]";
	}

}
