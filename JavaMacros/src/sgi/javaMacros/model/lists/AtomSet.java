package sgi.javaMacros.model.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.model.events.ListConfigChangeEvent;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;

public abstract class AtomSet<E extends IConfigAtom> extends HashSet<IConfigAtom> implements IAtomCollection<IConfigAtom> {

	@Override
	public boolean remove(Object o) {

		int size = size(), j = 0;

		IConfigAtom[] a = new IConfigAtom[size];
		super.toArray(a);

		for (; j < size && a[j] != o; j++)
			;
		boolean remove = super.remove(o);
		if (remove) {
			fireConfigChangeListeners(new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM, this, o, j, j));
		}
		return remove;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean remove = super.removeAll(c);
		if (remove) {
			fireConfigChangeListeners(
					new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM_COLLETION, this, c, 0, size()));
		}

		return remove;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6394645168453770417L;
	private transient IConfigAtom parent = null;

	private transient ArrayList<IConfigChangeListener> listeners = null;

	private transient IConfigChangeListener listenerRepepter;

	public AtomSet() {
		super();
	}

	public AtomSet(IConfigAtom parent2) {
		setParent(parent2);
	}

	public AtomSet(Collection<IConfigAtom> allWindows) {
		super(allWindows);
	}

	@Override
	public boolean add(IConfigAtom e) {
		// TODO Auto-generated method stub
		boolean add = super.add(e);
		if (add)
			_append(e);

		return add;
	}

	private void _append(IConfigAtom e) {
		e.addConfigChangeListener(getListenerRepepter());
		if (parent != null) {
			e.setParent(parent);
		}
		fireConfigChangeListeners(
				new ListConfigChangeEvent(ConfigChangeType.ADDED_ATOM, this, e, size() - 2, size() - 1));
	}

	@Override
	public boolean addAll(Collection<? extends IConfigAtom> c) {
		Iterator<? extends IConfigAtom> it = c.iterator();
		boolean addAll = true;
		int start = size();
		while (it.hasNext()) {
			addAll |= add(it.next());
		}
		fireConfigChangeListeners(
				new ListConfigChangeEvent(ConfigChangeType.ADDED_ATOM_RAMGE, this, c, start, size() - 1));
		return addAll;
	}

	@Override
	public IConfigAtom getParent() {
		return parent;
	}

	@Override
	public void setParent(IConfigAtom parent) {
		this.parent = parent;

	}

	public void fireConfigChangeListeners(ConfigChangeEvent ev) {
		if (listeners == null)
			return;
		Iterator<IConfigChangeListener> it = listeners.iterator();
		while (it.hasNext())
			it.next().handleConfigChangeEvent(ev);
	}

	public void addConfigChangeListener(IConfigChangeListener listener) {
		if (listeners == null)
			listeners = new ArrayList<IConfigChangeListener>();
		listeners.add(listener);
	}

	public boolean removeAtomListener(IConfigChangeListener listener) {
		if (listeners == null)
			return false;

		return listeners.remove(listener);
	}

	@Override
	public void setUniqueIdentifier(int uniqueIdentifier) {

	}

	@Override
	public int getUniqueIdentifier() {
		return -1;
	}

	public IConfigChangeListener getListenerRepepter() {
		if (listenerRepepter == null)
			listenerRepepter = new IConfigChangeListener() {
				@Override
				public void handleConfigChangeEvent(ConfigChangeEvent event) {
					fireConfigChangeListeners(event);
				}
			};
		return listenerRepepter;
	}

}