package com.github.rosmith.nlp.service.expression;

import com.github.rosmith.nlp.query.filter.QueryFilter;

public abstract class Expression {

	protected Operator operator;
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}
	
	public abstract QueryFilter toQuery();

}
