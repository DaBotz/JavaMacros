package sgi.javaMacros.model.internal;

import sgi.configuration.IConfigurationAtom;
import sgi.javaMacros.msgs.Messages;

public class NullKey extends Key {

	@SuppressWarnings("deprecation")
	public NullKey() {
		super.setScanCode(-1);
		super.setName(Messages.M._$(Key.class, "add_new_key"));
	}
	
	@Override
	public void setScanCode(int scanCode) {

	}
	
	@Override
	public void setName(String name) {


	}
	@Override
	public void setParent(IConfigurationAtom parent) {

		
	}

}
