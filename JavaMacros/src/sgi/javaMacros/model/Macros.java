package sgi.javaMacros.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import sgi.generic.serialization.XmlMemorizer;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.internal.Macro;
import sgi.javaMacros.model.internal.UseCase;
import sgi.javaMacros.model.lists.MacrosList;

public class Macros extends JavaMacrosMemoryParcel implements Cloneable {

	private MacrosList list;

	@Override
	protected void initializeDefaultValues() {
		list = new MacrosList();
		list.setParent(this);
	}

	public Macros() {
		super();
		getList().sort(new Comparator<Macro>() {
			@Override
			public int compare(Macro o1, Macro o2) {
				// TODO Auto-generated method stub
				if (o1 != null)
					return o1.compareByName(o2);
				else if (o2 != null)
					return -1;

				return 0;
			}
		});
	}

	@Override
	public void storeToFile() {
		if (slatedForReplacement.size() > 0) {
			Iterator<Macro> iterator = slatedForReplacement.iterator();
			MacrosList list2 = JavaMacrosMemory.instance().getReplacedMacros().getList();
			long time = System.currentTimeMillis();

			while (iterator.hasNext()) {
				Macro macro = (Macro) iterator.next();
				if (macro.getPreservedOriginal() != null) {
					list2.add(macro.replacedOn(++time));
					iterator.remove();
				}
			}
		}
		super.storeToFile();
	}

	private transient HashSet<Macro> slatedForReplacement = new HashSet<>();

	@Override
	public void propagatePropertyChange(PropertyChangeEvent evt) {
		if (!evt.getPropertyName().startsWith("transient") && evt.getSource() instanceof Macro) {

			Macro m = (Macro) evt.getSource();
			if (m.getPreservedOriginal() == null) {
				// System.err.println("Macro " + m.getName() + " has been modified but no
				// original can be found!!!!");
				// m.preserveOriginal();
			} else {
				slatedForReplacement.add(m);
			}
		}
		super.propagatePropertyChange(evt);
	}

	public MacrosList getList() {
		return list;
	}

	private transient Macros myClone;
	private transient File lastAncientLoaded;

