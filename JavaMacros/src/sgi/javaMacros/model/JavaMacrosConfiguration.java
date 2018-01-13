
package sgi.javaMacros.model;

import java.io.File;

import sgi.gui.ConfigPanelCreator;
import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;
import sgi.javaMacros.model.enums.FillLevels;

public class JavaMacrosConfiguration extends JavaMacrosMemoryParcel {

	private static final int MINIMUM_DELAY_OPENING_GUI = 500;
	private static final int MAXIMUM_DELAY_FOR_OPENING_GUI = 5000;
	public static OSDSettings OSD;
	private boolean askingNamesForNewDevices;
	private FillLevels fillMode;
	private int delayBetweenKeys;
	
	private boolean javaMacrosStartsMinimized;
	private boolean javaMacrosMinimizesToTray;

	private long deviceCheckInterval;

	// private long delayAfterAnAutoRestart;
	private boolean grabNewFoundDeviceEvents;

	private boolean useDoubleBuffering;
	private int delayOfGuiOpenAfterStart;

	private OSDSettings _osdSettings;

	private int keyHoldingTime;
	
	private boolean startUpDisplayToBeShown; 

	

	public boolean isStartUpDisplayToBeShown() {
		if( startUpHasEnded() ) return BootData.instance().isStartUpDisplayToBeShown();  
		return startUpDisplayToBeShown;
	}


	public void setStartUpDisplayToBeShown(boolean startUpDisplayToBeShown) {

		if( startUpHasEnded() )   BootData.instance().setStartUpDisplayToBeShown(startUpDisplayToBeShown); 
		this.startUpDisplayToBeShown = startUpDisplayToBeShown;
	}


	@Override
	protected void initializeDefaultValues() {
		lUaMacrosSettings = new LUaMacrosSettings(32000);
		lUaMacrosSettings.setLuaMacrosKeptAlive(true);
		lUaMacrosSettings.setLuaMacrosMinimizingToTray(true);
		lUaMacrosSettings.setLuaMacrosStartingMinimized(true);

		macroExecutionSettings = new MacroExecutionSettings();
		macroExecutionSettings.init();

		_osdSettings = new OSDSettings();

		deviceCheckInterval = 5000;
		// delayAfterAnAutoRestart = 3000;

		grabNewFoundDeviceEvents = true;
		delayOfGuiOpenAfterStart = 500;

		askingNamesForNewDevices = true;
		fillMode = FillLevels.EVERY_APPLICATION;
		colorsSettings = new JavaMacrosColors();
		colorsSettings.loadPreSets();
		javaMacrosStartsMinimized = true;
		javaMacrosMinimizesToTray = true;
		keyHeldTime = this.keyHoldingTime = 100;
		delayBetweenKeys = 300;

	}

	
	public MacroExecutionSettings getMacroExecutionSettings() {
		return macroExecutionSettings;
	}

	public int getDelayBetweenKeys() {
		return delayBetweenKeys;
	}

	public long getMaxNumberOfDeletedMacros() {
		return getMacroExecutionSettings().getMaxNumberOfDeletedMacros();
	}

	public boolean isJavaMacrosStartsMinimized() {
		return javaMacrosStartsMinimized;
	}

	public boolean isJavaMacrosMinimizesToTray() {
		return javaMacrosMinimizesToTray;
	}

	public JavaMacrosColors getColorsSettings() {
		return colorsSettings;
	}

	public static JavaMacrosConfiguration instance() {
		if (root == null)
			root = new JavaMacrosConfiguration();

		return root;
	}

	private JavaMacrosConfiguration() {
		ConfigPanelCreator.setDoubleBufferingUse(isUseDoubleBuffering());
		colorsSettings.alignStatics();
		OSD = get_osdSettings();
		relink(null);
		if (lUaMacrosSettings.getLuaMacrosLocation() == null //
				|| !lUaMacrosSettings.getLuaMacrosLocation().isFile())
			lUaMacrosSettings.setLuaMacrosLocation(LuaMacrosFinder.findLuaMacros());
		
		this.startUpDisplayToBeShown= BootData.instance().isStartUpDisplayToBeShown();
		
		//setConfigurableProperty(name, flag);
		endStartup();
	}


