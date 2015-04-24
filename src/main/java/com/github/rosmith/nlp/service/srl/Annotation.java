package com.github.rosmith.nlp.service.srl;

public enum Annotation {

	ARG_0("Normal argument"),
	ARG_1("Normal argument"),
	ARG_2("Normal argument"),
	DIR("Directionals"),
	LOC("Locatives"),
	MNR("Manner"),
	EXT("Extent"),
	REC("Reciprocals"),
	PRD("Secondary Predication"),
	PNC("Purpose"),
	CAU("Cause"),
	DIS("Discourse"),
	ADV("Adverbials"),
	MOD("Modals"),
	NEG("Negation"),
	TMP("Temporal");
	
	private String description;
	
	private Annotation(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
}
