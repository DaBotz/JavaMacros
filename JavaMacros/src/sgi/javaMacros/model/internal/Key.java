package sgi.javaMacros.model.internal;

import java.util.ArrayList;

import sgi.javaMacros.model.abstracts.JavaMacrosConfigAtom;

public class Key extends JavaMacrosConfigAtom implements Comparable<Key> {

	public static final String SCAN_CODE = "scanCode";
	public static final Key ADD_KEY = new NullKey();

	public static int getDeviceNumber(String luaMacrosId) {
		return Integer.parseInt(luaMacrosId.trim(), 16);
	}
	
@Override
public boolean equals(Object obj) {
	if( obj== null) return false; 
	if (obj instanceof Key) {
		return ((Key) obj).scanCode == scanCode;
		
	}
	return false;
}
	private int scanCode;

	/**
	 * @deprecated
	 */
	public Key() {
		super();
	}

	public Key(int scanCode) {
		setScanCode(scanCode);
	}

	@Override
	public int hashCode() {
		return scanCode;
	}

	@Override
	public int compareTo(Key o) {
		if (o == null)
			return 1;
		return o.scanCode - scanCode;
	}

	public int getScanCode() {
		return scanCode;
	}

	public void setScanCode(int scanCode) {
		int scanCode2 = this.scanCode;
		if (scanCode2 != scanCode) {
			this.scanCode = scanCode;
			notifyPropertyChange(SCAN_CODE, scanCode2, scanCode);
		}
	}

	@Override
	public void setName(String name) {

		super.setName(name);
	}

	public void addMacro(Macro macro) {
		getKnownMacros().add(macro);

		
	}

	public void removeMacro(Macro macro) {
		getKnownMacros().remove(macro);
		if (getKnownMacros().size() == 0) {
			knownMacros = null;
		}


	}

	private transient ArrayList<Macro> knownMacros;

	public ArrayList<Macro> getKnownMacros() {
		if (knownMacros == null)
			knownMacros = new ArrayList<Macro>();
		return knownMacros;
	}

}
