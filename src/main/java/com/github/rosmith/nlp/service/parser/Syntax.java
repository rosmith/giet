package com.github.rosmith.nlp.service.parser;

import java.util.ArrayList;
import java.util.List;

import com.github.rosmith.nlp.query.PrefixStatement;
import com.github.rosmith.nlp.query.QueryStatement;
import com.github.rosmith.nlp.query.SparQLQuery;
import com.github.rosmith.nlp.query.Variable;
import com.github.rosmith.nlp.query.filter.QueryFilter;
import com.github.rosmith.nlp.service.expression.BinaryExpression;
import com.github.rosmith.nlp.service.expression.BracketExpression;
import com.github.rosmith.nlp.service.expression.Expression;
import com.github.rosmith.nlp.service.expression.Literal;
import com.github.rosmith.nlp.service.expression.Operator;
import com.github.rosmith.nlp.service.expression.QuotedLiteral;
import com.github.rosmith.nlp.service.expression.RegexExpression;
import com.github.rosmith.nlp.service.parser.Symbol.Id;

import static com.github.rosmith.nlp.service.parser.Symbol.Id.*;
import static com.github.rosmith.nlp.service.util.ServiceUtil.*;

public class Syntax extends Lexer {

	private static final List<String> regexOptions = new ArrayList<String>();

	static {
		regexOptions.add("s");
		regexOptions.add("m");
		regexOptions.add("i");
		regexOptions.add("x");
	}

	private SparQLQuery query;

	private Syntax(String code, boolean printSymbols) throws Exception {
		super(code, printSymbols);

		query = new SparQLQuery();
		query.addPrefixStatement(new PrefixStatement(NAMESPACE_VARIABLE(),
				NAMESPACE()));
		query.addPrefixStatement(new PrefixStatement(XSD_NAMESPACE_VARIABLE(),
				XSD_NAMESPACE()));
		nextSymbol();
	}

	public static SparQLQuery parse(String code, boolean printSymbols)
			throws Exception {
		String toParse = code.trim().replace("\n", " ");
		Syntax syn = new Syntax(toParse, printSymbols);
		syn.select();
		return syn.query;
	}

	private void select() throws Exception {
		expectSymbol(SELECT);
		if (getSymbol().getId() == DISTINCT) {
			query.setDistinct(true);
			nextSymbol();
		}
		variables();
	}

	private void variables() throws Exception {
		while (getSymbol().getId() == INTERROGATION) {
			nextSymbol();
			if (getSymbol().getId() != IDENT) {
				throw new Exception(
						"An identifier should always follow an interrogation.");
			}
			query.addVariable(new Variable(getSymbol().getIdent()));
			nextSymbol();
		}
		where();
	}

	private void where() throws Exception {
		expectSymbol(WHERE);
		body();
	}

	private void body() throws Exception {
		boolean bracketsArePresent = false;
		if (getSymbol().getId() == LBRACK) {
			bracketsArePresent = true;
			nextSymbol();
		}

		while (statement())
			;

		if (bracketsArePresent) {
			expectSymbol(RBRACK);
		} else {
			nextSymbol();
		}
	}

	private QueryFilter filter() throws Exception {
		return disjunction().toQuery();
	}

	private Expression disjunction() throws Exception {
		Expression result = conjunction();
		while (getSymbol().getId() == Id.OR) {
			nextSymbol();
			result = new BinaryExpression(result, Operator.OR, conjunction());
		}
		return result;
	}

	private Expression conjunction() throws Exception {
		Expression result = relation();
		while (getSymbol().getId() == Id.AND) {
			nextSymbol();
			result = new BinaryExpression(result, Operator.AND, relation());
		}
		return result;
	}

