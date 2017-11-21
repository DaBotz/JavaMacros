package sgi.javaMacros.ui.dialogs;

import java.awt.Window;
import java.awt.Dialog.ModalityType;

import javax.swing.JPanel;

public class NoButtonsDeviceDialog extends DeviceInputDialog {
	/**
			 * 
			 */
	private static final long serialVersionUID = 8669873871979479671L;

	public NoButtonsDeviceDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
	}

	@Override
	protected JPanel createEndButtons() {
		super.createEndButtons();
		return null;
	}
}