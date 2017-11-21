package sgi.javaMacros.model.persistent;

import sgi.generic.serialization.AbstractMemoryRoot;
import sgi.javaMacros.model.events.ConfigChangeEvent;
import sgi.javaMacros.model.interfaces.IConfigChangeListener;

public abstract class AbstractJavaMacrosMemoryParcel extends AbstractMemoryRoot  implements IConfigChangeListener{

	private transient boolean dirty;

	public AbstractJavaMacrosMemoryParcel() {
		super();
	}

	@Override
	public void handleConfigChangeEvent(ConfigChangeEvent event) {
		this.dirty = true;
	
	}

	@Override
	public void storeToFile() {
	//	if (dirty)
			super.storeToFile();
		dirty=false;
	}

	@Override
	public String getFileName() {
		return getClass().getSimpleName()+".xml";
	}

}
