package sgi.javaMacros.msgs;

import java.net.URL;
import java.util.ResourceBundle;

import sgi.generic.ui.AbstractMsgs;
import sgi.javaMacros.ui.JavaMacrosUI;

public class Messages extends AbstractMsgs {
	private static final String BUNDLE_NAME = "sgi.javaMacros.msgs.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
		super(RESOURCE_BUNDLE);
	}
	
	public static final Messages M= new Messages();

	@Override
	public URL getSettingsImageResource() {
		return JavaMacrosUI.class.getResource("icon.png");
	}

	public String getString(Object owner, String subkey) {
		return getString(owner.getClass().getCanonicalName()+"."+subkey);
	}
}
