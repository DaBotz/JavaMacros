package sgi.javaMacros.model.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;

import sgi.generic.debug.Debug;
import sgi.gui.configuration.ILabelProvider;
import sgi.javaMacros.controller.LuaEvent;
import sgi.javaMacros.model.JavaMacrosMemory;
import sgi.javaMacros.model.abstracts.JavaMacrosConfigAtom;
import sgi.javaMacros.model.internal.defaults.BasicUseCase;
import sgi.javaMacros.msgs.Messages;
import sgi.os.WindowData;

public abstract class UseCase extends JavaMacrosConfigAtom implements Comparable<UseCase>, ILabelProvider {

	public static UseCase ADD_CASE = new UseCase(Messages.M._$("usecase.addUseCase")) {
		@Override
		public UseCase doACopy() {
			return this;
		}

		@Override
		public long getCreationTime() {

			this.creationTime = -2;
			return super.getCreationTime();
		}

		@Override
		protected boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase) {
			// TODO Auto-generated method stub
			return false;
		}

	};

	public static final long BASIC_CASE_TIMESTAMP = -1;
	static {
		ADD_CASE.getCreationTime();
	}

	public static UseCase getBasic() {
		BasicUseCase basicUseCase = new BasicUseCase();
		basicUseCase.getCreationTime();
		return basicUseCase;
	}

	public static String makeFastMacroId(Object oldValue, int scanCode) {
		return oldValue + ":" + scanCode;
	}

	protected long creationTime;


	private boolean enabled;

	private transient Hashtable<String, ArrayList<Macro>> fastMacros;

	private transient Hashtable<CompoundDevice, Hashtable<Integer, ArrayList<Macro>>> knownMacros;

	private transient int ___macroCounter = 0;

	private UseCasePriority priority = UseCasePriority.LOW;

	private transient PropertyChangeListener updateFastMacros = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() instanceof Macro) {
				Macro macro = (Macro) evt.getSource();
				String propertyName = evt.getPropertyName();
				if (Macro.DEVICE_NAME.equals(propertyName)) {
					int scanCode = macro.getScanCode();
					getFastMacros(evt.getOldValue(), scanCode).remove(macro);
					addFastMacro(macro);
				}
				if (Macro.SCAN_CODE.equals(propertyName)) {
					getFastMacros(macro.getDeviceName(), ((Number) evt.getOldValue()).intValue()).remove(macro);
					addFastMacro(macro);
				}
			}
		}

	};

	/**
	 * @deprecated Reserved for serialization only
	 */
	public UseCase() {
		creationTime = System.currentTimeMillis();
	}

	public UseCase(String name) {
		setName(name);
		creationTime = System.currentTimeMillis();
	}

	@Deprecated
	public void ____________setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}



	protected void addFastMacro(Macro macro) {
		getFastMacros(macro.getDeviceName(), macro.getScanCode()).add(macro);
		directCodeChanged();
	}

	public void addMacro(Macro macro) {

		CompoundDevice device = macro.getDevice();

		if (device == null)
			return;
		Hashtable<Integer, ArrayList<Macro>> hashtable = getKnownMacros().get(device);
		if (hashtable == null) {
			hashtable = new Hashtable<>();
			getKnownMacros().put(device, hashtable);
		}
		ArrayList<Macro> list = hashtable.get(macro.getScanCode());
		if (list == null) {
			list = new ArrayList<>();
			hashtable.put(macro.getScanCode(), list);
		}
		list.add(macro);
		___macroCounter++;
		addFastMacro(macro);
		macro.addPropertyChangeListener(updateFastMacros);
		directCodeChanged();
		// notifyPropertyChange("transient:added_macro", null, macro);
	}

	@Override
	public int compareTo(UseCase o) {
		if (o.isBasic())
			return -1;

		if (isManual() && !o.isManual())
			return 1;

		int rw = o.getPriority().ordinal() - getPriority().ordinal();

		if (rw != 0)
			return rw;

		return String.valueOf(getName()).compareTo(o.getName());
	}

	public abstract UseCase doACopy();

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof UseCase) {
			UseCase uc = (UseCase) obj;
			return uc.getCreationTime() == getCreationTime();
		}
		return false;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public Collection<String> getDirectCodesx() {
		return directCodes; 
	}

	public ArrayList<Macro> getFastMacro(String id) {
		ArrayList<Macro> arrayList = getFastMacros().get(id);
		if (arrayList == null) {
			arrayList = new ArrayList<>();
			getFastMacros().put(id, arrayList);
		}
		return arrayList;
	}

	public Hashtable<String, ArrayList<Macro>> getFastMacros() {
		if (fastMacros == null)
			fastMacros = new Hashtable<>();
		return fastMacros;
	}

	protected ArrayList<Macro> getFastMacros(Object deviceName, int scanCode) {
		return getFastMacro(makeFastMacroId(deviceName, scanCode));
	}

	public Hashtable<CompoundDevice, Hashtable<Integer, ArrayList<Macro>>> getKnownMacros() {
		if (knownMacros == null)
			knownMacros = new Hashtable<>();
		return knownMacros;
	}

	@Override

	public String getLabel() {

		return getName()

				+ " ( "//
				+ getPriority().ordinal()//

				+ " )";

	}

	public int getMacroCounter() {
		return ___macroCounter;
	}

	public UseCasePriority getPriority() {
		if (priority == null)
			priority = UseCasePriority.LOW;
		return priority;
	}

	@Override
	public String getTooltip() {
		Messages m = Messages.M;

		String rv = "<H4>Usecase:</h4><h3>" + getName() + "</h23\n" + "\n"//
				+ "<i><font face=\"monotype\">" //
				+ "  Priority:  " + m._$("usecase.priorities." + getPriority().name()) + "\n";

		rv += moreTooltipData();

		rv += "</font></i>\n";//

		return Messages.htmlSwing(rv);
	}

	private transient boolean directCodesFileUnclean= true;

	private File directCodesFile;
	private transient ArrayList<String> directCodes= new ArrayList<>(); 
	public boolean hasDirectCodes() {
		if( directCodesFileUnclean) 
			scanForDirectCodes();
		
		boolean b = directCodes != null && directCodes.size() > 0;
		
		if (directCodesFileUnclean) {
			this.directCodesFile = writeDirectCodes();
			directCodesFileUnclean = false;
		}
		
		return b;

	}

	private File writeDirectCodes() {

		ApplicationForMacros ff = (ApplicationForMacros) getParent().getParent();


		String exeFile = ff.isReal()? ff.getExeFile():"[EVERY_APPLICATION]";
		String baseName = new File(exeFile).getName()+ "["
				+ exeFile.hashCode()+"]" 
				+"."+ getName().replaceAll("\\W", "_") 
						+ "["+getCreationTime() + "]";

		System.out.println("Write codes invoked for "+ baseName);
		File folder = geetCreateDirectCodesFolder();
		File output = new File(folder, baseName + "[" + this.getCreationTime() + "].lua");

		StringBuffer bf2 = new StringBuffer();
		int k = 0;
		bf2.append("--[[\n" ////$NON-NLS-1$
				+ " Automatically generated file: this file can and will be erased and replaced by javamacros \n"); ////$NON-NLS-1$ 
		bf2.append("     the very next time any of the macros it that contributes to it is modified \n"  ////$NON-NLS-11$
				+ "       Edit at your risk. \n" ////$NON-NLS-1$
				+ "\n]]");; ////$NON-NLS-1$
		
		Collection<String> directCodes = this.getDirectCodesx();
		for (Iterator<String> iterator = directCodes.iterator(); iterator.hasNext();) {
			bf2.append("\n\n");////$NON-NLS-1$
			bf2.append(iterator.next());
			k++;
		}

		String string2 = bf2.toString();
		boolean writeRequired = true;

		String start = "-- " + bf2.hashCode() + "::" + k + " --"; ////$NON-NLS-1$ ////$NON-NLS-2$ ////$NON-NLS-3$
		
		try {
			if (output.isFile()) {
				BufferedReader reader = new BufferedReader(new FileReader(output));
				String readLine = reader.readLine();
				reader.close();
				if (readLine != null && readLine.trim().equals(start)) {
					writeRequired = false;
				}

			}

			if (writeRequired) {
				FileWriter fw = new FileWriter(output);
				fw.write(start.toCharArray());
				fw.write("\n\n");////$NON-NLS-1$
				fw.write(string2.toCharArray());
				fw.close();
			}

		} catch (IOException e1) {
			Debug.info(e1, 0, 5);
		}

		return output;

	}

	private static File folder;

	public static File geetCreateDirectCodesFolder() {
		if (folder != null && folder.isDirectory())
			return folder;

		JavaMacrosMemory memory = JavaMacrosMemory.instance();
		folder = new File(memory.getSaveFile().getParentFile(), "directCodes");////$NON-NLS-1$
		if (!folder.isDirectory())
			folder.mkdirs();
		return folder;
	}

	public File getDirectCodesFile() {
		return directCodesFile;
	}

	public void setDirectCodesFile(File directCodesFile) {
		this.directCodesFile = directCodesFile;
	}

	public boolean isActivable(String string, String lastTitle) {
		return false;
	}

	public boolean isBasic() {
		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isManual() {
		return false;
	}

	protected String moreTooltipData() {
		return "";
	}

	public final boolean processMacroRequest(LuaEvent event, WindowData activeWindow, UseCase currentUseCase) {
		if (rulesMatch(activeWindow, currentUseCase))
			return unconditionallyProcessMacroRequest(event);

		return false;
	}

	public void directCodeChanged() {	
		directCodesFileUnclean= true; 
	}

	
	private void scanForDirectCodes() {
		ArrayList<String> candidates= new ArrayList<>(128);  
		Collection<ArrayList<Macro>> values = getFastMacros().values(); 
		for (ArrayList<Macro> arrayList : values) {
			for (Macro macro : arrayList) {
				String directCode = macro.getDirectCode();
				if( ! (directCode== null || directCode.isEmpty() )){
					candidates.add(macro.getCompiledDirectCode()); 
				}
			}
		}
		if( candidates.size() != directCodes.size()) {
			directCodes= candidates; 
			directCodesFileUnclean= true; 
			return ; 
		}
		
		ListIterator<String> l1 = candidates.listIterator();
		ListIterator<String> l2 = directCodes.listIterator();
		while( l1.hasNext() && l2.hasNext()) {
			if( ! areTheseEquals(l1.next(),l2.next())) {
				directCodes = candidates;
				directCodesFileUnclean= true; 
				return ; 
			}
		}
		directCodesFileUnclean= false; 
	}

	protected void removeFastMacros(Object deviceName, int scanCode) {
		getFastMacros().remove(makeFastMacroId(deviceName, scanCode));

		scanForDirectCodes(); 
	}

	public void removeMacro(Macro macro) {
		macro.removePropertyChangeListener(updateFastMacros);
		directCodeChanged();
		IDevice device = macro.getDevice();
		if (device == null)
			return;
		Hashtable<Integer, ArrayList<Macro>> hashtable = getKnownMacros().get(device);
		if (hashtable == null)
			return;

		int scanCode = macro.getScanCode();

		ArrayList<Macro> list = hashtable.get(scanCode);
		if (list == null)
			return;
		list.remove(macro);
		___macroCounter--;

		if (list.size() == 0)
			hashtable.remove(scanCode);
		if (hashtable.size() == 0)
			getKnownMacros().remove(device);
		ArrayList<Macro> fastMacros2 = getFastMacros(macro.getDeviceName(), macro.getScanCode());
		fastMacros2.remove(macro);
		if (fastMacros2.isEmpty())

			removeFastMacros(macro.getDeviceName(), macro.getScanCode());
		// notifyPropertyChange("transient:removed_macro", null, macro);
	}

	protected abstract boolean rulesMatch(WindowData activeWindow, UseCase currentUseCase);

	public void setEnabled(boolean enabled) {
		boolean enabled2 = this.enabled;
		this.enabled = enabled;
		notifyPropertyChange("enabled", enabled2, enabled);
	}

	public void setPriority(UseCasePriority priority) {
		UseCasePriority priority2 = this.priority;
		this.priority = priority;
		super.notifyPropertyChange("priority", priority2, priority);
	}

	protected boolean unconditionallyProcessMacroRequest(LuaEvent event) {
		event.addUseCase(this);

		ArrayList<Macro> arrayList = getFastMacros().get(event.fastMacroID());
		if (arrayList == null)
			return false;
		if (arrayList.isEmpty())
			return false;
		for (Macro macro : arrayList) {
			if (macro.executeMacro(event))
				return true;
		}

		return false;
	}

	static HashMap<String, Boolean> configurabilityOverrides;

	@Override
	public HashMap<String, Boolean> getConfigurabilityOverrides() {
		if (configurabilityOverrides == null) {
			configurabilityOverrides = new HashMap<>();
			configurabilityOverrides.put("directCodesFile", false);

		}
		return configurabilityOverrides;
	}

	public ApplicationForMacros getApplication() {
		try {
			return (ApplicationForMacros) getParent().getParent();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getExeFile() {
		try {
			return ((ApplicationForMacros) getParent().getParent()).getExeFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
