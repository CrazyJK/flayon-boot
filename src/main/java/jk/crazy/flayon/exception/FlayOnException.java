package jk.crazy.flayon.exception;

import jk.crazy.flayon.FlayOnApplication;

public class FlayOnException extends RuntimeException {

	private static final long serialVersionUID = FlayOnApplication.SERIAL_VERSION_UID;

	public FlayOnException() {
		super();
	}

	public FlayOnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FlayOnException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlayOnException(String message) {
		super(message);
	}

	public FlayOnException(Throwable cause) {
		super(cause);
	}

}
