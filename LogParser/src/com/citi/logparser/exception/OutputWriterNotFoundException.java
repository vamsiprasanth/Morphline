package com.citi.logparser.exception;

public class OutputWriterNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OutputWriterNotFoundException(String msg) {
		super(msg);
	}

	public OutputWriterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
