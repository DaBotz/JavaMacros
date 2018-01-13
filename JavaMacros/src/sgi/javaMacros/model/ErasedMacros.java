package sgi.javaMacros.model;

import sgi.javaMacros.model.abstracts.AbstractAtticableMacros;
import sgi.javaMacros.model.internal.Macro;

public class ErasedMacros extends AbstractAtticableMacros {
	@Override
	protected long getRelevantTime(Macro o2) {
		return o2.getDeletionTime();
	}

	public ErasedMacros() {
		endStartup();
	}

}