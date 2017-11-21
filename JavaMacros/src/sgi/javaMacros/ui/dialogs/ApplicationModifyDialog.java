package sgi.javaMacros.ui.dialogs;

import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.Dialog.ModalityType;

import javax.swing.JPanel;

import sgi.generic.ui.IMessages;
import sgi.generic.ui.ModalConfigDialog;

public class ApplicationModifyDialog extends ModalConfigDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7800182944589046156L;

	public ApplicationModifyDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	@Override
	protected JPanel createEndButtons() {
		super.createEndButtons();
		return null;
	}

	@Override
	public void build(Object object, IMessages msgs) throws HeadlessException {
		setnoFields(new String[] { "enabled", "exeFile", "currentUseCase", "windowClasses" });
		setUsingRecursiveFieldScan(true);
		super.build(object, msgs);
	}
}