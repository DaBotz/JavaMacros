package sgi.javaMacros.model.macros.execution;

public class ScriptSegment {

	public void setPart(String part) {
		this.part = part;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setStart(int start) {
		this.start = start;
		end = part == null ? start : start + part.length();
	}

	public void setEnd(int end) {
		this.end = end;
	}

	private String part;
	private int status;
	private int start;
	private int end;

	public ScriptSegment(String part, int status, int start) {
		this(part, status, start, part == null ? start : start + part.length());
	}

	public ScriptSegment(String part, int status, int start, int end) {
		this.part = part;
		this.status = status;
		this.start = start;
		this.end = end;

	}

	public ScriptSegment(String pre, int status) {
		this(pre, status, 0);
	}

	@Override
	public String toString() {

		return start + ", " + end + ": >" + part + "<";
	}

	public String getPart() {
		return part;
	}

	public int getStatus() {
		return status;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

}
