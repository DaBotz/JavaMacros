package sgi.javaMacros.ui;

import javax.swing.JCheckBox;

public class ModifierCheckBox extends JCheckBox {
	private static final long serialVersionUID = -3564990165245232811L;

	public ModifierCheckBox(String text) {
		super(text);
		setActionCommand(ActionCommands.SELECT_MODIFIER.name() + "+" + text);
	}
}