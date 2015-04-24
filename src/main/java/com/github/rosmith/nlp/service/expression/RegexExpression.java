package com.github.rosmith.nlp.service.expression;

import com.github.rosmith.nlp.query.OperandValue;
import com.github.rosmith.nlp.query.filter.QueryFilter;
import com.github.rosmith.nlp.query.filter.RegexQueryFilter;

public class RegexExpression extends Expression {

	private Literal var;

	private String regex;

	private String option;

	private boolean hasOption;

	public RegexExpression(Literal var, String regex) {
		this.var = var;
		this.regex = regex;
		this.hasOption = false;
	}

	public RegexExpression(Literal var, String regex, String option) {
		this.var = var;
		this.regex = regex;
		this.hasOption = option != null && !option.isEmpty();
		this.option = option;
	}

	@Override
	public QueryFilter toQuery() {
		return hasOption ? new RegexQueryFilter(new OperandValue(
				var.getValue(), true), regex, option) : new RegexQueryFilter(
				new OperandValue(var.getValue(), true), regex);
	}

	public Literal getVar() {
		return var;
	}

	public void setVar(Literal var) {
		this.var = var;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public boolean hasOption() {
		return hasOption;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

}
