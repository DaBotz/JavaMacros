package sgi.javaMacros.model.lists;

import java.util.Collection;

import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;

public interface IAtomCollection<E extends IConfigAtom> extends IConfigAtom {

	boolean remove(Object o);

	boolean removeAll(Collection<?> c);

	boolean add(IConfigAtom e);

	boolean addAll(Collection<? extends IConfigAtom> c);

	IConfigAtom getParent();

	void setParent(IConfigAtom parent);

	void fireConfigChangeListeners(ConfigChangeEvent ev);

	void addConfigChangeListener(IConfigChangeListener listener);

	boolean removeAtomListener(IConfigChangeListener listener);

	void setUniqueIdentifier(int uniqueIdentifier);

	int getUniqueIdentifier();

	IConfigChangeListener getListenerRepepter();

	int size();

}