	public Macros loadAncients() {
		File[] brothersInAttic = getBrothersInAlternateFolder();
		Arrays.sort(brothersInAttic, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return o2.getName().compareTo(o2.getName());
			}
		});

		if (brothersInAttic == null || brothersInAttic.length == 0)
			return null;
		if (myClone == null) {
			try {
				myClone = (Macros) this.clone();
				myClone.setParent(null);
				myClone.setListeners(null);
				myClone.setSaveFile(null);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}

		if (lastAncientLoaded == null) {
			lastAncientLoaded = brothersInAttic[0];
		} else {
			int i = 0;
			int end = brothersInAttic.length - 1;

			for (; i < end && !(brothersInAttic[i].equals(lastAncientLoaded)); i++)
				;

			File file = brothersInAttic[i + 1];
			if (file.equals(lastAncientLoaded))

				return myClone;
			lastAncientLoaded = file;
		}

		XmlMemorizer memorizer = new XmlMemorizer(Macros.class.getPackage().getName());
		memorizer.setData(myClone);
		memorizer.setSaveFile(lastAncientLoaded);
		memorizer.load();
		TreeSet<Macro> cleaner = new TreeSet<>();
		cleaner.addAll(myClone.getList());
		myClone.list = new MacrosList();
		myClone.list.addAll(cleaner);

		return myClone;
	}

	public void setFilter(String deviceName, int scanCode, String exeFile, long creationTime) {
		Filter newFilter = new Filter();

		newFilter.deviceName = deviceName;
		newFilter.scanCode = scanCode;
		newFilter.exeFileName = exeFile;
		newFilter.useCaseTimestamp = creationTime;
		notifyPropertyChange("transient:macros_filter", this.filter, this.filter = newFilter);
	}

	public ArrayList<Macro> findCollisions(Macro m) {
		MacrosList list2 = getList();
		ArrayList<Macro> copy = new ArrayList<>(32);

	
		for (Macro macro : list2) {
			if (macro != m && m.collides(macro))
				copy.add(macro);
		}

		return copy;

	}
	
	
	private class Filter {
		String deviceName;
		String exeFileName;
		int scanCode = -1;
		long useCaseTimestamp;

		public boolean accept(Macro macro) {
			if (!(deviceName == null //
					|| deviceName.equals(macro.getDeviceName())))
				return false;
			if (!(exeFileName == null //
					|| exeFileName.equals(macro.getExeFile())))
				return false;
			if (!(scanCode < 1 //
					|| scanCode == macro.getScanCode()))
				return false;
			if (!(useCaseTimestamp == 0 //
					|| useCaseTimestamp == macro.getUseCaseCreationTime()))
				return false;

			return true;
		}

		public boolean isVoid() {
			return deviceName == null && exeFileName == null && scanCode <= 0 && useCaseTimestamp == 0;
		}
	}

	private transient Filter filter = new Filter();

	public class FilteredMacrosList extends ArrayList<Macro> implements IAwareOfChanges {
		private PropertyChangeListener[] listeners;

		public FilteredMacrosList() {
		}

		public FilteredMacrosList(int size) {

		}

		@Override
		public boolean add(Macro e) {
			boolean add = super.add(e);
			if (add)
				meChanged();
			return add;
		}

		private void meChanged() {
			notifyPropertyChange("transient:macrossnapshotchanged", null, null);

		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -4945070640610174420L;

		@Override
		public void setListeners(PropertyChangeListener[] listeners) {
			this.listeners = listeners;
		}

		@Override
		public PropertyChangeListener[] getListeners() {
			return listeners;
		}
		

		@Override
		public HashMap<String, Boolean> getConfigurabilityOverrides() {
			return configurabilityOverrides;
		}

		private transient HashMap<String, Boolean> configurabilityOverrides; 
		
		@Override
		public void setConfigurabilityOverrides(HashMap<String, Boolean> map) {
			configurabilityOverrides=map; 
			
		}



	}

	public List<Macro> getFilteredList() {

		MacrosList list2 = getList();

		if (filter.isVoid())
			return list2;

		ArrayList<Macro> copy = new FilteredMacrosList(list2.size());

		for (Macro macro : list2) {
			if (filter.accept(macro))
				copy.add(macro);
		}

		return copy;
	}

	@SuppressWarnings("deprecation")
	public void copyFromAppToApp(ApplicationForMacros source, UseCase ucase, ApplicationForMacros target) throws InstantiationException, IllegalAccessException {
		int fixedDelta = source.getExeFile().hashCode() - target.getExeFile().hashCode();
		fixedDelta &= 0xffff;
		MacrosList list2 = getList();
		ArrayList<Macro> srcList = new ArrayList<>();
		ArrayList<Macro> destList = new ArrayList<>();
		for (Macro macro : list2) {
			if (macro.getApplication() == source) {
				if (ucase == null || macro.getUseCaseCreationTime() == ucase.getCreationTime())
					srcList.add(macro);
			} else if (macro.getApplication() == target)
				destList.add(macro);
		}

		Macro[] presenti = new Macro[destList.size()];
		destList.toArray(presenti);
		destList.clear();

		for (Macro macro : srcList) {
			long tx = macro.getCreationTime() + fixedDelta;
			boolean needAdd = true;
			for (int i = 0; needAdd && i < presenti.length; i++) {
				needAdd &= (!(presenti[i].getCreationTime() == tx));
			}
			if (needAdd) {
				Macro copy = macro.copy(false);

				copy._____setCreationTime(tx);

				long uCaseTimeStamp = copy.getUseCaseCreationTime();

				if (uCaseTimeStamp > 0) {
					uCaseTimeStamp += fixedDelta;
				}

				UseCase byCreationTime = target.getUseCases().getByCreationTime(uCaseTimeStamp);
				if (byCreationTime == null) {
					byCreationTime = macro.get___useCase().doACopy();
					byCreationTime.____________setCreationTime(uCaseTimeStamp);
					target.getUseCases().add(byCreationTime);
				}
				copy.setName("[From " + source.getName() + "] " + copy.getName());
				copy._setUseCase(byCreationTime);
				copy._setApplication(target);
				copy.set___device(macro.getDevice());
				destList.add(copy);
				copy.relink(getList());
				macro.createTransientLinks();
			}
		}

		// for (Macro macro : destList) {
		// }

		if (destList.size() > 0) {
			getList().addAll(destList);
		}

	}


}
