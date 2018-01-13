package sgi.javaMacros.model.internal;

import sgi.javaMacros.model.abstracts.INameWrapper;
import sgi.javaMacros.model.abstracts.JavaMacrosConfigAtom;

public class WindowClass extends JavaMacrosConfigAtom implements Comparable<WindowClass>, INameWrapper {

	@Override
	public int compareTo(WindowClass o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	
	public boolean equals(Object obj) {
		if(obj==null) return false; 
		
		return super.equals(obj) || toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return getName();
	}
	
	public WindowClass() {

	}

	public WindowClass(String name) {
		this(); 
		setName(name);

	}


}
