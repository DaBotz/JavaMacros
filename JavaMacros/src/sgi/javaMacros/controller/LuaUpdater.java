package sgi.javaMacros.controller;

import java.util.ArrayList;

import sgi.javaMacros.model.JavaMacrosLuaMacrosConfiguration;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.lists.DeviceSet;

public class LuaUpdater {

	public static final String JMA = "JMA";
	public static final String crlf = new String(new char[] { 13, 10 });
	private JavaMacrosLuaMacrosConfiguration luaCfg;
	private DeviceSet devices;
	private ArrayList<String> lastUpdates;

	public LuaUpdater(JavaMacrosLuaMacrosConfiguration luaCfg, DeviceSet devices) {
		super();
		this.luaCfg = luaCfg;
		this.devices = devices;
	}

	public JavaMacrosLuaMacrosConfiguration getLuaCfg() {
		return luaCfg;
	}

	public DeviceSet getDevices() {
		return devices;
	}

	public ArrayList<String> getInitInstructions() {

		return ___getUpdates(false);
	}

	public ArrayList<String> getUpdates() {

		return ___getUpdates(true);
	}

	public static String getUseNumLockUpdate(String id) {
		return UpdateCommands.USE_NUM_LOCK + encapsulate(id);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<String> ___getUpdates(boolean atRruntime) {
		ArrayList<String> list = new ArrayList<>();

		DeviceSet devices2 = devices;
		for (IConfigAtom ica : devices2) {
			if (ica instanceof Device) {
				Device dev = (Device) ica;
				list.add(getUseKbdUpdate(dev));
			}
		}
		for (IConfigAtom ica : devices2) {
			if (ica instanceof Device) {
				Device dev = (Device) ica;
				list.add(getNumLockUpdate(dev));
			}
		}

		ArrayList<String> clone = (ArrayList<String>) list.clone();

		if (!atRruntime) {
	//		list.add(0, getSetPortUpdate());
			list.add(getScanUpdate());
		} else if (lastUpdates != null) {
			list.removeAll(lastUpdates);
		}

		lastUpdates = clone;
		return list;

	}

	protected String getUseKbdUpdate(Device dev) {
		if (dev.isIgnored()) {
			return getAvoidUpdate(dev);
		} else
			return getAllowUpdate(dev);
	}

	protected String getNumLockUpdate(Device dev) {
		if (dev.isAutonomousNumLock())
			return getNoNumLock(dev);
		else
			return getUseNumLockUpdate(dev);
	}

	public static String getUseNumLockUpdate(Device dev) {
		return getUseNumLockUpdate(dev.getLuaMacrosId());
	}

	public static String getNoNumLock(Device dev) {
		return getNoNumLock(dev.getLuaMacrosId());
	}

	public static String getAllowUpdate(Device dev) {
		return getAllowUpdate(dev.getLuaMacrosId());
	}

	public static String getAvoidUpdate(Device dev) {
		return getAvoidUpdate(dev.getLuaMacrosId());
	}

	public static String getNoNumLock(String id) {
		return UpdateCommands.NO_NUM_LOCK + encapsulate(id);

	}

	public static String getAllowUpdate(String id) {
		return UpdateCommands.ALLOW + encapsulate(id);
	}

	public static String getAvoidUpdate(String id) {
		return UpdateCommands.AVOID + encapsulate(id);
	}

	public static String encapsulate(String id) {
		String string = "('" + id + "')";
		return string;
	}

	public String getScanUpdate() {
		return UpdateCommands.SCAN + "()";
	}

	public String getSetPortUpdate() {
		return UpdateCommands.SET_PORT + "(" + luaCfg.getServerPort() + ")";
	}

	public String getJMAinit() {
		// TODO Auto-generated method stub
		return JMA + "." + UpdateCommands.INIT + "("//
				+ luaCfg.getServerPort()//
				+ ", "//
				+ luaCfg.isLuaMacrosMinimizingToTray()//
				+ ", "//
				+ luaCfg.isLuaMacrosStartingMinimized()//
				+ ")";//
	}

}