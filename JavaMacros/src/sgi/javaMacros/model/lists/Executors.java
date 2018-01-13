package sgi.javaMacros.model.lists;

import sgi.configuration.ConfigAtomEnumMap;
import sgi.javaMacros.model.enums.ActionType;
import sgi.javaMacros.model.internal.AutoCopier;
import sgi.javaMacros.model.macros.execution.Executor;

public class Executors extends ConfigAtomEnumMap<ActionType, Executor> implements AutoCopier<Executors>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4828055530982015670L;

	public Executors() {
		super(ActionType.class);
		
	}
}

//	public Executors(int initialCapacity, float loadFactor) {
//		super(initialCapacity, loadFactor);
//		
//	}
//
//	public Executors(int initialCapacity) {
//		super(initialCapacity);
//		
//	}
//
//	public Executors(Map<? extends ActionType, ? extends Executor> m) {
//		super(m);
//		
//	}
//
//}
