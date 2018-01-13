package sgi.javaMacros.model.lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import sgi.configuration.ConfigAtomHashSet;
import sgi.javaMacros.model.internal.Device;
import sgi.javaMacros.model.internal.Key;

public class KeySet extends ConfigAtomHashSet<Key> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6679617135459877124L;

	public KeySet() {

	}

	public KeySet(Device device) {
		setParent(device);
	}

	private transient Hashtable<Integer, Key> lookUpTable;

	public Hashtable<Integer, Key> getLookUpTable() {
		if (lookUpTable == null)
			lookUpTable = new Hashtable<>(512);
		return lookUpTable;

	}

	private transient Key[] lookUpList = new Key[256];

	public Key find(String luaId) {
		int k = Integer.parseInt(luaId.trim());

		return find(k);
	}

	public Key find(int scanCode) {
		if (scanCode >= 0 && scanCode < 256)
			return findInList(scanCode);

		return findInTable(scanCode);
	}

	protected Key findInList(int k) {
		Key key = lookUpList[k];
		if (key != null)
			return key;

		for (Key icfa : this) {
			key = (Key) icfa;

			int scanCode = key.getScanCode();
			if (scanCode < 256)
				lookUpList[scanCode] = key;
			if (scanCode == k) {
				return lookUpList[k] = key;
			}
		}

		return null;
	}

	protected Key findInTable(int k) {
		Hashtable<Integer, Key> lookUp = getLookUpTable();
		Key device = lookUp.get(k);
		if (device != null)
			return device;
		for (sgi.configuration.IConfigurationAtom icfa : this) {
			if (icfa instanceof Key) {
				device = (Key) icfa;
				if (device.getScanCode() == k) {
					lookUp.put(k, device);
					return device;
				}
			}
		}

		return null;
	}

	public List<Key> asNameOrderedList() {
		ArrayList<Key> l = new ArrayList<>(this);
		Collections.sort(l, Key.nameComparator);
		return l;
	}
}
