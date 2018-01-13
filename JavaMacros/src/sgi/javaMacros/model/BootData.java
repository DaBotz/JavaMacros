package sgi.javaMacros.model;

import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;

public class BootData extends JavaMacrosMemoryParcel {

	private static BootData root;

	private boolean startUpDisplayToBeShown;
	private long runs;
	private transient long startTime;

	

	public long getRuns() {
		return runs;
	}

	public long getStartTime() {
		return startTime;
	}


	public static BootData instance() {
		if (root == null)
			root = new BootData();
		return root;
	}

	@Deprecated
	public BootData() {
		startTime= System.currentTimeMillis();
		runs++;
	}

	public boolean isStartUpDisplayToBeShown() {
		return startUpDisplayToBeShown;
	}

	public void setStartUpDisplayToBeShown(boolean showStartUpDisplay) {
		boolean old = this.startUpDisplayToBeShown;
		this.startUpDisplayToBeShown = showStartUpDisplay;
		notifyPropertyChange("", old, startUpDisplayToBeShown );
	}
	
	public long runningTime() {
		return System.currentTimeMillis()-startTime;
	}

	@Override
	protected void initializeDefaultValues() {

		startUpDisplayToBeShown = true;

	}

}
