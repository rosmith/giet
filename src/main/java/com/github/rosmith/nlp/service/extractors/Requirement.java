package com.github.rosmith.nlp.service.extractors;

public class Requirement {

	public static Requirement WORD = new Requirement("WORD");
	public static Requirement LEMMA = new Requirement("LEMMA");
	public static Requirement TAG = new Requirement("TAG");
	public static Requirement NE = new Requirement("NE");
	public static Requirement POSITION = new Requirement("POSITION");
	public static Requirement DEPENDENCY = new Requirement("DEPENDENCY");
	public static Requirement COREF = new Requirement("COREF");
	public static Requirement SRL = new Requirement("SRL");

	private String value;

	private Requirement(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Requirement)) {
			return false;
		}
		return getValue().equals(((Requirement)obj).getValue());
	}

}
