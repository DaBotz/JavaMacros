package sgi.javaMacros.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;

import sgi.gui.configuration.AbstractSetter;
import sgi.gui.configuration.ISaveable;

public class ModalConfigDialog extends JDialog implements ISaveable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4702736756155994743L;
	private boolean saved;
	private boolean usingEndButtons;

	public ModalConfigDialog() {
		super();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public ModalConfigDialog(Frame owner) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Dialog owner) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Window owner) {
		super(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Frame owner, boolean modal) {
		super(owner, modal);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Frame owner, String title) {
		super(owner, title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Dialog owner, String title) {
		super(owner, title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Window owner, String title) {
		super(owner, title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Window owner, String title, ModalityType modalityType) {
		super(owner, title, modalityType);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	public ModalConfigDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	
	@Override
	public boolean isSaved() {
		return this.saved;
	}

	@Override
	public boolean setSaved(boolean saved) {
		return this.saved=saved;
	}

	@Override
	public Window getWindow() {
		return this;
	}

	@Override
	public boolean isUsingEndButtons() {
		return usingEndButtons;
	}

	public void setUsingEndButtons(boolean usingEndButtons) {
		this.usingEndButtons = usingEndButtons;
	}
	
	@Override
	public void dispose() {
		AbstractSetter.disposeContainer(getContentPane());
		super.dispose();
	}

}