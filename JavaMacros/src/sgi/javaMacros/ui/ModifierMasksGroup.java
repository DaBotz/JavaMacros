package sgi.javaMacros.ui;

import java.awt.AWTEvent;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import sgi.gui.configuration.IAwareOfChanges;
import sgi.javaMacros.model.enums.ModifierMasks;

public class ModifierMasksGroup implements IAwareOfChanges, ActionListener, ItemListener {

	private JToggleButton[] checkers;

	public JToggleButton[] getCheckers() {
		return checkers;
	}

	@Override
	public HashMap<String, Boolean> getConfigurabilityOverrides() {
		return null;
	}

	@Override
	public void setConfigurabilityOverrides(HashMap<String, Boolean> map) {

	}

	private transient boolean isMe = false;
	private int modifierMask;

	public int getModifierMask() {
		return modifierMask;
	}

	public void setModifierMask(int modifierMask) {
		if (isMe)
			return;
		isMe = true;
		this.modifierMask = modifierMask;
		updateUI();
		isMe = false;
	}

	public ModifierMasksGroup() {
		this(false);
	}

	public ModifierMasksGroup(boolean avoidExtremes) {

		values = ModifierMasks.values(avoidExtremes);
		this.checkers = new JToggleButton[values.length];
		for (int i = 0; i < values.length; i++) {
			ModifierMasks mMask = values[i];
			if (mMask.isNone() || mMask.isAlways())
				checkers[i] = new JRadioButton(mMask.getLabelText());
			else
				checkers[i] = new JCheckBox(mMask.getLabelText());

			checkers[i].addActionListener(this);
		}
	}

	private PropertyChangeListener[] listeners;
	private ModifierMasks[] values;
	private JPanel panel;

	public PropertyChangeListener[] getListeners() {
		return listeners;
	}

	public void setListeners(PropertyChangeListener[] listeners) {
		this.listeners = listeners;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		processEvent(e);
	}

	public void processEvent(AWTEvent e) {
		if (isMe)
			return;
		isMe = true;
		int initVal = modifierMask;
		readAction(e);
		System.out.println(modifierMask);
		if (initVal != modifierMask) {
			updateUI();
			notifyPropertyChange("modifierMask", initVal, modifierMask);
		}
		isMe = false;
	}

	protected void updateUI() {
		for (int i = 0; i < checkers.length; i++) {
			JToggleButton jToggleButton = checkers[i];
			jToggleButton.setSelected(values[i].isSet(modifierMask));
		}
	}

	protected void readAction(AWTEvent e) {
		JToggleButton jtb = (JToggleButton) e.getSource();

		modifierMask = 0;

		for (int i = 0; i < checkers.length; i++) {

			JToggleButton jToggleButton = checkers[i];
			boolean selected = jToggleButton.isSelected();
			ModifierMasks mms = values[i];

			if (jToggleButton == jtb && selected) {
				if (mms == ModifierMasks.ALWAYS_INVOKED) {
					modifierMask = ModifierMasks.ALWAYS_INVOKED.bitMask();
					return;
				}
				if (mms == ModifierMasks.NONE) {
					modifierMask = ModifierMasks.NONE.bitMask();
					return;
				}

			}
			int bMask = mms.bitMask();

			if (bMask == 0) {
				selected = !selected;
			}

			if (selected && mms != ModifierMasks.ALWAYS_INVOKED) {

				modifierMask = modifierMask | bMask;
			} else {

			}
		}

	}

	public JPanel getPanel() {
		return getPanel(null);
	}

	public JPanel getPanel(JLabel label) {
		if (panel != null)
			return panel;

		JToggleButton[] checkers = getCheckers();

		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
		if (label != null)
			panel.add(label);

		for (JToggleButton checker : checkers) {
			panel.add(checker);
		}

		return panel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		processEvent(e);
	}

	public void setEnabled(boolean b) {

		JToggleButton[] chks = getCheckers();
		for (JToggleButton jToggleButton : chks) {
			jToggleButton.setEnabled(b);
		}
	}

	public void setDisabledIcon(Icon disabledIcon) {

		JToggleButton[] chks = getCheckers();
		for (JToggleButton jToggleButton : chks) {
			jToggleButton.setDisabledIcon(disabledIcon);
		}
	}

	public void setdIcon(Icon disabledIcon) {

		JToggleButton[] chks = getCheckers();
		for (JToggleButton jToggleButton : chks) {
			jToggleButton.setIcon(disabledIcon);
		}
	}

}
