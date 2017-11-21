package sgi.javaMacros.model;

import java.util.Iterator;

import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.internal.Application;
import sgi.javaMacros.model.lists.ApplicationSet;
import sgi.javaMacros.model.persistent.AbstractJavaMacrosMemoryParcel;

public class Applications extends AbstractJavaMacrosMemoryParcel {

	ApplicationSet set;

	@Override
	protected void initializeDefaultValues() {
		set = new ApplicationSet();

	}

	public Applications() {

		set.addConfigChangeListener(this);

	}

	public ApplicationSet getSet() {
		return set;
	}

	@Override
	public void storeToFile() {
		
		getSet().purge();
		super.storeToFile();
	}
}
