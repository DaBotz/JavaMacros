package sgi.javaMacros.os.windows;

public class ProcessInfo {
	private int pid;
	private String processname;
	private String processPath;

	public ProcessInfo(String processname, String processPath, int PID) {
		super();
		this.processname = processname;
		this.processPath = processPath;
		this.pid = PID;
	}

	public ProcessInfo(String pName, long PID) {
		this(pName, pName, (int) PID);
	}

	public long getPid() {
		return pid;
	}

	public String getProcessname() {
		return processname;
	}

	public int hashCode() {
		return this.processname.hashCode();
	}

	public boolean equals(ProcessInfo obj) {
		return this.processname.equals(obj.processname) && this.pid == obj.pid;
	}

	public boolean equals(String obj) {
		return this.processname.equals(obj);
	}

	public String toString() {
		return this.processname + " :: " + this.pid; //$NON-NLS-1$
	}

	public String getProcessPath() {
		return processPath;
	}

}