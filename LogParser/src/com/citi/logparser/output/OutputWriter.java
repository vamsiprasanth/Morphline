package com.citi.logparser.output;

public interface OutputWriter {
	String getName();

	boolean process(String data) ;

}
