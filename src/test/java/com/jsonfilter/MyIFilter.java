package com.jsonfilter;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFilter("myFilter")
public abstract class MyIFilter {
	
	@JsonProperty("viewUserName")
	abstract String getName();
	
}