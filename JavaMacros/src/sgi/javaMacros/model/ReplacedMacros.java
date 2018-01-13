package sgi.javaMacros.model;

import sgi.javaMacros.model.abstracts.AbstractAtticableMacros;
import sgi.javaMacros.model.internal.Macro;

public class ReplacedMacros extends AbstractAtticableMacros {
	@Override
	protected long getRelevantTime(Macro o2) {
		return o2.getReplacementTime();
	}

	public ReplacedMacros() {
		endStartup();
	}
	

	@Override
	protected synchronized String getAlternateFolderName() {
			return "older_macros";
	}

}