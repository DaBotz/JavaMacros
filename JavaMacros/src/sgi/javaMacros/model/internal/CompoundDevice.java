package sgi.javaMacros.model.internal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.abstracts.JavaMacrosConfigAtom;

public class CompoundDevice extends JavaMacrosConfigAtom implements IDevice {
	// public String getName() {
	// return Name;
	// }

	public class ___ApplicationCounters extends Hashtable<ApplicationForMacros, Integer> {
		private static final long serialVersionUID = 7546434827589992940L;
	}

	public static class Devices extends HashSet<Device> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6663310441302446131L;

	}

	// private String Name;
	private // ArrayList<Device>
	Devices devices;// = new ArrayList<>();

	private transient ___ApplicationCounters /* Hashtable<ApplicationForMacros, Integer> */
	usedApplicationForMacrosCounter = new ___ApplicationCounters();

	private transient int ___macrosCounter = 0;

	public CompoundDevice(String name) {
		super();
		super.setName(name);
		devices = new Devices();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		String oldName = getName();
		JavaMacrosMemory.instance().getDeviceSet().getDevicesByName().remove(oldName, this);

		for (Device device : devices) {
			device.setName(name);
		}

		super.setName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * sgi.javaMacros.model.internal.IDevice#addApplicationForMacros(sgi.javaMacros.
	 * model.internal.ApplicationForMacros)
	 */

	public void addApplicationForMacros(ApplicationForMacros myapp) {
		Hashtable<ApplicationForMacros, Integer> usedApplicationForMacross2 = getUsedApplicationForMacrosCounter();
		Integer integer = usedApplicationForMacross2.get(myapp);
		if (integer == null)
			usedApplicationForMacross2.put(myapp, 1);
		else {
			usedApplicationForMacross2.put(myapp, integer.intValue() + 1);
		}
		notifyPropertyChange("transient:added_app", null, myapp);
	}

	public void addDevice(Device d) {
		getDevices().add(d);
	}

	public void addKnownMacro(Macro macro) {
		boolean raise = false;
		for (Device d : devices) {
			raise |= d.addKnownMacro(macro);
		}
		if (raise)
			___macrosCounter++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#findKey(int)
	 */
	@Override
	public Key findKey(int scanCode) {

		for (Device d : devices) {
			Key find = d.getKeySet().find(scanCode);
			if (find != null)
				return find;
		}

		return null;
	}

	public HashSet<Device> getDevices() {
		return devices;
	}
	//
	// public void setName(String name) {
	// Name = name;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#getKeys()
	 */

	public Key[] getKeys() {
		HashSet<Key> list = new HashSet<>();
		for (Device d : devices) {
			list.addAll(d.getKeySet());
		}

		Key[] k = new Key[list.size()];
		list.toArray(k);

		Arrays.sort(k, new Comparator<Key>() {
			@Override
			public int compare(Key o1, Key o2) {
				if (o1 == null)
					return -1;
				return o1.compareByName(o2);
			}
		});

		return k;
	}

	// public void addApplicationForMacros(ApplicationForMacros myApp) {
	// for (Device d : devices) {
	// d.addApplicationForMacros(myApp);
	// }
	// }
	//
	// public void removeApplicationForMacros(ApplicationForMacros myApp) {
	// for (Device d : devices) {
	// d.removeApplicationForMacros(myApp);
	// }
	// }

	public int get___macrosCounter() {
		return ___macrosCounter;
	}

	public ___ApplicationCounters getUsedApplicationForMacrosCounter() {
		if (usedApplicationForMacrosCounter == null)
			usedApplicationForMacrosCounter = new ___ApplicationCounters(); // Hashtable<>();
		return usedApplicationForMacrosCounter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#isDetected()
	 */
	@Override
	public boolean isDetected() {
		for (Device d : devices) {
			if (d.isDetected())
				return true;
		}
		return false;
	}

	public void removeApplicationForMacros(ApplicationForMacros myapp) {
		Hashtable<ApplicationForMacros, Integer> usedApplicationForMacross2 = getUsedApplicationForMacrosCounter();
		Integer integer = usedApplicationForMacross2.get(myapp);

		if (integer == null)
			return;
		else {
			int value = integer.intValue() - 1;
			if (value > 0)
				usedApplicationForMacross2.put(myapp, value);
			else
				usedApplicationForMacross2.remove(myapp);

		}

		notifyPropertyChange("transient:removed_app", null, myapp);

	}

	public void removeDevice(Device d) {
		getDevices().remove(d);
	}

	public void removeKnownMacro(Macro macro) {
		boolean decr = false;
		for (Device d : devices) {
			decr |= d.removeKnownMacro(macro);
		}
		if (decr)
			___macrosCounter--;

	}

	public int size() {
		return devices == null ? 0 : devices.size();
	}

	@Override
	public int hashCode() {

		return getName().hashCode();
	}

	private transient String type;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#getType()
	 */
	@Override
	public String getType() {
		if (type != null)
			return type;

		String t = null;

		for (Device d : devices) {
			if (t == null)
				t = d.getType();
			else if (!areTheseEquals(t, d.getType()))
				return "mixeddevice";
		}

		return type = t == null ? "compounddevice" : "compound_" + t;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sgi.javaMacros.model.internal.IDevice#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		HashSet<Device> d2 = getDevices();
		for (Device d : d2) {
			if (d.isEnabled())
				return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return devices == null || devices.size() == 0;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (obj instanceof String) {
	// return getName().equals( obj);
	// }
	//
	// if (obj instanceof CompoundDevice) {
	// return getName().equals(((CompoundDevice) obj).getName());
	// }
	//
	//
	// return super.equals(obj);
	// }

}