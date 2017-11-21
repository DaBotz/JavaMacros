package sgi.javaMacros.model.lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.model.events.ListConfigChangeEvent;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;

public abstract class AtomList<E extends IConfigAtom> extends ArrayList<IConfigAtom> implements IConfigAtom, IAtomCollection<IConfigAtom> {
	
	
	
	@Override
	public IConfigAtom remove(int index) {
		// TODO Auto-generated method stub
		IConfigAtom remove = super.remove(index);
		if (remove != null) {
			fireConfigChangeListeners(
					new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM, this, remove, index, index));
		}
		return remove;
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {

		int size = size(), j = 0;
		for (; j < size && get(j) != o; j++)
			;
		boolean remove = super.remove(o);
		if (remove) {
			fireConfigChangeListeners(new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM, this, o, j, j));
		}
		return remove;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		super.removeRange(fromIndex, toIndex);
		fireConfigChangeListeners(new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM_COLLETION, this, this, fromIndex, toIndex));
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean remove = super.removeAll(c);
		if (remove) {
			fireConfigChangeListeners(new ListConfigChangeEvent(ConfigChangeType.REMOVED_ATOM_COLLETION, this, c, 0, size()));
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

	public AtomList() {
		super();
	}

	public AtomList(IConfigAtom parent2) {
		setParent(parent2);
	}

	public AtomList(Collection<IConfigAtom> allWindows) {
		super(allWindows);
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#add(sgi.javaMacros.model.interfaces.IConfigAtom)
	 */
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
	public void add(int index, IConfigAtom element) {
		_append(element);
		super.add(index, element);
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends IConfigAtom> c) {
		Iterator<? extends IConfigAtom> it = c.iterator();
		boolean addAll = true;
		int start = size();
		while (it.hasNext()) {
			addAll &= add(it.next());
		}
		fireConfigChangeListeners(
				new ListConfigChangeEvent(ConfigChangeType.ADDED_ATOM_RAMGE, this, c, start, size() - 1));
		return addAll;
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#getParent()
	 */
	@Override
	public IConfigAtom getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#setParent(sgi.javaMacros.model.interfaces.IConfigAtom)
	 */
	@Override
	public void setParent(IConfigAtom parent) {
		this.parent = parent;

	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#fireConfigChangeListeners(sgi.javaMacros.model.events.ConfigChangeEvent)
	 */
	@Override
	public void fireConfigChangeListeners(ConfigChangeEvent ev) {
		if (listeners == null)
			return;
		Iterator<IConfigChangeListener> it = listeners.iterator();
		while (it.hasNext())
			it.next().handleConfigChangeEvent(ev);
		
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#addConfigChangeListener(sgi.javaMacros.model.interfaces.IConfigChangeListener)
	 */
	@Override
	public void addConfigChangeListener(IConfigChangeListener listener) {
		if (listeners == null)
			listeners = new ArrayList<IConfigChangeListener>();
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#removeAtomListener(sgi.javaMacros.model.interfaces.IConfigChangeListener)
	 */
	@Override
	public boolean removeAtomListener(IConfigChangeListener listener) {
		if (listeners == null)
			return false;

		return listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#setUniqueIdentifier(int)
	 */
	@Override
	public void setUniqueIdentifier(int uniqueIdentifier) {

	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#getUniqueIdentifier()
	 */
	@Override
	public int getUniqueIdentifier() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see sgi.javaMacros.model.lists.IAtomCollection#getListenerRepepter()
	 */
	@Override
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