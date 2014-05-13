package de.tu_darmstadt.elc.olw.api.misc.execution;

public class ExecutionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1846156678618117903L;
	
	public ExecutionException (Throwable cause) {
		super (cause);
	}
	
	public ExecutionException (String errorMessage) {
		super (errorMessage);
	}
}
