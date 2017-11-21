package sgi.javaMacros.model.internal;

import java.io.File;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.events.ConfigChangeEvent;

public class Device extends ConfigAtom {

	public static int getDeviceNumber(String luaMacrosId) {
		return Integer.parseInt(luaMacrosId, 16);
	}

	private String luaMacrosId;

	private boolean autonomousNumLock;
	private transient int codeId = -1;
	private boolean ignored;

	private File icon; 

	/**
	 * @deprecated
	 */
	public Device() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Device(String luaId) {
		this.luaMacrosId = luaId;
		setName(luaId);
	}

	public String getLuaMacrosId() {
		return luaMacrosId;
	}


	@Override
	public int hashCode() {
		if (codeId < 0)
			codeId = getDeviceNumber(luaMacrosId);
		return codeId;
	}

	public boolean isAutonomousNumLock() {
		return autonomousNumLock;
	}

	public File getIcon() {
		return icon;
	}

	public void setIcon(File icon) {
		this.icon = icon;
	}

	public boolean isIgnored() {
		return ignored;
	}

	public void setAutonomousNumLock(boolean autonomousNumLock) {
		this.autonomousNumLock = autonomousNumLock;
		if (this.autonomousNumLock != autonomousNumLock) {
			this.autonomousNumLock = autonomousNumLock;
			fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "autonomousNumLock", autonomousNumLock));
		}

	}

	public void setIgnored(boolean ignored) {
		if (this.ignored != ignored) {
			this.ignored = ignored;
			fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "ignored", ignored));
		}
	}

	public void setLuaMacrosId(String usbClass) {
		this.luaMacrosId = usbClass;
	}

}
