package com.github.rosmith.nlp.service.expression;

import com.github.rosmith.nlp.query.OperandValue;
import com.github.rosmith.nlp.query.filter.BinaryQueryFilter;
import com.github.rosmith.nlp.query.filter.GroupQueryFilter;
import com.github.rosmith.nlp.query.filter.QueryFilter;

public class BinaryExpression extends Expression {

	private Expression left;

	private Expression right;

	public BinaryExpression(Expression left, Operator operator, Expression right) {
		this.left = left;
		this.right = right;
		setOperator(operator);
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	@Override
	public QueryFilter toQuery() {
		if (operator.equals(Operator.OR)) {
			GroupQueryFilter gf = new GroupQueryFilter().changeToOr();
			gf.add(left.toQuery());
			gf.add(right.toQuery());
			return gf;
		}

		if (operator.equals(Operator.AND)) {
			GroupQueryFilter gf = new GroupQueryFilter().changeToAnd();
			gf.add(left.toQuery());
			gf.add(right.toQuery());
			return gf;
		}

		if (operator.equals(Operator.EQUALS)) {
			if ((left instanceof Literal)
					&& (right instanceof Literal)) {
				Literal leftLit = Literal.class.cast(left);
				Literal rightLit = Literal.class.cast(right);
				BinaryQueryFilter bqf = new BinaryQueryFilter(new OperandValue(
						leftLit.getValue(), leftLit.isVar()), "EQUALS",
						new OperandValue(rightLit.getValue(), rightLit.isVar()));
				return bqf;
			}
		}

		return null;
	}

}
