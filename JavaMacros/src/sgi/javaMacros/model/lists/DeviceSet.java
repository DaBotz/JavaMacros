package sgi.javaMacros.model.lists;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.TreeSet;

import sgi.configuration.ConfigAtomHashSet;
import sgi.configuration.IConfigurationAtom;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.internal.CompoundDevice;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.internal.IDevice;
import sgi.javaMacros.model.internal.Macro;

public class DeviceSet extends ConfigAtomHashSet<Device> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6679617135459877124L;

	public DeviceSet(IConfigurationAtom parent) {
		super(parent);
	}

	public DeviceSet() {

	}

	private transient Hashtable<Integer, Device> lookUpTable;
	private transient Hashtable<String, Device> devicesBySystemId;
	private transient DevicesByNameTable devicesByName;

	private transient Device[] lookUpListx;

	public Device[] getLookUpList() {
		if (lookUpListx == null)
			lookUpListx = new Device[256];

		return lookUpListx;
	}

	public Hashtable<String, Device> getDevicesBySystemId() {
		if (devicesBySystemId == null)
			devicesBySystemId = new Hashtable<>();
		return devicesBySystemId;
	}

	// public class DevicesAlias extends ConfigurationAtom {
	// private ArrayList<CompoundDevice> list;
	//
	// public DevicesAlias(ConfigAtomArrayList<CompoundDevice> list) {
	// super();
	//
	// this.list = list;
	// }
	//
	// public DevicesAlias(DevicesByNameTable devicesByNameTable) {
	// setName(Messages.M._$("devices.name"));
	// list = new ArrayList<>(devicesByNameTable.values());
	// }
	//
	// }
	public static class CompoundDeviceList extends ArrayList<CompoundDevice> {

		private static final long serialVersionUID = -4243354512331242525L;

		CompoundDeviceList(Collection<CompoundDevice> c) {
			super(c);
		}
	}

	public class DevicesByNameTable extends Hashtable<String, CompoundDevice> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8911900733721910432L;

		public synchronized CompoundDevice get(Object key, boolean createIfMissing) {
			CompoundDevice compoundDevice = super.get(key);
			if (compoundDevice == null && createIfMissing) {
				String nm = String.valueOf(key);
				compoundDevice = new CompoundDevice(nm);
				put(nm, compoundDevice);
			}
			return compoundDevice;
		}

		public void removeDevice(Object oldValue, Device device) {
			CompoundDevice compoundDevice = get(oldValue);
			if (compoundDevice == null)
				return;
			compoundDevice.removeDevice(device);
			if (compoundDevice.size() == 0) {
				remove(oldValue);
			}
		}

		public ArrayList<CompoundDevice> asListForViewer() {
			ArrayList<CompoundDevice> arrayList = new CompoundDeviceList(values());
			arrayList.sort(new Comparator<CompoundDevice>() {

				@Override
				public int compare(CompoundDevice o1, CompoundDevice o2) {
					if (o1 == o2)
						return 0;
					if (o1 == null)
						return 11;

					return o1.toString().toLowerCase().compareTo(String.valueOf(o2).toLowerCase());
				}
			});
			return arrayList;// new DevicesAlias(this);
		}

		public CompoundDevice[] asArray() {
			ArrayList<CompoundDevice> l = asListForViewer();
			CompoundDevice[] rv = new CompoundDevice[l.size()];
			l.toArray(rv);
			return rv;
		}
	}

	public DevicesByNameTable getDevicesByName() {
		if (devicesByName == null)
			devicesByName = new DevicesByNameTable();
		return devicesByName;
	}

	// @Override
	// public void relink(IConfigurationAtom parent) {
	// super.relink(parent);
	//
	// for (Device d : this)
	// updateLookupTables(d);
	//
	// }

	public Hashtable<Integer, Device> getLookUpTable() {
		if (lookUpTable == null)
			lookUpTable = new Hashtable<>(512);
		return lookUpTable;
	}

	@Override
	public boolean add(Device e) {
		updateLookupTables(e);
		return super.add(e);
	}

	public void updateLookupTables(Device e) {
		int luaMacrosIdAsInt = e.getLuaMacrosIdAsInt();
		Device[] lookUpList = getLookUpList();

		getDevicesByName().get(e.getName(), true).addDevice(e);
		if (e.getSystemId() != null)
			getDevicesBySystemId().put(e.getSystemId(), e);
		getLookUpTable().put(luaMacrosIdAsInt, e);

		if (luaMacrosIdAsInt < lookUpList.length) {
			lookUpList[luaMacrosIdAsInt] = e;
		}
	}

	public Device find(String luaId) {
		int k = Device.getDeviceNumber(luaId);
		if (k >= 0 && k < 256)
			return findInList(luaId, k);

		return findInTable(luaId, k);
	}

	public Device findInList(String luaId, int k) {
		Device[] lookUpList = getLookUpList();
		Device device = lookUpList[k];
		if (device != null)
			return device;
		for (IConfigurationAtom icfa : this) {
			if (icfa instanceof Device) {
				device = (Device) icfa;
				String luaMacrosId = device.getLuaMacrosId();
				if (luaId.equalsIgnoreCase(luaMacrosId)) {
					return lookUpList[k] = device;
				}
			}
		}

		return null;
	}

	public Device findInTable(String luaId, int k) {
		Hashtable<Integer, Device> lookUp = getLookUpTable();
		Device device = lookUp.get(k);
		if (device != null)
			return device;
		for (IConfigurationAtom icfa : this) {
			if (icfa instanceof Device) {
				device = (Device) icfa;
				String luaMacrosId = device.getLuaMacrosId();
				if (luaId.equalsIgnoreCase(luaMacrosId)) {
					lookUp.put(k, device);
					return device;
				}
			}
		}

		return null;
	}

	public String[] nameSet(Device newDevice) {
		ArrayList<String> set = new ArrayList<>(new TreeSet<>(getDevicesByName().keySet()));
		if (newDevice != null) {
			for (Device d : this) {
				if (//
				areTheseEquals(newDevice.getVendorId(), d.getVendorId())//
				&&	areTheseEquals(newDevice.getProductId(), d.getProductId())//

						&& !d.isDetected()) {
					String name = d.getName();
					set.remove(name); 
					set.add(0,  name);
				}
			}
		}

		return set.toArray(new String[set.size()]);
	}

	public Device[] asArray() {
		Device[] array = new Device[size()];
		toArray(array);
		Arrays.sort(array, new Comparator<Device>() {

			@Override
			public int compare(Device o1, Device o2) {

				try {
					return o1.getName().compareTo(o2.getName());
				} catch (Exception e) {
					return o1 == o2 ? 0 : -1;
				}
			}
		});

		return array;
	}

	@Override
	public void propagatePropertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof Device && evt.getPropertyName().equals("name")) {
			Device device = (Device) evt.getSource();
			getDevicesByName().removeDevice(evt.getOldValue(), device);
			getDevicesByName().get(String.valueOf(evt.getNewValue()), true).addDevice(device);

		}

		if (evt.getSource() == this && CONTENT_REMOVE_ATOM.equals(evt.getPropertyName())) {
			Device removedDevice = (Device) evt.getOldValue();
			String rName = removedDevice.getName();
			CompoundDevice compoundDevice = getDevicesByName().get(rName);
			compoundDevice.removeDevice(removedDevice);
			if (compoundDevice.getDevices().size() == 0) {
				getDevicesByName().remove(compoundDevice.getName());
				JavaMacrosMemory.instance().getMacros().getList().deviceNameChange(rName, Macro.NO_DEVICE);
			}
		}

		if ("detected".equals(evt.getPropertyName())) {
			firePropertyChangeListeners(evt);
		}

		super.propagatePropertyChange(evt);
	}

	public IDevice byName(String deviceName) {
		// if (deviceName == null)
		// deviceName = "";
		// Hashtable<String, Device> devicesByName = getDevicesByName();
		// Device d0 = null, device = devicesByName.get(deviceName);
		//
		// if (device == null) {
		// for (Device d : this) {
		// if (d0 == null)
		// d0 = d;
		// if (d.getName().equals(deviceName)) {
		// devicesByName.put(deviceName, (device = d));
		// }
		// }
		//
		// }

		return getDevicesByName().get(deviceName); // device == null ? d0 : device;
	}

}
