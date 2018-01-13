package sgi.javaMacros.model;




import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;
import sgi.javaMacros.model.lists.DeviceSet;

public class Devices extends JavaMacrosMemoryParcel {

	private DeviceSet set;
	@Override
	protected void initializeDefaultValues() {
		set = new DeviceSet();
		set.setParent(this);

	}

	public Devices() {


	}

	public DeviceSet getSet() {
		return set;
	}

	


}
