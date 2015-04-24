package com.github.rosmith.nlp.service.expression;

import com.github.rosmith.nlp.query.filter.QueryFilter;
import com.github.rosmith.nlp.query.filter.GroupQueryFilter;

public class BracketExpression extends Expression {
	
	private Expression wrapped;
	
	public BracketExpression(Expression wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public QueryFilter toQuery() {
		return new GroupQueryFilter(wrapped.toQuery());
	}

}
