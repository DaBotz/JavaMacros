package sgi.javaMacros.model;

import sgi.generic.serialization.AbstractMemoryParcel;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.model.lists.DeviceSet;

public class JavaMacrosMemory extends AbstractMemoryParcel implements IConfigAtom {

	/**
	 * @deprecated
	 */
	private JavaMacrosMemory() {
		super();

	}

	@Override
	public void loadFromFile() {
		devices = new Devices();
		applications = new Applications();

	}

	@Override
	public void storeToFile() {
		devices.storeToFile();
		applications.storeToFile();
	}

	private transient Devices devices;
	private transient Applications applications;

	public Applications getApplications() {
		return applications;
	}

	private static transient JavaMacrosMemory root;

	public static JavaMacrosMemory instance() {
		if (root == null)
			root = new JavaMacrosMemory();
		return root;
	}

	@Override
	protected void initializeDefaultValues() {

	}

	public ApplicationSet getApplicationSet() {
		return getApplications().getSet();
	}

	@Override
	public IConfigAtom getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParent(IConfigAtom parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addConfigChangeListener(IConfigChangeListener listener) {
		// TODO Auto-generated method stub

	}

	public DeviceSet getDeviceSet() {
		return getDevices().getSet();
	}

	public Devices getDevices() {
		return devices;
	}

}
