package sgi.javaMacros.model;


import sgi.javaMacros.model.lists.DeviceSet;
import sgi.javaMacros.model.persistent.AbstractJavaMacrosMemoryParcel;

public class Devices extends AbstractJavaMacrosMemoryParcel {

	DeviceSet set;
	@Override
	protected void initializeDefaultValues() {
		set = new DeviceSet();

	}

	public Devices() {

		set.addConfigChangeListener(this);

	}

	public DeviceSet getSet() {
		return set;
	}
}
