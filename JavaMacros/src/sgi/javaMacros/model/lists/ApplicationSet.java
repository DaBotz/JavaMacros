package sgi.javaMacros.model.lists;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import sgi.configuration.ConfigAtomHashSet;
import sgi.configuration.IConfigurationAtom;
import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.model.internal.ApplicationForMacros;
//import sgi.javaMacros.os.Application;
import sgi.os.Application;
import sgi.os.ApplicationsRetriever;

public class ApplicationSet extends ConfigAtomHashSet<ApplicationForMacros> implements IConfigurationAtom {

	private static String javawAlias = System.getProperty("javaMacros.javawAlias", "javaw.exe");

	public static class ApplicationList extends ArrayList<ApplicationForMacros> implements IAwareOfChanges {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7739694878609806831L;

		private PropertyChangeListener[] listeners;

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
			configurabilityOverrides = map;

		}

	}

	@Override
	public boolean add(ApplicationForMacros e) {

		notifyPropertyChange(CONTENT_ADD_ATOM, null, e);
		boolean add = super.add(e);

		return add;
	}

	private transient PropertyChangeListener autoLlistener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (applicationsLookupTable != null)
				createLookupTable();
		}
	};

	@Override
	public boolean remove(Object o) {
		if (o instanceof ApplicationForMacros) {
			if (!((ApplicationForMacros) o).isReal())
				return false;
		}
		notifyPropertyChange(CONTENT_REMOVE_ATOM, o, null);
		return super.remove(o);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4144279348348472651L;

	@Deprecated
	public ApplicationSet() {
		super();
		addPropertyChangeListener(autoLlistener);
	}

	public ApplicationSet(IConfigurationAtom parent2) {
		super(parent2);
		addPropertyChangeListener(autoLlistener);
	}

	// public ApplicationSet(List<Application> allWindows) {
	// this();
	// for (Application application : allWindows) {
	// ApplicationForMacros e = new ApplicationForMacros(application);
	// super.add(e);
	// }
	// }

	// public ApplicationSet(List<sgi.os.Application> allVisibleApplications) {
	// loadAppsFromSystem(allVisibleApplications);
	//
	// }

	public void loadAppsFromSystem(List<sgi.os.Application> allVisibleApplications) {
		HashSet<String> allFiles = new HashSet<>();

		for (Application application : allVisibleApplications) {
			String exeFile = application.getExeFile();
			if (!exeFile.endsWith(javawAlias)) {
				allFiles.add(exeFile);
				ApplicationForMacros e = new ApplicationForMacros(application);
				if (!contains(e))
					add(e);
			}
		}

		for (ApplicationForMacros app : this) {
			app.setAbsent(!allFiles.contains(app.getExeFile()));
		}
	}

	@Override
	public void relink(IConfigurationAtom parent) {
		super.relink(parent);

		if (!contains(ApplicationForMacros.getANY())) {
			add(ApplicationForMacros.getANY());
		}
	}

	private transient Hashtable<String, ApplicationForMacros> applicationsLookupTable;

	public ApplicationList aslist() {
		ApplicationList applicationList = new ApplicationList();
		Iterator<ApplicationForMacros> it = iterator();
		while (it.hasNext())
			applicationList.add((ApplicationForMacros) it.next());
		return applicationList;
	}

	public void purge() {
		Iterator<ApplicationForMacros> it = iterator();
		while (it.hasNext()) {
			ApplicationForMacros app = (ApplicationForMacros) it.next();
			if (app == null || app.mayBePurged())
				it.remove();
		}
	}

	public ApplicationForMacros fastFind(String exeFile) {
		if (applicationsLookupTable == null)
			createLookupTable();
		ApplicationForMacros app = applicationsLookupTable.get(exeFile);
		if (app == null) {
			loadAppsFromSystem();
			app = applicationsLookupTable.get(exeFile);
		}

		return app;
	}

	public ApplicationSet loadAppsFromSystem() {
		List<Application> aVa = ApplicationsRetriever.getAllVisibleApplications();
		loadAppsFromSystem(aVa);
		createLookupTable();
		return this;
	}

	private void createLookupTable() {
		Hashtable<String, ApplicationForMacros> table = new Hashtable<>();

		for (ApplicationForMacros app : this) {
			table.put(app.getExeFile(), app);
		}
		applicationsLookupTable = table;
	}

	public ApplicationSet purgedCopy() {
		final ApplicationSet realFather = this;
		ApplicationSet rv = new ApplicationSet() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7349877771707363817L;

			@Override
			public boolean add(ApplicationForMacros e) {
				boolean add = super.add(e);
				e.setParent(realFather);
				return add;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				boolean remove = super.remove(o);
				((ApplicationForMacros) o).setParent(realFather);
				return remove;
			}
		};

		for (ApplicationForMacros app : this) {
			if (!app.mayBePurged())
				rv.add(app);
		}
		return rv;
	}

	public ApplicationForMacros[] asArray(ApplicationForMacros every, boolean usePurgedCopy) {
		ApplicationSet purgedCopy = usePurgedCopy ? purgedCopy() : this;

		ApplicationForMacros[] array = new ApplicationForMacros[purgedCopy.size()];
		purgedCopy.toArray(array);
		Arrays.sort(array, new Comparator<ApplicationForMacros>() {

			@Override
			public int compare(ApplicationForMacros o1, ApplicationForMacros o2) {

				try {
					return o1.getName().compareTo(o2.getName());
				} catch (Exception e) {
					return o1 == o2 ? 0 : -1;
				}
			}
		});

		if (every != null) {
			ApplicationForMacros[] ar2 = new ApplicationForMacros[array.length + 1];
			ar2[0] = every;
			System.arraycopy(array, 0, ar2, 1, array.length);
			return ar2;
		}

		return array;
	}

}
