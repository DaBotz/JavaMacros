package sgi.javaMacros.model.internal;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Icon;

import sgi.configuration.ConfigAtomTreeSet;
import sgi.configuration.IConfigurationAtom;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.abstracts.JavaMacrosConfigAtom;
import sgi.javaMacros.model.internal.defaults.AnyApplicationMacros;
//import sgi.javaMacros.os.Application;
//import sgi.javaMacros.os.windows.Application;
import sgi.javaMacros.ui.RichFileChooser;
import sgi.os.Application;
import sgi.os.WindowData;

public class ApplicationForMacros extends JavaMacrosConfigAtom {

	@SuppressWarnings("deprecation")
	private static ApplicationForMacros ANY = new AnyApplicationMacros();

	public static ApplicationForMacros getANY() {
		return ANY;
	}

	public boolean isReal() {
		return true;
	}

	protected static void setANY(ApplicationForMacros aNY) {
		ApplicationForMacros.ANY = aNY;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		boolean old = this.enabled;
		if (enabled != old) {
			this.enabled = enabled;
			notifyPropertyChange("enabled", old, enabled);
			if (enabled && !old)
				setIgnored(false);
		}
	}

	public static class DeviceCounters extends Hashtable<CompoundDevice, Integer> {
		private static final long serialVersionUID = 4124892647957637872L;
	}

	private transient Icon bigIcon;
	private transient Icon icon;

	private transient int ___associatedMacrosCounter;

	public int get___associatedMacrosCounter() {
		return ___associatedMacrosCounter;
	}

	public void incrementAssociatedMacrosCounter() {
		___associatedMacrosCounter++;
	}

	public void decrementAssociatedMacrosCounter() {
		___associatedMacrosCounter--;
	}

	public boolean doesHaveMacros() {
		return ___associatedMacrosCounter > 0;
	}

	private transient boolean requiredErase;

	public boolean isRequiredErase() {
		return requiredErase;
	}

	public void setRequiredErase(boolean requiredErase) {
		this.requiredErase = requiredErase;
	}

	@Override
	public void relink(IConfigurationAtom parent) {

		// UseCases useCases2 = getUseCases();
		// for (UseCase useCase : useCases2) {
		// if (useCase.isBasic()) {
		//
		// super.relink(parent);
		// return;
		// }
		// }
		// useCases2.add(UseCase.getBasic());
		if (currentUseCaseCreationTime == 0)
			currentUseCaseCreationTime = -1;
		super.relink(parent);
	}

	private UseCases useCases;
	private boolean enabled;

	private transient UseCase ___currentUseCase;

	public void set___currentUseCase(UseCase currentUseCase) {
		this.___currentUseCase = currentUseCase == null ? UseCase.getBasic() : currentUseCase;
		long creationTime = currentUseCase.getCreationTime();
		setCurrentUseCaseCreationTime(creationTime);
	}

	protected void setCurrentUseCaseCreationTime(long creationTime) {
		long old = this.currentUseCaseCreationTime;
		this.currentUseCaseCreationTime = creationTime;
		notifyPropertyChange("currentUseCaseCreationTime", old, currentUseCaseCreationTime);
	}

	private long currentUseCaseCreationTime;
	private String exeFile;

	@Deprecated
	protected void setExeFile(String exeFile) {
		this.exeFile = exeFile;
	}

	private ConfigAtomTreeSet<WindowClass> windowClasses;
	private transient boolean absent;

	public void setAbsent(boolean absent) {
		this.absent = absent;
	}

	public ApplicationForMacros() {
		super();
	}

	@Override
	public int hashCode() {
		return getExeFile() != null ? getExeFile().hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (obj instanceof ApplicationForMacros) {
			ApplicationForMacros o2 = (ApplicationForMacros) obj;
			if (getExeFile() != null)
				return getExeFile().equalsIgnoreCase(o2.getExeFile());
		}
		return super.equals(obj);
	}

	public Icon getBigIcon() {
		if (bigIcon == null) {
			bigIcon = RichFileChooser.getFileIcon(new File(exeFile), RichFileChooser.BIG);
		}
		return bigIcon;
	}

	public final String getExeFile() {
		return exeFile;
	}

	public Icon getIcon() {
		if (icon == null) {
			icon = RichFileChooser.getFileIcon(new File(exeFile), RichFileChooser.SMALL);
		}
		return icon;
	}

	public UseCase get___currentUseCase() {
		if (___currentUseCase == null || ___currentUseCase.getCreationTime() != currentUseCaseCreationTime)
			___currentUseCase = getUseCases().getByCreationTime(currentUseCaseCreationTime);

		return ___currentUseCase;
	}

