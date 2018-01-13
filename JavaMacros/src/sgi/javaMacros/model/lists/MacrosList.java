package sgi.javaMacros.model.lists;


import java.util.function.Consumer;

import sgi.configuration.ConfigAtomArrayList;
import sgi.javaMacros.model.internal.Macro;

public class MacrosList extends ConfigAtomArrayList<Macro>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MacrosList() {
		super();
	}

//	@Override
//	public Macro remove(int index) {
//		// TODO Auto-generated method stub
//		return super.remove(index);
//	}
//	
//	@Override
//	public boolean remove(Object o) {
//		// TODO Auto-generated method stub
//		return super.remove(o);
//	}
	
	public void createTransientLinks() {
		
		for (Macro macro : this) {
			if(macro != null)
			macro.createTransientLinks();
			else {
				System.out.println( "nulls??"); 
			}
		}
		
	}

	public void deviceNameChange(final String rName, final String newName) {
		forEach(new Consumer<Macro>() {

			@Override
			public void accept(Macro t) {
				if( t.areTheseEquals(rName, t.getName()) )
					t.setName(newName);
			}
		});
		
	}

	
}
