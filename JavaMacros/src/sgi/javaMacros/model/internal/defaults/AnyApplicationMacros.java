package sgi.javaMacros.model.internal.defaults;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import sgi.javaMacros.model.internal.ApplicationForMacros;
import sgi.javaMacros.msgs.Messages;

public class AnyApplicationMacros extends ApplicationForMacros {

	private transient ImageIcon imageIcon;
	
	

@Override
public boolean isAbsent() {

	return false;
}
	@Deprecated
	public AnyApplicationMacros() {
		setName(Messages.M._$("ApplicationForMacros.ANY"));
		setExeFile("./*.*");
		ApplicationForMacros.setANY(this);
	}

	@Override
	public boolean mayBePurged() {
		return false;
	}

	@Override
	public Icon getIcon() {
		if (imageIcon == null)
			imageIcon = new ImageIcon(Messages.M.getIcon());
		return imageIcon;
	}
	
	@Override
	public Icon getBigIcon() {
		return getIcon();
	}
	
	public boolean isReal() {
		return false; 
	}
}