package com.citi.logparser.filters;

import com.citi.logparser.exception.FilterProcessFailedException;

public interface CommandFilter {
	String getName();

	String process(String data) throws FilterProcessFailedException;

}
