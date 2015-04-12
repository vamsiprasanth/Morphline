package com.citi.logparser.exception;

public class FilterProcessFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FilterProcessFailedException(String msg) {
		super(msg);
	}

	public FilterProcessFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
