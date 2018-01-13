package sgi.javaMacros.model.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sgi.configuration.IConfigurationAtom;
import sgi.generic.debug.Debug;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosConfiguration;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.Macros;
import sgi.javaMacros.model.OSDSettings;
import sgi.javaMacros.model.OSDSettings.DisplaySettings;
import sgi.javaMacros.model.abstracts.IconBearingConfigAtom;
import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.enums.ModifierMasks;
import sgi.javaMacros.model.lists.Executors;
import sgi.javaMacros.model.macros.execution.Executor;
import sgi.javaMacros.ui.MacroEditPanel;
import sgi.javaMacros.ui.OSD.MacroOSDDisplayer;

public class Macro extends IconBearingConfigAtom implements Comparable<Macro>, //
		PropertyChangeListener, //
		AutoCopier<Macro>

{

	private static final ActionType DEFAULT_ACTION_TYPE = ActionType.RUN_APPLICATION;

	public static final String USE_CASE_CREATION_TIME = "useCaseCreationTime";

	public static final String SCAN_CODE = "scanCode";

	public static final String ACTION_TYPE = "actionType";

	// public static final String KEY_DIRECTION = "direction";

	public static final String DEVICE_NAME = "deviceName";

	private static boolean _staticCleanUp;

	private ActionType actionType = null;

	private transient ApplicationForMacros ___application;
	private long creationTime;
	private long deletionTime;
	private transient CompoundDevice ___device;
	private String deviceName;
	private transient String OSDReminderDetails;
	private transient Key key;

	private long replacementTime;
	private int scanCode;

	private transient UseCase ___useCase;
	private long useCaseCreationTime;

	private int modifiersMask;

	// private int winUId;

	public String getOSDReminderDetails() {
		return OSDReminderDetails;
	}

	public void setOSDReminderDetails(String oSDReminderDetails) {
		OSDReminderDetails = oSDReminderDetails;
	}

	private Executors executors;

	private String exeFile = "";

	@Deprecated
	public Macro() {
		super();
	}

	public Macro(boolean setCreationTime) {
		this();
		if (setCreationTime) {
			creationTime = System.currentTimeMillis();
		}
		setActionType(DEFAULT_ACTION_TYPE);
		useCaseCreationTime = UseCase.BASIC_CASE_TIMESTAMP;
		exeFile = ApplicationForMacros.getANY().getExeFile();
		autoObserve();

	}

	// public Macro(long deletionTime) {
	// super();
	// this.deletionTime = deletionTime;
	// setName("Dt: " + deletionTime);
	// }

	public Macro(boolean b, String string) {
		this(b);
		setName(string);
	}

	@Deprecated
	public Macro(long i) {
		replacementTime = deletionTime = creationTime = i;
		setName("Ghost: " + deletionTime);

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	protected void crossLinkDevicesAndApps() {
		if (___device != null && ___application != null) {
			___device.addApplicationForMacros(___application);
			___application.addDevice(___device);
		}
	}

	public void eraseMe() {
		unlinkTransients();
		setDeletionTime(System.currentTimeMillis());
		JavaMacrosMemory instance = JavaMacrosMemory.instance();
		instance.getMacros().getList().remove(this);
		instance.getErasedMacros().getList().add(this);
	}

	public void replaceMe() {
		unlinkTransients();
		setReplacementTime(System.currentTimeMillis());
		JavaMacrosMemory instance = JavaMacrosMemory.instance();
		instance.getMacros().getList().remove(this);
		instance.getReplacedMacros().getList().add(this);
	}

	public boolean executeMacro(LuaEvent event) {
		if (erased)
			return false;

		if (!ModifierMasks.isAlwaysInvoked(modifiersMask)) {
			int myAppMask = getApplication().getModifiersMask();
			event.setApplicationModifiersMaskPreset(myAppMask);
			int devicemask = event.getSystemModifiersMask();
			int mask = myAppMask | devicemask;

			if (ActionType.VIRTUAL_MODIFIER != actionType//
					&& mask != modifiersMask) {
				return false;
			}
		}

		if (event.getScanCode() != scanCode) {
			Debug.printErr("Wrong Event scan code " + event.getScanCode() + " differs from macro scan code" + scanCode);
			return false;
		}
		if (!areTheseEquals(event.getDeviceName(), getDeviceName())) {
			Debug.printErr("Wrong Device name " + event.getDeviceName() + " differs from macro logical device "
					+ getDeviceName());
			return false;
		}
		if (getApplication().isReal() && !areTheseEquals(event.getActiveWindow().getExeFile(), getExeFile())) {
			Debug.printErr(
					"Wrong application event.getActiveWindow().getExeFile() " + event.getActiveWindow().getExeFile()//
							+ "\n   differs from macro reference app " + getExeFile());
			return false;
		}

		int executeResult = getExecutor().execute(event);

		if (executeResult != Executor.PASS && Macro._staticCleanUp == false) {
			Macro._staticCleanUp = true;
			Debug.print(getClass() + " may do with a cleanUp at 184-185 and above line 176 to 160");
		}

		/* -- to-do */

		OSDSettings oSDsettings = JavaMacrosConfiguration.OSD;
		if (oSDsettings.isOsdRequired(isUsingOSDReminder(), executeResult)) {
			showOSD(oSDsettings.getDisplaySettings());
		}

		return executeResult != Executor.FAIL;
	}

	private void showOSD(final DisplaySettings displaySettings) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MacroOSDDisplayer.displayOSD(displaySettings, Macro.this);
			}
		});

	}

	private transient ImageIcon icon;

	@Override
	public void setIconFile(File iconFile) {
		if (iconFile != null && !iconFile.isFile())
			iconFile = null;
		super.setIconFile(iconFile);
		this.icon = null;
	}

	public ImageIcon getIcon() {
		if (icon == null && getIconFile() != null) {
			if (getIconFile().isFile())
				icon = new ImageIcon(getIconFile().getPath());
			else
				setIconFile(null);
		}
		return icon;
	}

	public ActionType getActionType() {
		return actionType;
	}
	//
	// public int getWinUId() {
	// return winUId;
	// }

	public ApplicationForMacros getApplication() {

		if (exeFile != null && (___application == null || !___application.getExeFile().equals(getExeFile()))) {
			___application = JavaMacrosMemory.instance().getApplicationSet().fastFind(getExeFile());
			linkToApp(___application);
		}

		return ___application;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getDeletionTime() {
		return deletionTime;
	}

	public CompoundDevice getDevice() {
		if (deviceName != null && (___device == null || !___device.getName().equals(deviceName))) {
			___device = JavaMacrosMemory.instance().getDeviceSet().getDevicesByName().get(getDeviceName(), true);
		}
		return ___device;
	}

	public String getDeviceName() {
		return deviceName;
	}

	// protected void unLinkFromUseCase(UseCase oldUseCase) {
	// if (oldUseCase != null) {
	// oldUseCase.removeMacro(this);
	// }
	// ___useCase = null;
	// }

	public Executor getExecutor() {
		return getExecutor(getActionType());

	}

	public Executor getExecutor(ActionType actionType) {
		Executor iExecuteImpl = getExecutors().get(actionType);
		if (iExecuteImpl == null || iExecuteImpl.isUnimplemented()) {
			iExecuteImpl = Executor.getExecuteImplementation(this, actionType);
			getExecutors().put(actionType, iExecuteImpl);
		}
		return iExecuteImpl;
	}

	protected Executors getExecutors() {
		if (executors == null)
			executors = new Executors();
		return executors;
	}

	public String getExeFile() {
		return exeFile;
	}

	public Key getKey() {
		if (key == null || key.getScanCode() != getScanCode()) {
			key = getDevice() != null ? getDevice().findKey(getScanCode()) : null;
		}
		return key;
	}

	public long getReplacementTime() {
		return replacementTime;
	}

	public int getScanCode() {
		return scanCode;
	}

	public UseCase get___useCase() {
		return ___useCase;
	}

	public long getUseCaseCreationTime() {
		return useCaseCreationTime;
	}

	public boolean isUnattached() {
		return getDevice() == null //
				|| getDevice().isEmpty() //
				|| getApplication() == null//
				|| ((//
				!isCaseless() && ///
						get___useCase() == null

				));
	}

	protected void linkToApp(ApplicationForMacros myApp) {
		___application = myApp;
		___application.incrementAssociatedMacrosCounter();
		crossLinkDevicesAndApps();
		if (isCaseless())
			___application.addCaselessMacro(this);

		linkToUseCase(this.getUseCaseCreationTime());
	}

	protected void linkToDevice(CompoundDevice mydevice) {
		if (mydevice != null) {
			this.___device = mydevice;
			mydevice.addKnownMacro(this);
		}
		crossLinkDevicesAndApps();
	}

	protected void linkToDevice(String deviceNm) {
		if (deviceNm == null)
			return;
		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		CompoundDevice mydevice = memory.getDeviceSet().getDevicesByName().get(deviceNm, true);

		linkToDevice(mydevice);
	}

	protected void linkToUseCase(long useCaseCreationTime2) {
		UseCase myUseCase = ___application.getUseCases().getByCreationTime(useCaseCreationTime2);
		if (myUseCase != null) {
			myUseCase = ___application.getUseCases().getBasicUseCase();
			useCaseCreationTime = myUseCase.getCreationTime();
		}
		_setUseCase(myUseCase);
	}

	protected void lintToApp(String anExeFile) {
		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		ApplicationForMacros myApp = memory.getApplicationSet().fastFind(anExeFile);
		if (myApp != null) {
			linkToApp(myApp);
		}
	}

	public void createTransientLinks() {
		linkToDevice(this.getDeviceName());
		lintToApp(getExeFile());
		try {
			if( actionType== null) actionType = ActionType.JAVAMACROS_TEXT; 

			getExecutor().mount();
		} catch (NullPointerException e) {
			Debug.info(e, 3);
		}

		getExecutor().mount();
		preserveOriginal();
		autoObserve();

	}

	private void autoObserve() {
		if (observeMyself)
			return;
		observeMyself = true;
		addPropertyChangeListener(this);

	}

	public void setActionType(ActionType actionType) {
		ActionType old = this.actionType;
		if (old != null && ___application != null)
			getExecutor().unmount();

		this.actionType = actionType;
		if (___application != null)
			getExecutor().mount();
		notifyPropertyChange(ACTION_TYPE, old, actionType);

	}

	public void _setApplication(ApplicationForMacros application) {
		String old = exeFile;
		if (this.___application != null) {
			if (this.___application.equals(application))
				return;
			unLinkFromApp(this.___application);

		}

		this.___application = application;

		if (this.___application != null) {
			exeFile = application.getExeFile();
			linkToApp(application);
			_setUseCase(null);
			_setUseCase(application.getUseCases().getBasicUseCase());

		}
		notifyPropertyChange("exeFile", old, exeFile);
	}

	public void setDeletionTime(long deletionTime) {
		this.deletionTime = deletionTime;
	}

	public void set___device(CompoundDevice newDevice) {
		String oldDeviceName = this.deviceName;
		unlinkFromDevice(this.___device);
		this.___device = newDevice;
		if (newDevice != null) {
			this.deviceName = (newDevice.getName());
			linkToDevice(newDevice);
			notifyPropertyChange(DEVICE_NAME, oldDeviceName, deviceName);
		}
	}

	public void setKey(Key key) {
		this.key = key;
		if (key != null)
			_setScanCode(key.getScanCode());

	}

	public void setReplacementTime(long replacementTime) {
		this.replacementTime = replacementTime;
	}

	public void _setScanCode(int scanCode) {
		int scanCode2 = this.scanCode;
		this.scanCode = scanCode;

		notifyPropertyChange(SCAN_CODE, scanCode2, scanCode);
	}

	public void _setUseCase(UseCase myUseCase) {
		long oldUseCaseCreationTime = this.useCaseCreationTime;

		if (___useCase != null) {
			if (___useCase == myUseCase)
				return;

			___useCase.removeMacro(this);
		}

		___useCase = myUseCase;

		if (myUseCase != null) {
			myUseCase.addMacro(this);
			this.useCaseCreationTime = ___useCase.getCreationTime();
		} else
			useCaseCreationTime = UseCase.BASIC_CASE_TIMESTAMP;

		notifyPropertyChange(USE_CASE_CREATION_TIME, oldUseCaseCreationTime, useCaseCreationTime);
	}

	// public void setWinUId(int WinUI) {
	// int old = this.winUId;
	// this.winUId = WinUI;
	// notifyPropertyChange("winUId", old, actionType);
	//
	// }

	protected void unCrossLinkDevicesAndApps(CompoundDevice oldDevice, ApplicationForMacros oldApp) {
		if (oldDevice != null && oldApp != null) {
			oldDevice.removeApplicationForMacros(oldApp);
			oldApp.removeDevice(oldDevice);
		}
	}

	protected void unLinkFromApp(ApplicationForMacros application) {
		application.decrementAssociatedMacrosCounter();
		application.removeCaselessMacro(this);
		unCrossLinkDevicesAndApps(___device, application);
		if (___useCase != null)
			___useCase.removeMacro(this);

	}

	protected void unlinkFromDevice(CompoundDevice oldDdevice) {
		if (oldDdevice != null) {
			oldDdevice.removeKnownMacro(this);
			unCrossLinkDevicesAndApps(oldDdevice, ___application);
		}
	}

	protected void unlinkFromDevice(String oldName) {
		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		CompoundDevice oldDdevice = memory.getDeviceSet().getDevicesByName().get(oldName);
		unlinkFromDevice(oldDdevice);
	}

	protected void unlinkTransients() {
		unlinkFromDevice(getDevice());
		___device = null;
		unLinkFromApp(getApplication());
		___useCase.removeMacro(this);
		___useCase = null;
		___application = null;
	}

	private transient Macro preservedOriginal;

	public void preserveOriginal() {
		if (preservedOriginal != null//
				|| isNotViable())
			return;

		try {
			preservedOriginal = copy(false);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// preservedOriginal = copy();
		}

	}

	@Deprecated
	public void _____setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	private boolean isNotViable() {
		return this.actionType == null//
				|| this.deviceName == null//
				|| exeFile == null //
				|| scanCode <= 0 //
				|| getName() == null;

	}

	public Macro getPreservedOriginal() {
		return preservedOriginal;
	}

	@Override
	public int compareTo(Macro m) {
		if (m == null)
			return 1;
		int step1 = (int) (Math.signum(getCreationTime() - m.getCreationTime()));
		if (step1 == 0)
			step1 = (int) (Math.signum(getReplacementTime() - m.getReplacementTime()));
		if (step1 == 0)
			step1 = (int) (Math.signum(getDeletionTime() - m.getDeletionTime()));
		return step1;
	}

	public void setModifiersMask(int i) {
		Object old = this.modifiersMask;
		this.modifiersMask = i;
		notifyPropertyChange("modifiersMask", old, modifiersMask);

	}

	public int getModifiersMask() {
		return modifiersMask;
	}

	private boolean usingOSDReminder;

	public boolean isUsingOSDReminder() {
		return usingOSDReminder;
	}

	public void setUsingOSDReminder(boolean usingOSDReminder) {
		boolean usingOSDReminder2 = this.usingOSDReminder;
		this.usingOSDReminder = usingOSDReminder;
		notifyPropertyChange("usingOSDReminder", usingOSDReminder2, usingOSDReminder);
	}

	private String _OSDReminder;
	private long replacedBy;
	private long replaces;
	private int revision;

	public static ActionType getDefaultActionType() {
		return DEFAULT_ACTION_TYPE;
	}
	//
	// public static String getKeyDirection() {
	// return KEY_DIRECTION;
	// }

	public String getOSDReminder() {
		return _OSDReminder;
	}

	public long getReplaces() {
		return replaces;
	}

	public long getReplacedBy() {
		return replacedBy;
	}

	public void setOSDReminder(String _OSDReminder) {
		String _OSDReminder2 = this._OSDReminder;
		this._OSDReminder = _OSDReminder;
		notifyPropertyChange("_OSDReminder", _OSDReminder2, _OSDReminder);
	}

	public int getRevision() {
		return revision;
	}

	public Macro replacedOn(long l) {
		Macro pO = preservedOriginal;

		this.replaces = this.creationTime;
		this.creationTime = pO.replacedBy = l;

		preservedOriginal = null;
		revision++;

		preserveOriginal();
		pO.replacementTime = System.currentTimeMillis();
		return pO;
	}

	private File smallIconFile;
	private transient ImageIcon smallIcon;

	public File getSmallIconFile() {
		return smallIconFile;
	}

	public void setSmallIconFile(File smallIconFile) {
		File old = this.smallIconFile;
		this.smallIconFile = smallIconFile;
		this.smallIcon = null;

		notifyPropertyChange("smallIconFile", old, smallIconFile);
	}

	public ImageIcon getSmallIcon() {
		if (smallIcon == null && smallIconFile != null)
			smallIcon = new ImageIcon(smallIconFile.getPath());
		return smallIcon;
	}

	private transient boolean erased;

	public void setErased(boolean b) {
		if (erased = b) {
			_setUseCase(null);
			set___device(null);
			_setApplication(___application);
		}
	}

	public boolean isCaseless() {
		return actionType == ActionType.CHANGE_USE_CASE;
	}

	public boolean isAlwaysInVoked() {

		return actionType == ActionType.VIRTUAL_MODIFIER || ModifierMasks.isAlwaysInvoked(modifiersMask);
	}

	public boolean collides(Macro m2) {
		try {
			if (m2.isUnattached() || this.isUnattached())
				return false;

			return m2.getDevice() == getDevice() //
					&& m2.scanCode == this.scanCode //
					&& m2.getApplication() == getApplication()//

					&& (m2.isCaseless() //
							|| this.isCaseless() //
							|| m2.___useCase == this.___useCase //
							|| (m2.___useCase.getPriority() == this.___useCase.getPriority()) && //
									!(m2.___useCase.isManual() && this.___useCase.isManual()))

					&& (m2.modifiersMask == this.modifiersMask//
							|| m2.isAlwaysInVoked()//
							|| this.isAlwaysInVoked()//
					)

			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final String NO_DEVICE = "[NO DEVICE]";

	private class Reverter implements Runnable {

		private PropertyChangeEvent evt;
		private ArrayList<Macro> collisions;

		public Reverter(PropertyChangeEvent evt, ArrayList<Macro> collisions) {
			super();
			this.evt = evt;
			this.collisions = collisions;
		}

		private Runnable delayer = new Runnable() {

			@Override
			public void run() {
				Macro mm = Macro.this;
				MacroEditPanel.getInstance().setMacro(mm);
				IConfigurationAtom parent = mm;
				while (parent != null && !((parent = parent.getParent()) instanceof Macros))//
					;
				if (parent != null) {
					Macros macros = (Macros) parent;
					macros.setFilter(null, 0, null, 0);
				}

			}
		};

		@Override
		public void run() {
			int choice = JOptionPane.showConfirmDialog(null,
					"This macros has now acquired the same trigger" + "\nFingerprint of other " + collisions.size()
							+ " macros" + "\n" + "\nDo you ant to revert the change, or to de-activate the colliding"
							+ "\nMacros? \"OK\" to replace, \"Cancel\" to revert.",
					"Trigger Collision", JOptionPane.OK_CANCEL_OPTION);

			switch (choice) {
			case JOptionPane.CANCEL_OPTION:
				MacroEditPanel.getInstance().setMacro(null);
				Macro.this.setProperty(evt.getPropertyName(), evt.getOldValue(), false);

				SwingUtilities.invokeLater(delayer);
				break;
			default:
				for (Macro macro : collisions) {
					macro.linkToDevice(macro.deviceName = NO_DEVICE);
				}

			}
		}

		public Reverter setEvent(PropertyChangeEvent evt, ArrayList<Macro> collisions) {
			this.evt = evt;
			this.collisions = collisions;
			return this;
		}

	}

	private transient boolean observeMyself;

	private transient Reverter reverter;

	private boolean useDeferredOSD = true;

	public void _setUseDeferredOSD(boolean useDeferredOSD) {

		boolean useDeferredOSD2 = this.useDeferredOSD;
		this.useDeferredOSD = useDeferredOSD;
		notifyPropertyChange("useDeferredOSD", useDeferredOSD2, useDeferredOSD);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String lc = evt.getPropertyName().toLowerCase();
		if ((lc.contains("osd") || lc.equals("name") //
				|| lc.contains("icon") || lc.contains("enable"))//
		)
			return;

		IConfigurationAtom parent = this;
		while (parent != null && !((parent = parent.getParent()) instanceof Macros))//
			;
		if (parent != null) {
			Macros macros = (Macros) parent;

			ArrayList<Macro> collisions = macros.findCollisions(this);

			if (!collisions.isEmpty()) {
				macros.setFilter(deviceName, scanCode, exeFile, creationTime);
				// SwingUtilities.invokeLater
				(reverter == null ? (reverter = new Reverter(evt, collisions)) : reverter.setEvent(evt, collisions))
						.run();
				;
			}
		}
	}

	public boolean usesDeferredOSD() {
		return useDeferredOSD;
	}

	private transient String directCode;

	public String getDirectCode() {
		return directCode;
	}

	public void setDirectCode(String directCode) {
		this.directCode = directCode;
		UseCase useCase = get___useCase();
		if (useCase != null)
			useCase.directCodeChanged();
	}

	public String getCompiledDirectCode() {

		if (directCode == null || directCode.isEmpty())
			return "";

		if (getDevice() == null)
			return "";

		if (getDevice().getDevices().size() == 0)
			return "";

		if (scanCode == 0)
			return "";

		StringBuffer base = new StringBuffer(
				"\r\n     local funct;\r\n" + "      funct = " + getDirectCode() + "\r\n ");
		CompoundDevice device = getDevice();
		HashSet<Device> devices = device.getDevices();
		for (Device device2 : devices) {
			base.append("		JMA.addDirect('" + device2.getLuaMacrosId() + "', " + getScanCode() + ", funct ) \r\n");

		}

		return base.toString();
	}

	public void setDevice(String device) {
		String old = this.deviceName;
		this.deviceName = device;
		notifyPropertyChange("deviceName", old, deviceName);
	}

}