	private Expression relation() throws Exception {
		Expression result = expression();
		if (getSymbol().getId() == Id.EQ) {
			if (!(result instanceof Literal)) {
				throw new RuntimeException(
						"The left hand side of an equal sign should be a literal");
			}
			nextSymbol();
			boolean isVar = getSymbol().getId() == INTERROGATION;
			if (isVar) {
				nextSymbol();
			}
			Expression r = expression();
			if (!(r instanceof Literal)) {
				throw new RuntimeException(
						"The right hand side of an equal sign should be a literal");
			}
			Literal lit = (Literal) r;
			lit.setVar(isVar);
			result = new BinaryExpression(result, Operator.EQUALS, r);
		}
		return result;
	}

	private Expression expression() throws Exception {

		if (getSymbol().getId() == LPAREN) {
			nextSymbol();
			Expression result = new BracketExpression(disjunction());
			expectSymbol(RPAREN);
			return result;
		}

		if (getSymbol().getId() == Id.REGEX) {
			nextSymbol();
			return regex();
		}

		if (getSymbol().getId() == Id.INTERROGATION) {
			nextSymbol();
		}

		return literal();
	}

	private Expression regex() throws Exception {
		expectSymbol(LPAREN);
		expectSymbol(INTERROGATION);
		Literal l = (Literal) literal();
		expectSymbol(COMMA);
		expectQuoteSymbol(QUOTE);
		String regex = getSymbol().getIdent();
		if (regex != null && !regex.equals("QUOTE")) {
			nextSymbol();
			expectSymbol(QUOTE);
		} else {
			regex = "";
			nextSymbol();
		}
		Expression result = null;
		if (getSymbol().getId() == COMMA) {
			nextSymbol();
			expectQuoteSymbol(QUOTE);
			String flag = getSymbol().getIdent();
			if (flag == null || flag.isEmpty() || !regexOptions.contains(flag)) {
				throw new RuntimeException(
						"Please use the flags \"s, m, i, x\" as option for the regex function.");
			}
			nextSymbol();
			expectSymbol(QUOTE);
			result = new RegexExpression(l, regex, flag);
		} else {
			result = new RegexExpression(l, regex);
		}
		expectSymbol(RPAREN);
		return result;
	}

	private Expression literal() throws Exception {
		String value = null;
		Id id = getSymbol().getId();
		if (id == NUMBER) {
			value = String.valueOf(getSymbol().getNumber());
		}
		if (id == IDENT) {
			value = getSymbol().getIdent();
		}
		if (id == QUOTE) {
			nextQuoteSymbol();
			value = getSymbol().getIdent();
			if (value != null && !value.equals("QUOTE")) {
				nextSymbol();
				expectSymbol(QUOTE);
			} else {
				value = "";
				nextSymbol();
			}
			return new QuotedLiteral(value);
		}
		nextSymbol();
		return new Literal(value);
	}

	private boolean statement() throws Exception {
		if (getSymbol().getId() == FILTER) {
			nextSymbol();
			query.addFilter(filter());
			return true;
		}

		String subject = null, relation = null, object = null;
		boolean isVar = false;
		if (getSymbol().getId() != INTERROGATION) {
			return false;
		}
		nextSymbol();
		subject = getSymbol().getIdent();

		nextSymbol();
		relation = getSymbol().getIdent();

		nextSymbol();

		if (getSymbol().getId() == QUOTE) {
			nextSymbol();
			isVar = false;
			object = getSymbol().getIdent();
			nextSymbol();
			expectSymbol(QUOTE);
		} else if (getSymbol().getId() == INTERROGATION) {
			nextSymbol();
			isVar = true;
			object = getSymbol().getIdent();
			nextSymbol();
		}

		expectSymbol(PERIOD);

		query.addStatement(new QueryStatement(subject, namespace(relation),
				object, isVar));

		return true;
	}

	void expectSymbol(Symbol.Id id) throws Exception {
		if (id != getSymbol().getId()) {
			unexpectedSymbol();
		}
		nextSymbol();
	}

	void expectQuoteSymbol(Symbol.Id id) throws Exception {
		if (id != getSymbol().getId()) {
			unexpectedSymbol();
		}
		nextQuoteSymbol();
	}

	private void unexpectedSymbol() throws Exception {
		throw new Exception("unexpected symbol "
				+ getSymbol().getId().toString());
	}

}