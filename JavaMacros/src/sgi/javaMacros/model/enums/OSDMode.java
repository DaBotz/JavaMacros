package sgi.javaMacros.model.enums;

import static sgi.javaMacros.model.macros.execution.Executor.COMPLETE;
import static sgi.javaMacros.model.macros.execution.Executor.PASS;
import static sgi.javaMacros.model.macros.execution.Executor.REQUIRES_OSD;

public enum OSDMode {
	ALWAYS_HIDDEN, ALLOWED_TO_MACRO, FORCED, FORCED_ON_FAILS;

	private static final int FORFORCED = REQUIRES_OSD | COMPLETE;

	public boolean isOSDrequired(boolean macroWantsIt, int executorRV) {
		switch (this) {
		case ALLOWED_TO_MACRO:
			return macroWantsIt && (executorRV & REQUIRES_OSD)!=0 ;
		case FORCED:
			return  (executorRV & FORFORCED)!=0;
		case FORCED_ON_FAILS:
			return (executorRV & PASS)==0;
		case ALWAYS_HIDDEN:
		}
		return false;
	}

//	public static boolean isOsdRequiredx(OSDMode mode, boolean macroWantsIt, int executorRV) {
//		if (mode == null)
//			return FORCED_ON_FAILS._OSDrequired(macroWantsIt, executorRV);
//		return mode._OSDrequired(macroWantsIt, executorRV);
//
//	}
}
