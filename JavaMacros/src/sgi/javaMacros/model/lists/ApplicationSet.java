package sgi.javaMacros.model.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.internal.Application;

public class ApplicationSet extends AtomSet<Application> implements IConfigAtom {

	public static class ApplicationList extends ArrayList<Application> {

		public ApplicationList() {
			super();
			// TODO Auto-generated constructor stub
		}

		public ApplicationList(Collection<? extends Application> c) {
			super(c);
			// TODO Auto-generated constructor stub
		}

		public ApplicationList(int initialCapacity) {
			super(initialCapacity);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -7739694878609806831L;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4144279348348472651L;

	public ApplicationSet() {
		super();
	}

	public ApplicationSet(IConfigAtom parent2) {
		super(parent2);

	}

	public ApplicationList aslist() {
		ApplicationList applicationList = new ApplicationList();
		Iterator<IConfigAtom> it = iterator();
		while (it.hasNext())
			applicationList.add((Application) it.next());

		return applicationList;
	}

	public void purge() {
		Iterator<IConfigAtom> it = iterator();
		while (it.hasNext()) {
			Application app = (Application) it.next();
			if (app.mayBePurged())
				it.remove();
		}

	}

}
