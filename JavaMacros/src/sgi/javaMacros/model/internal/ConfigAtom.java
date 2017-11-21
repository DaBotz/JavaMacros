package sgi.javaMacros.model.internal;

import java.util.ArrayList;
import java.util.Iterator;

import sgi.javaMacros.model.ConfigChangeType;
import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;

public abstract class ConfigAtom implements IConfigAtom {


	private transient IConfigAtom parent = null;
	private transient ArrayList<IConfigChangeListener> listeners = null;
	private transient IConfigChangeListener repeter = null;
	
	protected IConfigChangeListener getRepeter(){
		if( repeter == null ){
			repeter= new IConfigChangeListener() {
				@Override
				public void handleConfigChangeEvent(ConfigChangeEvent event) {
					fireConfigChangeListeners(event); 
				}
			};
		}
		return	repeter;
	}

	public ConfigAtom() {
		super();
	}

	public IConfigAtom getParent() {
		return parent;
	}

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
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public boolean removeAtomListener(IConfigChangeListener listener) {
		if (listeners == null)
			return false;

		return listeners.remove(listener);
	}
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String dd2 = this.name;
		this.name = name;
		fireConfigChangeListeners(new ConfigChangeEvent(ConfigChangeType.MODIFIED_ATOM,  this,  "name", dd2)); 
	}




	@Override
	public String toString() {
		return ""+getName();
	}
	
	



}
