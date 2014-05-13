package de.tu_darmstadt.elc.olw.jbi.messages;

public class StandardStatus {
	public final static String SUCCESSFUL = "SUCCESSFUL";
	public final static String FINISHED = "FINISHED";
	public final static String TOLERACED_ERROR = "TOLERACED ERROR";
	public final static String UNTOLERACED_ERROR = "UNTOLERACE ERROR";
	
	public final static int getPriority(String status) {
		if (status.equals(UNTOLERACED_ERROR))
			return 1;
		if (status.equals(TOLERACED_ERROR))
			return 2;
		if (status.equals(SUCCESSFUL))
			return 3;
		if (status.equals(FINISHED))
			return 4;
		return 5;
	}
}
