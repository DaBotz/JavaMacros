package sgi.javaMacros.model.internal;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sgi.configuration.ConfigAtomTreeSet;
import sgi.javaMacros.controller.LuaEvent;
import sgi.os.WindowData;

public class UseCases extends ConfigAtomTreeSet<UseCase> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7746718501043411262L;

	private transient UseCase[] priorityList = null;

	public UseCases() {
	}

	public boolean processMacroRequest(LuaEvent event, WindowData activeWindow, UseCase currentUseCase) {
		if (priorityList == null) {
			buildPriorityArray();
		}
		for (UseCase useCase : priorityList) {
			if (useCase.processMacroRequest(event, activeWindow, currentUseCase))
				return true;

		}

		return false;
	}

	public ArrayList<UseCase> buildPriorityArray() {
		ArrayList<UseCase> l = asOrderedList();
		UseCase[] pList = new UseCase[l.size()];
		l.toArray(pList);
		priorityList = pList;
		return l;
	}

	public ArrayList<UseCase> asOrderedList() {
		ArrayList<UseCase> asList = new ArrayList<>(this);
		Collections.sort(asList);
		return asList;
	}

	@Override
	public void propagatePropertyChange(PropertyChangeEvent evt) {
		if ("priority".equals(evt.getPropertyName())) {
			ArrayList<UseCase> list = buildPriorityArray();
			clear();
			addAll(list);
		}
		super.propagatePropertyChange(evt);
	}

	public UseCase getByCreationTime(long useCaseCreationTime) {
		for (UseCase useCase : this) {
			if (useCase.getCreationTime() == useCaseCreationTime)
				return useCase;
		}
		return null;

	}

	public UseCase getBasicUseCase() {
		return getByCreationTime(-1);
	}

	public ArrayList<UseCase> getManuals() {
		ArrayList<UseCase> rv = new ArrayList<>(this.size());
		for (UseCase useCase : this) {
			if (useCase.isManual() && useCase.isEnabled())
				rv.add(useCase);
		}
		rv.sort(new Comparator<UseCase>() {

			@Override
			public int compare(UseCase o1, UseCase o2) {
				return o1.compareByName(o2);
			}
		});

		return rv;
	}
}