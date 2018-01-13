package sgi.javaMacros.model;

import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.model.lists.DeviceSet;

public class JavaMacrosMemory extends JavaMacrosMemoryParcel {

	private transient Devices devices;
	private transient Applications applications;
	private transient Macros macros; 
	private transient Macros erasedMacros; 
	private transient Macros replacedMacros;
	private boolean unlimitedTrees; 

	/**
	 * @deprecated
	 */
	private JavaMacrosMemory() {
		super();
		relink(null);
		devices.endStartup();
		applications.endStartup();
		macros.endStartup();
		endStartup();
	}

	@Override
	public void loadFromFile() {
		devices = new Devices();
		applications = new Applications();
		macros= new Macros(); 

	}

	@Override
	public void storeToFile() {
		devices.storeToFile();
		applications.storeToFile();
		macros.storeToFile();

		ifNotNullStore(erasedMacros);
		ifNotNullStore(replacedMacros);
	}

	public Macros getMacros() {
		return macros;
	}

	public Macros getErasedMacros() {
		if(erasedMacros== null )erasedMacros= new ErasedMacros(); 
		return erasedMacros;
	}

	public Macros getReplacedMacros() {
		if(replacedMacros== null )replacedMacros= new ReplacedMacros(); 
		return replacedMacros;
	}

	protected void ifNotNullStore(Macros eMacros) {
		if( eMacros != null ) {
			eMacros.storeToFile();
		}
	}

	public Applications getApplications() {
		return applications;
	}

	private static transient JavaMacrosMemory root;

	public static JavaMacrosMemory instance() {
		if (root == null) {
			root = new JavaMacrosMemory();
			root.getMacros().getList().createTransientLinks(); 
		}
		return root;
	}

	@Override
	protected void initializeDefaultValues() {

	}

	public ApplicationSet getApplicationSet() {
		return getApplications().getSet();
	}

	public DeviceSet getDeviceSet() {
		return getDevices().getSet();
	}

	
	public Devices getDevices() {
		return devices;
	}

	public void eraseMacro(Macro macro2) {
		if(macro2 == null) return;
		getMacros().getList().remove(macro2);
		macro2.setDeletionTime(System.currentTimeMillis());
		getErasedMacros().getList().add(macro2);

		
	}

	public boolean useUnLimitedTrees() {

		return unlimitedTrees;
	}


}
