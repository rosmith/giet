package com.github.rosmith.nlp.service.srl;

public enum StopWord {
	
	A("a"), 
	THE("the"), 
	AND("and"), 
	OR("or"), 
	IN("in"),
	OF("of"), 
	TO("to"), 
	THAT("that"), 
	IT("it"), 
	I("i"),
	YOU("you"), 
	ABOUT("about"), 
	AN("an"), 
	ARE("are"), 
	AS("as"),
	AT("at"), 
	BE("be"), 
	BY("by"), 
	FOR("for"), 
	FROM("from"),
	HOW("how"), 
	IS("is"), 
	ON("on"), 
	THIS("this"), 
	WAS("was"),
	WHAT("what"), 
	WHEN("when"), 
	WHERE("where"), 
	WHO("who"),
	WILL("will"), 
	INTO("into"), 
	WHICH("which"), 
	WITH("with");
	
	private String description;
	
	private StopWord(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public static boolean contains(String value){
		try{
			return valueOf(value.toUpperCase()) == null ? false : true;
		}catch(Exception e){
			return false;
		}
	}
	
}
