package sgi.javaMacros.model.internal;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Icon;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.ui.RichFileChooser;

public class Application extends ConfigAtom {

	private static final String _0_STANDARD = "0 - Standard";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private transient Icon bigIcon;
	private transient Icon icon;
	private transient boolean hasMacros;
	private transient boolean requiredErase;

	public boolean isRequiredErase() {
		return requiredErase;
	}

	public void setRequiredErase(boolean requiredErase) {
		this.requiredErase = requiredErase;
	}

	private TreeSet<String> useCases;
	private boolean enabled;
	

	private String currentUseCase;
	private String exeFile;
	private TreeSet<String> windowClasses;

	public void setBigIcon(Icon bigIcon) {
		this.bigIcon = bigIcon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Application() {
		super();
	}

	@Override
	public int hashCode() {
		return getExeFile().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (obj instanceof Application) {
			Application o2 = (Application) obj;
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

	public String getCurrentUseCase() {
		return currentUseCase;
	}

	public Set<String> getWindowClasses() {
		if (windowClasses == null)
			windowClasses = new TreeSet<>();
		return windowClasses;
	}

	public final void setExeFile(String pathToExe) {

		String oldExe = this.exeFile;
		this.exeFile = pathToExe;

		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "systemId", oldExe));

	}

	public void setCurrentUseCase(String systemId) {

		String dd2 = this.currentUseCase;
		this.currentUseCase = systemId;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM, this, "systemId", dd2));

	}
	
	public boolean mayBePurged() {
		
		return ! isEnabled() && ! hasMacros && (useCases== null || useCases.size()==0 ||useCases.size()== 1 && useCases.contains(_0_STANDARD));
	}

	public TreeSet<String> getUseCases() {
		if (useCases == null) {
			useCases = new TreeSet<>();
			useCases.add(_0_STANDARD);
		}

		return useCases;
	}

}
