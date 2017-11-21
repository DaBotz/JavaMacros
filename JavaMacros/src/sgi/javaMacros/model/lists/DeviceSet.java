package sgi.javaMacros.model.lists;

import java.util.ArrayList;
import java.util.HashSet;

import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.internal.Device;

public class DeviceSet extends AtomSet<Device> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6679617135459877124L;

	public DeviceSet(IConfigAtom parent) {
		super(parent);
	}

	public DeviceSet() {

	}

	private transient Device[] lookUpList = new Device[256];

	public boolean add(Device element) {

		boolean add = super.add(element);
		if (add)
			lookUpList[element.hashCode() % lookUpList.length] = (Device) element;

		return add;
	}

	public Device find(String luaId) {
		Device device = lookUpList[Device.getDeviceNumber(luaId)%lookUpList.length];
		
		if (device != null)
			return device;
		for (IConfigAtom icfa : this) {
			if (icfa instanceof Device) {
				device = (Device) icfa;
				if (luaId.equalsIgnoreCase(device.getLuaMacrosId()))
					return device;
			}
		}

		return null;
	}

	public String[] nameSet() {
		HashSet<String> noCase = new HashSet<>();
		ArrayList<String> l = new ArrayList<>();

		for (IConfigAtom icfa : this) {
			if (icfa instanceof Device) {
				Device d = (Device) icfa;
				String name = d.getName();
				
				if (noCase.add(name.toUpperCase()))
					l.add(name);
			}
		}

		String[] rv = new String[l.size()];
		l.toArray(rv);
		return rv;

	}

}
