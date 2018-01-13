package sgi.javaMacros.ui;

import javax.swing.JRadioButton;

import sgi.javaMacros.model.enums.ActionType;

public class PayloadSelectorRadioButton extends JRadioButton {
	private static final long serialVersionUID = 7445602356122938626L;
	private ActionType _action;

	public PayloadSelectorRadioButton(String text, ActionType type) {
		super(text);
//		setActionCommand(ActionCommands.PAYLOAD.name() + "+" + type.name());
		this._action = type;
	}

	public ActionType get_action() {
		return _action;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof PayloadSelectorRadioButton)
			return ((PayloadSelectorRadioButton) obj).get_action().equals(get_action());
		return super.equals(obj);
	}

}