	public OSDSettings get_osdSettings() {
		return _osdSettings;
	}

	public void set_osdSettings(OSDSettings osdSettings) {
		this._osdSettings = osdSettings;
	}

	private JavaMacrosColors colorsSettings;
	private MacroExecutionSettings macroExecutionSettings;
	private LUaMacrosSettings lUaMacrosSettings;

	public LUaMacrosSettings getlUaMacrosSettings() {
		return lUaMacrosSettings;
	}


	public boolean doGrabNewFoundDeviceEvents() {
		return grabNewFoundDeviceEvents;
	}

	public boolean isUseDoubleBuffering() {
		return useDoubleBuffering;
	}

	public void setUseDoubleBuffering(boolean useDoubleBuffering) {
		setProperty("useDoubleBuffering", useDoubleBuffering);
	}

	public File getLuaMacrosLocation() {
		return lUaMacrosSettings.getLuaMacrosLocation();
	}

	private static JavaMacrosConfiguration root;
	private static int keyHeldTime;

	public long getServerPort() {
		return lUaMacrosSettings.getServerPort();
	}

	public void setServerPort(int serverPort) {
		lUaMacrosSettings.setServerPort(serverPort);
	}

	public boolean isLuaMacrosStartingMinimized() {
		return lUaMacrosSettings.isLuaMacrosStartingMinimized();
	}

	public boolean isLuaMacrosMinimizingToTray() {
		return lUaMacrosSettings.isLuaMacrosMinimizingToTray();
	}

	@Override
	public String getFileName() {
		return getClass().getSimpleName() + ".xml";
	}

	public boolean isInconherent() {
		File luaMacrosLocation = getLuaMacrosLocation();
		return luaMacrosLocation == null || !luaMacrosLocation.isFile() || getServerPort() <= 0;
	}

	public long getDeviceCheckInterval() {
		return deviceCheckInterval;
	}

	public boolean isLuaMacrosKeptAlive() {
		return lUaMacrosSettings.isLuaMacrosKeptAlive();
	}

	public void setLuaMacrosKeptAlive(boolean luaMacrosKeptAlive) {

		lUaMacrosSettings.setLuaMacrosKeptAlive(luaMacrosKeptAlive);
	}

	public int getDelayOfGuiOpenAfterStart() {

		return delayOfGuiOpenAfterStart;
	}

	public void setDelayOfGuiOpenAfterStart(int delayOfGuiOpenAfterStart) {
		delayOfGuiOpenAfterStart = Math.max(MINIMUM_DELAY_OPENING_GUI, delayOfGuiOpenAfterStart);
		delayOfGuiOpenAfterStart = Math.min(MAXIMUM_DELAY_FOR_OPENING_GUI, delayOfGuiOpenAfterStart);

		int olddelayOfGuiOpenAfterStart = this.delayOfGuiOpenAfterStart;
		this.delayOfGuiOpenAfterStart = delayOfGuiOpenAfterStart;
		notifyPropertyChange("delayOfGuiOpenAfterStart", olddelayOfGuiOpenAfterStart, delayOfGuiOpenAfterStart);
	}

	public static int getKeyHeld() {

		return keyHeldTime;
	}


	public int getKeyHoldingTime() {
		return keyHoldingTime;
	}

	public void setKeyHoldingTime(int keyHoldingTime) {
		int keyHoldingTime2 = this.keyHoldingTime;
		keyHeldTime = this.keyHoldingTime = keyHoldingTime;
		notifyPropertyChange("keyHoldingTime", keyHoldingTime2, keyHoldingTime);

	}

	public boolean isAskingNamesForNewDevices() {
		return askingNamesForNewDevices;
	}

	public void setAskingNamesForNewDevices(boolean askingNamesForNewDevices) {
		boolean old = this.askingNamesForNewDevices;
		this.askingNamesForNewDevices = askingNamesForNewDevices;
		notifyPropertyChange("askingNamesForNewDevices", old, askingNamesForNewDevices);
	}

	public FillLevels isFillMode() {
		return fillMode;
	}

	public void setFillMode(FillLevels useFillMode) {
		FillLevels old = this.fillMode;
		this.fillMode = useFillMode;
		notifyPropertyChange("fillMode", old, useFillMode);
	}

}
