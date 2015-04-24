package com.github.rosmith.nlp.service.parser;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.rosmith.nlp.service.parser.Symbol.Id;

class Lexer {

	/** The stream from which the text is read. */
	private InputStreamReader reader;

	/** Should recognized symbols printed in the console? */
	private boolean printSymbols;

	/** The actual reading position. */
	private Position position;

	/** The last read character. */
	private int c;

	/** The last read symbol. */
	private Symbol symbol;

	/**
	 * This list contains a list of reserved characters. An identifier shouln't
	 * contain a character in this list.
	 */
	private List<Character> res_char;

	private Map<String, Symbol.Id> res_sym;

	private void init() {
		res_char = new LinkedList<Character>();
		res_char.add(Character.valueOf('!'));
		res_char.add(Character.valueOf('<'));
		res_char.add(Character.valueOf('>'));
		res_char.add(Character.valueOf('"'));
		res_char.add(Character.valueOf('='));
		res_char.add(Character.valueOf('.'));
		res_char.add(Character.valueOf('?'));
		res_char.add(Character.valueOf('('));
		res_char.add(Character.valueOf(')'));
		res_char.add(Character.valueOf('{'));
		res_char.add(Character.valueOf('}'));
		res_char.add(Character.valueOf('|'));
		res_char.add(Character.valueOf('&'));

		res_sym = new HashMap<>();
		res_sym.put("SELECT", Id.SELECT);
		res_sym.put("DISTINCT", Id.DISTINCT);
		res_sym.put("WHERE", Id.WHERE);
		res_sym.put("FILTER", Id.FILTER);
		res_sym.put("REGEX", Id.REGEX);
	}

	/**
	 * Konstruktor.
	 *
	 * @param fileName
	 *            Der Name des Quelltexts.
	 * @param printSymbols
	 *            Sollen die erkannten Symbole auf der Konsole ausgegeben
	 *            werden?
	 * @throws FileNotFoundException
	 *             Der Quelltext wurde nicht gefunden.
	 * @throws IOException
	 *             Ein Lesefehler ist aufgetreten.
	 */
	public Lexer(String code, boolean printSymbols)
			throws FileNotFoundException, IOException {
		this(new InputStreamReader(new ByteArrayInputStream(
				code.getBytes(Charset.forName("UTF-8"))), "UTF-8"),
				printSymbols);
	}

	private Lexer(InputStreamReader reader, boolean printSymbols)
			throws FileNotFoundException, IOException {
		this.reader = reader;
		this.printSymbols = printSymbols;
		init();

		position = new Position(1, 0);
		nextChar();
	}

	/**
	 * Die Methode liest das nächste Zeichen aus dem Quelltext. Dieses wird im
	 * Attribut {@link #c c} bereitgestellt.
	 *
	 * @throws IOException
	 *             Ein Lesefehler ist aufgetreten.
	 */
	private void nextChar() throws IOException {
		position.next((char) c);
		c = reader.read();
	}

	void nextQuoteSymbol() throws Exception {
		// Leerraum ignorieren
		while (c != -1 && Character.isWhitespace((char) c)) {
			if (c != '\n') {
				nextChar();
			} else {
				break;
			}
		}

		Position pos;
		switch (c) {
		case -1:
			symbol = new Symbol(Symbol.Id.EOF, position);
			break;
		case '"':
			symbol = new Symbol(Symbol.Id.QUOTE, position);
			nextChar();
			break;
		default:
			pos = new Position(position.getLine(), position.getColumn());
			String ident = "" + (char) c;
			nextChar();
			while ((c != -1 && c != '"')) {
				ident = ident + (char) c;
				nextChar();
			}
			if (res_sym.containsKey(ident.toUpperCase())) {
				symbol = new Symbol(res_sym.get(ident.toUpperCase()), pos);
			} else {
				symbol = new Symbol(Id.IDENT, ident, pos);
			}
		}
		if (printSymbols) {
			System.out.println(symbol.toString());
		}
	}

