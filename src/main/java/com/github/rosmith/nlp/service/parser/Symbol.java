package com.github.rosmith.nlp.service.parser;

/**
 * @author Smith Djomkam
 * This class represents a recognized symbol during the
 * syntax analysis.
 */
public class Symbol extends Position {

	/** Defined symbols. */
	public enum Id {
		NUMBER, EOF, COLON, PERIOD, QUOTE, 
		EQ, NEW_LINE, GT, LT, GE, LE, NE,
		IDENT, INTERROGATION, LPAREN, RPAREN,
		SELECT, WHERE, LBRACK, RBRACK, DISTINCT, 
		FILTER, COMMA, AND, OR, REGEX
	}

	/** Symbols type. */
	private final Id id;

	/**
	 * When the recognized symbol = NUMBER, then the read number is stored in
	 * this attribute.
	 */
	private final int number;

	/**
	 * When the recognized symbol = IDENT, then the read Identifier is stored in
	 * this attribute.
	 */
	private final String ident;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the recognized symbol
	 * @param number
	 *            the read number
	 * @param ident
	 *            the read identifier
	 * @param position
	 *            the source codes position, where the symbol was recognized
	 */
	private Symbol(Id id, int number, String ident, Position position) {
		super(position.getLine(), position.getColumn());
		this.id = id;
		this.number = number;
		this.ident = ident;
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the recognized symbol
	 * @param position
	 *            the source codes position, where the symbol was recognized
	 */
	public Symbol(Id id, Position position) {
		this(id, 0, null, position);
	}

	/**
	 * Constructor.
	 * 
	 * @param number
	 *            the read number
	 * @param position
	 *            the source codes position, where the symbol was recognized
	 */
	public Symbol(int number, Position position) {
		this(Id.NUMBER, number, null, position);
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the recognized symbol
	 * @param ident
	 *            the read identifier
	 * @param position
	 *            the source codes position, where the symbol was recognized
	 */
	public Symbol(Id id, String ident, Position position) {
		this(id, 0, ident, position);
	}

	/**
	 * returns the symbols type
	 * 
	 * @return symbols type.
	 */
	public Id getId() {
		return id;
	}

	/**
	 * returns the number for symbols NUMBER
	 * 
	 * @return the read number.
	 */
	public int getNumber() {
		assert id == Id.NUMBER;
		return number;
	}

	/**
	 * returns the identifier for symbols IDENT
	 * 
	 * @return the read identifier
	 */
	public String getIdent() {
		assert id == Id.IDENT;
		return ident;
	}

	@Override
	public String toString() {
		switch (id) {
		case IDENT:
			return "IDENT: " + ident;
		case NUMBER:
			return "NUMBER: " + number;
		default:
			return id.toString();
		}
	}
}
