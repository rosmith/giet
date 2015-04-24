package com.github.rosmith.nlp.service.extractors;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Plugin {
	
	public String namespace();
	
	public boolean multirelation() default false;
	
	public boolean usePredefinedProcessor() default true;

}
