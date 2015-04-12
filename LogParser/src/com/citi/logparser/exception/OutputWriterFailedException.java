package com.citi.logparser.exception;

public class OutputWriterFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OutputWriterFailedException(String msg) {
		super(msg);
	}

	public OutputWriterFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
