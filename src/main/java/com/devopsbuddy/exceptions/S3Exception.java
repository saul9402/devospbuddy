package com.devopsbuddy.exceptions;

public class S3Exception extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public S3Exception() {
		// TODO Auto-generated constructor stub
	}

	public S3Exception(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public S3Exception(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public S3Exception(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public S3Exception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
