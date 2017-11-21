package sgi.javaMacros.ui;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import sgi.javaMacros.model.enums.ActionType;

public class PayLoadSelectorsGroup extends ButtonGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8851665307675375272L;

	public void setSelected(ActionType type) {
		PayloadSelectorRadioButton him = null;
		Enumeration<AbstractButton> enm = super.getElements();
		while (enm.hasMoreElements()) {
			PayloadSelectorRadioButton pls = (PayloadSelectorRadioButton) enm.nextElement();
			if (pls.get_action().equals(type)) {
				him = pls;
			}
		}

		if (him != null) {
			him.setSelected(true);
		}
	}

}