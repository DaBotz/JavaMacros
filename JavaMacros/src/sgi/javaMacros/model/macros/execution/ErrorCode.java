package sgi.javaMacros.model.macros.execution;

public class ErrorCode {

	public String getMessage() {
		return message;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	private String message;
	private int start;
	private int end;

	public ErrorCode(String message, int start, int end) {
		this.message = message;
		this.start = start;
		this.end = end;
	}

}