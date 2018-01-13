package sgi.javaMacros.model;

import java.beans.PropertyChangeEvent;

import sgi.javaMacros.model.abstracts.JavaMacrosMemoryParcel;
import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.model.lists.ApplicationSet;

public class Applications extends JavaMacrosMemoryParcel {

	private ApplicationSet set;

	@Override
	protected void initializeDefaultValues() {
		set = new ApplicationSet(this);
		set.setParent(this);
	}

	public Applications() {

	}

	public ApplicationSet getSet() {
		return set;
	}

	@Override
	public void storeToFile() {

		getSet().purge();
		super.storeToFile();
	}

	@Override
	public void propagatePropertyChange(PropertyChangeEvent evt) {
		if ("content:add".equals(evt.getPropertyName())) {
			if (evt.getNewValue() instanceof ApplicationForMacros) {
				ApplicationForMacros app = (ApplicationForMacros) evt.getNewValue();
				if (app.mayBePurged()) {

					evt = new PropertyChangeEvent(evt.getSource(), "transient:" + evt.getPropertyName(),
							evt.getOldValue(), evt.getNewValue());
				}
			}

		}

		super.propagatePropertyChange(evt);
	}

}
