package sgi.javaMacros.model.lists;


import sgi.javaMacros.model.interfaces.IConfigAtom;
import sgi.javaMacros.model.internal.Macro;

public class Macros extends AtomList<Macro> implements  IConfigAtom{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Macros() {
		super();
	}

	public Macros(IConfigAtom parent2) {
		super(parent2);
	}
	
	
	
}
