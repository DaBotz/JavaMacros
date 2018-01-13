package sgi.javaMacros.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Window;

import javax.swing.JFrame;

import sgi.gui.configuration.ISaveable;

public class ConfigFrame extends JFrame implements ISaveable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2200946272387124304L;
	private transient boolean saved;
	private boolean usingEndButtons;

	@Override
	public boolean isSaved() {
		return saved;
	}

	@Override
	public boolean setSaved(boolean saved) {
		return (this.saved = saved);
	}

	public ConfigFrame() throws HeadlessException {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ConfigFrame(GraphicsConfiguration gc) {
		super(gc);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ConfigFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ConfigFrame(String title) throws HeadlessException {
		super(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	
	@Override
	public Window getWindow() {
		return this;
	}
	
	@Override
	public boolean isUsingEndButtons() {
		return usingEndButtons;
	}

	protected void setUsingEndButtons(boolean usingEndButtons) {
		this.usingEndButtons = usingEndButtons;
	}


}
