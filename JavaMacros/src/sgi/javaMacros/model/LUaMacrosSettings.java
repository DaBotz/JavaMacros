package sgi.javaMacros.model;

import java.io.File;

import sgi.configuration.ConfigurationAtom;

public class LUaMacrosSettings extends ConfigurationAtom {
	public LUaMacrosSettings(long serverPort) {
		super();
		this.serverPort = serverPort;
	}

	private long serverPort;
	private boolean luaMacrosStartingMinimized;
	private boolean luaMacrosMinimizingToTray;
	private boolean luaMacrosKeptAlive;
	private File luaMacrosLocation;

	public long getServerPort() {
		return serverPort;
	}

	public boolean isLuaMacrosStartingMinimized() {
		return luaMacrosStartingMinimized;
	}

	public boolean isLuaMacrosMinimizingToTray() {
		return luaMacrosMinimizingToTray;
	}

	public boolean isLuaMacrosKeptAlive() {
		return luaMacrosKeptAlive;
	}

	public File getLuaMacrosLocation() {
		return luaMacrosLocation;
	}

	public void setServerPort(long serverPort) {
		long serverPort2 = this.serverPort;
		this.serverPort = serverPort;
		notifyPropertyChange("serverPort", serverPort2, serverPort);
	}

	public void setLuaMacrosStartingMinimized(boolean luaMacrosStartingMinimized) {
		boolean luaMacrosStartingMinimized2 = this.luaMacrosStartingMinimized;
		this.luaMacrosStartingMinimized = luaMacrosStartingMinimized;
		notifyPropertyChange("luaMacrosStartingMinimized", luaMacrosStartingMinimized2, luaMacrosStartingMinimized);
	}

	public void setLuaMacrosMinimizingToTray(boolean luaMacrosMinimizingToTray) {
		boolean luaMacrosMinimizingToTray2 = this.luaMacrosMinimizingToTray;
		this.luaMacrosMinimizingToTray = luaMacrosMinimizingToTray;
		notifyPropertyChange("luaMacrosMinimizingToTray", luaMacrosMinimizingToTray2, luaMacrosMinimizingToTray);
	}

	public void setLuaMacrosKeptAlive(boolean luaMacrosKeptAlive) {
		boolean luaMacrosKeptAlive2 = this.luaMacrosKeptAlive;
		this.luaMacrosKeptAlive = luaMacrosKeptAlive;
		notifyPropertyChange("luaMacrosKeptAlive", luaMacrosKeptAlive2, luaMacrosKeptAlive);
	}

	public void setLuaMacrosLocation(File luaMacrosLocation) {
		File luaMacrosLocation2 = this.luaMacrosLocation;
		this.luaMacrosLocation = luaMacrosLocation;
		notifyPropertyChange("luaMacrosLocation", luaMacrosLocation2, luaMacrosLocation);

	}

}
