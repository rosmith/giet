package com.github.rosmith.nlp.service.srl;

public class IndentStream {
	
	private int count;
	private String value;
	
	public IndentStream(){
		count = 0;
		value = "";
	}
	
	public void indent(){
		count++;
	}
	
	public void unindent(){
		count--;
	}
	
	public void print(Object val){
		int tmp = count;
		while(tmp-- > 0){
			value += "\t";
		}
		value += val;
	}
	
	public void print(Object val, boolean tab){
		int tmp = count;
		while(tmp-- > 0){
			value += tab ? "\t" : "";
		}
		value += val;
	}
	
	public void println(Object val){
		val = val+"\n";
		int tmp = count;
		while(tmp-- > 0){
			value += "\t";
		}
		value += val;
	}
	
	@Override
	public String toString(){
		return value;
	}
	
	public void reset(){
		count = 0;
		value = "";
	}

}