	public ConfigAtomTreeSet<WindowClass> getWindowClasses() {
		if (windowClasses == null)
			windowClasses = new ConfigAtomTreeSet<>();
		return windowClasses;
	}

	public boolean mayBePurged() {

		return !isEnabled() && !isIgnored() && !doesHaveMacros() && //
				(getUseCases().size() < 1 || getUseCases().size() == 1 && getUseCases().iterator().next().isBasic());
	}

	public UseCases getUseCases() {
		if (useCases == null) {
			useCases = new UseCases();
			useCases.add(UseCase.getBasic());
		} else if (useCases.size() == 0) {
			useCases.add(UseCase.getBasic());
		}

		useCases.setParent(this);

		return useCases;
	}

	public ApplicationForMacros(Application original) {
		this();
		this.exeFile = original.getExeFile();
		setName(original.getName());

		ConfigAtomTreeSet<WindowClass> wc = getWindowClasses();
		List<String> windowClasses2 = original.getWindowClasses();
		for (String string : windowClasses2) {
			wc.add(new WindowClass(string));
		}
		wc.setParent(this);
	}

	@Override
	public void notifyPropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (!areTheseEquals(oldValue, newValue))
			super.notifyPropertyChange(propertyName, oldValue, newValue);
	}

	public String getLabel() {

		return getName() + " ("//
		// TODO ADD STUFF
				+ ")";

	}

	public boolean processMacroRequest(LuaEvent event, WindowData activeWindow) {
		if (!enabled)
			return false;

		return getUseCases().processMacroRequest(event, activeWindow, get___currentUseCase())
				|| processCaselessMacros(event);
	}

	private boolean processCaselessMacros(LuaEvent event) {
		if (!(___caselessMacros == null || ___caselessMacros.length == 0))
			for (Macro macro : ___caselessMacros) {

				if (macro.executeMacro(event))
					return true;
			}

		return false;
	}

	public boolean isAbsent() {

		return this.absent;
	}

	private transient DeviceCounters ___usedDeviceCounter;

	public DeviceCounters get___usedDeviceCounter() {
		if (___usedDeviceCounter == null)
			___usedDeviceCounter = new DeviceCounters();
		return ___usedDeviceCounter;
	}

	public void addDevice(CompoundDevice mydevice) {
		DeviceCounters usedDevices2 = get___usedDeviceCounter();
		Integer integer = usedDevices2.get(mydevice);
		if (integer == null)
			usedDevices2.put(mydevice, 1);
		else {
			usedDevices2.put(mydevice, integer.intValue() + 1);
		}

		notifyPropertyChange("transient:added_device", null, mydevice);

	}

	public void removeDevice(CompoundDevice mydevice) {
		DeviceCounters usedDevices2 = get___usedDeviceCounter();
		Integer integer = usedDevices2.get(mydevice);

		if (integer == null)
			return;
		else {
			int value = integer.intValue() - 1;
			if (value > 0)
				usedDevices2.put(mydevice, value);
			else
				usedDevices2.remove(mydevice);

		}

		notifyPropertyChange("transient:removed_device", null, mydevice);

	}

	private transient int modifiersMask;

	public void setModifiersMask(int modifiersMask) {
		this.modifiersMask = modifiersMask;
	}

	public int getModifiersMask() {
		return modifiersMask;
	}

	private transient Macro[] ___caselessMacros;
	private boolean ignored;

	public void addCaselessMacro(Macro macro) {
		if (___caselessMacros == null)
			___caselessMacros = new Macro[] { macro };
		else {
			HashSet<Macro> aL = new HashSet<Macro>(Arrays.asList(___caselessMacros));
			aL.add(macro);
			Macro[] _mcr = new Macro[aL.size()];
			aL.toArray(_mcr);
			___caselessMacros = _mcr;
		}
	}

	public void removeCaselessMacro(Macro macro) {
		if (___caselessMacros == null)
			return;
		if (___caselessMacros.length == 1 && ___caselessMacros[0] == macro) {
			___caselessMacros = null;
			return;
		}

		HashSet<Macro> aL = new HashSet<Macro>(Arrays.asList(___caselessMacros));
		aL.remove(macro);
		Macro[] _mcr = new Macro[aL.size()];
		aL.toArray(_mcr);
		___caselessMacros = _mcr;

	}

	public boolean isIgnored() {
		return this.ignored;
	}

	public void setIgnored(boolean ignored) {
		boolean ignored2 = this.ignored;
		this.ignored = ignored;

		if (ignored && !ignored2)
			setEnabled(false);
		notifyPropertyChange("ignored", ignored2, ignored);
	}

}
