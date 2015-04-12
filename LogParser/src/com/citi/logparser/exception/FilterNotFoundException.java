package com.citi.logparser.exception;

public class FilterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FilterNotFoundException(String msg) {
		super(msg);
	}

	public FilterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
