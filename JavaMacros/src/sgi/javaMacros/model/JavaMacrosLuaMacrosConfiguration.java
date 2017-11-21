package sgi.javaMacros.model;

import java.io.File;

import sgi.generic.serialization.AbstractMemoryParcel;

public class JavaMacrosLuaMacrosConfiguration extends AbstractMemoryParcel {

	@Override
	protected void initializeDefaultValues() {
		serverPort = 32000;
		deviceCheckInterval= 5000;
		luaMacrosStartingMinimized = true;
		luaMacrosMinimizingToTray = true;
	}

	public static JavaMacrosLuaMacrosConfiguration instance() {
		if (root == null)
			root = new JavaMacrosLuaMacrosConfiguration();
		return root;
	}

	private JavaMacrosLuaMacrosConfiguration() {
		// TODO Auto-generated constructor stub
	}

	private long serverPort;
	private long deviceCheckInterval;
	private boolean luaMacrosStartingMinimized;
	private boolean luaMacrosMinimizingToTray;
	private File luaMacrosLocation;

	public File getLuaMacrosLocation() {
		return luaMacrosLocation;
	}

	private static JavaMacrosLuaMacrosConfiguration root;

	public long getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public boolean isLuaMacrosStartingMinimized() {
		return luaMacrosStartingMinimized;
	}

	public boolean isLuaMacrosMinimizingToTray() {
		return luaMacrosMinimizingToTray;
	}

	@Override
	public String getFileName() {
		return getClass().getSimpleName() + ".xml";
	}

	public boolean isInconherent() {
		return luaMacrosLocation == null || !luaMacrosLocation.isFile() || serverPort <= 0;
	}

	public long getDeviceCheckInterval() {
		return deviceCheckInterval;
	}
}
