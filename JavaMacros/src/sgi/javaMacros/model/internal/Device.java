package sgi.javaMacros.model.internal;

import java.util.HashSet;

import sgi.javaMacros.model.abstracts.IconBearingConfigAtom;
import sgi.javaMacros.model.lists.KeySet;
import sgi.javaMacros.msgs.Messages;
import sgi.localization.AbstractMsgs;

public class Device extends IconBearingConfigAtom implements IDevice {

	public static String UNKNOWN_DEVICE = Messages.M.getString("device.unknown.name");
	private boolean known;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public static final String AUTONOMOUS_NUM_LOCK = "autonomousNumLock";
	public static final String IGNORED = "ignored";

	public static int getDeviceNumber(String luaMacrosId) {
		return Integer.parseInt(luaMacrosId.trim(), 16);
	}

	public boolean isKnown() {
		return known;
	}

	private transient boolean detected;
	private transient HashSet<ApplicationForMacros> ___applicationsSet = new HashSet<>();

	protected void setKnown(boolean known) {
		boolean known2 = this.known;
		this.known = known;
		notifyPropertyChange("known", known2, known);
	}

	public HashSet<ApplicationForMacros> get___applicationsSet() {
		if (___applicationsSet == null) {
			___applicationsSet = new HashSet<>();
		}
		return ___applicationsSet;
	}

	private String luaMacrosId;
	private KeySet keySet;

	private String label;
	private transient int handle;
	private String systemId;
	private String productId;
	private String vendorId;
	private String type;

	private boolean autonomousNumLock;
	private boolean enabled;

	public boolean isDetected() {
		return detected;
	}

	public void setDetected(boolean detected) {
		boolean old = this.detected;
		this.detected = detected;
		notifyPropertyChange("transient:detected", old, detected);
	}

	public static String getAutonomousNumLock() {
		return AUTONOMOUS_NUM_LOCK;
	}

	public static String getIgnored() {
		return IGNORED;
	}

	public String getType() {
		return type;
	}

	public int getHandle() {
		return handle;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getVendorId() {
		return vendorId;
	}

	public String getProductId() {
		return productId;
	}

	public void setLuaMacrosId(String luaMacrosId) {
		String oldluaMacrosId = this.luaMacrosId;
		this.luaMacrosId = luaMacrosId;
		notifyPropertyChange("luaMacrosId", oldluaMacrosId, luaMacrosId);

	}

	public void setType(String type) {
		String oldtype = this.type;
		this.type = type;
		notifyPropertyChange("type", oldtype, type);

	}

	public void setHandle(int handle) {
		int oldhandle = this.handle;
		this.handle = handle;
		notifyPropertyChange("transient:handle", oldhandle, handle);
	}

	public void setSystemId(String systemId) {
		String oldsystemId = this.systemId;
		this.systemId = systemId;
		notifyPropertyChange("systemId", oldsystemId, systemId);
	}

	public void setVendorId(String vendorId) {
		String oldvendorId = this.vendorId;
		this.vendorId = vendorId;
		notifyPropertyChange("vendorId", oldvendorId, vendorId);
	}

	public void setProductId(String productId) {
		String oldproductId = this.productId;
		this.productId = productId;
		notifyPropertyChange("productId", oldproductId, productId);
	}

	public KeySet getKeySet() {
		if (keySet == null) {
			keySet = new KeySet(this);
		}
		return keySet;
	}

	/**
	 * @deprecated
	 */
	public Device() {
		super();
		// setName(UNKNOWN_DEVICE);

	}

	private String safestID; 
	
	public String getSafestID() {
		return safestID;
	}

	public void setSafestID(String safestID) {
		String oldSafestID = this.safestID;
		this.safestID = safestID;
		notifyPropertyChange("safestID", oldSafestID, safestID);
	}

	public Device(String luaId) {
		this.luaMacrosId = luaId;
		super.setName(UNKNOWN_DEVICE);
	}

	public Device(String luaId, boolean enable) {
		this(luaId);
		setEnabled(enable);
	}

	public Device(String luaId, String name) {
		this(luaId);
		setName(name);
		known= false;
	}

	public String getLuaMacrosId() {
		return luaMacrosId;
	}

	public int getLuaMacrosIdAsInt() {
		return getDeviceNumber(getLuaMacrosId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Device) {
			Device new_name = (Device) obj;
			return ("" + getLuaMacrosId()).equals(new_name.getLuaMacrosId());
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return luaMacrosId == null ? super.hashCode() : luaMacrosId.hashCode();
	}

	public boolean isAutonomousNumLock() {
		return autonomousNumLock;
	}

	public boolean isIgnored() {
		return !enabled;
	}

	public void setAutonomousNumLock(boolean autonomousNumLock) {
		boolean old = this.autonomousNumLock;
		this.autonomousNumLock = autonomousNumLock;
		notifyPropertyChange(AUTONOMOUS_NUM_LOCK, old, autonomousNumLock);
	}

	public void setEnabled(boolean enabled) {
		boolean old = this.enabled;
			this.enabled = enabled;
			notifyPropertyChange("enabled", old, enabled);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		getLabel();
		setKnown(true);
	}

	public String getLabel() {

		label = AbstractMsgs.htmlSwing(getName() + " <i><font color=\"blue\">(" + getLuaMacrosId() + ")</font></i>");
		return label;
	}

	// private transient boolean absent = false;
	//
	// public boolean isAbsent() {
	// return ! detected;
	// }
	////
	//// public void setAbsent(boolean absent) {
	//// this.absent = absent;
	//// }

	public void parseSystemId(String systemId2) {
		System.out.println(systemId2);

		setSystemId(systemId2);
		// VID_
		String vid = "VID_";

		int[] i2 = new int[1];
		setVendorId(extractId(vid, systemId2, i2));
		systemId2 = systemId2.substring(i2[0]);
		setProductId(extractId("PID_", systemId2, i2));
		if (i2[0] > 0) {
			systemId2 = systemId2.substring(i2[0]);

		}

	}

	private String extractId(String vid, String systemId2, int[] i2) {
		int indexof = systemId2.indexOf(vid);
		String vendor = "";
		if (indexof >= 0) {
			i2[0] = systemId2.indexOf("&", indexof);
			vendor = systemId2.substring(indexof + vid.length(), i2[0]);
		}
		System.out.println(vendor);
		return vendor;
	}

	public boolean itSme(String systemId2) {
		return systemId2.equals(getSystemId());
	}

	private transient int ___macrosCounter;

	public int get___macrosCounter() {
		return ___macrosCounter;
	}

	public boolean addKnownMacro(Macro macro) {
		int scanCode = macro.getScanCode();
		Key find = getKeySet().find(scanCode);
		if (find == null) {
			// find = new Key(scanCode);
			return false;
		}

		find.addMacro(macro);
		___macrosCounter++;
		return true;
	}

	public boolean removeKnownMacro(Macro macro) {
		int scanCode = macro.getScanCode();
		Key find = getKeySet().find(scanCode);
		___macrosCounter--;
		if (find == null) {
			return false;
		}
		find.removeMacro(macro);
		return true;
	}

	@Override
	public Key findKey(int scanCode) {
		return getKeySet().find(scanCode);
	}

	public boolean itSme(String systemId2, String computedSafestID) {
			if( String.valueOf(systemId2).indexOf("&0&")>0)
				return itSme(systemId2); 
		
		return areTheseEquals(this.safestID, computedSafestID);
	}

}
