package com.github.rosmith.nlp.service.srl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Blacklist {
	DOT("."), COMMA(","), PERCENT("%"), DOLLAR("$"), HASH("#");

	private String description;

	private Blacklist(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public static boolean contains(String value) {
		return ((List<Blacklist>)Arrays.asList(values())).stream().filter(x -> { 
			return x.getDescription() != null && x.getDescription().equals(value);
		}).collect(Collectors.<Blacklist> toList()).size() >= 1;
	}

}
