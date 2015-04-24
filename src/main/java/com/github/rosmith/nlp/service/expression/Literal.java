package com.github.rosmith.nlp.service.expression;

import com.github.rosmith.nlp.query.filter.QueryFilter;

public class Literal extends Expression {

	private String value;
	
	private boolean var;
	
	public Literal(String value, boolean isVar) {
		this.value = value;
		this.var = isVar;
	}
	
	public Literal(String value) {
		this.value = value;
		this.var = true;
	}

	@Override
	public QueryFilter toQuery() {
		return null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isVar() {
		return var;
	}

	public void setVar(boolean var) {
		this.var = var;
	}

}