	/**
	 * Die Methode liest das nächste Symbol. Dieses wird im Attribut
	 * {@link #symbol symbol} bereitgestellt.
	 *
	 * @throws DATRException
	 *             Der Quelltext entspricht nicht der Syntax.
	 * @throws IOException
	 *             Ein Lesefehler ist aufgetreten.
	 */
	void nextSymbol() throws Exception {
		// Leerraum ignorieren
		while (c != -1 && Character.isWhitespace((char) c)) {
			if (c != '\n') {
				nextChar();
			} else {
				break;
			}
		}

		Position pos;
		switch (c) {
		case -1:
			symbol = new Symbol(Symbol.Id.EOF, position);
			break;
		case ':':
			symbol = new Symbol(Symbol.Id.COLON, position);
			nextChar();
			break;
		case '!':
			nextChar();
			if ((char) c == '=') {
				symbol = new Symbol(Symbol.Id.NE, position);
				nextChar();
			} else {
				throw new Exception(
						"Please use the correct symbol for negation.");
			}
			break;
		case '.':
			symbol = new Symbol(Symbol.Id.PERIOD, position);
			nextChar();
			break;
		case ',':
			symbol = new Symbol(Symbol.Id.COMMA, position);
			nextChar();
			break;
		case '"':
			symbol = new Symbol(Symbol.Id.QUOTE, position);
			nextChar();
			break;
		case '?':
			symbol = new Symbol(Symbol.Id.INTERROGATION, position);
			nextChar();
			break;
		case '=':
			symbol = new Symbol(Symbol.Id.EQ, position);
			nextChar();
			break;
		case '>':
			symbol = new Symbol(Symbol.Id.GT, position);
			nextChar();
			if ((char) c == '=') {
				symbol = new Symbol(Symbol.Id.GE, position);
				nextChar();
			}
			break;
		case '<':
			symbol = new Symbol(Symbol.Id.LT, position);
			nextChar();
			if ((char) c == '=') {
				symbol = new Symbol(Symbol.Id.LE, position);
				nextChar();
			}
			break;
		case '|':
			nextChar();
			if ((char) c == '|') {
				symbol = new Symbol(Symbol.Id.OR, position);
				nextChar();
			} else {
				throw new Exception("Unexpected character: " + (char) c
						+ " (Code " + c + ")");
			}
			break;
		case '&':
			nextChar();
			if ((char) c == '&') {
				symbol = new Symbol(Symbol.Id.AND, position);
				nextChar();
			} else {
				throw new Exception("Unexpected character: " + (char) c
						+ " (Code " + c + ")");
			}
			break;
		case '(':
			symbol = new Symbol(Symbol.Id.LPAREN, position);
			nextChar();
			break;
		case ')':
			symbol = new Symbol(Symbol.Id.RPAREN, position);
			nextChar();
			break;
		case '{':
			symbol = new Symbol(Symbol.Id.LBRACK, position);
			nextChar();
			break;
		case '}':
			symbol = new Symbol(Symbol.Id.RBRACK, position);
			nextChar();
			break;
		case '\n':
			symbol = new Symbol(Symbol.Id.NEW_LINE, position);
			while (c != -1 && c == '\n') {
				nextChar();
			}
			break;
		default:
			pos = new Position(position.getLine(), position.getColumn());
			if (Character.isDigit((char) c)) {
				int number = c - '0';
				nextChar();
				while (c != -1 && Character.isDigit((char) c)) {
					number = number * 10 + c - '0';
					nextChar();
				}
				symbol = new Symbol(number, pos);
			} else if (Character.isLetter((char) c)) {
				String ident = "" + (char) c;
				nextChar();
				while ((c != -1 && Character.isLetterOrDigit((char) c))) {
					ident = ident + (char) c;
					nextChar();
				}
				if (res_sym.containsKey(ident.toUpperCase())) {
					symbol = new Symbol(res_sym.get(ident.toUpperCase()), pos);
				} else {
					symbol = new Symbol(Id.IDENT, ident, pos);
				}
			} else {
				throw new Exception("Unexpected character: " + (char) c
						+ " (Code " + c + ")");
			}
		}
		if (printSymbols) {
			System.out.println(symbol.toString());
		}
	}

	/**
	 * Gibt das zuletzt gelesene Symbol zurück. Zuvor muss {@link #nextSymbol()
	 * nextSymbol} aufgerufen worden sein.
	 *
	 * @return Das aktuelle Symbol.
	 */
	public Symbol getSymbol() {
		return symbol;
	}

}